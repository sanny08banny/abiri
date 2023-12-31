package com.example.carapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.carapp.R;

import java.util.ArrayList;

public class MediaAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<String> filePaths;
    private ArrayList<String> selectedItems;
    private int selectedItemCount = 0;
    private boolean isMultiSelectMode;

    public MediaAdapter(Context context, ArrayList<String> filePaths) {
        this.context = context;
        this.filePaths = filePaths;
        this.selectedItems = new ArrayList<>();
    }

    public void setMultiSelectMode(boolean multiSelectMode) {
        isMultiSelectMode = multiSelectMode;
    }

    @Override
    public int getCount() {
        return filePaths.size();
    }

    @Override
    public String getItem(int position) {
        return filePaths.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_view_item, parent, false);
            holder = new ViewHolder();
            holder.imageView = convertView.findViewById(R.id.imageView);
            holder.checkImage = convertView.findViewById(R.id.checkImage);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final String filePath = filePaths.get(position);
        Glide.with(context)
                .load(filePath)
                .into(holder.imageView);

        if (isMultiSelectMode) {
            holder.checkImage.setVisibility(View.VISIBLE);
            if (selectedItems.contains(filePath)) {
                holder.checkImage.setImageResource(R.drawable.baseline_check_24);
            } else {
                holder.checkImage.setVisibility(View.GONE);
            }
        } else {
            holder.checkImage.setVisibility(View.GONE);
        }

        return convertView;
    }

    private static class ViewHolder {
        ImageView imageView;
        ImageView checkImage;
    }

    public void toggleItemSelection(int position) {
        String selectedFilePath = filePaths.get(position);
        if (selectedItems.contains(selectedFilePath)) {
            selectedItems.remove(selectedFilePath);
            selectedItemCount--;
        } else {
            selectedItems.add(selectedFilePath);
            selectedItemCount++;
        }
        notifyDataSetChanged();
    }


    public ArrayList<String> getSelectedItems() {
        return selectedItems;
    }
}