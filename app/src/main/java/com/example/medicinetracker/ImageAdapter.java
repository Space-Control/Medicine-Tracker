package com.example.medicinetracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    private int[] mImageIds;
    private String[] mImageNames;

    public ImageAdapter(Context context, int[] imageIds, String[] imageNames) {
        mContext = context;
        mImageIds = imageIds;
        mImageNames = imageNames;
    }

    @Override
    public int getCount() {
        return mImageIds.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View gridView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            gridView = inflater.inflate(R.layout.grid_item, null);
        } else {
            gridView = convertView;
        }

        ImageView imageView = gridView.findViewById(R.id.imageItem);
        TextView textView = gridView.findViewById(R.id.imageName);

        imageView.setImageResource(mImageIds[position]);
        textView.setText(mImageNames[position]);

        return gridView;
    }
}
