package zeljko.dejan.rpginventorymanager

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class ChatService {
    companion object {
        private const val BASE_URL = "https://detschn.net/atd"

        suspend fun callCreateChatService(title: String, description: String): String? {
            val urlString = "$BASE_URL/createChat"
            val jsonBody = JSONObject()
            jsonBody.put("name", title)
            jsonBody.put("description", description)

            return withContext(Dispatchers.IO) {
                try {
                    val url = URL(urlString)
                    with(url.openConnection() as HttpURLConnection) {
                        requestMethod = "POST"
                        doOutput = true
                        setRequestProperty("Content-Type", "application/json")
                        setRequestProperty("Accept", "application/json")

                        OutputStreamWriter(outputStream).use { it.write(jsonBody.toString()) }

                        if (responseCode == HttpURLConnection.HTTP_CREATED) {
                            val result = inputStream.bufferedReader().use { it.readText() }
                            Log.i(
                                "ChatService",
                                "callCreateChatService: " + result
                            )

                            JSONObject(result).getString("threadId")
                        } else {
                            Log.e(
                                "ChatService",
                                "callCreateChatService: " + responseMessage
                            )
                            null
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
        }

        suspend fun callGetNextMessage(threadId: String): String? {
            val urlString = "$BASE_URL/getNextMessage"
            val jsonBody = JSONObject()
            jsonBody.put("threadId", threadId)

            Log.i("ChatService", "callGetNextMessage: Sending request to $urlString")

            return withContext(Dispatchers.IO) {
                try {
                    val url = URL(urlString)
                    with(url.openConnection() as HttpURLConnection) {
                        requestMethod = "POST"
                        doOutput = true
                        setRequestProperty("Content-Type", "application/json")
                        setRequestProperty("Accept", "application/json")

                        OutputStreamWriter(outputStream).use { it.write(jsonBody.toString()) }

                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            val result = inputStream.bufferedReader().use { it.readText() }
                            Log.i(
                                "ChatService",
                                "callGetNextMessage: " + result
                            )

                            JSONObject(result).getJSONArray("content").getJSONObject(0)
                                .getJSONObject("text")
                                .getString("value")
                        } else {
                            Log.e(
                                "ChatService",
                                "callGetNextMessage: " + responseMessage
                            )
                            null
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
        }

        suspend fun callSendMessageAndGetResponse(threadId: String, message: String): String? {
            val urlString = "$BASE_URL/sendMessageAndGetResponse"
            val jsonBody = JSONObject()
            jsonBody.put("threadId", threadId)
            jsonBody.put("message", message)

            Log.i("ChatService", "callSendMessageAndGetResponse: Sending request to $urlString")

            return withContext(Dispatchers.IO) {
                try {
                    val url = URL(urlString)
                    with(url.openConnection() as HttpURLConnection) {
                        requestMethod = "POST"
                        doOutput = true
                        setRequestProperty("Content-Type", "application/json")
                        setRequestProperty("Accept", "application/json")

                        OutputStreamWriter(outputStream).use { it.write(jsonBody.toString()) }

                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            val result = inputStream.bufferedReader().use { it.readText() }
                            Log.i(
                                "ChatService",
                                "callSendMessageAndGetResponse: " + result
                            )

                            JSONObject(result).getJSONArray("content").getJSONObject(0)
                                .getJSONObject("text")
                                .getString("value")
                        } else {
                            Log.e(
                                "ChatService",
                                "callSendMessageAndGetResponse: " + responseMessage
                            )
                            null
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
        }

        fun callDeleteChatService(threadId: String): Boolean {
            val urlString = "$BASE_URL/deleteChat"
            val jsonBody = JSONObject()
            jsonBody.put("threadId", threadId)

            Log.i("ChatService", "callDeleteChatService: Sending request to $urlString")

            return try {
                val url = URL(urlString)
                with(url.openConnection() as HttpURLConnection) {
                    requestMethod = "POST"
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty("Accept", "application/json")

                    OutputStreamWriter(outputStream).use { it.write(jsonBody.toString()) }

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        val result = inputStream.bufferedReader().use { it.readText() }
                        Log.i(
                            "ChatService",
                            "callDeleteChatService: " + result
                        )

                        true
                    } else {
                        Log.e(
                            "ChatService",
                            "callDeleteChatService: " + responseMessage
                        )
                        false
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }

        }
    }
}
