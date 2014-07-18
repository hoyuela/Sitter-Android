package com.sitter.widgets;



import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

import com.solstice.sitter.R;

public abstract class BaseMenu extends Dialog implements OnClickListener, AnimationListener{
	
	private int parentViewId;
	
	public BaseMenu(final Context context,final int parent,  final int layout, final int animation) {
		super(context);
		
		init(parent, layout, animation);
	}
	
	protected BaseMenu(final Context context,final boolean cancelable,
			final OnCancelListener cancelListener, final int parent, final int layout, final int animation) {
		super(context, cancelable, cancelListener);

		init(parent, layout, animation);	
	}

	public BaseMenu(final Context context, final int theme,final int parent, final int layout, final int animation ) {
		super(context, theme);

		init(parent, layout, animation);
	}

	public void init(final int parent, final int layout, final int animation) {
		
		 // Making sure there's no title.
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Making dialog content transparent.
        this.getWindow().setBackgroundDrawable(new ColorDrawable(R.color.blackopactity));
        this.getWindow().getAttributes().windowAnimations = animation;
        
        this.setCancelable(true);
       
        // Setting the content using prepared XML layout file.
        this.setContentView(layout);
        this.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT); 
       
        parentViewId = parent;       
        final ViewGroup parentView = (ViewGroup) this.findViewById(parent);
        setClickListeners(parentView);
	}
	
	private void setClickListeners(ViewGroup viewGroup) {
		for (int i = 0; i < viewGroup.getChildCount(); i++) {
			final View v = viewGroup.getChildAt(i);
			if (v instanceof ViewGroup) {
				setClickListeners((ViewGroup)v);
			} else {
				v.setOnClickListener(this);
			}
		}
	}
	
	@Override
	public void onAnimationEnd(Animation arg0) {
		//Called when cancelling the dialog
		super.cancel();
	}

	@Override
	public void onAnimationRepeat(Animation arg0) {
		//Not Required
	}

	@Override
	public void onAnimationStart(Animation arg0) {		
		//Not Required
	}
	
	@Override
	public void cancel() {
		final ViewGroup parentView = (ViewGroup) this.findViewById(parentViewId);
		
		boolean animatedCancel = false;
		for (int i = 0; i < parentView.getChildCount(); i++)  {
			final View v = parentView.getChildAt(i);
			
			if( v instanceof AnimatedMenuItem ) {
				if( i == parentView.getChildCount()/2) {
					animatedCancel= true;
					
					((AnimatedMenuItem)v).exitAnimation(i, this);
				} else {
					((AnimatedMenuItem)v).exitAnimation(i, null);
				}
			}
		}
		
		if( !animatedCancel ) {
			super.cancel();
		}
	}
	
	@Override
	public void show() {
		super.show();
		
		final ViewGroup parentView = (ViewGroup) this.findViewById(parentViewId);
		
		for (int i = 0; i < parentView.getChildCount(); i++) {
			final View v = parentView.getChildAt(i);	
			if( v instanceof AnimatedMenuItem ) {
				((AnimatedMenuItem)v).enterAnimation(i);
			}
		}
		
	}		
}
