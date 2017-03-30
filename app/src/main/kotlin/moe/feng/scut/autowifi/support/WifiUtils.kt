package moe.feng.scut.autowifi.support

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiConfiguration.KeyMgmt
import android.net.wifi.WifiConfiguration.AuthAlgorithm
import android.text.TextUtils

class WifiUtils {

	companion object {

		private val SCUT_STUDENT_SSID = "scut-student"

		enum class WifiCipherType { WIFICIPHER_WEP, WIFICIPHER_WPA, WIFICIPHER_NOPASS, WIFICIPHER_INVALID }

		private fun getWifiManager(context : Context) =
				context.getSystemService(Context.WIFI_SERVICE) as WifiManager
		private fun getConnectivityManager(context : Context) =
				context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

		fun isWifiConnected(context : Context) =
				getConnectivityManager(context).getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected

		fun getCurrentSSID(context : Context) = getWifiManager(context).connectionInfo.ssid

		fun getCurrentIP(context : Context) = convertIpToStr(getWifiManager(context).connectionInfo.ipAddress)

		fun isSCUTSSID(context : Context) = getCurrentSSID(context).contains(SCUT_STUDENT_SSID, false)

		fun switchToSCUT(context : Context) : Boolean {
			val wifiConfig = createWifiInfo(SCUT_STUDENT_SSID, null, WifiCipherType.WIFICIPHER_NOPASS)
			val wifiManager = getWifiManager(context)

			val tempConfig = isExists(context, SCUT_STUDENT_SSID)
			if (tempConfig != null) wifiManager.removeNetwork(tempConfig.networkId)

			val netID = wifiManager.addNetwork(wifiConfig)
			val enabled = wifiManager.enableNetwork(netID, true)

			return enabled
		}

		fun reconnect(context : Context) = getWifiManager(context).reconnect()

		private fun isExists(context : Context, SSID: String): WifiConfiguration? {
			val existingConfigs = getWifiManager(context).configuredNetworks
			return existingConfigs.firstOrNull { it.SSID == "\"" + SSID + "\"" }
		}

		private fun createWifiInfo(SSID: String, password: String?, type: WifiCipherType): WifiConfiguration {
			val config = WifiConfiguration()
			config.allowedAuthAlgorithms.clear()
			config.allowedGroupCiphers.clear()
			config.allowedKeyManagement.clear()
			config.allowedPairwiseCiphers.clear()
			config.allowedProtocols.clear()
			config.SSID = "\"" + SSID + "\""
			// nopass
			if (type === WifiCipherType.WIFICIPHER_NOPASS) {
				config.wepKeys[0] = ""
				config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
				config.wepTxKeyIndex = 0
			}
			// wep
			if (type === WifiCipherType.WIFICIPHER_WEP) {
				if (!TextUtils.isEmpty(password)) {
					if (isHexWepKey(password!!)) {
						config.wepKeys[0] = password
					} else {
						config.wepKeys[0] = "\"" + password + "\""
					}
				}
				config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN)
				config.allowedAuthAlgorithms.set(AuthAlgorithm.SHARED)
				config.allowedKeyManagement.set(KeyMgmt.NONE)
				config.wepTxKeyIndex = 0
			}
			// wpa
			if (type === WifiCipherType.WIFICIPHER_WPA) {
				config.preSharedKey = "\"" + password + "\""
				config.hiddenSSID = true
				config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN)
				config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
				config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
				config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
				// 此处需要修改否则不能自动重联
				// config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
				config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
				config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
				config.status = WifiConfiguration.Status.ENABLED
			}
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