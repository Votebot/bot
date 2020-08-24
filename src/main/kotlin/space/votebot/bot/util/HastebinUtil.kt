package space.votebot.bot.util

import net.dv8tion.jda.api.utils.data.DataObject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.CompletableFuture

/**
 * Util for interacting with Hastebin.
 * @see Constants.hastebinUrl
 */
object HastebinUtil {

    /**
     * Posts the [text] to [Constants.hastebinUrl] using the [client].
     * @return a [CompletableFuture] containing the haste-url
     */
    fun postErrorToHastebin(text: String, client: OkHttpClient): CompletableFuture<String> {
        val body = text.toRequestBody("application/json".toMediaTypeOrNull())
        val request = Request.Builder()
                .url(Constants.hastebinUrl.newBuilder().addPathSegment("documents").build())
                .post(body)
                .build()
        return client.newCall(request).executeAsync().thenApply {
            it.use { response ->
                response.body!!.use { body ->
                    Constants.hastebinUrl.newBuilder().addPathSegment(
                            DataObject.fromJson(body.string()).getString(
                                    "key"
                            )
                    ).toString()
                }
            }
        }
    }
}
