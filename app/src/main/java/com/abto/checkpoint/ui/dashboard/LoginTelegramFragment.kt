package com.abto.checkpoint.ui.dashboard

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.abto.checkpoint.data.AuthState
import com.abto.checkpoint.databinding.FragmentLoginTelegramBinding
import com.abto.checkpoint.databinding.ItemChatBinding
import com.abto.checkpoint.databinding.ItemMessageBinding
import org.drinkless.td.libcore.telegram.TdApi

class LoginTelegramFragment : Fragment() {
    private val viewModel: LoginTelegramViewModel by viewModels()
    private lateinit var binding: FragmentLoginTelegramBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginTelegramBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            viewModel.authState.observe(viewLifecycleOwner) { state ->
                clPhoneNumber.isVisible = state == AuthState.EnterPhone
                clOtp.isVisible = state == AuthState.EnterCode
                rvChats.isVisible = state == AuthState.LoggedIn
                if (state == AuthState.LoggedIn) {
                    viewModel.getChats()
                }
            }
            viewModel.getChats()
            viewModel.chatsData.observe(viewLifecycleOwner) {
                rvChats.isVisible = true
                rvMessages.isVisible = false
                clPhoneNumber.isVisible = false
                clOtp.isVisible = false
                rvChats.adapter = ChatAdapter(it) {chat ->
                    viewModel.getMessages(chat.id)
                }
            }
            viewModel.messagesData.observe(viewLifecycleOwner) {
                rvChats.isVisible = false
                rvMessages.isVisible = true
                rvMessages.adapter = MessagesAdapter(it) { message ->
                    Toast.makeText(requireContext(), message.content.toString(), Toast.LENGTH_SHORT).show()
                    message.content.let { content ->
                        if (content is TdApi.MessageDocument) {
                            viewModel.downloadFile(content.document.document.id)
                        }
                    }
                }
            }
            viewModel.fileDownloaded.observe(viewLifecycleOwner) { filePath ->
                Toast.makeText(requireContext(), "File downloaded: $filePath", Toast.LENGTH_SHORT).show()
                //open pdf file
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(Uri.parse(filePath), "application/pdf")
                intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
                startActivity(intent)
            }
            btnSendOtp.setOnClickListener {
                viewModel.sendPhone(etPhoneNumber.text.toString())
            }
            btnVerifyOtp.setOnClickListener {
                viewModel.sendCode(etOtp.text.toString())
            }
        }
    }
}

class ChatAdapter(private val data: List<TdApi.Chat>, private val onItemClick: (TdApi.Chat) -> Unit) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ChatViewHolder(private val binding: ItemChatBinding, private val onItemClick: (TdApi.Chat) -> Unit) : RecyclerView.ViewHolder(binding.root) {
        fun bind(chat: TdApi.Chat) {
            binding.tvTitle.text = chat.title
            binding.root.setOnClickListener { onItemClick(chat) }
        }
    }
}

class MessagesAdapter(private val data: List<TdApi.Message>, private val onItemClick: (TdApi.Message) -> Unit) : RecyclerView.Adapter<MessagesAdapter.MessageViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding = ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MessageViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MessageViewHolder(private val binding: ItemMessageBinding, private val onItemClick: (TdApi.Message) -> Unit) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: TdApi.Message) {
            binding.tvName.text = message.authorSignature
            message.content.let { content ->
                binding.tvMessage.text = when (content) {
                    is TdApi.MessageText -> content.text.text
                    is TdApi.MessageDocument -> content.document.fileName
                    else -> "Unsupported message type"
                }
            }
            binding.tvDate.text = message.date.toString()
            binding.root.setOnClickListener { onItemClick(message) }
        }
    }
}