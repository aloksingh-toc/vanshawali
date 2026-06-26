package com.meragaw.vanshawali.ui.profile;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import com.bumptech.glide.Glide;
import com.meragaw.vanshawali.R;
import com.meragaw.vanshawali.databinding.FragmentMemberProfileBinding;
import com.meragaw.vanshawali.model.FamilyMember;
import com.meragaw.vanshawali.model.Memory;
import com.meragaw.vanshawali.ui.photos.PhotoGridAdapter;
import com.meragaw.vanshawali.ui.sheets.AddMemoryBottomSheet;
import com.meragaw.vanshawali.ui.sheets.ComposeMessageActivity;
import com.meragaw.vanshawali.viewmodel.FamilyViewModel;
import java.util.ArrayList;
import java.util.List;

public class MemberProfileFragment extends Fragment {

    private FragmentMemberProfileBinding binding;
    private FamilyViewModel viewModel;
    private String memberId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            memberId = getArguments().getString("memberId");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMemberProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(FamilyViewModel.class);
        setupToolbar();

        viewModel.getMemberById(memberId).observe(getViewLifecycleOwner(), member -> {
            if (member == null) return;
            setupHero(member);
            setupActions(member);
            setupQuickFacts(member);
            setupBio(member);
            setupFamilyStrip(member);
        });

        viewModel.getMemoriesForMember(memberId).observe(getViewLifecycleOwner(), this::setupPhotosGrid);
    }

    private void setupHero(FamilyMember member) {
        binding.tvMemberName.setText(member.getFullName());
        binding.tvMemberDetails.setText(member.getLifespanLabel()
            + (member.birthPlace != null ? "  ·  " + member.birthPlace : ""));
        binding.tvRoleBadge.setText("GRANDMOTHER"); // Set from member.role

        // Load hero photo if available
        if (member.profilePhotoUri != null) {
            Glide.with(this)
                .load(member.profilePhotoUri)
                .centerCrop()
                .into(binding.ivProfileHero);
        } else {
            binding.ivProfileHero.setBackgroundColor(
                ContextCompat.getColor(requireContext(), member.getAvatarColorRes()));
        }
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v ->
            requireActivity().onBackPressed());

        // Optional overflow menu
        binding.toolbar.inflateMenu(R.menu.profile_overflow);
        binding.toolbar.setOnMenuItemClickListener(item -> {
            // Handle overflow actions (Edit, Share, Delete)
            return false;
        });
    }

    private void setupActions(FamilyMember member) {
        binding.btnMessage.setOnClickListener(v ->
            startActivity(ComposeMessageActivity.newIntent(
                requireContext(), member.id, member.getFullName())));

        binding.btnAddMemory.setOnClickListener(v -> {
            AddMemoryBottomSheet sheet = AddMemoryBottomSheet.newInstance(member.id);
            sheet.setOnMemoryAddedListener(memory -> viewModel.addMemory(memory));
            sheet.show(getChildFragmentManager(), "add_memory");
        });
    }

    private void setupQuickFacts(FamilyMember member) {
        // Replace static XML values with real data
        // binding.tvSpouse.setText(member.spouseName);
        // binding.tvChildren.setText(childCountLabel);
        // binding.tvGrandchildren.setText(grandchildCount);
        // binding.tvMaidenName.setText(member.maidenName != null ? member.maidenName : "—");
    }

    private void setupBio(FamilyMember member) {
        if (member.bio != null && !member.bio.isEmpty()) {
            binding.tvBio.setText("\u201C" + member.bio + "\u201D");
        } else {
            binding.tvBio.setVisibility(View.GONE);
        }
    }

    private void setupFamilyStrip(FamilyMember member) {
        LinearLayout container = binding.familyStripContainer;

        if (member.spouseId != null) {
            viewModel.getMemberById(member.spouseId).observe(getViewLifecycleOwner(),
                spouse -> bindFamilyStrip(container, member));
        }

        viewModel.getChildrenOf(member.id).observe(getViewLifecycleOwner(),
            children -> bindFamilyStrip(container, member));
    }

    private void bindFamilyStrip(LinearLayout container, FamilyMember member) {
        container.removeAllViews();
        List<FamilyMember> relatives = new ArrayList<>();

        if (member.spouseId != null) {
            FamilyMember spouse = viewModel.getMemberById(member.spouseId).getValue();
            if (spouse != null) relatives.add(roleLabeled(spouse, getString(R.string.profile_role_spouse)));
        }

        List<FamilyMember> children = viewModel.getChildrenOf(member.id).getValue();
        if (children != null) {
            for (FamilyMember child : children) {
                relatives.add(roleLabeled(child, child.bio != null ? child.bio
                    : getString(R.string.profile_role_son)));
            }
        }

        LayoutInflater inflater = LayoutInflater.from(requireContext());

        for (FamilyMember relative : relatives) {
            View strip = inflater.inflate(R.layout.item_family_strip, container, false);

            TextView tvInitial = strip.findViewById(R.id.tv_strip_initial);
            TextView tvName = strip.findViewById(R.id.tv_strip_name);
            TextView tvRole = strip.findViewById(R.id.tv_strip_role);
            View ivAvatar = strip.findViewById(R.id.iv_strip_avatar);

            tvInitial.setText(relative.getInitial());
            tvName.setText(relative.firstName);
            tvRole.setText(relative.bio);

            ivAvatar.setBackgroundTintList(ColorStateList.valueOf(
                ContextCompat.getColor(requireContext(), relative.getAvatarColorRes())));

            strip.setOnClickListener(v -> {
                Bundle args = new Bundle();
                args.putString("memberId", relative.id);
                Navigation.findNavController(v).navigate(R.id.nav_member_profile, args);
            });

            container.addView(strip);
        }
    }

    private FamilyMember roleLabeled(FamilyMember member, String role) {
        member.bio = role;
        return member;
    }

    private void setupPhotosGrid(List<Memory> memories) {
        List<String> photoUris = new ArrayList<>();
        for (Memory memory : memories) {
            if (memory.photoUri != null) photoUris.add(memory.photoUri);
        }

        PhotoGridAdapter adapter = new PhotoGridAdapter(photoUris);
        binding.rvProfilePhotos.setLayoutManager(
            new GridLayoutManager(requireContext(), 3));
        binding.rvProfilePhotos.setAdapter(adapter);

        binding.btnSeeAllPhotos.setText(
            getString(R.string.profile_see_all_photos, photoUris.size()));

        binding.btnSeeAllPhotos.setOnClickListener(v -> {
            // Navigate to PhotosFragment filtered for this member
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
