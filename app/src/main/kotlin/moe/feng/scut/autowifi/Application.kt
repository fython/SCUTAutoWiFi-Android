package moe.feng.scut.autowifi

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Build
import com.orhanobut.hawk.Hawk
import moe.feng.scut.autowifi.receiver.NetworkListener
import moe.feng.scut.autowifi.support.HttpUtils

class Application : android.app.Application() {

	override fun onCreate() {
		super.onCreate()
		HttpUtils.init(this)
		Hawk.init(this).build()

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
			cm.requestNetwork(NetworkRequest.Builder().build(), object : ConnectivityManager.NetworkCallback() {
				override fun onAvailable(network: Network) {
					super.onAvailable(network)
					NetworkListener.check(this@Application)
				}
			})
		}
	}

}