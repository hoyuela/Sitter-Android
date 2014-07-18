package com.sitter.widgets;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.solstice.sitter.R;

public class ActivityMenuItem extends RelativeLayout implements AnimatedMenuItem{
	final long BASE_TIME_DURATION = 250;
	final long INCREMENTAL_TIME = 50;
	
	
	private TextView menuText;
	private Button menuButton;
	
	public ActivityMenuItem(Context context) {
		super(context);
		init(context, null);
	}

	public ActivityMenuItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public ActivityMenuItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}


	@SuppressWarnings("deprecation")
	@SuppressLint({ "InlinedApi", "NewApi" })
	public void init(Context context, AttributeSet attrs) {
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.activity_menu_item, null);
		
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
		
		menuText = (TextView) layout.findViewById(R.id.menu_text);
		menuButton = (Button) layout.findViewById(R.id.menu_button);
		
		this.addView(layout, params);
		
		if (attrs != null) {
			TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ActivityMenuItem, 0, 0);
			int textID = a.getResourceId(R.styleable.ActivityMenuItem_menuText, 0);
			if( textID > 0 ) {
				menuText.setText(textID);
			}
			a.recycle();

			a = getContext().obtainStyledAttributes(attrs, R.styleable.ActivityMenuItem, 0, 0);
			Drawable menuImage = context.getResources().getDrawable(a.getResourceId(R.styleable.ActivityMenuItem_menuImage, 0));
			menuButton.setBackgroundDrawable(menuImage);
			a.recycle();
			
		}	
	}
	
	public void enterAnimation(final int index) {
		final long start = (index > 0) ? AnimationUtils.currentAnimationTimeMillis() + (index-1)*INCREMENTAL_TIME : 0;
		final long duration = BASE_TIME_DURATION + index*INCREMENTAL_TIME;
		
		AnimationSet animSet = new AnimationSet(true);
		
		
		TranslateAnimation anim =  new TranslateAnimation(
			    Animation.RELATIVE_TO_PARENT, 1.0f,
			    Animation.RELATIVE_TO_PARENT, 0,
			    Animation.RELATIVE_TO_PARENT, 0,
			    Animation.RELATIVE_TO_PARENT, 0);
		anim.setStartTime(start);
		anim.setDuration(duration);
		menuButton.setAnimation(anim);
		
		TranslateAnimation anim2 =  new TranslateAnimation(
			    Animation.RELATIVE_TO_PARENT, 1.0f,
			    Animation.RELATIVE_TO_PARENT, 0,
			    Animation.RELATIVE_TO_PARENT, 0,
			    Animation.RELATIVE_TO_PARENT, 0);
		anim2.setDuration(BASE_TIME_DURATION);
		anim2.setStartOffset(50*index);
		menuText.setAnimation(anim2);
		
		animSet.addAnimation(anim);
		animSet.addAnimation(anim2);
		animSet.startNow();
	}
	
	public void exitAnimation(final int index, final AnimationListener listener) {
		final long start = (index > 0) ? AnimationUtils.currentAnimationTimeMillis() + (index-1)*INCREMENTAL_TIME : 0;
		final long duration = BASE_TIME_DURATION + index*INCREMENTAL_TIME;
		
		AnimationSet animSet = new AnimationSet(true);
				
		TranslateAnimation anim =  new TranslateAnimation(
			    Animation.RELATIVE_TO_PARENT, 0,
			    Animation.RELATIVE_TO_PARENT, 1.0f,
			    Animation.RELATIVE_TO_PARENT, 0,
			    Animation.RELATIVE_TO_PARENT, 0);	
		anim.setStartTime(start);
		anim.setDuration(duration);
		anim.setFillAfter(true);
		menuText.clearAnimation();
		menuText.setAnimation(anim);
		
		TranslateAnimation anim2 =  new TranslateAnimation(
			    Animation.RELATIVE_TO_PARENT, 0.0f,
			    Animation.RELATIVE_TO_PARENT, 1.0f,
			    Animation.RELATIVE_TO_PARENT, 0.0f,
			    Animation.RELATIVE_TO_PARENT, 0.0f);
		
		anim2.setInterpolator(new AccelerateInterpolator());
		anim2.setStartOffset(50*index);
		anim2.setFillAfter(true);
		anim2.setDuration(BASE_TIME_DURATION);
		menuButton.clearAnimation();
		menuButton.setAnimation(anim2);
		
		if( listener != null ) {
			anim2.setAnimationListener(listener);
		}
		
		animSet.addAnimation(anim);
		animSet.addAnimation(anim2);
		animSet.startNow();	
	}
}
