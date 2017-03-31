package moe.feng.scut.autowifi

import com.orhanobut.hawk.Hawk
import moe.feng.scut.autowifi.support.HttpUtils

class Application : android.app.Application() {

	override fun onCreate() {
		super.onCreate()
		HttpUtils.init(this)
		Hawk.init(this).build()
	}

}