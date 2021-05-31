package com.AudioToolkit.CustomViews;

import com.AudioToolkitPro.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class BoundedLinearLayout extends LinearLayout {

    private int mMaxWidth = 500;


    public BoundedLinearLayout(Context context) {
        super(context);
    }

    public BoundedLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.boundedlinearlayout);
        mMaxWidth = a.getInteger(R.styleable.boundedlinearlayout_bll_width, mMaxWidth);
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	
    	 int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
         if (mMaxWidth > 0 && mMaxWidth < measuredWidth) {
             int measureMode = MeasureSpec.getMode(widthMeasureSpec);
             widthMeasureSpec = MeasureSpec.makeMeasureSpec(mMaxWidth, measureMode);
         }
         super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}