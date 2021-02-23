package com.jotagalilea.posts.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jotagalilea.posts.apiclient.Endpoints
import com.jotagalilea.posts.db.PostsDao
import com.jotagalilea.posts.db.asDomainModel
import com.jotagalilea.posts.db.asDomainModelMap
import com.jotagalilea.posts.db.getDatabase
import com.jotagalilea.posts.model.Comment
import com.jotagalilea.posts.model.Post
import com.jotagalilea.posts.model.User
import com.jotagalilea.posts.model.asDBObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Viewmodel que tiene las referencias a objetos del modelo, acceso a base de datos y api web.
 */
class PostsViewModel(application: Application) : AndroidViewModel(application) {
	private val TAG = "Obteniendo posts..."
	private val TAG_ERROR = "Error obteniendo datos"

	// Map que almacena los posts. El id del post se usa como clave.
	private var main_postsMap: MutableLiveData<MutableMap<Int, Post>> = MutableLiveData(mutableMapOf())
	// Map con los usuarios que hicieron los posts. El id del usuario se usa como clave.
	private var main_usersMap: MutableLiveData<MutableMap<Int, User>> = MutableLiveData(mutableMapOf())
	private var detail_comments: MutableLiveData<MutableList<Comment>> = MutableLiveData(mutableListOf())
	private var usersIDsSearched: MutableList<Int>
	private var dao: PostsDao
	private val retrofit: Retrofit
	private val api: Endpoints


	init {
		dao = getDatabase(application.applicationContext).getDao()

		/////////////////////////////////////////////////////////////
		/*val dispatcher = Dispatcher()
		dispatcher.maxRequests = 1
		val okHttpClient = OkHttpClient()
		okHttpClient.newBuilder().dispatcher(dispatcher)
		 */
		/////////////////////////////////////////////////////////////

		retrofit = Retrofit.Builder()
			.baseUrl(Endpoints.BASE_URL)
			.addConverterFactory(GsonConverterFactory.create())
			//.client(okHttpClient)
			.build()

		api = retrofit.create(Endpoints::class.java)
		usersIDsSearched = mutableListOf()
	}


	/**
	 * Carga de ítems en el modelo de BD o de servicio web, según se necesite.
	 * @param reload Indica si hay que recargar el map.
	 */
	fun findPosts(reload: Boolean){
		viewModelScope.launch {
			if (reload) {
				main_postsMap.value?.clear()
				main_usersMap.value?.clear()
			}
			val dbItems: Map<Int, Post> = dao.getAllPosts().asDomainModelMap()
			if (!dbItems.isNullOrEmpty()) {
				main_postsMap.addNewItems(dbItems)
			}
			else {
				requestPostsList()
			}
		}
	}


	fun findCommentsOfPost(postId: Int) {
		viewModelScope.launch {
			detail_comments.value?.clear()
			val dbComments = dao.getCommentsFromPost(postId).asDomainModel()
			if (!dbComments.isNullOrEmpty()){
				detail_comments.postValue(dbComments as MutableList<Comment>?)
			}
			else {
				requestCommentsOfPost(postId)
			}
		}
	}


	private fun requestCommentsOfPost(postId: Int){
		val call: Call<List<Comment>> = api.getCommentsOfPost(postId)
		call.enqueue(object : Callback<List<Comment>> {

			override fun onResponse(call: Call<List<Comment>>, response: Response<List<Comment>>) {
				if (response.isSuccessful) {
					val apiResponse = response.body()
					apiResponse?.let {
						CoroutineScope(Dispatchers.IO).launch {
							dao.insertComments(it.asDBObject())
						}
						detail_comments.postValue(it as MutableList<Comment>)
					}
				}else {
					detail_comments.value?.clear()
					Log.e(TAG_ERROR, response.message())
				}
			}

			override fun onFailure(call: Call<List<Comment>>, t: Throwable) {
				detail_comments.value?.clear()
				Log.e(TAG_ERROR, call.toString())
			}
		})
	}


	/**
	 * Devuelve el map de posts.
	 * @return Map de posts.
	 */
	fun getPostsMap(): MutableLiveData<MutableMap<Int, Post>> {
		return main_postsMap
	}

