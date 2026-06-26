package com.meragaw.vanshawali.ui.adapters;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.meragaw.vanshawali.R;
import com.meragaw.vanshawali.model.FamilyMember;
import java.util.List;

public class MemberRowAdapter extends RecyclerView.Adapter<MemberRowAdapter.ViewHolder> {

    public interface OnMemberClickListener {
        void onMemberClick(String memberId);
    }

    private List<FamilyMember> members;
    private final OnMemberClickListener listener;

    public MemberRowAdapter(List<FamilyMember> members, OnMemberClickListener listener) {
        this.members = members;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_member_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FamilyMember member = members.get(position);

        holder.tvName.setText(member.getFullName());
        holder.tvRelationBirth.setText(
            (member.bio != null ? member.bio : "") +
            " · " + member.getLifespanLabel());
        holder.tvInitial.setText(member.getInitial());

        // Avatar background color
        int colorRes = member.getAvatarColorRes();
        holder.ivAvatar.setBackgroundTintList(ColorStateList.valueOf(
            ContextCompat.getColor(holder.itemView.getContext(), colorRes)));

        // If member has a photo, load with Glide and hide initial
        // Glide.with(holder.itemView.getContext())
        //     .load(member.profilePhotoUri)
        //     .circleCrop()
        //     .into((ImageView) holder.ivAvatar);

        // Hide divider on last item
        holder.divider.setVisibility(
            position == members.size() - 1 ? View.GONE : View.VISIBLE);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onMemberClick(member.id);
        });
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    public void updateData(List<FamilyMember> newMembers) {
        this.members = newMembers;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View ivAvatar;
        TextView tvInitial;
        TextView tvName;
        TextView tvRelationBirth;
        View divider;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_avatar);
            tvInitial = itemView.findViewById(R.id.tv_initial);
            tvName = itemView.findViewById(R.id.tv_name);
            tvRelationBirth = itemView.findViewById(R.id.tv_relation_birth);
            divider = itemView.findViewById(R.id.divider);
        }
    }
}
