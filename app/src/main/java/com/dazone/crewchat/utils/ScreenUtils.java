package com.dazone.crewchat.utils;

import android.content.Context;

public class ScreenUtils {
	public static String getDensityName(Context context) {
		float density = context.getResources().getDisplayMetrics().density;
		if (density >= 4.0) {
			return "xxxhdpi";
		}
		if (density >= 3.0) {
			return "xxhdpi";
		}
		if (density >= 2.0) {
			return "xhdpi";
		}
		if (density >= 1.5) {
			return "hdpi";
		}
		if (density >= 1.0) {
			return "mdpi";
		}
		return "ldpi";
	}
	
	public static float dpFromPx(final Context context, final float px) {
	    return px / context.getResources().getDisplayMetrics().density;
	}

	public static float pxFromDp(final Context context, final int dp) {
	    return dp * context.getResources().getDisplayMetrics().density;
	}
}
