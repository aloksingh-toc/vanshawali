package com.meragaw.vanshawali.ui.photos;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.meragaw.vanshawali.R;
import java.util.List;

public class PhotoGridAdapter extends RecyclerView.Adapter<PhotoGridAdapter.ViewHolder> {

    public interface OnPhotoClickListener {
        void onPhotoClick(String uri, int position);
    }

    private List<String> photoUris;
    private OnPhotoClickListener listener;

    public PhotoGridAdapter(List<String> photoUris) {
        this.photoUris = photoUris;
    }

    public void setOnPhotoClickListener(OnPhotoClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_photo_grid, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String uri = photoUris.get(position);

        Glide.with(holder.ivPhoto.getContext())
            .load(uri)
            .centerCrop()
            .placeholder(R.drawable.bg_photo_placeholder)
            .into(holder.ivPhoto);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onPhotoClick(uri, position);
        });
    }

    @Override
    public int getItemCount() {
        return photoUris.size();
    }

    public void updateData(List<String> newUris) {
        this.photoUris = newUris;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPhoto;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.iv_photo);
        }
    }
}


// ─── PhotoGridSpacingDecoration.java ─────────────────────────────────────────
// Place in the same package: com.meragaw.vanshawali.ui.photos

class PhotoGridSpacingDecoration extends RecyclerView.ItemDecoration {

    private final int spacing;

    PhotoGridSpacingDecoration(int spacingPx) {
        this.spacing = spacingPx;
    }

    @Override
    public void getItemOffsets(@NonNull android.graphics.Rect outRect,
                               @NonNull View view,
                               @NonNull RecyclerView parent,
                               @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int column = position % 3;

        outRect.left = column == 0 ? 0 : spacing / 2;
        outRect.right = column == 2 ? 0 : spacing / 2;
        outRect.top = position < 3 ? 0 : spacing;
        outRect.bottom = 0;
    }
}


// ─── NotificationAdapter.java ─────────────────────────────────────────────────
// Place in: com.meragaw.vanshawali.ui.adapters

class NotificationAdapterHelper {
    // See NotificationAdapter.java in com.meragaw.vanshawali.ui.adapters package
}
