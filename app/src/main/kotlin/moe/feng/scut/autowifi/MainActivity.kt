package moe.feng.scut.autowifi

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView

import moe.feng.material.statusbar.StatusBarCompat
import moe.feng.scut.autowifi.api.DormitoryApi
import moe.feng.scut.autowifi.support.WifiUtils
import moe.feng.scut.autowifi.view.FloatingActionButton
import org.jetbrains.anko.*

class MainActivity : Activity(), AnkoLogger {

	val fab by lazy { find<FloatingActionButton>(R.id.fab) }
	val statusText by lazy { find<TextView>(R.id.status_text) }
	val userName by lazy { find<EditText>(R.id.user_name) }
	val userPwd by lazy { find<EditText>(R.id.user_pwd) }

	override fun onCreate(savedInstanceState: Bundle?) {
		StatusBarCompat.setUpActivity(this)

		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main2)

		fab.onClick {
			if (WifiUtils.isWifiConnected(this) && !WifiUtils.isSCUTSSID(this)) {
				val dialog = AlertDialogBuilder(this)
				dialog.title(R.string.dialog_connected_wifi_but_not_scut)
				dialog.message(R.string.dialog_connected_wifi_but_not_scut_msg)
				dialog.okButton {
					if (WifiUtils.switchToSCUT(this@MainActivity)) {
						doConnect()
					} else {

					}
				}
				dialog.cancelButton {  }
				dialog.show()
				return@onClick
			}
			doConnect()
		}

		/** Check manually after launching */
		networkStateReceiver.checkManually(this)
	}

	override fun onResume() {
		val filter = IntentFilter()
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
		registerReceiver(networkStateReceiver, filter)
		super.onResume()
	}

	override fun onPause() {
		unregisterReceiver(networkStateReceiver)
		super.onPause()
	}

	private fun setStatusText(any : Any) {
		statusText.text = getString(R.string.status_text_format, any)
	}

	private fun doConnect() {
		doAsync {
			val result = DormitoryApi
					.setCurrentIp(WifiUtils.getCurrentIP(this@MainActivity))
					.connect(username = userName.text.toString(), password = userPwd.text.toString())
			val errCode = DormitoryApi.checkError()
			uiThread {
				val dialog = AlertDialogBuilder(this@MainActivity)
				dialog.title(if (result is String && result.contains("登陆成功", false)) "Success" else "Fail")
				dialog.message("$result\n\n$errCode")
				dialog.okButton { }
				dialog.show()
			}
		}
	}

	private class NetworkStateReceiver(
			val disconnected: () -> Unit, val connectedMobile: () -> Unit, val connectedWifi: (Boolean) -> Unit
	) : BroadcastReceiver() {

		override fun onReceive(context: Context?, intent: Intent?) {
			context?.let { checkManually(it) }
		}

		fun checkManually(context : Context) {
			val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

			val wifiNetworkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
			val dataNetworkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)

			if (!dataNetworkInfo.isConnected && !wifiNetworkInfo.isConnected) {
				disconnected.invoke()
			} else if (dataNetworkInfo.isConnected && !wifiNetworkInfo.isConnected) {
				connectedMobile.invoke()
			} else if (!dataNetworkInfo.isConnected && wifiNetworkInfo.isConnected) {
				connectedWifi.invoke(WifiUtils.isSCUTSSID(context))
			}
		}

	}

	private val networkStateReceiver = NetworkStateReceiver(
			disconnected = {
				setStatusText(getString(R.string.status_text_disconnected))
			},
			connectedMobile = {
				setStatusText(getString(R.string.status_text_mobile_connected))
			},
			connectedWifi = {
				if (it) {
					setStatusText(getString(R.string.status_text_wifi_connected_scut))
				} else {
					setStatusText(getString(R.string.status_text_wifi_connected_other))
				}
			}
	)

}