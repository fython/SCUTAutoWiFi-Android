package moe.feng.scut.autowifi.view

import android.animation.AnimatorInflater
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.Outline
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.StateListDrawable
import android.graphics.drawable.shapes.OvalShape
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.view.ViewTreeObserver
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageButton

import moe.feng.scut.autowifi.R

class FloatingActionButton : ImageButton {

	var isVisible: Boolean = false
		private set

	private var mColorNormal: Int = 0
	private var mColorPressed: Int = 0
	private var mColorRipple: Int = 0
	private var mColorDisabled: Int = 0
	private var mShadow: Boolean = false
	private var mType: Int = 0

	private var mShadowSize: Int = 0

	private var mScrollThreshold: Int = 0

	private var mMarginsSet: Boolean = false

	private val mInterpolator = AccelerateDecelerateInterpolator()

	@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs) {
		init(context, attrs)
	}

	constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
		init(context, attrs)
	}

	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec)
		val size = getDimension(if (mType == TYPE_NORMAL) R.dimen.fab_size_normal else R.dimen.fab_size_mini)
		setMeasuredDimension(size, size)
	}

	@SuppressLint("NewApi")
	private fun init(context: Context, attributeSet: AttributeSet?) {
		isVisible = true
		mColorNormal = getColor(R.color.orange_a200)
		mColorPressed = darkenColor(mColorNormal)
		mColorRipple = lightenColor(mColorNormal)
		mColorDisabled = getColor(android.R.color.darker_gray)
		mType = TYPE_NORMAL
		mShadow = true
		mScrollThreshold = resources.getDimensionPixelOffset(R.dimen.fab_scroll_threshold)
		mShadowSize = getDimension(R.dimen.fab_shadow_size)
		val stateListAnimator = AnimatorInflater.loadStateListAnimator(context,
				R.animator.fab_press_elevation)
		setStateListAnimator(stateListAnimator)
		if (attributeSet != null) {
			initAttributes(context, attributeSet)
		}
		updateBackground()
	}

	private fun initAttributes(context: Context, attributeSet: AttributeSet) {
		val attr = getTypedArray(context, attributeSet, R.styleable.FloatingActionButton)
		if (attr != null) {
			try {
				mColorNormal = attr.getColor(R.styleable.FloatingActionButton_fab_colorNormal,
						getColor(R.color.orange_a200))
				mColorPressed = attr.getColor(R.styleable.FloatingActionButton_fab_colorPressed,
						darkenColor(mColorNormal))
				mColorRipple = attr.getColor(R.styleable.FloatingActionButton_fab_colorRipple,
						lightenColor(mColorNormal))
				mColorDisabled = attr.getColor(R.styleable.FloatingActionButton_fab_colorDisabled,
						mColorDisabled)
				mShadow = attr.getBoolean(R.styleable.FloatingActionButton_fab_shadow, true)
				mType = attr.getInt(R.styleable.FloatingActionButton_fab_type, TYPE_NORMAL)
			} finally {
				attr.recycle()
			}
		}
	}

	private fun updateBackground() {
		val drawable = StateListDrawable()
		drawable.addState(intArrayOf(android.R.attr.state_pressed), createDrawable(mColorPressed))
		drawable.addState(intArrayOf(-android.R.attr.state_enabled), createDrawable(mColorDisabled))
		drawable.addState(intArrayOf(), createDrawable(mColorNormal))
		setBackgroundCompat(drawable)
	}

	private fun createDrawable(color: Int): Drawable {
		val ovalShape = OvalShape()
		val shapeDrawable = ShapeDrawable(ovalShape)
		shapeDrawable.paint.color = color
		return shapeDrawable
	}

	private fun getTypedArray(context: Context, attributeSet: AttributeSet, attr: IntArray): TypedArray? {
		return context.obtainStyledAttributes(attributeSet, attr, 0, 0)
	}

	private fun getColor(id: Int): Int {
		return resources.getColor(id)
	}

	private fun getDimension(id: Int): Int {
		return resources.getDimensionPixelSize(id)
	}

	private fun setMarginsWithoutShadow() {
		if (!mMarginsSet) {
			if (layoutParams is ViewGroup.MarginLayoutParams) {
				val layoutParams = layoutParams as ViewGroup.MarginLayoutParams
				val leftMargin = layoutParams.leftMargin - mShadowSize
				val topMargin = layoutParams.topMargin - mShadowSize
				val rightMargin = layoutParams.rightMargin - mShadowSize
				val bottomMargin = layoutParams.bottomMargin - mShadowSize
				layoutParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin)

				requestLayout()
				mMarginsSet = true
			}
		}
	}

	private fun setBackgroundCompat(drawable: Drawable) {
		val elevation: Float
		if (mShadow) {
			elevation = if (getElevation() > 0.0f)
				getElevation()
			else
				getDimension(R.dimen.fab_elevation_lollipop).toFloat()
		} else {
			elevation = 0.0f
		}
		setElevation(elevation)
		val rippleDrawable = RippleDrawable(ColorStateList(arrayOf(intArrayOf()),
				intArrayOf(mColorRipple)), drawable, null)
		outlineProvider = object : ViewOutlineProvider() {
			override fun getOutline(view: View, outline: Outline) {
				val size = getDimension(if (mType == TYPE_NORMAL)
					R.dimen.fab_size_normal
				else
					R.dimen.fab_size_mini)
				outline.setOval(0, 0, size, size)
			}
		}
		clipToOutline = true
		background = rippleDrawable
	}

	private val marginBottom: Int
		get() {
			var marginBottom = 0
			val layoutParams = layoutParams
			if (layoutParams is ViewGroup.MarginLayoutParams) {
				marginBottom = layoutParams.bottomMargin
			}
			return marginBottom
		}

	fun setColorNormalResId(colorResId: Int) {
		colorNormal = getColor(colorResId)
	}

	var colorNormal: Int
		get() = mColorNormal
		set(color) {
			if (color != mColorNormal) {
				mColorNormal = color
				updateBackground()
			}
		}

	fun setColorPressedResId(colorResId: Int) {
		colorPressed = getColor(colorResId)
	}

	var colorPressed: Int
		get() = mColorPressed
		set(color) {
			if (color != mColorPressed) {
				mColorPressed = color
				updateBackground()
			}
		}

	fun setColorRippleResId(colorResId: Int) {
		colorRipple = getColor(colorResId)
	}

	var colorRipple: Int
		get() = mColorRipple
		set(color) {
			if (color != mColorRipple) {
				mColorRipple = color
				updateBackground()
			}
		}

	fun setShadow(shadow: Boolean) {
		if (shadow != mShadow) {
			mShadow = shadow
			updateBackground()
		}
	}

	fun hasShadow(): Boolean {
		return mShadow
	}

	var type: Int
		get() = mType
		set(type) {
			if (type != mType) {
				mType = type
				updateBackground()
			}
		}

	@JvmOverloads fun show(animate: Boolean = true) {
		toggle(true, animate, false)
	}

	@JvmOverloads fun hide(animate: Boolean = true) {
		toggle(false, animate, false)
	}

	private fun toggle(visible: Boolean, animate: Boolean, force: Boolean) {
		if (isVisible != visible || force) {
			isVisible = visible
			val height = height
			if (height == 0 && !force) {
				val vto = viewTreeObserver
				if (vto.isAlive) {
					vto.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
						override fun onPreDraw(): Boolean {
							val currentVto = viewTreeObserver
							if (currentVto.isAlive) {
								currentVto.removeOnPreDrawListener(this)
							}
							toggle(visible, animate, true)
							return true
						}
					})
					return
				}
			}
			val translationY = if (visible) 0 else height + marginBottom
			if (animate) {
				animate().setInterpolator(mInterpolator)
						.setDuration(TRANSLATE_DURATION_MILLIS.toLong())
						.translationY(translationY.toFloat())
			} else {
				setTranslationY(translationY.toFloat())
			}

		}
	}

	companion object {

		private val TRANSLATE_DURATION_MILLIS = 200

		val TYPE_NORMAL = 0
		val TYPE_MINI = 1

		private fun darkenColor(color: Int): Int {
			val hsv = FloatArray(3)
			Color.colorToHSV(color, hsv)
			hsv[2] *= 0.9f
			return Color.HSVToColor(hsv)
		}

		private fun lightenColor(color: Int): Int {
			val hsv = FloatArray(3)
			Color.colorToHSV(color, hsv)
			hsv[2] *= 1.1f
			return Color.HSVToColor(hsv)
		}
	}

}