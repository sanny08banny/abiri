package com.example.carapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carapp.R;
import com.example.carapp.databinding.SearchItemBinding;

import java.util.List;

public class SearchHistoryAdapter extends RecyclerView.Adapter<SearchHistoryAdapter.SearchHistoryViewHolder> {
    private List<String> searchItems;
    private Context context;
    private OnItemClickListener listener;

    public SearchHistoryAdapter(List<String> searchItems, Context context) {
        this.searchItems = searchItems;
        this.context = context;
    }

    @NonNull
    @Override
    public SearchHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        SearchItemBinding searchItemBinding = DataBindingUtil.inflate(layoutInflater,
                R.layout.search_item,parent,false);
        return new SearchHistoryViewHolder(searchItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchHistoryViewHolder holder, int position) {
        String searchItem = searchItems.get(position);

        holder.bind(searchItem);
    }

    @Override
    public int getItemCount() {
        return searchItems.size();
    }

    public interface OnItemClickListener {
        void onItemClick(String item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public class SearchHistoryViewHolder extends RecyclerView.ViewHolder{
        private SearchItemBinding searchItemBinding;

        public SearchHistoryViewHolder(@NonNull SearchItemBinding searchItemBinding) {
            super(searchItemBinding.getRoot());
            this.searchItemBinding = searchItemBinding;
        }
        public void bind(String searchItem) {
            searchItemBinding.searchText.setText(searchItem);
        }
    }
}
