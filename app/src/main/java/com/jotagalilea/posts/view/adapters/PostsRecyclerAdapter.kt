package com.jotagalilea.posts.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.jotagalilea.posts.R
import com.jotagalilea.posts.model.domainmodel.Post
import com.jotagalilea.posts.model.domainmodel.User


/**
 * Adaptador para un recycler de posts.
 */
class PostsRecyclerAdapter: RecyclerView.Adapter<PostsRecyclerAdapter.PostsRowViewHolder>() {

	//private var postsList: MutableLiveData<MutableList<Post>> = MutableLiveData(mutableListOf())
	private lateinit var postsList: MutableList<Post>
	private lateinit var usersMap: MutableMap<Int, User>
	private lateinit var onItemClickListener: OnItemClickListener


	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsRowViewHolder {
		val container = LayoutInflater.from(parent.context)
			.inflate(R.layout.item_post, parent, false) as ConstraintLayout
		return PostsRowViewHolder(container, onItemClickListener)

	}


	override fun onBindViewHolder(holder: PostsRowViewHolder, position: Int) {
		val item = postsList.get(position)
		holder.user.text = usersMap[item.userId]?.name
		holder.title.text = item.title
	}


	/**
	 * Actualiza el recycler con nuevos elementos.
	 * @param newItems Nuevos posts para agregar.
	 * @param users Usuarios que crearon los posts.
	 */
	fun setItems(newPosts: List<Post>, users: Map<Int, User>){
		usersMap.putAll(users)
		postsList.addAll(newPosts)
		notifyDataSetChanged()
	}


	fun setOnItemClickListener(listener: OnItemClickListener){
		onItemClickListener = listener
	}


	/**
	 * Limpia el recycler.
	 */
	fun clearItems(){
		postsList.clear()
		notifyDataSetChanged()
	}


	override fun getItemCount(): Int {
		return postsList.size
	}


	/**
	 * Interfaz para acoplar distintos onClickListener al viewHolder del recycler.
	 */
	interface OnItemClickListener {
		fun onItemClick(post: Post, userName: String)
	}


	//------------------------- ViewHolder -----------------------------//
	/**
	 * ViewHolder para mostrar el título y el nombre del usuario de cada post.
	 */
	inner class PostsRowViewHolder(
		itemView: View,
		private var onItemClickListener: OnItemClickListener
	) : RecyclerView.ViewHolder(itemView), View.OnClickListener  {

		var title: TextView = itemView.findViewById(R.id.li_post_title)
		var user: TextView = itemView.findViewById(R.id.li_post_user)


		/**
		 * A la vista que contiene el view holder se le asigna el evento de click del viewHolder.
		 * Es útil si este viewHolder se usa en más de una vista, ya que permite que cada una tenga
		 * distinta implementación del onClick, la cual se debería hacer en la misma vista.
		 */
		init {
			itemView.setOnClickListener(this)
		}

		/**
		 * Llama al listener de la vista en la que está implementado.
		 */
		override fun onClick(v: View?) {
			val post: Post = postsList[adapterPosition]
			usersMap[post.userId]?.let{
				onItemClickListener.onItemClick(post, it.name)
			}
		}
	}
}