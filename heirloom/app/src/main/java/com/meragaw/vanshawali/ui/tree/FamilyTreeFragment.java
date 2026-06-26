package com.meragaw.vanshawali.ui.tree;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.meragaw.vanshawali.R;
import com.meragaw.vanshawali.databinding.FragmentFamilyTreeBinding;
import com.meragaw.vanshawali.ui.sheets.AddMemberBottomSheet;
import com.meragaw.vanshawali.viewmodel.FamilyViewModel;

public class FamilyTreeFragment extends Fragment {

    private FragmentFamilyTreeBinding binding;
    private FamilyViewModel viewModel;

    // Zoom scale bounds
    private static final float ZOOM_MIN = 0.5f;
    private static final float ZOOM_MAX = 3.0f;
    private static final float ZOOM_STEP = 0.25f;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentFamilyTreeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(FamilyViewModel.class);

        setupTreeView();
        setupZoomControls();
        setupFab();
        setupHeader();
    }

    private void setupTreeView() {
        binding.familyTreeView.setOnMemberTappedListener(memberId -> {
            Bundle args = new Bundle();
            args.putString("memberId", memberId);
            Navigation.findNavController(requireView())
                .navigate(R.id.action_tree_to_profile, args);
        });

        viewModel.getAllMembers().observe(getViewLifecycleOwner(),
            members -> binding.familyTreeView.setTreeData(members));
    }

    private void setupZoomControls() {
        binding.btnZoomIn.setOnClickListener(v -> {
            float current = binding.familyTreeView.getScale();
            binding.familyTreeView.setScale(Math.min(current + ZOOM_STEP, ZOOM_MAX), true);
        });

        binding.btnZoomOut.setOnClickListener(v -> {
            float current = binding.familyTreeView.getScale();
            binding.familyTreeView.setScale(Math.max(current - ZOOM_STEP, ZOOM_MIN), true);
        });
    }

    private void setupFab() {
        binding.fabAddMember.setOnClickListener(v -> {
            AddMemberBottomSheet sheet = AddMemberBottomSheet.newInstance();
            sheet.setOnMemberAddedListener((member, relation) -> viewModel.addMember(member, relation));
            sheet.show(getChildFragmentManager(), "add_member");
        });
    }

    private void setupHeader() {
        // Update subtitle with real family name + generation count
        // String subtitle = getString(R.string.tree_subtitle, familyName, generationCount);
        // binding.tvTreeSubtitle.setText(subtitle);

        binding.btnTreeSearch.setOnClickListener(v ->
            Navigation.findNavController(v).navigate(R.id.nav_search));

        binding.btnTreeFilter.setOnClickListener(v -> {
            // Show tree filter options (by generation, by surname, etc.)
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
