package com.hcmus.picbox.activities;

import static com.hcmus.picbox.utils.SharedPreferencesUtils.KEY_FOOD_QUESTION;
import static com.hcmus.picbox.utils.SharedPreferencesUtils.KEY_PASSWORD;
import static com.hcmus.picbox.utils.SharedPreferencesUtils.KEY_PET_QUESTION;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
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
    Context context;

    EditText petQuestionEditText;
    EditText foodQuestionEditText;
    Button confirmRecoverQuestionButton;
    TextView notifyRecoverQuestionTextView;


    private final String RESPONSE_KEY = "accept_recover_question";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        deClareUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        notifyRecoverQuestionTextView.setText("");
    }

    private void deClareUI() {
        context = getBaseContext();
        petQuestionEditText = findViewById(R.id.petQuestionEditText);
        foodQuestionEditText = findViewById(R.id.foodQuestionEditText);
        notifyRecoverQuestionTextView = findViewById(R.id.notifyRecoverQuestionTextView);

        confirmRecoverQuestionButton = findViewById(R.id.confirmRecoverQuestionButton);
        confirmRecoverQuestionButton.setOnClickListener(v -> {
            String petFromSharedPref = SharedPreferencesUtils.getStringData(context, KEY_PET_QUESTION);
            String foodFromSharedPref = SharedPreferencesUtils.getStringData(context, KEY_FOOD_QUESTION);

            if (Objects.equals(petFromSharedPref, String.valueOf(petQuestionEditText.getText())) &&
                    Objects.equals(foodFromSharedPref, String.valueOf(foodQuestionEditText.getText()))) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra(RESPONSE_KEY, true);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            } else {
                Intent returnIntent = new Intent();
                returnIntent.putExtra(RESPONSE_KEY, false);
                setResult(Activity.RESULT_OK, returnIntent);
                notifyRecoverQuestionTextView.setText(getResources().getString(R.string.wrong_recover_question_answer));
            }
        });
    }
}
