package com.jotagalilea.posts.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jotagalilea.posts.apiclient.Endpoints
import com.jotagalilea.posts.db.*
import com.jotagalilea.posts.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

	private var main_postsMap: MutableLiveData<MutableMap<Int, Post>> = MutableLiveData(mutableMapOf())
	private var main_usersMap: MutableLiveData<MutableMap<Int, User>> = MutableLiveData(mutableMapOf())
	private var detail_comments: MutableLiveData<MutableList<Comment>> = MutableLiveData(mutableListOf())
	private var usersIDsSearched: MutableList<Int> = mutableListOf()
	private var usersFound: Int = 0
	private var dao: PostsDao
	private val retrofit: Retrofit
	private val api: Endpoints


	init {
		dao = getDatabase(application.applicationContext).getDao()

		retrofit = Retrofit.Builder()
			.baseUrl(Endpoints.BASE_URL)
			.addConverterFactory(GsonConverterFactory.create())
			.build()

		api = retrofit.create(Endpoints::class.java)
	}


	/**
	 * Carga de ítems en el modelo desde BD o servicio web, según se necesite.
	 * @param reload Indica si hay que volver a cargar datos.
	 */
	fun findPosts(reload: Boolean){
		viewModelScope.launch {
			if (reload) {
				main_postsMap.value?.clear()
				main_usersMap.value?.clear()
				//usersFound = 0
				usersIDsSearched = mutableListOf()
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


	/**
	 * Busca los comentarios referentes a un post en la base de datos o lanzando una petición
	 * al servicio web.
	 */
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

	fun saveUsersAndPostsInDB() = CoroutineScope(Dispatchers.IO).launch {
		dao.insertAllUsers(main_usersMap.value?.values?.toList()?.asDBObjects()!!)
		dao.insertAllPosts(main_postsMap.value?.values?.toList()?.asDBObjects()!!)
	}

	/**
	 * Busca los usuarios autores de un conjunto de posts.
	 * @param postMap posts cuyos usuarios se van a buscar.
	 */
	fun findUsersOfPosts(postMap: Map<Int, Post>) = viewModelScope.launch{
		val it = postMap.iterator()
		while (it.hasNext()) {
			findUserWithId(it.next().value.userId)
		}

		CoroutineScope(Dispatchers.Default).launch {
			while (usersFound < usersIDsSearched.size){
				Log.d("Esperando suficientes usuarios... ",
					"Buscados: ${usersIDsSearched.size}. Encontrados: ${usersFound}")
			}
			main_usersMap.postValue(main_usersMap.value)
		}
	}


	/**
	 * Busca un usuario en BD o en servicio web.
	 * @param userId ID del usuario buscado.
	 */
	private fun findUserWithId(userId: Int){
		if (!usersIDsSearched.contains(userId)) {
			usersIDsSearched.add(userId)
			//CoroutineScope(Dispatchers.IO).launch {
			/**
			 * Corregido bug de carga de usuarios. No se estaban despachando suficientes workers
			 * para obtener todos los usuarios, lo que provocaba el cuelgue aleatorio en la
			 * primera pantalla.
			 */
			viewModelScope.launch {
				val userDB = dao.getUserWithID(userId)
				if (userDB != null) {
					val user = userDB.asDomainModel()
					main_usersMap.value?.put(user.id, user)
					++usersFound
				} else {
					requestUserWithID(userId)
				}
			}
		}
	}


	/**
	 * Realiza la petición al servicio web para obtener una serie de posts.
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


	/**
	 * Lanza petición web para obtener los datos de un usuario.
	 * @param id ID del usuario.
	 */
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
						++usersFound
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


	/**
	 * Lanza una petición para obtener los comentarios de un post. Al recibirlos se insertan
	 * en la base de datos.
	 * @param postId ID del post al que pertenecen.
	 */
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
				} else {
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
	 */
	fun getPostsMap(): MutableLiveData<MutableMap<Int, Post>> {
		return main_postsMap
	}

	/**
	 * Devuelve el map de usuarios.
	 */
	fun getUsersMap(): MutableLiveData<MutableMap<Int, User>> {
		return main_usersMap
	}

	/**
	 * Devuelve la lista con los últimos comentarios obtenidos.
	 */
	fun getCommentsList(): MutableLiveData<MutableList<Comment>>{
		return detail_comments
	}




	//------------------------- Extensiones -------------------------//

	/**
	 * Extensión de MutableLiveData<MutableMap<Int, Post>> para añadir elementos al postsMap.
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

}
