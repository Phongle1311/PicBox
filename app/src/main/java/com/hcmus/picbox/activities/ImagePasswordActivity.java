package com.hcmus.picbox.activities;


import static com.hcmus.picbox.activities.ForgotPasswordActivity.KEY_ACCEPT_RECOVER_QUESTION;
import static com.hcmus.picbox.utils.SharedPreferencesUtils.KEY_PASSWORD;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.chaos.view.PinView;
import com.hcmus.picbox.R;
import com.hcmus.picbox.utils.SharedPreferencesUtils;

import java.util.Objects;

public class ImagePasswordActivity extends AppCompatActivity {

    public static final String KEY_ENTER_PASSWORD_RESPONSE = "accept_password";
    private final ActivityResultLauncher<Intent> forgotPasswordActivityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data == null) return;
                    if (data.getBooleanExtra(KEY_ACCEPT_RECOVER_QUESTION, false)) {
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra(KEY_ENTER_PASSWORD_RESPONSE, true);
                        setResult(Activity.RESULT_OK, returnIntent);
                        SharedPreferencesUtils.removeData(ImagePasswordActivity.this, KEY_PASSWORD);
                        finish();
                    }
                }
            });
    private TextView notifyTextView;
    private @Nullable
    String pinViewValue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_password);

        notifyTextView = findViewById(R.id.notifyTextView);
        notifyTextView.setText("");

        setListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        notifyTextView.setText("");
    }

    private void setListener() {
        // enter PIN listener
        PinView passwordImagePinView = findViewById(R.id.passwordImagePinView);
        passwordImagePinView.requestFocus();
        passwordImagePinView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                pinViewValue = String.valueOf(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        // Submit password: true and false
        findViewById(R.id.submitPasswordButton).setOnClickListener(view -> {
            String passwordFromSharedReference = SharedPreferencesUtils.getStringData(ImagePasswordActivity.this, KEY_PASSWORD);
            if (pinViewValue != null && pinViewValue.length() == 5 &&
                    Objects.equals(pinViewValue, passwordFromSharedReference)) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra(KEY_ENTER_PASSWORD_RESPONSE, true);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            } else {
                String error = pinViewValue == null || pinViewValue.length() != 5
                        ? getResources().getString(R.string.password_must_have_5_character)
                        : getResources().getString(R.string.wrong_password);

                notifyTextView.setText(error);
            }
        });

        // Forget password
        findViewById(R.id.forgotPasswordTextView).setOnClickListener(v -> {
            Intent intent = new Intent(this, ForgotPasswordActivity.class);
            forgotPasswordActivityResultLauncher.launch(intent);
        });
    }
}
