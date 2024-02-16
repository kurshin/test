//
// NOTE: THIS FILE IS AUTO-GENERATED by the "TdApiKtxGenerator".kt
// See: https://github.com/tdlibx/td-ktx-generator/
//
package kotlinx.telegram.coroutines

import kotlin.Int
import kotlinx.telegram.core.TelegramFlow
import org.drinkless.td.libcore.telegram.TdApi
import org.drinkless.td.libcore.telegram.TdApi.StorageStatistics
import org.drinkless.td.libcore.telegram.TdApi.StorageStatisticsFast

/**
 * Suspend function, which returns storage usage statistics. Can be called before authorization.
 *
 * @param chatLimit The maximum number of chats with the largest storage usage for which separate
 * statistics should be returned. All other chats will be grouped in entries with chatId == 0. If the
 * chat info database is not used, the chatLimit is ignored and is always set to 0.
 *
 * @return [StorageStatistics] Contains the exact storage usage statistics split by chats and file
 * type.
 */
suspend fun TelegramFlow.getStorageStatistics(chatLimit: Int): StorageStatistics =
    this.sendFunctionAsync(TdApi.GetStorageStatistics(chatLimit))

/**
 * Suspend function, which quickly returns approximate storage usage statistics. Can be called
 * before authorization.
 *
 * @return [StorageStatisticsFast] Contains approximate storage usage statistics, excluding files of
 * unknown file type.
 */
suspend fun TelegramFlow.getStorageStatisticsFast(): StorageStatisticsFast =
    this.sendFunctionAsync(TdApi.GetStorageStatisticsFast())
