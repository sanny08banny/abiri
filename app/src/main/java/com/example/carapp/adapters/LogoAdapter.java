package com.example.carapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.carapp.R;
import com.example.carapp.databinding.LogoItemBinding;

import java.util.List;

public class LogoAdapter extends RecyclerView.Adapter<LogoAdapter.LogoViewHolder> {

    private Context context;
    private List<String> companies;
    private String baseUrl;

    public LogoAdapter(Context context, List<String> companies) {
        this.context = context;
        this.companies = companies;
        this.baseUrl = context.getResources().getString(R.string.base_url_title);
    }

    @NonNull
    @Override
    public LogoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        LogoItemBinding logoItemBinding = DataBindingUtil.inflate(layoutInflater,
                R.layout.car_item,parent,false);
        return new LogoViewHolder(logoItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull LogoViewHolder holder, int position) {
        String company = companies.get(position);

        holder.bind(company);
    }

    @Override
    public int getItemCount() {
        return companies.size();
    }

    public void setItems(List<String> data) {
        companies.clear();
        companies.addAll(data);
        notifyDataSetChanged();
    }

    public class LogoViewHolder extends RecyclerView.ViewHolder {
        private LogoItemBinding logoItemBinding;

        public LogoViewHolder(@NonNull LogoItemBinding logoItemBinding) {
            super(logoItemBinding.getRoot());
            this.logoItemBinding = logoItemBinding;
        }
        void bind(String urlString){
                glideImage(urlString, logoItemBinding.logoImageView);
        }
    }

    private void glideImage(String url, ImageView imageView) {
        if (url != null) {
            String endPoint = baseUrl + "/";
            Glide.with(context)
                    .load(endPoint)
                    .into(imageView);
        }
    }
}

