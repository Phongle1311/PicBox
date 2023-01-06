package com.hcmus.picbox.activities;

import static com.hcmus.picbox.utils.SharedPreferencesUtils.KEY_FOOD_QUESTION;
import static com.hcmus.picbox.utils.SharedPreferencesUtils.KEY_PET_QUESTION;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hcmus.picbox.R;
import com.hcmus.picbox.utils.SharedPreferencesUtils;

import java.util.Objects;

public class ForgotPasswordActivity extends AppCompatActivity {

    public static final String KEY_ACCEPT_RECOVER_QUESTION = "accept_recover_question";
    private EditText petQuestionEditText;
    private EditText foodQuestionEditText;
    private TextView notifyRecoverQuestionTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        bindUI();
        setListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        notifyRecoverQuestionTextView.setText("");
    }

    private void bindUI() {
        notifyRecoverQuestionTextView = findViewById(R.id.notifyRecoverQuestionTextView);
        petQuestionEditText = findViewById(R.id.petQuestionEditText);
        foodQuestionEditText = findViewById(R.id.foodQuestionEditText);
    }

    private void setListener() {
        Button confirmRecoverQuestionButton = findViewById(R.id.confirmRecoverQuestionButton);
        confirmRecoverQuestionButton.setOnClickListener(v -> {
            String petFromSharedPref = SharedPreferencesUtils.getStringData(
                    ForgotPasswordActivity.this, KEY_PET_QUESTION);
            String foodFromSharedPref = SharedPreferencesUtils.getStringData(
                    ForgotPasswordActivity.this, KEY_FOOD_QUESTION);

            if (Objects.equals(petFromSharedPref, String.valueOf(petQuestionEditText.getText())) &&
                    Objects.equals(foodFromSharedPref, String.valueOf(foodQuestionEditText.getText()))) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra(KEY_ACCEPT_RECOVER_QUESTION, true);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            } else {
                notifyRecoverQuestionTextView.setText(getResources().getString(R.string.wrong_recover_question_answer));
            }
        });
    }
}
