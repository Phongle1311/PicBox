package com.hcmus.picbox.components;

import static com.hcmus.picbox.adapters.MediaAdapter.LAYOUT_MODE_1;
import static com.hcmus.picbox.adapters.MediaAdapter.LAYOUT_MODE_2;
import static com.hcmus.picbox.adapters.MediaAdapter.LAYOUT_MODE_3;
import static com.hcmus.picbox.adapters.MediaAdapter.LAYOUT_MODE_4;
import static com.hcmus.picbox.utils.SharedPreferencesUtils.KEY_LAYOUT_MODE;

import android.app.Dialog;
import android.content.Context;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;

import com.hcmus.picbox.R;
import com.hcmus.picbox.utils.SharedPreferencesUtils;

/**
 * Create on 01/01/2023 by Phong Le <br/>
 * This dialog is shown when choose layout mode
 */
public class ChooseLayoutModeDialog extends Dialog {

    private int option = 1;

    public ChooseLayoutModeDialog(@NonNull Context context, int currentOption, IChooseLayoutModeDialogCallback callback) {
        super(context);
        setContentView(R.layout.dialog_choose_layout_mode);

        RadioGroup rg = findViewById(R.id.radio_group);

        findViewById(R.id.btn_cancel).setOnClickListener(view -> dismiss());

        switch (currentOption) {
            case LAYOUT_MODE_2:
                ((RadioButton) findViewById(R.id.rb_list)).setChecked(true);
                break;
            case LAYOUT_MODE_3:
                ((RadioButton) findViewById(R.id.rb_staggered)).setChecked(true);
                break;
            case LAYOUT_MODE_4:
                ((RadioButton) findViewById(R.id.rb_spanned)).setChecked(true);
                break;
            default:
                ((RadioButton) findViewById(R.id.rb_grid)).setChecked(true);
                break;
        }

        findViewById(R.id.btn_confirm).setOnClickListener(view -> {
            // save to shared-pref and do callback
            int checkedRadioButtonId = rg.getCheckedRadioButtonId();
            if (checkedRadioButtonId == R.id.rb_grid) {
                option = LAYOUT_MODE_1;
            } else if (checkedRadioButtonId == R.id.rb_list) {
                option = LAYOUT_MODE_2;
            } else if (checkedRadioButtonId == R.id.rb_staggered) {
                option = LAYOUT_MODE_3;
            } else if (checkedRadioButtonId == R.id.rb_spanned) {
                option = LAYOUT_MODE_4;
            }
            if (option != currentOption) {
                SharedPreferencesUtils.saveData(context, KEY_LAYOUT_MODE, option);
                if (callback != null)
                    callback.onConfirm(option);
            }
            dismiss();
        });
    }

    public interface IChooseLayoutModeDialogCallback {
        void onConfirm(int option);
    }
}
