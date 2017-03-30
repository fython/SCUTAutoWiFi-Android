package moe.feng.scut.autowifi.api

import moe.feng.scut.autowifi.support.HttpUtils
import java.nio.charset.Charset

class DormitoryApi {

	companion object {

		private var wlanuserip = ""
		private var wlancip = ""
		val iTermType = 2

		fun setCurrentIp(newWlanuserip : String) : DormitoryApi.Companion {
			wlanuserip = newWlanuserip
			wlancip = getWlanCenterIp(ip = wlanuserip)
			return this
		}

		fun connect(username : String, password : String) : String? {
			val operation = "Login"

			val result = HttpUtils.postForm(
					url = "https://s.scut.edu.cn:801/eportal/?c=ACSetting&a=$operation" +
					"&wlanuserip=$wlanuserip&wlanacip=$wlancip&wlanacname=&redirect=&session=" +
					"&vlanid=scut-student&port=&iTermType=$iTermType&protocol=https:",

					params = hashMapOf(
							"0MKKey" to "123456",
							"DDDDD" to username,
							"R1" to "0",
							"R2" to "",
							"R6" to "0",
							"para" to "00",
							"upass" to password
					)
			)
			// return result is String && result.contains("已经成功登录")
			return if (result is ByteArray) String(bytes = result, charset = Charset.forName("GB2312")) else null
		}

		fun checkError() : String? {
			val result = HttpUtils.get("https://s.scut.edu.cn/errcode")
			return if (result is ByteArray) String(bytes = result, charset = Charset.forName("GB2312")) else null
		}

		fun getWlanCenterIp(ip : String) : String {
			val array = ip.split(".")
			return "${array[0]}.${array[1]}.255.250"
		}

	}

}