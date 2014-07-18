package com.sitter.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * 
 * Messed up because missing density calculations in many spots. magic numbers
 * galore.
 * 
 */
public class ChildProfileView extends ImageView {

	public enum Position {
		UPPER_LEFT, UPPER_RIGHT, LOWER_RIGHT, LOWER_LEFT;
	}

	// Defaults
	/** Pixels to offset the name bubble by. Need to be added to width/height. */
	private int NAME_BUBBLE_OFFSET = 50;
	private int NAME_BUBBLE_RADIUS = 150;
	private int NAME_BUBBLE_TEXTSIZE = 12;

	private int textSize;
	private int textBubbleRadius;
	private int textBubbleTextSize;
	private float scale;

	private int borderWidth;

	private float density;

	private boolean isTouched;

	private Paint whitePaint;
	private Paint textPaint;
	private Paint borderPaint;
	private Paint touchPaint;

	private Position childNamePosition;

	private String childName;

	private int pictureWidth;
	private int pictureHeight; // should be the same as width.

	public ChildProfileView(Context context) {
		super(context);

		setup(context);
	}

	public ChildProfileView(Context context, AttributeSet attrs) {
		super(context, attrs);

		setup(context);
	}

	public ChildProfileView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		setup(context);
	}

	// ========== PUBLIC ========== //

	public void setBorderColor(int color) {
		borderPaint.setColor(color);
		postInvalidate();
	}

	public void setBorderWidth(int pixels) {
		borderWidth = pixels;
		postInvalidate();
	}

	public void setChildName(String name) {
		childName = name;
		postInvalidate();
	}

	public void setChildNamePosition(Position pos) {
		childNamePosition = pos;
		postInvalidate();
	}

	public void setBubbleScaleSize(float scale) {
		this.scale = scale;
		pictureWidth = (int) (pictureWidth * scale);
		pictureHeight = (int) (pictureHeight * scale);

		textSize = (int) (textSize * scale);
		textBubbleRadius = (int) (textBubbleRadius * scale);
		textBubbleTextSize = (int) (textBubbleTextSize * scale);

		requestLayout();
	}

	// ========== PRIVATE ========== //

	/**
	 * Sets necessary attributes and default settings.
	 */
	private void setup(Context context) {
		isTouched = false;
		scale = 1.0f;

		textBubbleRadius = NAME_BUBBLE_RADIUS;
		textBubbleTextSize = NAME_BUBBLE_TEXTSIZE;

		DisplayMetrics dm = getResources().getDisplayMetrics();
		density = dm.density;
		textSize = (int) (textBubbleTextSize * density);

		borderWidth = 10;
		borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		borderPaint.setColor(0xFF000000);

		whitePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		whitePaint.setColor(0xFFFFFFFF);

		textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		textPaint.setColor(0xFFAA00FF);
		textPaint.setTextSize(textSize);
		textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

		touchPaint = new Paint();
		touchPaint.setColor(0x88000000);

		childNamePosition = Position.UPPER_LEFT;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		// int lMargin =
		// ((RelativeLayout.LayoutParams)this.getLayoutParams()).leftMargin;
		// int rMargin =
		// ((RelativeLayout.LayoutParams)this.getLayoutParams()).rightMargin;
		// int tMargin =
		// ((RelativeLayout.LayoutParams)this.getLayoutParams()).topMargin;
		// int bMargin =
		// ((RelativeLayout.LayoutParams)this.getLayoutParams()).bottomMargin;

		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(widthMeasureSpec);
		int widthSpec = MeasureSpec.getMode(widthMeasureSpec);
		int heightSpec = MeasureSpec.getMode(heightMeasureSpec);

		if (pictureWidth == 0 || pictureHeight == 0) {
			pictureWidth = (int) (widthSize * scale);
			pictureHeight = (int) (heightSize * scale);
		}
		if (textBubbleRadius == 0) {
			textBubbleRadius = NAME_BUBBLE_OFFSET;
		}

		// widthMeasureSpec = widthMeasureSpec + NAME_BUBBLE_OFFSET;
		// heightMeasureSpec = heightMeasureSpec + NAME_BUBBLE_OFFSET;

		setMeasuredDimension((int) (pictureWidth + (textBubbleRadius / 3.0f)),
				(int) (pictureHeight + (textBubbleRadius / 3.0f)));
		// super.onMeasure(MeasureSpec.makeMeasureSpec(pictureWidth +
		// textBubbleRadius, widthSpec),
		// MeasureSpec.makeMeasureSpec(pictureHeight + textBubbleRadius,
		// heightSpec));
	}

	@Override
	protected void onDraw(Canvas canvas) {

		Drawable drawable = getDrawable();

		if (drawable == null) {
			return;
		}

		if (getWidth() == 0 || getHeight() == 0) {
			return;
		}

		int heightOffset = 0, widthOffset = 0;
		int w = pictureWidth;
		int innerWidth = w;
		int smallCircleWidthOffset = 0, smallCircleHeightOffset = 0;

		switch (childNamePosition) {
		case UPPER_LEFT:
			heightOffset = NAME_BUBBLE_OFFSET;
			widthOffset = NAME_BUBBLE_OFFSET;
			break;
		case UPPER_RIGHT:
			heightOffset = NAME_BUBBLE_OFFSET;
			smallCircleWidthOffset = (int) (w - (2.0f * NAME_BUBBLE_OFFSET));
			break;

		case LOWER_LEFT:
			widthOffset = NAME_BUBBLE_OFFSET;
			smallCircleHeightOffset = (int) (w - (2.0f * NAME_BUBBLE_OFFSET));
			break;

		case LOWER_RIGHT:
			smallCircleWidthOffset = (int) (w - (3.0f * NAME_BUBBLE_OFFSET));
			smallCircleHeightOffset = (int) (w - (3.0f * NAME_BUBBLE_OFFSET));
			break;
		}

		// DRAW IMAGE

		// border
		canvas.drawCircle((w / 2.0f) + widthOffset, (w / 2.0f) + heightOffset,
				w / 2.0f, borderPaint);

		// inner border
		innerWidth = (int) (innerWidth - (2 * (0.33f * borderWidth)));
		canvas.drawCircle((w / 2.0f) + widthOffset, (w / 2.0f) + heightOffset,
				innerWidth / 2.0f, whitePaint);

		// Bitmap image
		Bitmap b = ((BitmapDrawable) drawable).getBitmap();
		Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);

		w = w - (2 * borderWidth);
		Bitmap roundBitmap = getCroppedBitmap(bitmap, w);
		canvas.drawBitmap(roundBitmap, borderWidth + widthOffset, borderWidth
				+ heightOffset, null);

		// DRAW NAME BUBBLE // TODO code so text is horizontally centered...
		canvas.drawCircle(smallCircleWidthOffset + (textBubbleRadius / 2.0f),
				smallCircleHeightOffset + (textBubbleRadius / 2.0f),
				textBubbleRadius / 2.0f, whitePaint);

		if (childName != null) {
			int bubbleWidth = (int) (textBubbleRadius / 2.0f);
			int textWidth = (int) (textPaint.measureText(childName)/ 2.0f);
			int offset = (bubbleWidth - textWidth) + smallCircleWidthOffset;
			
			canvas.drawText(childName, offset,
					smallCircleHeightOffset + textBubbleRadius
							- (density * (textSize / 1.7f)), textPaint);
		}
	}

	private Bitmap getCroppedBitmap(Bitmap bmp, int radius) {
		Bitmap sbmp;

		if (bmp.getWidth() != radius || bmp.getHeight() != radius) {
			float smallest = Math.min(bmp.getWidth(), bmp.getHeight());
			float factor = smallest / radius;
			sbmp = Bitmap.createScaledBitmap(bmp,
					(int) (bmp.getWidth() / factor),
					(int) (bmp.getHeight() / factor), false);
		} else {
			sbmp = bmp;
		}

		Bitmap output = Bitmap.createBitmap(radius, radius, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, radius, radius);

		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setDither(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(Color.parseColor("#BAB399"));
		canvas.drawCircle(radius / 2 + 0.7f, radius / 2 + 0.7f,
				radius / 2 + 0.1f, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(sbmp, rect, rect, paint);
		if (isTouched) {
			canvas.drawCircle(radius / 2 + 0.7f, radius / 2 + 0.7f,
					radius / 2 + 0.1f, touchPaint);
		}

		return output;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean isReleased = event.getAction() == MotionEvent.ACTION_UP
				|| event.getAction() == MotionEvent.ACTION_CANCEL;
		boolean isPressed = event.getAction() == MotionEvent.ACTION_DOWN;
		if (isReleased) {
			isTouched = false;
			invalidate();

			return true;

		} else if (isPressed) {
			isTouched = true;
			invalidate();

			return true;
		}

		return super.onTouchEvent(event);
	}

}