package com.sitter.widgets;

import android.content.Context;
import android.view.View;

import com.solstice.sitter.R;

public class ActivityMenu extends BaseMenu  {
	static private boolean ACTIVITY_MENU_OPEN = false;
	
	public ActivityMenu(Context context) {
		super(context,R.id.activity_menu, R.layout.activity_menu, R.style.ActivityMenuAnimation);
	}
	
	protected ActivityMenu(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener,R.id.activity_menu, R.layout.activity_menu, R.style.ActivityMenuAnimation);
	}

	public ActivityMenu(Context context, int theme) {
		super(context, theme,R.id.activity_menu, R.layout.activity_menu, R.style.ActivityMenuAnimation);
	}

	@Override
	public void show() {
		super.show();
		ACTIVITY_MENU_OPEN = true;
	}

	@Override
	public void cancel() {
		super.cancel();
		ACTIVITY_MENU_OPEN = false;
	}
	
	@Override
	public void onClick(View view) {
		int id = ((View)view.getParent().getParent()).getId();
		
//		switch( id ) {
//		case R.id.menu_call_button:
//			break;
//		case R.id.menu_contact_button:
//			break;
//		case R.id.menu_organization_button:
//			break;
//		case R.id.menu_note_button:
//			break;
//		case R.id.menu_opportunity_button:
//			break;
//		case R.id.menu_meeting_button:
//			break;
//		default:
//			cancel();
//			break;
//		}	
		
		cancel();
	}
	
	public static boolean isMenuOpen() {
		return ACTIVITY_MENU_OPEN;
	}
}
