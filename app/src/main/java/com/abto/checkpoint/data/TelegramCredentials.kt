package com.abto.checkpoint.data

import org.drinkless.td.libcore.telegram.TdApi


//Replace with your personal telegram credentials
object TelegramCredentials {
    val parameters = TdApi.TdlibParameters().apply {
        databaseDirectory = "/data/user/0/com.abto.checkpoint/files/td"
        useMessageDatabase = false
        useSecretChats = false
        apiId = 26990344
        apiHash = "27e333f24d0084180dfc4dee11d2b871"
        useFileDatabase = true
        systemLanguageCode = "en"
        deviceModel = "Android"
        systemVersion = "Example"
        applicationVersion = "1.0"
        enableStorageOptimizer = true
    }
}