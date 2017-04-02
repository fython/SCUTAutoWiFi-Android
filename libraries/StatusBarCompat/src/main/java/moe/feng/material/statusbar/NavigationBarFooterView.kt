package moe.feng.material.statusbar

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View

import moe.feng.material.statusbar.util.ViewHelper

class NavigationBarFooterView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : View(context, attrs, defStyle) {

	public override fun onMeasure(widthSpec: Int, heightSpec: Int) {
		super.onMeasure(widthSpec, heightSpec)
		adjustHeight()
	}

	override fun invalidate() {
		super.invalidate()
		adjustHeight()
	}

	fun adjustHeight() {
		val params = layoutParams
		params.height = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) ViewHelper.getNavigationBarHeight(context) else 0
	}

}
