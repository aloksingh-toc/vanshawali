package com.meragaw.vanshawali.ui.photos;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import com.meragaw.vanshawali.R;
import com.meragaw.vanshawali.databinding.FragmentPhotosBinding;
import com.meragaw.vanshawali.model.Memory;
import com.meragaw.vanshawali.viewmodel.FamilyViewModel;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PhotosFragment extends Fragment {

    private FragmentPhotosBinding binding;
    private FamilyViewModel viewModel;
    private PhotoGridAdapter photoAdapter;

    private final ActivityResultLauncher<String> pickPhotoLauncher =
        registerForActivityResult(new ActivityResultContracts.GetContent(), this::onPhotoPicked);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentPhotosBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(FamilyViewModel.class);

        setupAlbums();
        setupPhotoGrid();
        setupFilterChips();
        setupAddPhoto();

        viewModel.getPhotoMemories().observe(getViewLifecycleOwner(), memories -> {
            List<String> uris = new ArrayList<>();
            for (Memory memory : memories) {
                if (memory.photoUri != null) uris.add(memory.photoUri);
            }
            photoAdapter.updateData(uris);
        });
    }

    private void setupAlbums() {
        // Inflate album cards dynamically
        LinearLayout container = binding.albumsContainer;
        container.removeAllViews();

        String[][] albums = {
            {"Summer Reunions", "48"},
            {"Weddings",        "31"},
            {"The Old House",   "22"}
        };

        LayoutInflater inflater = LayoutInflater.from(requireContext());
        for (String[] album : albums) {
            View card = inflater.inflate(R.layout.item_album_card, container, false);
            android.widget.TextView tvTitle = card.findViewById(R.id.tv_album_title);
            android.widget.TextView tvCount = card.findViewById(R.id.tv_album_count);
            tvTitle.setText(album[0]);
            tvCount.setText(album[1] + " photos");
            card.setOnClickListener(v -> { /* open album */ });
            container.addView(card);
        }
    }

    private void setupPhotoGrid() {
        // 3-column grid, 6dp item spacing
        photoAdapter = new PhotoGridAdapter(new ArrayList<>());
        binding.rvPhotos.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        binding.rvPhotos.setAdapter(photoAdapter);
        binding.rvPhotos.addItemDecoration(new PhotoGridSpacingDecoration(
            (int) (6 * requireContext().getResources().getDisplayMetrics().density)));
    }

    private void setupFilterChips() {
        binding.chipGroupFilter.setOnCheckedStateChangeListener(
            (group, checkedIds) -> {
                if (checkedIds.isEmpty()) return;
                int id = checkedIds.get(0);
                if (id == R.id.chip_all) showAll();
                else if (id == R.id.chip_albums) showAlbumsView();
                else if (id == R.id.chip_people) showPeopleView();
            });
    }

    private void showAll() {
        binding.albumsScroll.setVisibility(View.VISIBLE);
        binding.rvPhotos.setVisibility(View.VISIBLE);
    }

    private void showAlbumsView() {
        binding.albumsScroll.setVisibility(View.VISIBLE);
        binding.rvPhotos.setVisibility(View.GONE);
    }

    private void showPeopleView() {
        binding.albumsScroll.setVisibility(View.GONE);
        // Show people grid — replace with real implementation
    }

    private void setupAddPhoto() {
        binding.btnAddPhoto.setOnClickListener(v -> pickPhotoLauncher.launch("image/*"));
    }

    private void onPhotoPicked(Uri uri) {
        if (uri == null) return;
        Memory memory = new Memory();
        memory.id = UUID.randomUUID().toString();
        memory.photoUri = uri.toString();
        viewModel.addMemory(memory);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
