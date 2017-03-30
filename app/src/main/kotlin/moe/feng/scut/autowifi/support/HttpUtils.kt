package moe.feng.scut.autowifi.support

import android.content.Context
import android.util.Log
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.FormBody

class HttpUtils {

	companion object {

		private var okHttpClient = OkHttpClient.Builder().build()

		private val TAG = "HttpUtils"

		fun init(context : Context) {
			val cookieJar = PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(context))
			okHttpClient = OkHttpClient.Builder().cookieJar(cookieJar).build()
		}

		fun get(url : String) : ByteArray? {
			Log.d(TAG, "Request url: " + url)
			val request = Request.Builder().url(url).build()
			try {
				val response = okHttpClient.newCall(request).execute()
				Log.d(TAG, "Response code:" + response.code())
				val result = response.body().bytes()
				Log.d(TAG, "Response data: " + String(result))
				return result
			} catch (e : Exception) {
				e.printStackTrace()
				return null
			}
		}

		fun postForm(url: String, params: Map<String, Any>?): ByteArray? {
			Log.d(TAG, "Request url: " + url)
			val builder = FormBody.Builder()
			if (params != null) for ((key, value) in params) {
				builder.add(key, value.toString())
			}
			val requestBody = builder.build()
			Log.d(TAG, "Request body: " + requestBody.toString())
			val request = Request.Builder().url(url).post(requestBody).build()
			try {
				val response = okHttpClient.newCall(request).execute()
				Log.d(TAG, "Response code: " + response.code())
				val result = response.body().bytes()
				Log.d(TAG, "Response data: " + String(result))
				return result
			} catch (e: Exception) {
				e.printStackTrace()
				return null
			}
		}

	}

}