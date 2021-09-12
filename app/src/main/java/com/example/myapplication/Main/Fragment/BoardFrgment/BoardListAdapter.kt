package com.example.myapplication.Main.Fragment.BoardFrgment

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.DTO.BoardDTO
import com.example.myapplication.Main.Board.Detail.BoardDetail
import com.example.myapplication.R

class BoardListAdapter(private val boarddtos: MutableList<BoardDTO>, private val contentsUid : ArrayList<String>) : RecyclerView.Adapter<BoardListAdapter.CTViewholder>(){
    class CTViewholder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val profile : ImageView = itemView.findViewById(R.id.boardlist_profile)
        val title : TextView = itemView.findViewById(R.id.boardlist_title)
        val Contents : TextView = itemView.findViewById(R.id.boarlist_content)
        val boarddate : TextView = itemView.findViewById(R.id.boardlist_date)
        val boardimage : ImageView = itemView.findViewById(R.id.boardlist_image)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CTViewholder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.board_list_item, parent, false)
        return CTViewholder(itemView)
    }

    override fun onBindViewHolder(holder: CTViewholder, position: Int) {
        val currentitem = boarddtos[position]
        val currentUid  = contentsUid[position]
        holder.title.text = currentitem.postTitle
        holder.Contents.text = currentitem.contents
        holder.boarddate.text = currentitem.Writed_date
        Glide.with(holder.itemView.context).load(currentitem.ProfileUrl).into(holder.profile)
        Glide.with(holder.itemView.context).load(currentitem.imageUrlWrite).into(holder.boardimage)

        holder.itemView.setOnClickListener{
            val intent = Intent(holder.itemView.context, BoardDetail::class.java)
            intent.putExtra("contentsUid",currentUid)
            ContextCompat.startActivity(holder.itemView.context, intent, null)

        }
    }


    override fun getItemCount() = boarddtos.size

    fun addItems(items: List<BoardDTO>) {
        this.boarddtos.addAll(items)
        notifyDataSetChanged()
    }

}