	fun getUsersMap(): MutableLiveData<MutableMap<Int, User>> {
		return main_usersMap
	}

	fun getCommentsList(): MutableLiveData<MutableList<Comment>>{
		return detail_comments
	}


	/**
	 * Realiza la petición al servicio web para obtener una serie de posts.
	 * Además lanza las peticiones necesarias para obtener los datos de los posts.
	 */
	private fun requestPostsList(){
		val call: Call<List<Post>> = api.getPostsList()
		call.enqueue(object : Callback<List<Post>> {
			override fun onResponse(
				call: Call<List<Post>>,
				response: Response<List<Post>>
			) {
				if (response.isSuccessful) {
					val apiResponse = response.body()
					apiResponse?.let {
						main_postsMap.addNewItems(it)
					}
				} else {
					main_postsMap.value?.clear()
					Log.e(TAG_ERROR, response.message())
				}
			}

			override fun onFailure(call: Call<List<Post>>, t: Throwable) {
				main_postsMap.value?.clear()
				Log.e(TAG_ERROR, call.toString())
			}

		})
	}


	//------------------------- Extensiones -------------------------//
	//TODO: Mover arriba lo que no sea una extensión.
	/**
	 * Extensión de MutableLiveData<MutableMap<Int, posts>> para añadir elementos al postsMap.
	 * Versión para cuando se recibe un List<Post>.
	 * @param newList Lista con nuevos elementos.
	 */
	private fun MutableLiveData<MutableMap<Int, Post>>.addNewItems(newList: List<Post>){
		val map = this.value
		val newItems = newList.map { it.id to it }.toMap()
		map?.putAll(newItems)
		this.setValue(map)
	}
	/**
	 * Extensión de MutableLiveData<MutableMap<Int, Post>> para añadir elementos al postsMap.
	 * Versión para cuando se recibe un Map.
	 * @param newMap Map con nuevos elementos.
	 */
	private fun MutableLiveData<MutableMap<Int, Post>>.addNewItems(newPosts: Map<Int, Post>){
		val old = this.value
		old?.putAll(newPosts)
		this.setValue(old)
	}

	fun findUsersOfPosts(postMap: Map<Int, Post>) = viewModelScope.launch{
		val it = postMap.iterator()
		//val auxUsersMap: MutableMap<Int, User> = mutableMapOf()
		val job = CoroutineScope(Dispatchers.Default).launch {
			while (it.hasNext()){
				findUserWithId(it.next().value.userId)
				//val user = findUserWithId(it.next().value.userId)
				//auxUsersMap.put(user.id, user)
			}
		}
		// Se necesita esperar a que obtenga el último usuario, entonces dispara el evento para que
		// actúe el observador:
		job.join()
		main_usersMap.postValue(main_usersMap.value)
	}



	//TODO: Mejorar comentario.
	private suspend fun findUserWithId(userId: Int){
		/*
		 * 1- 	Mirar en BD
		 *  	Si no está, entonces petición JSON, inserción en BD y devolver.
		 * 2-	Si está, devolver de BD.
		 */
		if (!usersIDsSearched.contains(userId)) {
			usersIDsSearched.add(userId)
			val userDB = dao.getUserWithID(userId)
			if (userDB != null) {
				val user = userDB.asDomainModel()
				main_usersMap.value?.put(user.id, user)
			} else {
				requestUserWithID(userId)
			}
		}
	}


	private fun requestUserWithID(id: Int){
		var user: User?
		val call: Call<List<User>> = api.getUserWithId(id)
		call.enqueue(object : Callback<List<User>> {
			override fun onResponse(
				call: Call<List<User>>,
				response: Response<List<User>>
			) {
				if (response.isSuccessful) {
					user = response.body()?.get(0)
					user?.let {
						main_usersMap.value?.put(it.id, it)
						CoroutineScope(Dispatchers.Main).launch {
							dao.insertUser(it.asDBObject())
						}
					}
				} else {
					Log.e(TAG_ERROR, response.message())
				}
			}

			override fun onFailure(call: Call<List<User>>, t: Throwable) {
				Log.e(TAG_ERROR, call.toString())
			}
		})
	}
}
