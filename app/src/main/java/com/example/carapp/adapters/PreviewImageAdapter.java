package com.example.carapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.carapp.R;
import com.example.carapp.databinding.PreviewImageBinding;

import java.util.List;

public class PreviewImageAdapter extends RecyclerView.Adapter<PreviewImageAdapter.PreviewImageViewHolder> {
    private List<String> images;
    private Context context;
    private OnItemClickListener listener;

    public PreviewImageAdapter(List<String> images, Context context) {
        this.images = images;
        this.context = context;
    }

    @NonNull
    @Override
    public PreviewImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        PreviewImageBinding previewImageBinding = DataBindingUtil.inflate(layoutInflater,
                R.layout.preview_image,parent,false);
        return new PreviewImageViewHolder(previewImageBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull PreviewImageViewHolder holder, int position) {
        String image = images.get(position);

        holder.bind(image);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public interface OnItemClickListener {
        void onItemClick(String item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public class PreviewImageViewHolder extends RecyclerView.ViewHolder{
        private PreviewImageBinding previewImageBinding;

        public PreviewImageViewHolder(@NonNull PreviewImageBinding previewImageBinding) {
            super(previewImageBinding.getRoot());
            this.previewImageBinding = previewImageBinding;
        }
        public void bind(String image) {
            Glide.with(context)
                    .asBitmap()
                    .load(image)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.baseline_downloading_350) // Placeholder image while loading
                            .error(R.drawable.baseline_downloading_350)      // Error image if loading fails
                            .diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            // The image has been loaded into the 'resource' Bitmap.
                            // You can now use 'resource' as needed (e.g., set it to an ImageView).
                            previewImageBinding.previewImage.setImageBitmap(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            // This method is called when the image loading is cleared.
                            // You can handle this case if needed.
                        }
                    });
        }
    }
}
