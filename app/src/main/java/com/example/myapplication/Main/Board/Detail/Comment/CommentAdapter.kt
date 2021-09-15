package com.example.myapplication.Main.Board.Detail.Comment

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.DTO.BoardDTO
import com.example.myapplication.R
import com.google.firebase.firestore.FirebaseFirestore

@SuppressLint("NotifyDataSetChanged")
class CommentAdapter(var boarduid : String): RecyclerView.Adapter<CommentAdapter.CommentHolder>(){
    private val commentdto  : MutableList<BoardDTO.Comment> = arrayListOf()
    init {
        FirebaseFirestore.getInstance().collection("Board").document(boarduid).collection("Comments").orderBy("timestamp")
            .addSnapshotListener { value, error ->
                commentdto.clear()
                if(value == null) return@addSnapshotListener

                for(snapshot in value.documents!! ){
                    commentdto.add(snapshot.toObject(BoardDTO.Comment::class.java)!!)
                }
                notifyDataSetChanged()
            }
    }

    class CommentHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        var profile : ImageView = itemView.findViewById(R.id.commet_profile)
        var nickname : TextView = itemView.findViewById(R.id.commet_nickname)
        var comment : TextView = itemView.findViewById(R.id.comment_context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentAdapter.CommentHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_comment,parent,false)
        return CommentHolder(itemView)
    }

    override fun onBindViewHolder(holder: CommentHolder, position: Int) {
        var data = commentdto[position]
        holder.nickname.text = data.userNickname
        holder.comment.text = data.comment
        Glide.with(holder.itemView.context).load(data.userprofile).into(holder.profile)
    }
    override fun getItemCount() = commentdto.size



}



