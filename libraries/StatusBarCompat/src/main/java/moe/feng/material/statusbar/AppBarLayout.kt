package moe.feng.material.statusbar

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.os.Build
import android.util.AttributeSet
import android.widget.LinearLayout

import moe.feng.material.statusbar.util.ViewHelper

class AppBarLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : LinearLayout(context, attrs, defStyle) {

	private var colorNormal: Int = 0
	private var colorDark: Int = 0
	private var enableMode: Int = 0

	private val headerView: StatusBarHeaderView

	init {
		val a = context.obtainStyledAttributes(attrs, R.styleable.StatusBarHeaderView, defStyle,
				R.style.Widget_FengMoe_StatusBarHeaderView)
		colorNormal = a.getColor(R.styleable.StatusBarHeaderView_colorNormal, Color.TRANSPARENT)
		if (a.hasValue(R.styleable.StatusBarHeaderView_colorDark)) {
			colorDark = a.getColor(R.styleable.StatusBarHeaderView_colorDark, Color.TRANSPARENT)
		} else {
			colorDark = ViewHelper.getMiddleColor(colorNormal, Color.BLACK, 0.2f)
		}
		enableMode = a.getInt(R.styleable.StatusBarHeaderView_enableMode, MODE_ALL)
		headerView = StatusBarHeaderView(context, colorNormal, colorDark, enableMode)
		this.setBackgroundColorWithoutAlpha(colorNormal)
		this.orientation = LinearLayout.VERTICAL
		this.addView(headerView)
		a.recycle()
		if (Build.VERSION.SDK_INT >= 21) {
			this.elevation = ViewHelper.dpToPx(context, 4f)
		}
	}

	fun setColor(colorNormal: Int, colorDark: Int) {
		this.colorNormal = colorNormal
		this.colorDark = colorDark
		//this.setBackgroundColorWithoutAlpha(colorNormal);
		headerView.normalColor = colorNormal
		headerView.darkColor = colorDark
		headerView.init()
	}

	fun setColorResources(colorNormal: Int, colorDark: Int) {
		this.setColor(
				resources.getColor(colorNormal),
				resources.getColor(colorDark)
		)
	}

	var normalColor: Int
		get() = this.colorNormal
		set(colorNormal) {
			this.colorNormal = colorNormal
			this.setBackgroundColorWithoutAlpha(colorNormal)
			headerView.normalColor = colorNormal
			headerView.init()
		}

	var darkColor: Int
		get() = this.colorDark
		set(colorDark) {
			this.colorDark = colorDark
			headerView.darkColor = colorDark
			headerView.init()
		}

	var mode: Int
		get() = this.enableMode
		set(mode) {
			this.enableMode = mode
			headerView.mode = mode
			headerView.init()
		}

	private fun setBackgroundColorWithoutAlpha(color: Int) {
		this.setBackgroundColor(Color.argb(255, Color.red(color), Color.green(color), Color.blue(color)))
	}

	companion object {

		val MODE_KITKAT = 1
		val MODE_LOLLIPOP = 2
		val MODE_ALL = 3
	}

}
