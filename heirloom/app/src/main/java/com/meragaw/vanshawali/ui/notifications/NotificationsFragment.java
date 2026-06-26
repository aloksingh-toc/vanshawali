package com.meragaw.vanshawali.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.meragaw.vanshawali.databinding.FragmentNotificationsBinding;
import com.meragaw.vanshawali.model.FamilyMember;
import com.meragaw.vanshawali.ui.adapters.NotificationAdapter;
import com.meragaw.vanshawali.viewmodel.FamilyViewModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private FamilyViewModel viewModel;
    private NotificationAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(FamilyViewModel.class);

        setupHeader();
        setupRecyclerView();

        viewModel.getAllNotifications().observe(getViewLifecycleOwner(), adapter::updateData);

        viewModel.getAllMembers().observe(getViewLifecycleOwner(), members -> {
            Map<String, FamilyMember> membersById = new HashMap<>();
            if (members != null) {
                for (FamilyMember m : members) membersById.put(m.id, m);
            }
            adapter.setMembers(membersById);
        });
    }

    private void setupHeader() {
        binding.btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        binding.btnReadAll.setOnClickListener(v -> {
            adapter.markAllRead();
            viewModel.markAllNotificationsRead();
        });
    }

    private void setupRecyclerView() {
        adapter = new NotificationAdapter(new ArrayList<>());
        binding.rvNotifications.setLayoutManager(
            new LinearLayoutManager(requireContext()));
        binding.rvNotifications.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
