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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.jotagalilea.posts.R
import com.jotagalilea.posts.model.Post
import com.jotagalilea.posts.model.User
import com.jotagalilea.posts.view.activities.MainActivity
import com.jotagalilea.posts.view.adapters.PostsRecyclerAdapter
import com.jotagalilea.posts.viewmodel.PostsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Fragmento de la pantalla principal. Contiene la lista de post.
 */
class MainFragment: Fragment(), PostsRecyclerAdapter.OnItemClickListener {

	private lateinit var viewModel: PostsViewModel
	private lateinit var recyclerView: RecyclerView
	private lateinit var recyclerAdapter: PostsRecyclerAdapter
	private lateinit var recyclerLayoutManager: RecyclerView.LayoutManager
	private lateinit var errorImage: ImageView
	private lateinit var errorMsg: TextView
	private lateinit var loader: ProgressBar
	private lateinit var swipeRefreshLayout: SwipeRefreshLayout

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
							  savedInstanceState: Bundle?): View {
		return inflater.inflate(R.layout.fragment_main, container, false)
	}


	override fun onResume() {
		super.onResume()
		viewModel = (activity as MainActivity).getViewModel()
		if (viewModel.getPostsMap().value?.isEmpty()!!) {
			showLoader()
			setupObservers()
			viewModel.findPosts(true)
		}
		else {
			hideLoader()
			val	postsList: MutableList<Post> = viewModel.getPostsMap().value!!.values.toMutableList()
			val users: MutableMap<Int, User> = viewModel.getUsersMap().value!!
			recyclerAdapter.setItems(postsList, users)
		}
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
		recyclerAdapter = PostsRecyclerAdapter(this)
		recyclerView = view.findViewById<RecyclerView>(R.id.posts_recycler)!!.apply {
			setHasFixedSize(true)
			layoutManager = recyclerLayoutManager
			adapter = recyclerAdapter
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
		viewModel.getPostsMap().observe(viewLifecycleOwner,
			Observer<MutableMap<Int, Post>> { posts ->
				if (!posts.isNullOrEmpty()) {
					viewModel.findUsersOfPosts(posts)
				}
				else {
					if (posts == null)
						showErrorMsg()
				}
			}
		)

		// Usuarios:
		viewModel.getUsersMap().observe(viewLifecycleOwner,
			Observer<MutableMap<Int, User>> {users ->
				if (!users.isNullOrEmpty()) {
					hideLoader()
					viewModel.saveUsersAndPostsInDB()

					val postsList = viewModel.getPostsMap().value?.values?.toMutableList()
					postsList?.let {
						hideErrorMsg()
						CoroutineScope(Dispatchers.Main).launch {
							recyclerAdapter.setItems(postsList, users)
						}
					}
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


}