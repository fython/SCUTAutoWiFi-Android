package moe.feng.scut.autowifi.receiver

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.net.wifi.WifiInfo
import android.net.NetworkInfo
import android.text.TextUtils
import android.util.Log
import com.orhanobut.hawk.Hawk
import moe.feng.scut.autowifi.MainActivity
import moe.feng.scut.autowifi.R
import moe.feng.scut.autowifi.SettingsActivity
import moe.feng.scut.autowifi.api.DormitoryApi
import moe.feng.scut.autowifi.support.WifiUtils
import org.jetbrains.anko.*

class NetworkListener : BroadcastReceiver(), AnkoLogger {

	override fun onReceive(context: Context?, intent: Intent?) {
		if (context == null) return
		debug("Receive WiFi changed: ${intent?.action}")

		if (intent?.action == ConnectivityManager.CONNECTIVITY_ACTION) {

			val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
			val networkInfo = cm.activeNetworkInfo

			if (networkInfo != null && networkInfo.type == ConnectivityManager.TYPE_WIFI && networkInfo.isConnected) {
				val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
				val wifiInfo = wifiManager.connectionInfo;
				if (wifiInfo.ssid.contains(WifiUtils.SCUT_STUDENT_SSID)) {
					check(context)
				}
			}
		}
		else if (intent?.action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
			val networkInfo = intent?.getParcelableExtra<NetworkInfo>(WifiManager.EXTRA_NETWORK_INFO)

			if (networkInfo != null && networkInfo.isConnected) {
				val wifiInfo = intent.getParcelableExtra<WifiInfo>(WifiManager.EXTRA_WIFI_INFO)
				if (wifiInfo.ssid.contains(WifiUtils.SCUT_STUDENT_SSID)) {
					check(context)
				}
			}
		}
	}

	companion object {

		fun check(context : Context) {
			if (!Hawk.get("auto_login", true)) return
			if (TextUtils.isEmpty(Hawk.get("username", "")) || TextUtils.isEmpty(Hawk.get("password", ""))) {
				val builder = Notification.Builder(context)
				builder.setContentTitle(context.getString(R.string.noti_auto_login_need_account))
				builder.setContentText(context.getString(R.string.noti_auto_login_need_account_text))
				builder.setSmallIcon(R.drawable.ic_perm_scan_wifi_white_16dp)
				builder.setColor(context.resources.getColor(R.color.teal_500))
				builder.setAutoCancel(true)

				val stackBuilder = TaskStackBuilder.create(context)
				stackBuilder.addParentStack(MainActivity::class.java)
				stackBuilder.addNextIntent(Intent(context, SettingsActivity::class.java))

				builder.setContentIntent(stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT))

				(context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
						.notify(100, builder.build())
			} else {
				doAsync {
					val result = DormitoryApi
							.setCurrentIp(WifiUtils.getCurrentIP(context))
							.connect(username = Hawk.get("username"), password = Hawk.get("password"))
					val errCode = DormitoryApi.checkError()
					Log.d("NetworkListener", "Login isSuccess = ${result?.contains("成功")}, $errCode")
				}
			}
		}

	}

}