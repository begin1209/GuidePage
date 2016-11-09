package com.example.zhy.guidepageactivity;

import android.app.Activity;
import android.media.Image;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements ViewPager.OnPageChangeListener{

    private ViewPager mViewPager = null;

    private List<ImageView> views = new ArrayList<>();

    private PagerAdapter mPagerAdapter = null;

    private ImageButton mPassImage = null;

    private LinearLayout mLinearLayout = null;

    private ImageView mImageDot = null;

    private static final int[] pics = {R.mipmap.pic_guidepage_1,
            R.mipmap.pic_guidepage_2, R.mipmap.pic_guidepage_3, R.mipmap.pic_guidepage_4 };

    private static final int[] dots = {R.mipmap.point1, R.mipmap.point2};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        mViewPager = (ViewPager)findViewById(R.id.view_pager);
        mPassImage = (ImageButton)findViewById(R.id.pass_view);
        mLinearLayout = (LinearLayout)findViewById(R.id.dot_linear_layout);
        initImageViews();
        mPagerAdapter = new GuidePageAdapter(views);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setCurrentItem(0);
        ((ImageView)(mLinearLayout.getChildAt(0))).setImageResource(dots[1]);
    }

    private void initImageViews(){
        for(int i = 0; i < pics.length; i++){
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(pics[i]);
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            views.add(imageView);
            ImageView dotView = new ImageView(this);
            dotView.setPadding(8,0,8,0);
            dotView.setImageResource(dots[0]);
            dotView.setAdjustViewBounds(true);
            dotView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            LinearLayout.LayoutParams params = new
                    LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            mLinearLayout.addView(dotView, params);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if(position == 3){
            mPassImage.setVisibility(View.INVISIBLE);
        }else {
            mPassImage.setVisibility(View.VISIBLE);
        }
        updateDotStatus();
        ((ImageView)(mLinearLayout.getChildAt(position))).setImageResource(dots[1]);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void updateDotStatus(){
        for(int i = 0; i < mLinearLayout.getChildCount(); i++){
            ((ImageView)(mLinearLayout.getChildAt(i))).setImageResource(dots[0]);
        }
    }
}
