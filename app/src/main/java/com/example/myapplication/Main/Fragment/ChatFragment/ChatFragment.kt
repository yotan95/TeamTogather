package com.example.myapplication.Main.Fragment.ChatFragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.Main.Fragment.BoardFragment.BoardFragment
import com.example.myapplication.R
import kotlinx.android.synthetic.main.frag_chat.*

class ChatFragment: Fragment(){
    companion object {
        const val BoardTAG: String = "BoardList"
        fun newInstance(): ChatFragment {
            return ChatFragment()
        }
    }
    private lateinit var adapter: ChatListAdapter
    private val viewModel by lazy{
        ViewModelProvider(this).get(ChatListViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.frag_chat,container,false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chat_list_recyclerview.apply {
            val chatlistAdapter : ChatListAdapter
            layoutManager = LinearLayoutManager(requireContext())
            chatlistAdapter = ChatListAdapter()
            adapter = chatlistAdapter
            observeData()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeData(){
        viewModel.getChatListData().observe(viewLifecycleOwner, Observer {
            adapter.setDataChatAapter(it)
            adapter.notifyDataSetChanged()
        })
    }
}


