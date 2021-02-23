package com.jotagalilea.posts.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jotagalilea.posts.viewmodel.PostsViewModel
import com.jotagalilea.posts.R
import com.jotagalilea.posts.model.Comment
import com.jotagalilea.posts.model.Post
import com.jotagalilea.posts.view.activities.MainActivity
import com.jotagalilea.posts.view.adapters.CommentsRecyclerAdapter


class DetailFragment: Fragment() {

	private lateinit var viewModel: PostsViewModel
	private lateinit var post: Post
	private lateinit var userName: String
	private lateinit var titleText: TextView
	private lateinit var bodyText: TextView
	private lateinit var userText: TextView
	private lateinit var recycler: RecyclerView
	private lateinit var recyclerAdapter: CommentsRecyclerAdapter
	private lateinit var recyclerLayoutManager: RecyclerView.LayoutManager
	//private var comments: LiveData<List<Comment>> = MutableLiveData(mutableListOf())
	//private var users: LiveData<Map<Int, User>> = MutableLiveData(mutableMapOf())
	//private var dao: PostsDao? = null



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
		viewModel = (activity as MainActivity).getViewModel()
		setupView(view)
		setupObservers()
		viewModel.findCommentsOfPost(post.id)
	}


	private fun setupObservers(){
		viewModel.getCommentsList().observe(viewLifecycleOwner,
			Observer<MutableList<Comment>> { comments ->
				if (!comments.isNullOrEmpty()){
					recyclerAdapter.setItems(comments)
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
		}
		titleText.text = post.title
		bodyText.text = post.body
		userText.text = userName
	}


}