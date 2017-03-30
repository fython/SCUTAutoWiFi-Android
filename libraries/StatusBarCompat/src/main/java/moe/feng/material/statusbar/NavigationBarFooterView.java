package moe.feng.material.statusbar;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import moe.feng.material.statusbar.util.ViewHelper;

public class NavigationBarFooterView extends View {

	public NavigationBarFooterView(Context context) {
		this(context, null);
	}

	public NavigationBarFooterView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public NavigationBarFooterView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void onMeasure(int widthSpec, int heightSpec) {
		super.onMeasure(widthSpec, heightSpec);
		adjustHeight();
	}

	@Override
	public void invalidate() {
		super.invalidate();
		adjustHeight();
	}

	public void adjustHeight() {
		ViewGroup.LayoutParams params = getLayoutParams();
		params.height = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ? ViewHelper.getNavigationBarHeight(getContext()) : 0;
	}

}
