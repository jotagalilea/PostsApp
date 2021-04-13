package com.jotagalilea.posts.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jotagalilea.posts.common.errorhandling.AppAction
import com.jotagalilea.posts.common.errorhandling.ErrorBundleBuilder
import com.jotagalilea.posts.common.model.ResourceState
import com.jotagalilea.posts.common.model.ResourceState.*
import com.jotagalilea.posts.common.viewmodel.CommonEventsViewModel
import com.jotagalilea.posts.datasources.cache.dao.CachedPostsDao
import com.jotagalilea.posts.datasources.remote.PostsService
import com.jotagalilea.posts.datasources.cache.db.*
import com.jotagalilea.posts.domain.post.interactor.GetCommentsFromPost
import com.jotagalilea.posts.domain.post.interactor.GetPostsFromUser
import com.jotagalilea.posts.domain.post.interactor.GetPostsList
import com.jotagalilea.posts.domain.post.interactor.GetUserWithId
import com.jotagalilea.posts.model.*
import com.jotagalilea.posts.model.domainmodel.Comment
import com.jotagalilea.posts.model.domainmodel.Post
import com.jotagalilea.posts.model.domainmodel.User
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Viewmodel que tiene las referencias a objetos del modelo, acceso a base de datos y api web.
 */
class PostsViewModel(
	application: Application,
	private val getPostsListUseCase: GetPostsList,
	private val getPostsFromUserUseCase: GetPostsFromUser,
	private val getUserWithIdUseCase: GetUserWithId,
	private val getCommentsFromPostUseCase: GetCommentsFromPost,
	//private var dao: CachedPostsDao,
	//private val api: PostsService,
	private val errorBundleBuilder: ErrorBundleBuilder
//) : AndroidViewModel(application) {
) : CommonEventsViewModel() {

	// Viejo:
	private val TAG = "Obteniendo posts..."
	private val TAG_ERROR = "Error obteniendo datos"
	private var main_postsMap: MutableLiveData<MutableMap<Int, Post>> = MutableLiveData(mutableMapOf())
	private var main_usersMap: MutableLiveData<MutableMap<Int, User>> = MutableLiveData(mutableMapOf())
	private var detail_comments: MutableLiveData<MutableList<Comment>> = MutableLiveData(mutableListOf())
	private var usersIDsSearched: MutableList<Int> = mutableListOf()
	private var usersFound: Int = 0

	// Nuevo:
	// TODO: Estudiar para qué sirve el disposable:
	private var disposable: Disposable? = null
	private var postsMap : MutableMap<Int, Post> = mutableMapOf()
	private var usersMap : MutableMap<Int, User> = mutableMapOf()
	private var commentsList : MutableList<Comment> = mutableListOf()
	private val postsLiveData : MutableLiveData<ResourceState<MutableMap<Int, Post>>> = MutableLiveData()
	private val usersLiveData : MutableLiveData<ResourceState<MutableMap<Int, User>>> = MutableLiveData()
	private val commentsLiveData : MutableLiveData<ResourceState<List<Comment>>> = MutableLiveData()


	/**
	 * Carga de ítems en el modelo desde BD o servicio web, según se necesite.
	 * @param reload Indica si hay que volver a cargar datos.
	 */
	fun findPosts(reload: Boolean){
		/*viewModelScope.launch {
			if (reload) {
				main_postsMap.value?.clear()
				main_usersMap.value?.clear()
				//usersFound = 0
				usersIDsSearched = mutableListOf()
			}
			//TODO: Al llamar al execute del SingleUseCase más abajo ya se está buscando en la BD.
			val dbItems: Map<Int, Post> = dao.getAllPosts().asDomainModelMap()
			if (!dbItems.isNullOrEmpty()) {
				main_postsMap.addNewItems(dbItems)
			}
			else {
				requestPostsList()
			}
		}*/
		requestPostsList()
	}


	/**
	 * Busca los comentarios referentes a un post en la base de datos o lanzando una petición
	 * al servicio web.
	 */
	fun findCommentsOfPost(postId: Int) {
		/*viewModelScope.launch {
			detail_comments.value?.clear()
			val dbComments = dao.getCommentsFromPost(postId).asDomainModel()
			if (!dbComments.isNullOrEmpty()){
				detail_comments.postValue(dbComments as MutableList<Comment>?)
			}
			else {
				requestCommentsOfPost(postId)
			}
		}*/
		requestCommentsOfPost(postId)
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
			requestUserWithID(userId)
			/*viewModelScope.launch {
				val userDB = dao.getUserWithID(userId)
				if (userDB != null) {
					val user = userDB.asDomainModel()
					main_usersMap.value?.put(user.id, user)
					++usersFound
				} else {
					requestUserWithID(userId)
				}
			}*/
		}
	}



	/*fun saveUsersAndPostsInDB() = CoroutineScope(Dispatchers.IO).launch {
		dao.insertAllUsers(main_usersMap.value?.values?.toList()?.asDBObjects()!!)
		dao.insertAllPosts(main_postsMap.value?.values?.toList()?.asDBObjects()!!)
	}*/



	/**
	 * Realiza la petición al servicio web para obtener una serie de posts.
	 */
	private fun requestPostsList(){
		//TODO: Lo que tengo que conseguir es llamar al execute() de cada UseCase,
		//		en este caso sería el de getPostsListUseCase.
		//getPostsListUseCase.execute()...
		//TODO: ¿Tiene que ser un disposable?
		//TODO: Comprobar que el execute funciona tanto para obtener datos de la BD,
		// 		como para hacer la petición al servicio:
		disposable = getPostsListUseCase.execute()
			.subscribeWith(
				object : SingleRemoteInterceptor<List<Post>>(commonLiveEvent) {
					override fun onSuccess(t: List<Post>) {
						//TODO: ¿Para qué sirve usar this@PostsViewModel? ¿Es thread safe o algo así?
						//TODO: Preferiría gestionarlo con put(), remove(), y clear()...
						this@PostsViewModel.postsMap = t.map { it.userId to it }.toMap() as MutableMap<Int, Post>
						postsLiveData.value = Success(postsMap)
					}

					override fun onRegularError(e: Throwable) {
						postsLiveData.value =
							Error(errorBundleBuilder.build(e, AppAction.GET_POSTS))
					}
				}
			)

		//////////////////////////////////////////////////////////////////////
		/*val call: Call<List<Post>> = api.getPostsList()
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
		})*/
	}


	/**
	 * Lanza petición web para obtener los datos de un usuario.
	 * @param id ID del usuario.
	 */
	private fun requestUserWithID(id: Int){
		getUserWithIdUseCase.setId(id)
		disposable = getUserWithIdUseCase.execute()
			.subscribeWith(
				object : SingleRemoteInterceptor<User>(commonLiveEvent) {
					override fun onSuccess(t: User) {
						this@PostsViewModel.usersMap[t.id] = t
						usersLiveData.value = Success(usersMap)
					}

					override fun onRegularError(e: Throwable) {
						usersLiveData.value = Error(errorBundleBuilder.build(e, AppAction.GET_USER))
					}
				}
			)
		/*val call: Call<List<User>> = api.getUserWithId(id)
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
		 */
	}


	/**
	 * Lanza una petición para obtener los comentarios de un post. Al recibirlos se insertan
	 * en la base de datos.
	 * @param postId ID del post al que pertenecen.
	 */
	private fun requestCommentsOfPost(postId: Int){
		getCommentsFromPostUseCase.setId(postId)
		disposable = getCommentsFromPostUseCase.execute()
			.subscribeWith(
				object : SingleRemoteInterceptor<List<Comment>>(commonLiveEvent) {
					override fun onSuccess(t: List<Comment>) {
						this@PostsViewModel.commentsList = t as MutableList
						commentsLiveData.value = Success(this@PostsViewModel.commentsList)
					}

					override fun onRegularError(e: Throwable) {
						commentsLiveData.value =
							Error(errorBundleBuilder.build(e, AppAction.GET_COMMENTS))
					}
				}
			)
		/*val call: Call<List<Comment>> = api.getCommentsOfPost(postId)
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
		 */
	}


	fun getPostsMap(): Map<Int, Post>{
		return postsMap
	}
	fun getUsersMap(): Map<Int, User>{
		return usersMap
	}
	fun getCommentsList(): List<Comment>{
		return commentsList
	}
	fun getPostsLiveData(): MutableLiveData<ResourceState<MutableMap<Int, Post>>>{
		return postsLiveData
	}
	fun getUsersLiveData(): MutableLiveData<ResourceState<MutableMap<Int, User>>>{
		return usersLiveData
	}
	fun getCommentsLiveData(): MutableLiveData<ResourceState<List<Comment>>>{
		return commentsLiveData
	}


	/*/**
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
	}*/




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
