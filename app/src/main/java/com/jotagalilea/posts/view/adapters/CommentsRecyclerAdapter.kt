package com.jotagalilea.posts.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.jotagalilea.posts.R
import com.jotagalilea.posts.model.Comment
import com.jotagalilea.posts.model.Post
import com.jotagalilea.posts.model.User

class CommentsRecyclerAdapter : RecyclerView.Adapter<CommentsRecyclerAdapter.CommentsRowViewHolder>() {

	private var commentsList: MutableLiveData<MutableList<Comment>> = MutableLiveData(mutableListOf())


	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsRowViewHolder {
		val container = LayoutInflater.from(parent.context)
			.inflate(R.layout.item_comment, parent, false) as ConstraintLayout
		return CommentsRowViewHolder(container)
	}


	override fun onBindViewHolder(holder: CommentsRowViewHolder, position: Int) {
		val item = commentsList.value?.get(position)
		holder.name.text = item?.name
		holder.body.text = item?.body
	}


	/**
	 * Actualiza el recycler con nuevos elementos.
	 * @param newItems Nuevos comentarios para agregar.
	 */
	fun setItems(newItems: MutableList<Comment>){
		commentsList.postValue(newItems)
		notifyDataSetChanged()
	}


	/**
	 * Limpia el recycler.
	 */
	fun clearItems(){
		commentsList.value?.clear()
		notifyDataSetChanged()
	}


	override fun getItemCount(): Int {
		return commentsList.value?.size ?: 0
	}



	//------------------------- ViewHolder -----------------------------//
	/**
	 * ViewHolder para mostrar el nombre y la imagen de cada comentario.
	 */
	inner class CommentsRowViewHolder(
		itemView: View
	) : RecyclerView.ViewHolder(itemView)  {

		var name: TextView = itemView.findViewById(R.id.li_comment_name)
		var body: TextView = itemView.findViewById(R.id.li_comment_body)
		//private var loader: ProgressBar = itemView.findViewById(R.id.img_loader)


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