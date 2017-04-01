package moe.feng.scut.autowifi.support

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.net.wifi.WifiConfiguration
import java.net.InetAddress

class WifiUtils {

	companion object {

		val SCUT_STUDENT_SSID = "scut-student"

		private fun getWifiManager(context : Context) =
				context.getSystemService(Context.WIFI_SERVICE) as WifiManager
		private fun getConnectivityManager(context : Context) =
				context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

		fun isWifiConnected(context : Context) =
				getConnectivityManager(context).getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected

		fun getCurrentSSID(context : Context) = getWifiManager(context).connectionInfo.ssid.replace("\"", "", false)

		fun getCurrentIP(context : Context) = convertIpToStr(getWifiManager(context).connectionInfo.ipAddress)

		fun isSCUTSSID(context : Context) = getCurrentSSID(context).contains(SCUT_STUDENT_SSID, false)

		fun isReachableBaidu() : Boolean = HttpUtils.ping("https://www.baidu.com")

		fun switchToSCUT(context : Context) : Boolean {
			val wifiConfig = createWifiInfo(SCUT_STUDENT_SSID)
			val wifiManager = getWifiManager(context)

			val tempConfig = isExists(context, SCUT_STUDENT_SSID)
			if (tempConfig != null) wifiManager.removeNetwork(tempConfig.networkId)

			val netID = wifiManager.addNetwork(wifiConfig)
			val enabled = wifiManager.enableNetwork(netID, true)

			return enabled && wifiManager.saveConfiguration()
		}

		fun reconnect(context : Context) = getWifiManager(context).reconnect()

		fun enableWifi(context : Context) = getWifiManager(context).setWifiEnabled(true)

		fun getState(context : Context) = getWifiManager(context).wifiState

		private fun isExists(context : Context, SSID: String): WifiConfiguration? {
			val existingConfigs = getWifiManager(context).configuredNetworks
			return existingConfigs.firstOrNull { it.SSID == "\"" + SSID + "\"" }
		}

		private fun createWifiInfo(SSID: String): WifiConfiguration {
			val config = WifiConfiguration()
			config.SSID = "\"$SSID\""
			config.preSharedKey = null
			config.hiddenSSID = true
			config.status = WifiConfiguration.Status.ENABLED
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
			config.allowedProtocols.set(WifiConfiguration.Protocol.WPA)
			return config
		}

		private fun isHexWepKey(wepKey: String): Boolean {
			val len = wepKey.length
			if (len != 10 && len != 26 && len != 58) {
				return false
			}
			return isHex(wepKey)
		}

		private fun isHex(key: String): Boolean {
			return (key.length - 1 downTo 0)
					.map { key[it] }
					.any { it in '0'..'9' || it in 'A'..'F' || it in 'a'..'f' }
		}

		private fun convertIpToStr(i: Int): String = (i and 0xFF).toString() + "." +
					(i shr 8 and 0xFF) + "." + (i shr 16 and 0xFF) + "." + (i shr 24 and 0xFF)

	}

}