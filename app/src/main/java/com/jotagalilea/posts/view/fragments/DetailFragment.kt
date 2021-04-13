package com.jotagalilea.posts.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jotagalilea.posts.R
import com.jotagalilea.posts.common.model.ResourceState
import com.jotagalilea.posts.common.model.ResourceState.*
import com.jotagalilea.posts.model.domainmodel.Comment
import com.jotagalilea.posts.model.domainmodel.Post
import com.jotagalilea.posts.view.adapters.CommentsRecyclerAdapter
import com.jotagalilea.posts.viewmodel.PostsViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


/**
 * Fragmento que muestra el contenido de un post y los comentarios hechos por los usuarios.
 */
class DetailFragment: Fragment() {

	private val viewModel: PostsViewModel by sharedViewModel()
	private lateinit var post: Post
	private lateinit var userName: String
	private lateinit var titleText: TextView
	private lateinit var bodyText: TextView
	private lateinit var userText: TextView
	private lateinit var recycler: RecyclerView
	private lateinit var recyclerAdapter: CommentsRecyclerAdapter
	private lateinit var recyclerLayoutManager: RecyclerView.LayoutManager


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		post = arguments?.get("Post") as Post
		userName = arguments?.get("UserName") as String
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		return inflater.inflate(R.layout.fragment_detail, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		//viewModel = (activity as MainActivity).getViewModel()
		setupView(view)
		setupObservers()
		viewModel.findCommentsOfPost(post.id)
	}


	/**
	 * Configuración del observador de comentarios para llenar el recyclerView.
	 */
	private fun setupObservers(){
		viewModel.getCommentsLiveData().observe(viewLifecycleOwner,
			Observer<ResourceState<List<Comment>>> { comments ->
				if (comments != null){
					handleCommentsDataState(comments)
					//recyclerAdapter.setItems(comments)
				}
			}
		)
	}


	/**
	 * Configura la vista con la información recibida.
	 * @param view Vista a configurar.
	 */
	private fun setupView(view: View){
		titleText = view.findViewById(R.id.detail_title)
		bodyText = view.findViewById(R.id.detail_body)
		userText = view.findViewById(R.id.detail_user)
		recyclerLayoutManager = LinearLayoutManager(activity)
		recyclerAdapter = CommentsRecyclerAdapter()
		recycler = view.findViewById<RecyclerView>(R.id.detail_comments_recycler)!!.apply {
			setHasFixedSize(true)
			layoutManager = recyclerLayoutManager
			adapter = recyclerAdapter
			val dividerItemDecoration = DividerItemDecoration(
				context,
				DividerItemDecoration.VERTICAL
			)
			addItemDecoration(dividerItemDecoration)
		}
		titleText.text = post.title
		bodyText.text = post.body
		userText.text = userName
	}


	private fun handleCommentsDataState(commentsState: ResourceState<List<Comment>>) {
		when (commentsState) {
			is Loading -> {}//setupScreenForLoadingState()
			is Success -> setupScreenForSuccess(commentsState.data)
			is Error -> {}//setupScreenForError(postsState.errorBundle)
		}
	}


	private fun setupScreenForSuccess(commList: List<Comment>){
		//hideLoaders y demás...
		//val commentsVM = viewModel.getCommentsList()
		//TODO: Ver que coinciden los comments pillados aquí con los del viewmodel.
		recyclerAdapter.setItems(commList)
	}

}