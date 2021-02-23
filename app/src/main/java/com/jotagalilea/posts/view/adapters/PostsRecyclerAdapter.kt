package com.jotagalilea.posts.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.jotagalilea.posts.R
import com.jotagalilea.posts.model.Post
import com.jotagalilea.posts.model.User


/**
 * Adaptador para un recycler de posts.
 */
class PostsRecyclerAdapter(private val onItemClickListener: OnItemClickListener)
	: RecyclerView.Adapter<PostsRecyclerAdapter.PostsRowViewHolder>() {

	private var postsList: MutableLiveData<MutableList<Post>> = MutableLiveData(mutableListOf())
	//TODO: Si me sirve comentar que esto es para sacar al usuario rápido con su ID como clave.
	private lateinit var usersMap: MutableMap<Int, User>

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsRowViewHolder {
		val container = LayoutInflater.from(parent.context)
			.inflate(R.layout.item_post, parent, false) as ConstraintLayout
		return PostsRowViewHolder(container, onItemClickListener)

	}


	override fun onBindViewHolder(holder: PostsRowViewHolder, position: Int) {
		val item = postsList.value?.get(position)
		holder.user.text = usersMap[item?.userId]?.name
		holder.title.text = item?.title
	}


	/**
	 * Actualiza el recycler con nuevos elementos.
	 * @param newItems Nuevos posts para agregar.
	 */
	fun setItems(newItems: MutableList<Post>, users: MutableMap<Int, User>){
		usersMap = users
		postsList.postValue(newItems)
		notifyDataSetChanged()
	}


	/**
	 * Limpia el recycler.
	 */
	fun clearItems(){
		postsList.value?.clear()
		notifyDataSetChanged()
	}


	override fun getItemCount(): Int {
		return postsList.value?.size ?: 0
	}


	/**
	 * Interfaz para hacer más fácil acoplar distintos onClickListener al viewHolder del recycler.
	 */
	interface OnItemClickListener {
		fun onItemClick(post: Post, userName: String)
	}


	//------------------------- ViewHolder -----------------------------//
	/**
	 * ViewHolder para mostrar el nombre y la imagen de cada post.
	 */
	inner class PostsRowViewHolder(
		itemView: View,
		private var onItemClickListener: OnItemClickListener
	) : RecyclerView.ViewHolder(itemView), View.OnClickListener  {

		var title: TextView = itemView.findViewById(R.id.li_post_title)
		var user: TextView = itemView.findViewById(R.id.li_post_user)
		//private var loader: ProgressBar = itemView.findViewById(R.id.img_loader)

		/**
		 * A la vista que contiene el view holder se le asigna el evento de click del view holder.
		 * Es útil si este viewHolder se usa en más de una vista, ya que permite que cada una tenga
		 * distinta implementación del onClick, la cual se debería hacer en la misma vista. En el
		 * caso particular de esta aplicación, como no se va a tener este view holder fuera de
		 * un recycler se puede tener como clase interna del adaptador, aunque está preparado para
		 * otro caso.
		 */
		init {
			itemView.setOnClickListener(this)
		}

		/**
		 * Llama al listener de la vista en la que está implementado.
		 */
		override fun onClick(v: View?) {
			val post: Post = postsList.value!![adapterPosition]
			usersMap[post.userId]?.let{
				onItemClickListener.onItemClick(post, it.name)
			}
		}

		/*
		/**
		 * Oculta un progressBar.
		 */
		fun hideLoader(){
			loader.visibility = View.GONE
		}

		/**
		 * Muestra un progressBar.
		 */
		fun showLoader(){
			loader.visibility = View.VISIBLE
		}
		 */
	}
}