package com.hcmus.picbox.activities;


import static com.hcmus.picbox.utils.SharedPreferencesUtils.KEY_PASSWORD;
import static com.hcmus.picbox.utils.SharedPreferencesUtils.getSharedPrefEditor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.chaos.view.PinView;
import com.hcmus.picbox.R;
import com.hcmus.picbox.utils.SharedPreferencesUtils;

import java.util.Objects;

public class ImagePasswordActivity extends AppCompatActivity {
    private Context context;
    private TextView notifyTextView;
    private PinView passwordImagePinView;
    private Button submitPasswordButton;
    private @Nullable String pinViewValue;
    private TextView forgotPasswordTextView;
    private final int FORGOT_PASSWORD_ACTIVITY_REQUEST_CODE = 19209;
    private final String RESPONSE_KEY = "accept_password";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_password);
        declareUI();
        initUI();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FORGOT_PASSWORD_ACTIVITY_REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK){
                Boolean result = data.getBooleanExtra("accept_recover_question", false);
                if (result){
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(RESPONSE_KEY, true);
                    setResult(Activity.RESULT_OK, returnIntent);
                    SharedPreferencesUtils.removeData(context, KEY_PASSWORD);
                    finish();
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // Write your code if there's no result
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initUI();
    }

    private void initUI() {
        notifyTextView.setText("");
    }

    private void declareUI() {
        context = getBaseContext();
        notifyTextView = findViewById(R.id.notifyTextView);
        passwordImagePinView = findViewById(R.id.passwordImagePinView);
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

        submitPasswordButton = findViewById(R.id.submitPasswordButton);
        submitPasswordButton.setOnClickListener(view -> {
            String passwordFromSharedReference = SharedPreferencesUtils.getStringData(context, KEY_PASSWORD);
            if (pinViewValue != null && pinViewValue.length() == 5 &&
                    Objects.equals(pinViewValue, passwordFromSharedReference)) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra(RESPONSE_KEY, true);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            } else {
                String error = pinViewValue == null || pinViewValue.length() != 5
                        ? getResources().getString(R.string.password_must_have_5_character)
                        : getResources().getString(R.string.wrong_password);

                notifyTextView.setText(error);

                Intent returnIntent = new Intent();
                returnIntent.putExtra(RESPONSE_KEY, false);
                setResult(Activity.RESULT_OK, returnIntent);
            }
        });

        forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView);
        forgotPasswordTextView.setOnClickListener(v->{
            Intent intent = new Intent(this, ForgotPasswordActivity.class);
            startActivityForResult(intent, FORGOT_PASSWORD_ACTIVITY_REQUEST_CODE);
        });
    }
}
