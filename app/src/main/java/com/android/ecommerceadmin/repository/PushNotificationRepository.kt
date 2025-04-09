import android.content.Context
import com.google.auth.oauth2.GoogleCredentials
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.FileInputStream
import javax.inject.Inject

class PushNotificationRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    // Get OAuth 2.0 token using the service account file
    private suspend fun getOAuthToken(): String {
        return withContext(Dispatchers.IO) {
            val credentials = GoogleCredentials.fromStream(FileInputStream(""))
                .createScoped(listOf("https://www.googleapis.com/auth/firebase.messaging"))
            credentials.refreshAccessToken().tokenValue
        }
    }

    // Send notification using FCM v1 API
    suspend fun sendNotification(title: String, message: String) {
        try {
            val accessToken = getOAuthToken()

            val url = "https://fcm.googleapis.com/v1/projects/project_ID/messages:send"

            val notification = JSONObject().apply {
                put("title", title)
                put("body", message)
            }

            val json = JSONObject().apply {
                put("message", JSONObject().apply {
                    put("topic", "all")
                    put("notification", notification)
                })
            }

            val body = RequestBody.create("application/json".toMediaTypeOrNull(), json.toString())

            val client = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $accessToken")
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build()

            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                // Handle success (e.g., show success message)
                println("Notification sent successfully.")
            } else {
                val responseBody = response.body?.string() ?: "No response body"
                println("Failed to send notification: ${response.code} - $responseBody")

            }
        } catch (e: Exception) {
            // Handle exceptions (e.g., network errors, authentication failures)
            println("Error sending notification: ${e.localizedMessage}")
        }
    }
}