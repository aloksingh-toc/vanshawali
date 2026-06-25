package com.meragaw.vanshawali.ui.sheets;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.meragaw.vanshawali.R;
import com.meragaw.vanshawali.databinding.SheetAddMemoryBinding;
import com.meragaw.vanshawali.model.Memory;

import java.util.UUID;

/**
 * Bottom sheet for attaching a photo + caption memory to a family member.
 * Photo selection uses the system document picker, which on modern Android
 * versions requires no storage permission.
 */
public class AddMemoryBottomSheet extends BottomSheetDialogFragment {

    public interface OnMemoryAddedListener {
        void onMemoryAdded(Memory memory);
    }

    private static final String ARG_MEMBER_ID = "member_id";

    private SheetAddMemoryBinding binding;
    private OnMemoryAddedListener listener;
    private Uri pickedPhotoUri;

    private final ActivityResultLauncher<String> pickPhotoLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null && binding != null) {
                    pickedPhotoUri = uri;
                    binding.ivMemoryPhotoPreview.setVisibility(View.VISIBLE);
                    binding.layoutMemoryPhotoHint.setVisibility(View.GONE);
                    Glide.with(this).load(uri).centerCrop().into(binding.ivMemoryPhotoPreview);
                }
            });

    public static AddMemoryBottomSheet newInstance(String memberId) {
        AddMemoryBottomSheet sheet = new AddMemoryBottomSheet();
        Bundle args = new Bundle();
        args.putString(ARG_MEMBER_ID, memberId);
        sheet.setArguments(args);
        return sheet;
    }

    public void setOnMemoryAddedListener(@Nullable OnMemoryAddedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                              @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        binding = SheetAddMemoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.frameMemoryPhoto.setOnClickListener(v -> pickPhotoLauncher.launch("image/*"));
        binding.btnCancel.setOnClickListener(v -> dismiss());
        binding.btnSave.setOnClickListener(v -> trySave());
    }

    private void trySave() {
        String caption = textOf(binding.etCaption);
        if (caption.isEmpty()) {
            binding.tilCaption.setError(getString(R.string.add_memory_caption_required));
            return;
        }
        binding.tilCaption.setError(null);

        Memory memory = new Memory();
        memory.id = UUID.randomUUID().toString();
        memory.memberId = getArguments() != null ? getArguments().getString(ARG_MEMBER_ID) : null;
        memory.caption = caption;
        memory.date = emptyToNull(textOf(binding.etDate));
        memory.photoUri = pickedPhotoUri != null ? pickedPhotoUri.toString() : null;

        if (listener != null) {
            listener.onMemoryAdded(memory);
        }
        dismiss();
    }

    private String textOf(com.google.android.material.textfield.TextInputEditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }

    private String emptyToNull(String value) {
        return value.isEmpty() ? null : value;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
