package moe.feng.material.statusbar.util

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.WindowManager

object ViewHelper {

	val isChrome: Boolean
		get() = Build.BRAND === "chromium" || Build.BRAND === "chrome"

	fun getStatusBarHeight(context: Context): Int {
		var result = 0
		val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
		if (resourceId > 0) {
			result = context.resources.getDimensionPixelSize(resourceId)
		}
		return result
	}

	fun getTrueScreenHeight(context: Context): Int {
		val display = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
		val dm = DisplayMetrics()
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			display.getRealMetrics(dm)
		} else {
			display.getMetrics(dm)
		}
		return dm.heightPixels
	}

	fun getNavigationBarHeight(context: Context): Int {
		val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
		val dm = DisplayMetrics()
		wm.defaultDisplay.getMetrics(dm)

		return getTrueScreenHeight(context) - dm.heightPixels
	}

	private fun getMiddleValue(prev: Int, next: Int, factor: Float): Int {
		return Math.round(prev + (next - prev) * factor)
	}

	fun getMiddleColor(prevColor: Int, curColor: Int, factor: Float): Int {
		if (prevColor == curColor) {
			return curColor
		}

		if (factor == 0f) {
			return prevColor
		} else if (factor == 1f) {
			return curColor
		}

		val a = getMiddleValue(Color.alpha(prevColor), Color.alpha(curColor), factor)
		val r = getMiddleValue(Color.red(prevColor), Color.red(curColor), factor)
		val g = getMiddleValue(Color.green(prevColor), Color.green(curColor), factor)
		val b = getMiddleValue(Color.blue(prevColor), Color.blue(curColor), factor)

		return Color.argb(a, r, g, b)
	}

	fun getColor(baseColor: Int, alphaPercent: Float): Int {
		val alpha = Math.round(Color.alpha(baseColor) * alphaPercent)

		return baseColor and 0x00FFFFFF or (alpha shl 24)
	}

	fun dpToPx(context: Context, dp: Float): Float {
		return TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP,
				dp,
				context.resources.displayMetrics
		) + 0.5f
	}

}
