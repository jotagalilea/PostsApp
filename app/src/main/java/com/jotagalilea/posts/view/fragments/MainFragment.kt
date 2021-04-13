package com.jotagalilea.posts.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.jotagalilea.posts.R
import com.jotagalilea.posts.common.errorhandling.AppError
import com.jotagalilea.posts.common.errorhandling.ErrorBundle
import com.jotagalilea.posts.common.model.ResourceState
import com.jotagalilea.posts.common.model.ResourceState.*
import com.jotagalilea.posts.model.domainmodel.Post
import com.jotagalilea.posts.model.domainmodel.User
import com.jotagalilea.posts.view.activities.MainActivity
import com.jotagalilea.posts.view.adapters.PostsRecyclerAdapter
import com.jotagalilea.posts.viewmodel.PostsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


/**
 * Fragmento de la pantalla principal. Contiene la lista de post.
 */
class MainFragment: Fragment(), PostsRecyclerAdapter.OnItemClickListener {

	private var binding: MainFragmentBinding? = null

	private val viewModel: PostsViewModel by sharedViewModel()
	private lateinit var recyclerView: RecyclerView
	private lateinit var recyclerAdapter: PostsRecyclerAdapter
	private lateinit var recyclerLayoutManager: RecyclerView.LayoutManager
	private lateinit var errorImage: ImageView
	private lateinit var errorMsg: TextView
	private lateinit var loader: ProgressBar
	private lateinit var swipeRefreshLayout: SwipeRefreshLayout

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		return inflater.inflate(R.layout.fragment_main, container, false)
	}


	//TODO: Me falta build con viewBinding.
	override fun onResume() {
		super.onResume()
		showLoader()
		viewModel.findPosts(false)
		//viewModel = (activity as MainActivity).getViewModel()
		/*if (viewModel.getPostsMap().value?.isEmpty()!!) {
			showLoader()
			setupObservers()
			viewModel.findPosts(true)
		}
		else {
			hideLoader()
			val	postsList: MutableList<Post> = viewModel.getPostsMap().value!!.values.toMutableList()
			val users: MutableMap<Int, User> = viewModel.getUsersMap().value!!
			recyclerAdapter.setItems(postsList, users)
		}*/
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		setupView(view)
		super.onViewCreated(view, savedInstanceState)
	}


	/**
	 * Configuración de la vista y el listener del scroll.
	 * @param view Vista pasada en el onViewCreated.
	 */
	private fun setupView(view: View){
		recyclerLayoutManager = LinearLayoutManager(activity)
		recyclerAdapter = PostsRecyclerAdapter()
		recyclerAdapter.setOnItemClickListener(this)
		recyclerView = view.findViewById<RecyclerView>(R.id.posts_recycler)!!.apply {
			setHasFixedSize(true)
			layoutManager = recyclerLayoutManager
			adapter = recyclerAdapter
			val dividerItemDecoration = DividerItemDecoration(
				context,
				DividerItemDecoration.VERTICAL
			)
			addItemDecoration(dividerItemDecoration)
		}
		errorImage = view.findViewById(R.id.main_error_image)
		errorMsg = view.findViewById(R.id.main_error_text)
		loader = view.findViewById(R.id.loader)
		swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout)

		swipeRefreshLayout.setOnRefreshListener {
			viewModel.findPosts(true)
			swipeRefreshLayout.isRefreshing = false
		}
	}


	/**
	 * Configura el observador para el modelo de la lista. Permite mostrar un mensaje de error
	 * en caso de que haya un problema en las peticiones.
	 */
	private fun setupObservers(){
		// Posts:
		viewModel.getPostsLiveData().observe(viewLifecycleOwner,
			Observer<ResourceState<MutableMap<Int, Post>>> { posts ->
				if (posts != null)
					handlePostsDataState(posts)
			}
		)

		// Usuarios:
		viewModel.getUsersLiveData().observe(viewLifecycleOwner,
			Observer<ResourceState<MutableMap<Int, User>>> { users ->
				if (users != null) {
					handleUsersDataState(users)
					/*hideLoader()
					viewModel.saveUsersAndPostsInDB()
					val postsList = viewModel.getPostsMap().value?.values?.toMutableList()
					postsList?.let {
						hideErrorMsg()
						CoroutineScope(Dispatchers.Main).launch {
							recyclerAdapter.setItems(postsList, users)
						}
					}*/
				}
			}
		)
	}


	/**
	 * Muestra el mensaje de error por defecto.
	 */
	private fun showErrorMsg(){
		errorImage.visibility = View.VISIBLE
		errorMsg.visibility = View.VISIBLE
	}

	/**
	 * Oculta el mensaje de error.
	 */
	private fun hideErrorMsg(){
		errorImage.visibility = View.INVISIBLE
		errorMsg.visibility = View.INVISIBLE
	}


	/**
	 * Muestra un progressBar.
	 */
	private fun showLoader(){
		loader.visibility = View.VISIBLE
	}

	/**
	 * Oculta un progressBar.
	 */
	private fun hideLoader(){
		loader.visibility = View.INVISIBLE
	}

	/**
	 * Implementación para el ViewHolder de un elemento de la lista de la navegación a la pantalla
	 * de detalle del post seleccionado.
	 * @param post Post seleccionado por el usuario.
	 */
	override fun onItemClick(post: Post, userName: String) {
		(activity as MainActivity).navigateToDetail(post, userName)
	}


	//region Handling state
	private fun handlePostsDataState(postsState: ResourceState<MutableMap<Int, Post>>) {
		when (postsState) {
			is Loading -> setupScreenForLoadingState()
			is Success -> viewModel.findUsersOfPosts(postsState.data)
			is Error -> setupScreenForError(postsState.errorBundle)
		}
	}

	private fun handleUsersDataState(usersState: ResourceState<MutableMap<Int, User>>) {
		when (usersState) {
			is Loading -> setupScreenForLoadingState()
			is Success -> {
				setupScreenForSuccess()
				updateView()
			}
			is Error -> setupScreenForError(usersState.errorBundle)
		}
	}

	private fun setupScreenForLoadingState() {
		hideErrorMsg()
		showLoader()
	}

	private fun setupScreenForSuccess() {
		hideLoader()
		hideErrorMsg()
	}

	private fun updateView() {
		val posts = viewModel.getPostsMap().map{it.value}
		val users = viewModel.getUsersMap()
		recyclerAdapter.setItems(posts, users)
		//recyclerAdapter.notifyDataSetChanged()
	}

	private fun setupScreenForError(errorBundle: ErrorBundle) {
		hideLoader()
		if (errorBundle.appError == AppError.NO_INTERNET || errorBundle.appError == AppError.TIMEOUT) {
			// Example of using a custom error view as part of the fragment view
			//showErrorView(errorBundle)
			showErrorMsg()
		} else {
			// Example of using an error fragment dialog
			//showErrorDialog(errorBundle)
			showErrorMsg()
		}
	}
	//endregion


}