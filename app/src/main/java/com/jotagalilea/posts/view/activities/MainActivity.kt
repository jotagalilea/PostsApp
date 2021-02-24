package com.jotagalilea.posts.view.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.jotagalilea.posts.R
import com.jotagalilea.posts.model.Post
import com.jotagalilea.posts.viewmodel.PostsViewModel


/**
 * Actividad principal sobre la que se usa el componente de navegación.
 */
class MainActivity : AppCompatActivity() {

	private lateinit var navController: NavController
	private lateinit var viewModel: PostsViewModel


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		viewModel = ViewModelProvider(this).get(PostsViewModel::class.java)
		navController = findNavController(R.id.nav_host)
	}


	/**
	 * Obtención del ViewModel de MainActivity.
	 */
	fun getViewModel(): PostsViewModel{
		return viewModel
	}


	/**
	 * Navegación a DetailFragment. Se pasa el post y el nombre del usuario.
	 */
	fun navigateToDetail(post: Post, userName: String){
		val bundle = bundleOf("Post" to post, "UserName" to userName)
		navController.navigate(R.id.action_main_to_detail, bundle)
	}

}