package moe.feng.scut.autowifi

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.text.TextUtils
import android.widget.ImageButton
import android.widget.TextView
import com.orhanobut.hawk.Hawk

import moe.feng.material.statusbar.StatusBarCompat
import moe.feng.scut.autowifi.api.DormitoryApi
import moe.feng.scut.autowifi.support.WifiUtils
import moe.feng.scut.autowifi.view.FloatingActionButton
import org.jetbrains.anko.*

class MainActivity : Activity(), AnkoLogger {

	val settingsButton by lazy { find<ImageButton>(R.id.btn_settings) }
	val fab by lazy { find<FloatingActionButton>(R.id.fab) }
	val statusText by lazy { find<TextView>(R.id.status_text) }

	override fun onCreate(savedInstanceState: Bundle?) {
		StatusBarCompat.setUpActivity(this)

		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main2)

		settingsButton.onClick { startActivity<SettingsActivity>() }
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
			} else if (!WifiUtils.isWifiConnected(this)) {
				WifiUtils.enableWifi(this)
				doAsync {
					while (WifiUtils.getState(this@MainActivity) == WifiManager.WIFI_STATE_ENABLING) Thread.sleep(100)
					uiThread { WifiUtils.switchToSCUT(this@MainActivity) }
				}
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
		if (TextUtils.isEmpty(Hawk.get("username", "")) || TextUtils.isEmpty(Hawk.get("password", ""))) {
			val dialog = AlertDialogBuilder(this)
			dialog.title(R.string.dialog_no_account)
			dialog.message(R.string.dialog_no_account_msg)
			dialog.okButton { startActivity<SettingsActivity>() }
			dialog.show()
			return
		}
		doAsync {
			val result = DormitoryApi
					.setCurrentIp(WifiUtils.getCurrentIP(this@MainActivity))
					.connect(username = Hawk.get("username"), password = Hawk.get("password"))
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
					setStatusText(getString(R.string.status_text_wifi_connected_other, WifiUtils.getCurrentSSID(this)))
				}
			}
	)

}