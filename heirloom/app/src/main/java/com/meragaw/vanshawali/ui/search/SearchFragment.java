package com.meragaw.vanshawali.ui.search;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.meragaw.vanshawali.R;
import com.meragaw.vanshawali.databinding.FragmentSearchBinding;
import com.meragaw.vanshawali.model.FamilyMember;
import com.meragaw.vanshawali.ui.adapters.MemberRowAdapter;
import com.meragaw.vanshawali.viewmodel.FamilyViewModel;
import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;
    private FamilyViewModel viewModel;
    private MemberRowAdapter adapter;
    private List<FamilyMember> allMembers = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(FamilyViewModel.class);

        setupRecyclerView();
        setupSearchField();
        setupFilterChips();

        viewModel.getAllMembers().observe(getViewLifecycleOwner(), members -> {
            allMembers = members != null ? members : new ArrayList<>();
            String query = binding.etSearch.getText() != null
                ? binding.etSearch.getText().toString().trim() : "";
            filterMembers(query);
        });
    }

    private void setupRecyclerView() {
        adapter = new MemberRowAdapter(new ArrayList<>(), memberId -> {
            Bundle args = new Bundle();
            args.putString("memberId", memberId);
            Navigation.findNavController(requireView())
                .navigate(R.id.action_search_to_profile, args);
        });
        binding.rvSearchResults.setLayoutManager(
            new LinearLayoutManager(requireContext()));
        binding.rvSearchResults.setAdapter(adapter);
    }

    private void setupSearchField() {
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterMembers(s.toString().trim());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void setupFilterChips() {
        binding.chipGroupSearchFilter.setOnCheckedStateChangeListener(
            (group, checkedIds) -> {
                String query = binding.etSearch.getText() != null
                    ? binding.etSearch.getText().toString().trim() : "";
                filterMembers(query);
            });
    }

    private void filterMembers(String query) {
        List<FamilyMember> filtered = new ArrayList<>();

        for (FamilyMember m : allMembers) {
            boolean matchesQuery = query.isEmpty()
                || m.getFullName().toLowerCase().contains(query.toLowerCase())
                || (m.lastName != null && m.lastName.toLowerCase().contains(query.toLowerCase()));

            boolean matchesFilter = true;
            int checkedId = binding.chipGroupSearchFilter.getCheckedChipId();
            if (checkedId == R.id.chip_filter_living) {
                matchesFilter = m.deathDate == null;
            } else if (checkedId == R.id.chip_filter_gen2) {
                matchesFilter = m.generation == 2;
            } else if (checkedId == R.id.chip_filter_surname) {
                matchesFilter = "Hartwell".equalsIgnoreCase(m.lastName);
            }

            if (matchesQuery && matchesFilter) filtered.add(m);
        }

        showResults(filtered);
    }

    private void showResults(List<FamilyMember> results) {
        adapter.updateData(results);

        if (results.isEmpty()) {
            binding.rvSearchResults.setVisibility(View.GONE);
            binding.emptyState.setVisibility(View.VISIBLE);
            binding.tvResultsCount.setVisibility(View.GONE);
        } else {
            binding.rvSearchResults.setVisibility(View.VISIBLE);
            binding.emptyState.setVisibility(View.GONE);
            binding.tvResultsCount.setVisibility(View.VISIBLE);
            binding.tvResultsCount.setText(
                getString(R.string.search_results_count, results.size()));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
