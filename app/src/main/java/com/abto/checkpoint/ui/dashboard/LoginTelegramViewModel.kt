package com.abto.checkpoint.ui.dashboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.abto.checkpoint.data.TelegramRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import org.drinkless.td.libcore.telegram.TdApi

class LoginTelegramViewModel: ViewModel() {

    val error = MutableLiveData<String>()

    val authState = TelegramRepository.authFlow.asLiveData()
    val chatsData = MutableLiveData<List<TdApi.Chat>>()
    val messagesData = MutableLiveData<List<TdApi.Message>>()
    val fileDownloaded = MutableLiveData<String>()
    var currentChatId: Long? = null

    private val scope = viewModelScope + CoroutineExceptionHandler { _, throwable ->
        error.postValue(throwable.message)
    }

    init {
        TelegramRepository.api.attachClient()
    }

    fun sendPhone(phone: String) = scope.launch {
        TelegramRepository.sendPhone(phone)
    }

    fun sendCode(code: String) = scope.launch {
        TelegramRepository.sendCode(code)
    }

    fun sendPassword(password: String) = scope.launch {
        TelegramRepository.sendPassword(password)
    }

    fun sendMessage(text: String) = scope.launch {
        currentChatId?.let {
            TelegramRepository.sendMessage(it, text)
        }
    }

    fun getChats() = scope.launch {
        currentChatId = null
        TelegramRepository.getChats().let {
            chatsData.postValue(it)
        }
    }

    fun getMessages(chatId: Long) = scope.launch {
        currentChatId = chatId
        TelegramRepository.getChatHistory(chatId).let {
            messagesData.postValue(it.toList())
        }
    }

    fun downloadFile(fileId: Int) = scope.launch {
        TelegramRepository.downloadFile(fileId).let {
            fileDownloaded.postValue(it)
        }
    }
}