package com.memoryclean.views;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.wust.memoryclean.R;

public class AnimationTranslate {
	public static LayoutAnimationController translate_item(Context context) {
		Animation animation = AnimationUtils.loadAnimation(context,
				R.anim.item_translate);
		LayoutAnimationController controller = new LayoutAnimationController(
				animation);
		controller.setDelay(0.1f);
		controller.setOrder(LayoutAnimationController.ORDER_RANDOM);
		return controller;
	}
}
