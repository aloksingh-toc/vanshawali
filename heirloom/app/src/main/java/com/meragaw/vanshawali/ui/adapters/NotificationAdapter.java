package com.meragaw.vanshawali.ui.adapters;

import android.content.res.ColorStateList;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.meragaw.vanshawali.R;
import com.meragaw.vanshawali.model.FamilyMember;
import com.meragaw.vanshawali.model.FamilyNotification;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    // View types
    private static final int TYPE_SECTION_LABEL  = 0;
    private static final int TYPE_HIGHLIGHTED     = 1;
    private static final int TYPE_ACTIVITY        = 2;

    private List<FamilyNotification> notifications;
    private Map<String, FamilyMember> membersById = new HashMap<>();

    public NotificationAdapter(List<FamilyNotification> notifications) {
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FamilyNotification notif = notifications.get(position);

        // Section label — show "TODAY" for first item, "EARLIER" for first read item
        boolean isFirstItem = position == 0;
        boolean isFirstReadItem = !notif.isHighlighted
            && (position == 0 || notifications.get(position - 1).isHighlighted);

        if (isFirstItem) {
            holder.tvSectionLabel.setVisibility(View.VISIBLE);
            holder.tvSectionLabel.setText(R.string.notif_section_today);
        } else if (isFirstReadItem) {
            holder.tvSectionLabel.setVisibility(View.VISIBLE);
            holder.tvSectionLabel.setText(R.string.notif_section_earlier);
        } else {
            holder.tvSectionLabel.setVisibility(View.GONE);
        }

        if (notif.isHighlighted) {
            // Featured card (birthday / anniversary)
            holder.cardHighlighted.setVisibility(View.VISIBLE);
            holder.rowActivity.setVisibility(View.GONE);

            holder.tvMessageHighlighted.setText(
                Html.fromHtml(notif.message, Html.FROM_HTML_MODE_COMPACT));

            // Unread dot
            holder.unreadDot.setVisibility(notif.isRead ? View.GONE : View.VISIBLE);

            // Icon
            holder.ivNotifIcon.setImageResource(notif.getIconRes());
            holder.cardIcon.setCardBackgroundColor(ColorStateList.valueOf(
                ContextCompat.getColor(holder.itemView.getContext(),
                    notif.getIconBgColorRes())));

        } else {
            // Standard activity row
            holder.cardHighlighted.setVisibility(View.GONE);
            holder.rowActivity.setVisibility(View.VISIBLE);

            holder.tvActivityMessage.setText(
                Html.fromHtml(notif.message, Html.FROM_HTML_MODE_COMPACT));
            holder.tvActivityTime.setText(notif.getRelativeTime());

            // Actor initial + avatar color, resolved from the actor's FamilyMember
            FamilyMember actor = notif.actorId != null ? membersById.get(notif.actorId) : null;
            if (actor != null) {
                holder.tvActivityInitial.setText(actor.getInitial());
                holder.ivActivityAvatar.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(holder.itemView.getContext(), actor.getAvatarColorRes())));
            } else {
                holder.tvActivityInitial.setText("?");
            }
        }
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public void updateData(List<FamilyNotification> newData) {
        this.notifications = newData;
        notifyDataSetChanged();
    }

    public void setMembers(Map<String, FamilyMember> membersById) {
        this.membersById = membersById;
        notifyDataSetChanged();
    }

    public void markAllRead() {
        for (FamilyNotification n : notifications) n.isRead = true;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSectionLabel;
        com.google.android.material.card.MaterialCardView cardHighlighted;
        com.google.android.material.card.MaterialCardView cardIcon;
        ImageView ivNotifIcon;
        TextView tvMessageHighlighted;
        View unreadDot;
        View rowActivity;
        View ivActivityAvatar;
        TextView tvActivityInitial;
        TextView tvActivityMessage;
        TextView tvActivityTime;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSectionLabel       = itemView.findViewById(R.id.tv_section_label);
            cardHighlighted      = itemView.findViewById(R.id.card_highlighted);
            cardIcon             = itemView.findViewById(R.id.card_icon);
            ivNotifIcon          = itemView.findViewById(R.id.iv_notif_icon);
            tvMessageHighlighted = itemView.findViewById(R.id.tv_message_highlighted);
            unreadDot            = itemView.findViewById(R.id.unread_dot);
            rowActivity          = itemView.findViewById(R.id.row_activity);
            ivActivityAvatar     = itemView.findViewById(R.id.iv_activity_avatar);
            tvActivityInitial    = itemView.findViewById(R.id.tv_activity_initial);
            tvActivityMessage    = itemView.findViewById(R.id.tv_activity_message);
            tvActivityTime       = itemView.findViewById(R.id.tv_activity_time);
        }
    }
}
