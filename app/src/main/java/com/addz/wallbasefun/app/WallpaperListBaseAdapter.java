package com.addz.wallbasefun.app;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by sunilnt on 31/05/14.
 */
public class WallpaperListBaseAdapter extends BaseAdapter {

    private Activity mContext;

    private List<Wallpaper> wallpaperList;

    private ImageView mainImageView;

    public WallpaperListBaseAdapter(Activity mContext, List<Wallpaper> wallpaperList, ImageView mainImageView) {
        this.mContext = mContext;
        this.wallpaperList = wallpaperList;
        this.mainImageView = mainImageView;
    }

    @Override
    public int getCount() {
        return wallpaperList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    static class ViewHolder
    {
        public ImageView wallpaperThumbnailImageView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View elementView = convertView;

        if(elementView == null)
        {
            LayoutInflater inflater = mContext.getLayoutInflater();
            elementView = inflater.inflate(R.layout.grid_element, null);

            ViewHolder viewHolder = new ViewHolder();
            viewHolder.wallpaperThumbnailImageView = (ImageView) elementView.findViewById(R.id.wallpaperThumbnailImageView);
            elementView.setTag(viewHolder);
        }

        final ViewHolder viewHolder = (ViewHolder) elementView.getTag();

        Picasso.with(mContext).load(wallpaperList.get(position).thumbURL).placeholder(R.drawable.ic_launcher).error(R.drawable.ic_drawer).into(viewHolder.wallpaperThumbnailImageView);

        if(position < 3)
        {
            elementView.setPadding(0, mainImageView.getHeight(), 0, 0);
        }
        return elementView;
    }
}
