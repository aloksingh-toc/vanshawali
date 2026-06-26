package com.meragaw.vanshawali.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.meragaw.vanshawali.R;
import com.meragaw.vanshawali.databinding.FragmentHomeBinding;
import com.meragaw.vanshawali.model.FamilyMember;
import com.meragaw.vanshawali.model.Memory;
import com.meragaw.vanshawali.ui.sheets.AddMemberBottomSheet;
import com.meragaw.vanshawali.viewmodel.FamilyViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private FamilyViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(FamilyViewModel.class);

        setupClickListeners();
        observeData();
    }

    private void observeData() {
        viewModel.getAllMembers().observe(getViewLifecycleOwner(), members -> {
            List<FamilyMember> currentMembers = members != null ? members : new ArrayList<>();
            setupGreeting(currentMembers);
            setupBirthdayCard(currentMembers);
            setupTreePreview(currentMembers);
            setupRecentlyAdded(currentMembers);
        });

        viewModel.getMemberCount().observe(getViewLifecycleOwner(), count ->
            binding.tvStatMembers.setText(String.valueOf(count != null ? count : 0)));

        viewModel.getMaxGeneration().observe(getViewLifecycleOwner(), gen ->
            binding.tvStatGenerations.setText(String.valueOf(gen != null ? gen : 0)));

        viewModel.getPhotoCount().observe(getViewLifecycleOwner(), count ->
            binding.tvStatPhotos.setText(String.valueOf(count != null ? count : 0)));

        viewModel.getPhotoMemories().observe(getViewLifecycleOwner(), this::setupOnThisDay);
    }

    private void setupGreeting(List<FamilyMember> members) {
        FamilyMember currentUser = findCurrentUser(members);
        String userName = currentUser != null && currentUser.firstName != null
            ? currentUser.firstName.toUpperCase() : "";
        binding.tvGreeting.setText(getString(R.string.home_greeting_prefix) + " " + userName);

        String surname = currentUser != null && currentUser.lastName != null
            ? currentUser.lastName : "Hartwell";
        binding.tvFamilyName.setText(getString(R.string.home_family_title, surname));
    }

    private void setupBirthdayCard(List<FamilyMember> members) {
        FamilyMember soonest = null;
        int soonestDays = Integer.MAX_VALUE;

        for (FamilyMember member : members) {
            if (member.deathDate != null || member.birthDate == null) continue;
            int days = daysUntilNextBirthday(member.birthDate);
            if (days < soonestDays) {
                soonestDays = days;
                soonest = member;
            }
        }

        if (soonest == null) {
            binding.cardBirthday.setVisibility(View.GONE);
            return;
        }

        binding.cardBirthday.setVisibility(View.VISIBLE);
        binding.tvBirthdayName.setText(soonest.firstName);
        binding.tvBirthdayInitial.setText(soonest.getInitial());

        int age = ageAtNextBirthday(soonest.birthDate);
        String when = soonestDays == 0 ? "today" : soonestDays == 1 ? "tomorrow" : "in " + soonestDays + " days";
        binding.tvBirthdaySubtitle.setText("turns " + age + " · " + when);

        if (soonest.profilePhotoUri != null) {
            Glide.with(this).load(soonest.profilePhotoUri).circleCrop().into(binding.ivBirthdayAvatar);
            binding.tvBirthdayInitial.setVisibility(View.GONE);
        } else {
            binding.tvBirthdayInitial.setVisibility(View.VISIBLE);
        }
    }

    /** Days from today until the next occurrence of the "MM-DD" portion of birthDate. */
    private int daysUntilNextBirthday(String birthDate) {
        Calendar birth = Calendar.getInstance();
        birth.setTimeInMillis(parseDate(birthDate));

        Calendar today = Calendar.getInstance();
        clearTime(today);

        Calendar next = Calendar.getInstance();
        clearTime(next);
        next.set(Calendar.MONTH, birth.get(Calendar.MONTH));
        next.set(Calendar.DAY_OF_MONTH, birth.get(Calendar.DAY_OF_MONTH));
        if (next.before(today)) {
            next.add(Calendar.YEAR, 1);
        }

        long diffMillis = next.getTimeInMillis() - today.getTimeInMillis();
        return (int) (diffMillis / (24 * 60 * 60 * 1000L));
    }

    private int ageAtNextBirthday(String birthDate) {
        Calendar birth = Calendar.getInstance();
        birth.setTimeInMillis(parseDate(birthDate));

        Calendar today = Calendar.getInstance();
        int years = today.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
        boolean alreadyHadBirthdayThisYear = today.get(Calendar.MONTH) > birth.get(Calendar.MONTH)
            || (today.get(Calendar.MONTH) == birth.get(Calendar.MONTH)
                && today.get(Calendar.DAY_OF_MONTH) > birth.get(Calendar.DAY_OF_MONTH));
        return alreadyHadBirthdayThisYear ? years + 1 : years;
    }

    private long parseDate(String yyyyMmDd) {
        String[] parts = yyyyMmDd.split("-");
        Calendar c = Calendar.getInstance();
        c.set(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]) - 1, Integer.parseInt(parts[2]));
        return c.getTimeInMillis();
    }

    private void clearTime(Calendar c) {
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
    }

    private void setupTreePreview(List<FamilyMember> members) {
        binding.miniTreeView.setTreeData(members);
    }

    private void setupRecentlyAdded(List<FamilyMember> members) {
        List<FamilyMember> recentMembers = new ArrayList<>(members);
        recentMembers.sort((a, b) -> Long.compare(b.createdAt, a.createdAt));
        if (recentMembers.size() > 5) {
            recentMembers = recentMembers.subList(0, 5);
        }

        LinearLayout container = binding.recentlyAddedContainer;
        container.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(requireContext());

        for (FamilyMember member : recentMembers) {
            View card = inflater.inflate(R.layout.item_member_card, container, false);
            bindMemberCard(card, member);
            card.setOnClickListener(v -> navigateToProfile(v, member.id));
            container.addView(card);
        }

        View addCard = inflater.inflate(R.layout.item_member_card_add, container, false);
        addCard.setOnClickListener(v -> openAddMemberSheet());
        container.addView(addCard);
    }

    private void bindMemberCard(View card, FamilyMember member) {
        TextView tvName = card.findViewById(R.id.tv_member_name);
        TextView tvRelation = card.findViewById(R.id.tv_member_relation);
        TextView tvInitial = card.findViewById(R.id.tv_member_initial);
        View ivAvatar = card.findViewById(R.id.iv_member_avatar);

        tvName.setText(member.firstName);
        tvRelation.setText(member.bio != null ? member.bio : "Member");
        tvInitial.setText(member.getInitial());

        ivAvatar.setBackgroundTintList(
            android.content.res.ColorStateList.valueOf(
                androidx.core.content.ContextCompat.getColor(requireContext(),
                    member.getAvatarColorRes())));
    }

    private void setupOnThisDay(List<Memory> memories) {
        if (memories == null || memories.isEmpty()) return;

        Memory first = memories.get(0);
        Memory second = memories.size() > 1 ? memories.get(1) : null;
        Memory third = memories.size() > 2 ? memories.get(2) : null;

        Glide.with(this).load(first.photoUri).centerCrop().into(binding.ivMemoryMain);
        if (first.date != null && first.date.length() >= 4) {
            binding.tvMemoryYear.setText(first.date.substring(0, 4));
        }
        if (second != null) {
            Glide.with(this).load(second.photoUri).centerCrop().into(binding.ivMemoryTop);
        }
        if (third != null) {
            Glide.with(this).load(third.photoUri).centerCrop().into(binding.ivMemoryBottom);
        }
    }

    private void setupClickListeners() {
        binding.btnNotifications.setOnClickListener(v ->
            Navigation.findNavController(v)
                .navigate(R.id.action_home_to_notifications));

        binding.cardTreePreview.setOnClickListener(v ->
            Navigation.findNavController(v)
                .navigate(R.id.action_home_to_tree));

        binding.btnOpenTree.setOnClickListener(v ->
            Navigation.findNavController(v)
                .navigate(R.id.action_home_to_tree));

        binding.btnBirthdayWish.setOnClickListener(v ->
            startActivity(com.meragaw.vanshawali.ui.sheets.ComposeMessageActivity.newIntent(
                requireContext(), null, binding.tvBirthdayName.getText().toString())));
    }

    private void navigateToProfile(View view, String memberId) {
        Bundle args = new Bundle();
        args.putString("memberId", memberId);
        Navigation.findNavController(view)
            .navigate(R.id.nav_member_profile, args);
    }

    private void openAddMemberSheet() {
        AddMemberBottomSheet sheet = AddMemberBottomSheet.newInstance();
        sheet.setOnMemberAddedListener((member, relation) -> viewModel.addMember(member, relation));
        sheet.show(getChildFragmentManager(), "add_member");
    }

    private FamilyMember findCurrentUser(List<FamilyMember> members) {
        for (FamilyMember m : members) {
            if (m.isCurrentUser) return m;
        }
        return null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
