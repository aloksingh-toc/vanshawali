package com.meragaw.vanshawali.ui.sheets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.meragaw.vanshawali.R;
import com.meragaw.vanshawali.databinding.SheetAddMemberBinding;
import com.meragaw.vanshawali.model.FamilyMember;

import java.util.UUID;

/**
 * Bottom sheet for adding a new family member. The caller decides how the
 * resulting {@link FamilyMember} is persisted; this sheet only collects and
 * validates input.
 */
public class AddMemberBottomSheet extends BottomSheetDialogFragment {

    public interface OnMemberAddedListener {
        void onMemberAdded(FamilyMember member, String relation);
    }

    private SheetAddMemberBinding binding;
    private OnMemberAddedListener listener;

    public static AddMemberBottomSheet newInstance() {
        return new AddMemberBottomSheet();
    }

    public void setOnMemberAddedListener(@Nullable OnMemberAddedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                              @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        binding = SheetAddMemberBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnCancel.setOnClickListener(v -> dismiss());
        binding.btnSave.setOnClickListener(v -> trySave());
    }

    private void trySave() {
        String firstName = textOf(binding.etFirstName);
        if (firstName.isEmpty()) {
            binding.tilFirstName.setError(getString(R.string.add_member_first_name_required));
            return;
        }
        binding.tilFirstName.setError(null);

        FamilyMember member = new FamilyMember();
        member.id = UUID.randomUUID().toString();
        member.firstName = firstName;
        member.lastName = textOf(binding.etLastName);
        member.birthDate = emptyToNull(textOf(binding.etBirthDate));
        member.birthPlace = emptyToNull(textOf(binding.etBirthPlace));
        member.bio = emptyToNull(textOf(binding.etBio));

        String relation = relationOf(binding.chipGroupRelation.getCheckedChipId());

        if (listener != null) {
            listener.onMemberAdded(member, relation);
        }
        dismiss();
    }

    private String relationOf(int checkedChipId) {
        if (checkedChipId == binding.chipRelationParent.getId()) return "parent";
        if (checkedChipId == binding.chipRelationSpouse.getId()) return "spouse";
        if (checkedChipId == binding.chipRelationSibling.getId()) return "sibling";
        return "child";
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
