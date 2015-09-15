package com.example.myownslidetounlockbutton;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class UnlockBar extends RelativeLayout
{
	private OnUnlockListener listener = null;
	//why null ??
	private TextView text_label = null;
	private ImageView image_slider = null;
	private RelativeLayout rellayout_background_grey = null;
	private RelativeLayout rellayout_background_trans = null;
	private RelativeLayout rellayout_background_red = null;
	
	private int thumbWidth = 0;
	boolean sliding = false; //mostly the state for sliding function
	private int sliderPosition = 0;
	int initialSliderPosition = 0;
	int slider_center_offset_x = 36;
	int slider_center_offset_y = 36;
	
	//figure out what initialslidingx is 
	float initialSlidingX = 0;
	//Constructor Overloading is done to accomodate multiple constructors
	//constructor, why do we need to pass the context?
	public UnlockBar(Context context)
	{
		super(context);
		init(context, null);
	}

	public UnlockBar(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context, attrs);
	}

	public UnlockBar(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	public void setOnUnlockListener(OnUnlockListener listener)
	{
		this.listener = listener;
	}

	public void reset()
	{
		
		final RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) image_slider.getLayoutParams();
		final RelativeLayout.LayoutParams params_red = (RelativeLayout.LayoutParams) rellayout_background_red.getLayoutParams();
		ValueAnimator animator_redback = ValueAnimator.ofInt(params_red.width,thumbWidth);
		animator_redback.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				// TODO Auto-generated method stub
				params_red.width = (Integer) animation.getAnimatedValue();
				
			}
		});
		rellayout_background_red.setLayoutParams(params_red);
		//params_red.width = thumbWidth;
		ValueAnimator animator = ValueAnimator.ofInt(params.leftMargin, 0);
		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
		    @Override
		    public void onAnimationUpdate(ValueAnimator valueAnimator)
		    {
		        params.leftMargin = (Integer) valueAnimator.getAnimatedValue();
		        image_slider.requestLayout();
		    }
		});
		animator_redback.setDuration(300);
		animator_redback.start();
		animator.setDuration(300);
		animator.start();
		
		
		text_label.setAlpha(1f);
	}
	
	//init function:does???- Ans:-
	private void init(Context context, AttributeSet attrs)
	{		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		//what happens here exactly???
		//how does attach to root does not matter ????
		//why not getting a return value in a Veiw???
		inflater.inflate(R.layout.view_unlock, this, true);
		
		// Retrieve layout elements
		text_label = (TextView) findViewById(R.id.textView_delivered);
		image_slider = (ImageView) findViewById(R.id.imageView_slider);
		rellayout_background_red = (RelativeLayout) findViewById(R.id.rellayout_background_red);
		
		
		// Get padding
		//[devansh] why do we need to add this padding ??
		thumbWidth = dpToPx(72); // 60dp + 2*10dp
	}
	
	@Override
	@SuppressLint("ClickableViewAccessibility")//why is this annotation used for??
	public boolean onTouchEvent(MotionEvent event)
	{
		super.onTouchEvent(event);
		
		if (event.getAction() == MotionEvent.ACTION_DOWN)
		{
			if (event.getX() > sliderPosition && event.getX() < (sliderPosition + thumbWidth))
			{
				rellayout_background_red.setVisibility(View.VISIBLE);
				sliding = true;
				initialSlidingX = event.getX();
				initialSliderPosition = sliderPosition;
			}
		}
		else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_OUTSIDE)
		{
			if (sliderPosition >= (getMeasuredWidth() - thumbWidth)*.75) //.8 is the 80% of the total width
			{
				final RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) image_slider.getLayoutParams();
				RelativeLayout.LayoutParams params_red = (RelativeLayout.LayoutParams) rellayout_background_red.getLayoutParams();
				params.leftMargin = getMeasuredWidth() - thumbWidth;
				params_red.width = getMeasuredWidth()+ thumbWidth;
				rellayout_background_red.setLayoutParams(params_red);
				image_slider.setLayoutParams(params);
				if (listener != null) listener.onUnlock();
			}
			else
			{
				sliding = false;
				sliderPosition = 0;
			//	rellayout_background_red.setVisibility(View.INVISIBLE);
				reset();
			}
		}
		else if (event.getAction() == MotionEvent.ACTION_MOVE && sliding)
		{
			sliderPosition = (int) (initialSliderPosition + (event.getX() - initialSlidingX));
			if (sliderPosition <= 0) sliderPosition = 0;
			
			if (sliderPosition >= (getMeasuredWidth() - thumbWidth))
			{
				sliderPosition = (int) (getMeasuredWidth()  - thumbWidth);
			}
			else
			{
				int max = (int) (getMeasuredWidth() - thumbWidth);
				int progress = (int) (sliderPosition * 100 / (max * 1.0f));
				text_label.setAlpha(1f - progress * 0.01f);
			}
			setMarginLeft(sliderPosition);
		}
		
		return true;
	}
	
	private void setMarginLeft(int margin)
	{
		if (image_slider == null) return;
		else if (rellayout_background_red ==null) return;
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) image_slider.getLayoutParams();
		RelativeLayout.LayoutParams params_red = (RelativeLayout.LayoutParams) rellayout_background_red.getLayoutParams();
		params.setMargins(margin, 0, 0, 0);
		image_slider.setLayoutParams(params);
		params_red.width = margin + thumbWidth;
		rellayout_background_red.setLayoutParams(params_red);
	}
	
	private int dpToPx(int dp)
	{
		float density = getResources().getDisplayMetrics().density;
	    return Math.round((float)dp * density);
	}
	
	public static interface OnUnlockListener {
		void onUnlock();
	}
}
