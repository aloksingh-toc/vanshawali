package com.meragaw.vanshawali.ui.profile;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.meragaw.vanshawali.R;
import com.meragaw.vanshawali.databinding.FragmentProfileBinding;
import com.meragaw.vanshawali.model.FamilyMember;

import java.util.ArrayList;
import java.util.List;

/**
 * "My Profile" bottom-nav tab — the current user's own account screen.
 * Distinct from {@link com.meragaw.vanshawali.ui.profile.MemberProfileFragment},
 * which displays *other* family members.
 */
public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                              @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // TODO: Load the signed-in user from ViewModel/Repository.
        FamilyMember me = getMockCurrentUser();
        setupHero(me);
        setupRows();
    }

    private void setupHero(FamilyMember me) {
        binding.tvMyName.setText(me.getFullName());
        binding.tvMyRole.setText(getString(R.string.my_profile_role));
        binding.tvMyInitial.setText(me.getInitial());

        binding.ivMyAvatar.setBackgroundTintList(
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), me.getAvatarColorRes())));

        if (me.profilePhotoUri != null) {
            Glide.with(this).load(me.profilePhotoUri).circleCrop().into(binding.ivMyAvatar);
            binding.tvMyInitial.setVisibility(View.GONE);
        }

        View.OnClickListener openEdit = v ->
                Toast.makeText(requireContext(), R.string.my_profile_edit, Toast.LENGTH_SHORT).show();
        binding.btnEditProfileIcon.setOnClickListener(openEdit);
    }

    private void setupRows() {
        List<Row> accountRows = new ArrayList<>();
        accountRows.add(new Row(R.drawable.ic_tab_profile, getString(R.string.my_profile_edit),
                () -> Toast.makeText(requireContext(), R.string.my_profile_edit, Toast.LENGTH_SHORT).show()));
        accountRows.add(new Row(R.drawable.ic_bell, getString(R.string.my_profile_notifications),
                () -> androidx.navigation.Navigation.findNavController(requireView())
                        .navigate(R.id.nav_notifications)));
        addRows(binding.containerAccountRows, accountRows);

        List<Row> familyRows = new ArrayList<>();
        familyRows.add(new Row(R.drawable.ic_add, getString(R.string.my_profile_invite),
                () -> Toast.makeText(requireContext(), R.string.my_profile_invite, Toast.LENGTH_SHORT).show()));
        familyRows.add(new Row(R.drawable.ic_tab_tree, getString(R.string.my_profile_manage_tree),
                () -> androidx.navigation.Navigation.findNavController(requireView())
                        .navigate(R.id.nav_tree)));
        addRows(binding.containerFamilyRows, familyRows);

        List<Row> aboutRows = new ArrayList<>();
        aboutRows.add(new Row(R.drawable.ic_memory, getString(R.string.my_profile_about),
                () -> Toast.makeText(requireContext(), R.string.my_profile_about, Toast.LENGTH_SHORT).show()));
        aboutRows.add(new Row(R.drawable.ic_heart, getString(R.string.my_profile_help),
                () -> Toast.makeText(requireContext(), R.string.my_profile_help, Toast.LENGTH_SHORT).show()));
        addRows(binding.containerAboutRows, aboutRows);

        binding.tvSignOut.setOnClickListener(v ->
                Toast.makeText(requireContext(), R.string.my_profile_sign_out, Toast.LENGTH_SHORT).show());
    }

    private void addRows(LinearLayout container, List<Row> rows) {
        container.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(requireContext());

        for (int i = 0; i < rows.size(); i++) {
            Row row = rows.get(i);
            View rowView = inflater.inflate(R.layout.item_settings_row, container, false);

            ImageView icon = rowView.findViewById(R.id.iv_settings_icon);
            TextView label = rowView.findViewById(R.id.tv_settings_label);
            icon.setImageResource(row.iconRes);
            label.setText(row.label);
            rowView.setOnClickListener(v -> row.action.run());

            container.addView(rowView);

            if (i < rows.size() - 1) {
                View divider = new View(requireContext());
                divider.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(1)));
                divider.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.heirloom_border));
                container.addView(divider);
            }
        }
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    private static class Row {
        final int iconRes;
        final String label;
        final Runnable action;

        Row(int iconRes, String label, Runnable action) {
            this.iconRes = iconRes;
            this.label = label;
            this.action = action;
        }
    }

    // ── Mock data (replace with ViewModel) ───────────────────────────────────
    private FamilyMember getMockCurrentUser() {
        FamilyMember me = new FamilyMember();
        me.id = "current_user";
        me.firstName = "Alok";
        me.lastName = "Singh";
        me.isCurrentUser = true;
        me.avatarColorIndex = 1;
        return me;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
