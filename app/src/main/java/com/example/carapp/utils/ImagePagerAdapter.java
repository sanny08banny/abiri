package com.example.carapp.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.carapp.R;

import java.util.ArrayList;

public class ImagePagerAdapter extends RecyclerView.Adapter<ImagePagerAdapter.ImageViewHolder> {

    private Context context;
    private ArrayList<String> imageResources;
    private String baseUrl;

    public ImagePagerAdapter(Context context, ArrayList<String> imageResources) {
        this.context = context;
        this.imageResources = imageResources;
        this.baseUrl = context.getResources().getString(R.string.base_url_title);
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String resourceId = imageResources.get(position);
        Glide.with(context)
                .asBitmap()
                .load(resourceId)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.baseline_downloading_350) // Placeholder image while loading
                        .error(R.drawable.baseline_downloading_350)      // Error image if loading fails
                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return imageResources.size();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}

