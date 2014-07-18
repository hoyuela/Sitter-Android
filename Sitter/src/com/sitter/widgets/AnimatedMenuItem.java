package com.sitter.widgets;

import android.view.animation.Animation.AnimationListener;

/**
 * Interface used to start animations for a menu item on opening a menu and closing a menu.
 * The implementation should define the animation that is to be applied to the menu item for
 * both open and close of the menu.
 * 
 * @author Henry Oyuela
 *
 */
public interface AnimatedMenuItem {
	/**
	 * Interface used to start the  enter animation for a menu with the index specified. 
	 * 
	 * @param index The index of a menu item with respect to the list of menu items it is a part of.
	 */
	public void enterAnimation(final int index);
	
	/**
	 * Interface used to start the exit animation for a menu with the index specified. 
	 * 
	 * @param index The index of the menu item with respect to the list of menu items it is a part of.
	 */
	public void exitAnimation(final int index, final AnimationListener listener);
}
