package com.example.spectrumanalyzer.Screens;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.spectrumanalyzer.Controllers.Bluetooth.BluetoothController;
import com.example.spectrumanalyzer.Controllers.FilterDesigner.FilterDesignController;
import com.example.spectrumanalyzer.R;
import com.example.spectrumanalyzer.Controllers.SettingsController.SettingsController;

public class FilterDesignerActivity extends AppCompatActivity {

    TextView filterDesignSampleRate;
    TextView filterDesignFreqRes;
    RadioGroup methodRadioGroup;

    RadioGroup responseTypeRadioGroup;
    TextView fc1FinalValText, fc2FinalValText, fc1FinalValue, fc2FinalValue;

    RadioGroup techniqueRadioGroup;
    TextView techniqueTextView;

    private boolean isBpOrBs = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_designer);

        initLayoutHandlers();
    }

    private void initLayoutHandlers() {
        initResponseType();

        filterDesignSampleRate = findViewById(R.id.filterDesignSampleRate);
        filterDesignSampleRate.setText(String.valueOf(SettingsController.GetInstance().getSampleRate()));

        filterDesignFreqRes = findViewById(R.id.filterDesign_df);
        filterDesignFreqRes.setText(String.valueOf(FilterDesignController.GetInstance().getFrequencyResolution()));

        methodRadioGroup = findViewById(R.id.methodRadioGroup);
        techniqueRadioGroup = findViewById(R.id.techniqueRadioGroup);
        techniqueTextView = new TextView(this);
    }

    private void initResponseType() {
        responseTypeRadioGroup = findViewById(R.id.responseTypeRadioGroup);
        responseTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.response_type_lp:
                        fc1FinalValue.setText(String.valueOf(0));
                        fc2FinalValue.setText("");
                        isBpOrBs = false;
                        PopUpEditText(0, -1);
                        break;
                    case R.id.response_type_bp:
                    case R.id.response_type_bs:
                        fc1FinalValue.setText("");
                        fc2FinalValue.setText("");
                        isBpOrBs = true;
                        PopUpEditText(-1, -1);
                        break;
                    case R.id.response_type_hp:
                        isBpOrBs = false;
                        fc2FinalValue.setText(String.valueOf(SettingsController.GetInstance().getSampleRate() / 2));
                        fc1FinalValue.setText("");
                        PopUpEditText(-1, SettingsController.GetInstance().getSampleRate() / 2);
                        break;
                }
            }
        });
        fc1FinalValText = findViewById(R.id.fc1FinalValText);
        fc2FinalValText = findViewById(R.id.fc2FinalValText);
        fc1FinalValue = findViewById(R.id.fc1FinalValue);
        fc2FinalValue = findViewById(R.id.fc2FinalValue);
    }

    public void onMethodFIRClicked(View view) {
        RemoveAllTechniqueOptions();
        ShowTechniqueTextLabel();
        GenerateFIRTechniqueOptions();
    }

    public void onMethodIIRClicked(View view) {
        RemoveAllTechniqueOptions();
        ShowTechniqueTextLabel();
        GenerateIIRTechniqueOptions();
    }

    private void ShowTechniqueTextLabel() {
        techniqueTextView.setText("Response Technique");
        techniqueTextView.setVisibility(View.VISIBLE);
        techniqueRadioGroup.addView(techniqueTextView);
    }

    private void RemoveAllTechniqueOptions() {
        techniqueRadioGroup.removeAllViewsInLayout();
    }

    private void GenerateFIRTechniqueOptions() {
        RadioButton hammingRdb = new RadioButton(this);
        hammingRdb.setText("Hamming");
        techniqueRadioGroup.addView(hammingRdb);

        RadioButton hannRdb = new RadioButton(this);
        hannRdb.setText("Hann");
        techniqueRadioGroup.addView(hannRdb);

        RadioButton blackManRdb = new RadioButton(this);
        blackManRdb.setText("Blackman");
        techniqueRadioGroup.addView(blackManRdb);

    }

    private void GenerateIIRTechniqueOptions() {
        RadioButton butterworthRdb = new RadioButton(this);
        butterworthRdb.setText("Butterworth");
        techniqueRadioGroup.addView(butterworthRdb);

        RadioButton chebyshev1Rdb = new RadioButton(this);
        chebyshev1Rdb.setText("Cheybyshev1");
        techniqueRadioGroup.addView(chebyshev1Rdb);

        RadioButton chebyshev2Rdb = new RadioButton(this);
        chebyshev2Rdb.setText("Cheybyshev2");
        techniqueRadioGroup.addView(chebyshev2Rdb);
    }

    private void PopUpEditText(int fc1Val, int fc2Val) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("FC1/FC2 Parameters");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        // Add a TextView here for the "Title" label, as noted in the comments
        final EditText fc1Text = new EditText(this);
        fc1Text.setHint("FC1");
        layout.addView(fc1Text);
        fc1Text.setInputType(InputType.TYPE_CLASS_NUMBER);
        if (fc1Val != -1) {
            fc1Text.setText(String.valueOf(fc1Val));
            fc1Text.setEnabled(false);
        }

        // Add another TextView here for the "Description" label
        final EditText fc2Text = new EditText(this);
        fc2Text.setHint("FC2");
        layout.addView(fc2Text);
        fc2Text.setInputType(InputType.TYPE_CLASS_NUMBER);
        if (fc2Val != -1) {
            fc2Text.setText(String.valueOf(fc2Val));
            fc2Text.setEnabled(false);
        }

        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newFc1TextVal = fc1Text.getText().toString();
                String newFc2TextVal = fc2Text.getText().toString();

