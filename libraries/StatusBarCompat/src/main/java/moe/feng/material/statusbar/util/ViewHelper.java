package moe.feng.material.statusbar.util;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

public class ViewHelper {

	public static boolean isChrome(){
		return Build.BRAND == "chromium" || Build.BRAND == "chrome";
	}

	public static int getStatusBarHeight(Context context) {
		int result = 0;
		int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = context.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}

	public static int getTrueScreenHeight(Context context) {
		Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		DisplayMetrics dm = new DisplayMetrics();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			display.getRealMetrics(dm);
		} else {
			display.getMetrics(dm);
		}
		return dm.heightPixels;
	}

	public static int getNavigationBarHeight(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);

		return getTrueScreenHeight(context) - dm.heightPixels;
	}

	private static int getMiddleValue(int prev, int next, float factor) {
		return Math.round(prev + (next - prev) * factor);
	}

	public static int getMiddleColor(int prevColor, int curColor, float factor) {
		if (prevColor == curColor) {
			return curColor;
		};

		if (factor == 0f) {
			return prevColor;
		} else if(factor == 1f) {
			return curColor;
		}

		int a = getMiddleValue(Color.alpha(prevColor), Color.alpha(curColor), factor);
		int r = getMiddleValue(Color.red(prevColor), Color.red(curColor), factor);
		int g = getMiddleValue(Color.green(prevColor), Color.green(curColor), factor);
		int b = getMiddleValue(Color.blue(prevColor), Color.blue(curColor), factor);

		return Color.argb(a, r, g, b);
	}

	public static int getColor(int baseColor, float alphaPercent) {
		int alpha = Math.round(Color.alpha(baseColor) * alphaPercent);

		return (baseColor & 0x00FFFFFF) | (alpha << 24);
	}

	public static float dpToPx(Context context, float dp) {
		return TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP,
				dp,
				context.getResources().getDisplayMetrics()
		) + 0.5f;
	}
	
}
