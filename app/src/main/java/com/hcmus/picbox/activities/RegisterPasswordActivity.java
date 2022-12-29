package com.hcmus.picbox.activities;

import static com.hcmus.picbox.utils.SharedPreferencesUtils.KEY_FOOD_QUESTION;
import static com.hcmus.picbox.utils.SharedPreferencesUtils.KEY_PASSWORD;
import static com.hcmus.picbox.utils.SharedPreferencesUtils.KEY_PET_QUESTION;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.chaos.view.PinView;
import com.hcmus.picbox.R;
import com.hcmus.picbox.utils.SharedPreferencesUtils;

public class RegisterPasswordActivity extends AppCompatActivity {
    Context context;

    TextView titleRegisterPasswordTextView;
    PinView passwordPinView;
    Button registerPasswordButton;
    TextView notifyTextView;
    LinearLayout passwordPinViewLayout;
    LinearLayout recoverQuestionLayout;
    private String pinViewValue;
    EditText petQuestionEditText;
    EditText foodQuestionEditText;

    private final int PASSWORD_STEP = 1;
    private final int RECOVER_QUESTION_STEP = 2;
    private int step;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_password);
        declareUI();
        initRegisterPasswordUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initRegisterPasswordUI();
    }

    private void initRegisterPasswordUI(){
        passwordPinViewLayout.setVisibility(View.VISIBLE);
        recoverQuestionLayout.setVisibility(View.GONE);

        titleRegisterPasswordTextView.setText(getResources().getString(R.string.register_password));
        notifyTextView.setText("");
        step = PASSWORD_STEP;
    }

    private void initRecoverQuestionUI(){
        passwordPinViewLayout.setVisibility(View.GONE);
        recoverQuestionLayout.setVisibility(View.VISIBLE);

        titleRegisterPasswordTextView.setText(getResources().getString(R.string.recover_question));
        notifyTextView.setText("");
        step = RECOVER_QUESTION_STEP;
    }

    private void declareUI() {
        context = getBaseContext();
        passwordPinViewLayout = findViewById(R.id.passwordPinViewLayout);
        recoverQuestionLayout = findViewById(R.id.recoverQuestionLayout);
        titleRegisterPasswordTextView = findViewById(R.id.titleRegisterPasswordTextView);
        petQuestionEditText = findViewById(R.id.petQuestionEditText);
        foodQuestionEditText = findViewById(R.id.foodQuestionEditText);
        passwordPinView = findViewById(R.id.passwordPinView);
        passwordPinView.requestFocus();
        notifyTextView = findViewById(R.id.notifyTextView);

        passwordPinView.addTextChangedListener(new TextWatcher() {
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

        registerPasswordButton = findViewById(R.id.registerPasswordButton);
        registerPasswordButton.setOnClickListener(v -> {
            switch (step) {
                case PASSWORD_STEP:
                    if (pinViewValue != null && pinViewValue.length() == 5) {
                        SharedPreferencesUtils.saveData(context, KEY_PASSWORD, pinViewValue);
                        initRecoverQuestionUI();
                    } else {
                        notifyTextView.setText(getResources().getString(R.string.password_must_have_5_character));
                    }
                    break;
                case RECOVER_QUESTION_STEP:
                    if (String.valueOf(petQuestionEditText.getText()).trim().length() > 0
                            && String.valueOf(foodQuestionEditText.getText()).trim().length() > 0){
                        SharedPreferencesUtils.saveData(context, KEY_PET_QUESTION, String.valueOf(petQuestionEditText.getText()).trim());
                        SharedPreferencesUtils.saveData(context, KEY_FOOD_QUESTION, String.valueOf(foodQuestionEditText.getText()).trim());
                        finish();
                    } else {
                        notifyTextView.setText(getResources().getString(R.string.answer_can_not_empty));
                    }
                    break;
            }
        });
    }
}