//              Check that FC1 & FC2 text aren't empty.
                if (newFc1TextVal != null && !newFc1TextVal.isEmpty() && newFc2TextVal != null && !newFc2TextVal.isEmpty()) {
                    if (isBpOrBs && Integer.parseInt(newFc1TextVal) > Integer.parseInt(newFc2TextVal))
                        Toast.makeText(getApplicationContext(), "0 [Hz] > FC1 > FC2", Toast.LENGTH_SHORT).show();
                    else {
                        if (Integer.parseInt(newFc2TextVal) > SettingsController.GetInstance().getSampleRate() / 2)
                            Toast.makeText(getApplicationContext(), "FC2 < Nyq. frequency", Toast.LENGTH_SHORT).show();
                        else {
                            if ((Integer.parseInt(newFc2TextVal) - Integer.parseInt(newFc1TextVal) ) < Math.ceil(SettingsController.GetInstance().getSampleRate() / (2 * SettingsController.GetInstance().getFftPointsNumber())))
                                Toast.makeText(getApplicationContext(), "Filter BW >= freq. resolution!", Toast.LENGTH_SHORT).show();
                            else {
                                fc1FinalValue.setText(newFc1TextVal);
                                fc2FinalValue.setText(newFc2TextVal);
                            }
                        }
                    }
                } else
                    Toast.makeText(getApplicationContext(), "One or more parameters are empty, try again.", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
                Toast.makeText(getApplicationContext(), "No new values inserted.", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    public void onFilterDesignSubmit(View view) {
        if (BluetoothController.GetInstance().GetCurrentBTState() != BluetoothController.btState.CONNECTED) {
            BluetoothController.GetInstance().DisplayBluetoothDisconnectedText(getApplicationContext());
            return;
        }

        if (methodRadioGroup.getCheckedRadioButtonId() == -1 ||
                techniqueRadioGroup.getCheckedRadioButtonId() == -1 ||
                responseTypeRadioGroup.getCheckedRadioButtonId() == -1 ||
                fc1FinalValue.getText().toString().isEmpty() ||
                fc2FinalValue.getText().toString().isEmpty()) {

            Toast.makeText(getApplicationContext(),
                    "One or more parameters is not valid, please fill in everything correctly.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String responseMethod = ((RadioButton) findViewById(methodRadioGroup.getCheckedRadioButtonId())).getText().toString();
        String responseTechnique = ((RadioButton) findViewById(techniqueRadioGroup.getCheckedRadioButtonId())).getText().toString();
        String responseType = ((RadioButton) findViewById(responseTypeRadioGroup.getCheckedRadioButtonId())).getText().toString();
        String fc1Value = fc1FinalValue.getText().toString();
        String fc2Value = fc2FinalValue.getText().toString();

        FilterDesignController.GetInstance().SetNewFilterDesigner(
                responseMethod,
                responseTechnique,
                responseType,
                fc1Value,
                fc2Value
        );

        String message = FilterDesignController.GetInstance().GetRequestRepresentation();

        BluetoothController.GetInstance().Write(message);
        FilterDesignController.GetInstance().setDesignFilterMode(true);
    }
}