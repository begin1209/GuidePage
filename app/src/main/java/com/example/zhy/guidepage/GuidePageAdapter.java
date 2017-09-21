package com.example.zhy.guidepage;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

/**
 * @Description:
 * @author: zhouy
 * @date: 2016-11-09
 * @version:
 */

public class GuidePageAdapter extends PagerAdapter{

    private List<ImageView> mImageViews = null;

    public GuidePageAdapter(List<ImageView> views){
        mImageViews = views;
    }

    @Override
    public int getCount() {
        return mImageViews == null ? 0: mImageViews.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager)container).removeView(mImageViews.get(position));
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ((ViewPager)container).addView(mImageViews.get(position),0);
        return mImageViews.get(position);
    }
}
