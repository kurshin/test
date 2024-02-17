package com.abto.checkpoint.data

import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.retryWhen
import kotlinx.telegram.core.TelegramException
import kotlinx.telegram.core.TelegramFlow
import kotlinx.telegram.coroutines.checkAuthenticationCode
import kotlinx.telegram.coroutines.checkAuthenticationPassword
import kotlinx.telegram.coroutines.checkDatabaseEncryptionKey
import kotlinx.telegram.coroutines.downloadFile
import kotlinx.telegram.coroutines.getChat
import kotlinx.telegram.coroutines.getChatHistory
import kotlinx.telegram.coroutines.getChats
import kotlinx.telegram.coroutines.getMe
import kotlinx.telegram.coroutines.getUser
import kotlinx.telegram.coroutines.sendMessage
import kotlinx.telegram.coroutines.setAuthenticationPhoneNumber
import kotlinx.telegram.coroutines.setTdlibParameters
import kotlinx.telegram.extensions.ChatKtx
import kotlinx.telegram.extensions.UserKtx
import kotlinx.telegram.flows.authorizationStateFlow
import kotlinx.telegram.flows.userFlow
import kotlinx.telegram.flows.userStatusFlow
import org.drinkless.td.libcore.telegram.TdApi
import org.drinkless.td.libcore.telegram.TdApi.Message

object TelegramRepository : UserKtx, ChatKtx {

    override val api: TelegramFlow = TelegramFlow()
    override suspend fun TdApi.User.get() = api.getMe()

    override suspend fun TdApi.Chat.get() = api.getChat(id)

    val authFlow = api.authorizationStateFlow()
        .onEach {
            checkRequiredParams(it)
        }
        .map {
            when (it) {
                is TdApi.AuthorizationStateReady -> AuthState.LoggedIn
                is TdApi.AuthorizationStateWaitCode -> AuthState.EnterCode
                is TdApi.AuthorizationStateWaitPassword -> AuthState.EnterPassword(it.passwordHint)
                is TdApi.AuthorizationStateWaitPhoneNumber -> AuthState.EnterPhone
                else -> AuthState.EnterPhone
            }
        }

    private suspend fun checkRequiredParams(state: TdApi.AuthorizationState?) {
        when (state) {
            is TdApi.AuthorizationStateWaitTdlibParameters ->
                api.setTdlibParameters(TelegramCredentials.parameters)

            is TdApi.AuthorizationStateWaitEncryptionKey ->
                api.checkDatabaseEncryptionKey(null)
        }
    }

    suspend fun sendPhone(phone: String) {
        api.setAuthenticationPhoneNumber(phone, null)
    }

    suspend fun sendCode(code: String) {
        api.checkAuthenticationCode(code)
    }

    suspend fun sendPassword(password: String) {
        api.checkAuthenticationPassword(
            password
        )
    }

    suspend fun sendMessage(chatId: Long, text: String) {
        api.sendMessage(
            chatId,
            0,
            0,
            null,
            null,
            TdApi.InputMessageText(TdApi.FormattedText(text, null), false, false)
        )
    }

    suspend fun getChats() =
        api.getChats(
            TdApi.ChatListMain(),
            100
        ).chatIds.map { api.getChat(it) }

    suspend fun getChatHistory(chatId: Long) =
        api.getChatHistory(chatId, 0, 0, 100, false).messages

    suspend fun downloadFile(fileId: Int) =
        api.downloadFile(fileId, 1, 0, 0, true).local.path
}