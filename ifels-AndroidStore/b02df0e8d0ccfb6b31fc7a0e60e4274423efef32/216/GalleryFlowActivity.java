package com.lee.gallery3d;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.lee.gallery3d.utils.BitmapUtil;
import com.lee.gallery3d.widget.GalleryFlow;

public class GalleryFlowActivity extends Activity
{
    GalleryFlow mGallery = null;
    ArrayList<BitmapDrawable> mBitmaps = new ArrayList<BitmapDrawable>();
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        generateBitmaps();
        
        mGallery = (GalleryFlow) findViewById(R.id.gallery_flow);
        mGallery.setBackgroundColor(Color.GRAY);
        mGallery.setSpacing(20);
        mGallery.setMaxZoom(-100);
        mGallery.setMaxRotationAngle(60);
        mGallery.setFadingEdgeLength(0);
        mGallery.setGravity(Gravity.CENTER_VERTICAL);
        mGallery.setAdapter(new GalleryAdapter());
        
    }
    
    
    private void generateBitmaps()
    {
        int[] ids =
        {
            R.drawable.a,
            R.drawable.b,
            R.drawable.c,
            R.drawable.d,
            R.drawable.e,
            R.drawable.f,
            R.drawable.g,
            R.drawable.h,
            R.drawable.i,
            R.drawable.j,
            R.drawable.k,
            R.drawable.l,
        };
        
        for (int id : ids)
        {
            Bitmap bitmap = createReflectedBitmapById(id);
            if (null != bitmap)
            {
                BitmapDrawable drawable = new BitmapDrawable(bitmap);
                drawable.setAntiAlias(true);
                mBitmaps.add(drawable);
            }
        }
    }
    
    private Bitmap createReflectedBitmapById(int resId)
    {
        Drawable drawable = getResources().getDrawable(resId);
        if (drawable instanceof BitmapDrawable)
        {
            Bitmap reflectedBitmap = BitmapUtil.createReflectedBitmap(((BitmapDrawable)drawable).getBitmap());
            return reflectedBitmap;
        }
        
        return null;
    }
    
    private class GalleryAdapter extends BaseAdapter
    {
        @Override
        public int getCount()
        {
            return mBitmaps.size();
        }

        @Override
        public Object getItem(int position)
        {
            return null;
        }

        @Override
        public long getItemId(int position)
        {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            if (null == convertView)
            {
                convertView = new MyImageView(GalleryFlowActivity.this);
                convertView.setLayoutParams(new Gallery.LayoutParams(Gallery.LayoutParams.WRAP_CONTENT, Gallery.LayoutParams.WRAP_CONTENT));
            }
            
            ImageView imageView = (ImageView)convertView;
            imageView.setImageDrawable(mBitmaps.get(position));
            //imageView.setScaleType(ScaleType.FIT_XY);
            imageView.setScaleType(ScaleType.CENTER_CROP);
            
            return imageView;
        }
    }
    
    private class MyImageView extends ImageView
    {
        public MyImageView(Context context)
        {
            this(context, null);
        }
        
        public MyImageView(Context context, AttributeSet attrs)
        {
            super(context, attrs, 0);
        }
        
        public MyImageView(Context context, AttributeSet attrs, int defStyle)
        {
            super(context, attrs, defStyle);
        }
        
        protected void onDraw(Canvas canvas)
        {
            super.onDraw(canvas);
        }
    }
}
