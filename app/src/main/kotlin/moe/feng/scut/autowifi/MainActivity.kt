package moe.feng.scut.autowifi

import android.app.Activity
import android.os.Bundle

import moe.feng.material.statusbar.StatusBarCompat
import moe.feng.scut.autowifi.api.DormitoryApi
import moe.feng.scut.autowifi.support.WifiUtils
import moe.feng.scut.autowifi.view.FloatingActionButton
import org.jetbrains.anko.*

class MainActivity : Activity(), AnkoLogger {

	val fab by lazy { find<FloatingActionButton>(R.id.fab) }

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
	}

	private fun doConnect() {
		doAsync {
			val isConnected = DormitoryApi
					.setCurrentIp(WifiUtils.getCurrentIP(this@MainActivity))
					.connect("", "")
			uiThread {
				info("isConnected: $isConnected")
			}
		}
	}

}