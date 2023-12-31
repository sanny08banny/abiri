package com.example.carapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carapp.R;
import com.example.carapp.databinding.CommentLtBinding;
import com.example.carapp.entities.Comment;
import com.example.carapp.utils.IpAddressManager;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<Comment> houses;
    private Context context;
    private OnItemClickListener listener;
    private String baseUrl;

    public CommentAdapter(List<Comment> houses, Context context) {
        this.houses = houses;
        this.context = context;
        this.baseUrl = IpAddressManager.getIpAddress(context);
    }
    public void setItems(List<Comment> data) {
        houses.clear();
        houses.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        CommentLtBinding commentLtBinding = DataBindingUtil.inflate(layoutInflater,
                R.layout.comment_lt,parent,false);
        return new CommentViewHolder(commentLtBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = houses.get(position);

        holder.bind(comment);
    }

    @Override
    public int getItemCount() {
        return houses.size();
    }

    public interface OnItemClickListener {
        void onItemClick(String item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder{
        private CommentLtBinding commentLtBinding;

        public CommentViewHolder(@NonNull CommentLtBinding commentLtBinding) {
            super(commentLtBinding.getRoot());
            this.commentLtBinding = commentLtBinding;
        }
        public void bind(Comment comment) {
           commentLtBinding.username.setText(comment.getUser_name());
           commentLtBinding.ratingBar.setRating(comment.getRating());
           commentLtBinding.comment.setText(comment.getComment());
        }
    }
}
