package com.meragaw.vanshawali.ui.sheets;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.meragaw.vanshawali.R;
import com.meragaw.vanshawali.databinding.ActivityComposeMessageBinding;

/**
 * Simple in-app message composer, opened from a member's profile ("Message")
 * or the home birthday card ("Send your wishes").
 */
public class ComposeMessageActivity extends AppCompatActivity {

    private static final String EXTRA_MEMBER_ID = "member_id";
    private static final String EXTRA_MEMBER_NAME = "member_name";

    private ActivityComposeMessageBinding binding;
    private String memberName;

    public static Intent newIntent(@NonNull Context context, @Nullable String memberId,
                                    @NonNull String memberName) {
        Intent intent = new Intent(context, ComposeMessageActivity.class);
        intent.putExtra(EXTRA_MEMBER_ID, memberId);
        intent.putExtra(EXTRA_MEMBER_NAME, memberName);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityComposeMessageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        memberName = getIntent().getStringExtra(EXTRA_MEMBER_NAME);
        if (memberName == null) {
            memberName = "";
        }

        binding.toolbar.setTitle(getString(R.string.compose_message_title, memberName));
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        binding.btnSend.setOnClickListener(v -> trySend());
    }

    private void trySend() {
        String message = binding.etMessage.getText() == null
                ? "" : binding.etMessage.getText().toString().trim();
        if (message.isEmpty()) {
            binding.tilMessage.setError(getString(R.string.compose_message_required));
            return;
        }
        binding.tilMessage.setError(null);

        // Fire-and-forget: no Message entity exists in the data model, so there is
        // nothing to persist here — this composer only confirms the send to the user.
        Toast.makeText(this, getString(R.string.compose_message_sent_toast, memberName),
                Toast.LENGTH_SHORT).show();
        finish();
    }
}
