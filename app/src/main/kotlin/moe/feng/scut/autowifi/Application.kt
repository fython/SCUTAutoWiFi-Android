package moe.feng.scut.autowifi

import moe.feng.scut.autowifi.support.HttpUtils

class Application : android.app.Application() {

	override fun onCreate() {
		super.onCreate()
		HttpUtils.init(this)
	}

}