package com.jotagalilea.posts.view.fragments

import android.os.Bundle
import android.util.Log
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
import com.jotagalilea.posts.db.PostsDao
import com.jotagalilea.posts.db.getDatabase
import com.jotagalilea.posts.model.Post
import com.jotagalilea.posts.model.User
import com.jotagalilea.posts.model.asDBObjects
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
	private var dao: PostsDao? = null

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
							  savedInstanceState: Bundle?): View {
		return inflater.inflate(R.layout.fragment_main, container, false)
	}


	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		viewModel = (activity as MainActivity).getViewModel()
		dao = context?.let { getDatabase(it).getDao() }
		setupView(view)
		setupObservers()
		showLoader()
		viewModel.findPosts(true)
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
		/*
		 * Con este listener se detecta cuando el usuario llega al final del recycler
		 * para hacer una nueva petición.
		 */
		recyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener() {
			override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
				super.onScrollStateChanged(recyclerView, newState)
				if (!recyclerView.canScrollVertically(1) && (newState == RecyclerView.SCROLL_STATE_IDLE)){
					try{
						showLoader()
						viewModel.findPosts(false)
					}
					catch (e: Exception){
						Log.e("ERROR LOADING", e.printStackTrace().toString())
						hideLoader()
						showErrorMsg()
					}
				}
			}
		})

		/*
		 * Con un swipeRefreshLayout puedo recargar la lista deslizando hacia abajo desde
		 * el principio de la misma.
		 */
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
				} else {
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

					CoroutineScope(Dispatchers.Main).launch {
						val postsList = viewModel.getPostsMap().value?.values?.toMutableList()
						postsList?.let {
							//TODO: Comentar que uso Dispatchers.Main y no Dispatchers.IO porque necesito
							//		el hilo de IU para el notify del setItems.
							//	¡¡Joder, pues entonces separo en 2 corrutinas con dispatchers distintos!!
							dao?.insertAllPosts(postsList.asDBObjects())
							recyclerAdapter.setItems(postsList, users)
							hideErrorMsg()
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