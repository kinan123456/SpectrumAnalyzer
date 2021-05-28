package com.example.spectrumanalyzer.Screens;

import android.os.Bundle;

import androidx.annotation.ArrayRes;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.spectrumanalyzer.Controllers.Bluetooth.BluetoothController;
import com.example.spectrumanalyzer.R;
import com.example.spectrumanalyzer.Controllers.SettingsController.SettingsController;

public class AnalyzerSettingsActivity extends AppCompatActivity {

    // The submit button
    private Button submitButton;

    private Spinner channelsSpinner, fftPointsSpinner, amplitudeRangeSpinner,intervalSpinner;
    public static Spinner sampleRateSpinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_settings);

        SetSampleRateSpinner();
        SetChannelsNumberSpinner();
        SetFFTPointsSpinner();
        SetAmplitudeSpinner();
        HandleSubmitForm();
    }

    private void SetSpinner(Spinner spinner, @ArrayRes int textArrayResId, String currentValue) {
        // Create an ArrayAdapter using the string array and a default spinner
        ArrayAdapter<CharSequence> sampleRateAdapter = ArrayAdapter
                .createFromResource(this, textArrayResId,
                        android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        sampleRateAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(sampleRateAdapter);
        spinner.setSelection(sampleRateAdapter.getPosition(currentValue));
    }

    private void SetAmplitudeSpinner() {
        amplitudeRangeSpinner = findViewById(R.id.amplitudeRangeSpinner);
        SetSpinner(amplitudeRangeSpinner, R.array.amplitude_range, String.valueOf(SettingsController.GetInstance().getAmplitudeRange()));
    }

    private void SetFFTPointsSpinner() {
        fftPointsSpinner = findViewById(R.id.fftPointsSpinner);
        SetSpinner(fftPointsSpinner, R.array.num_of_fft_points, String.valueOf(SettingsController.GetInstance().getFftPointsNumber()));

        fftPointsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int selectedFftPoints = Integer.parseInt(parent.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void SetSampleRateSpinner() {
        sampleRateSpinner = findViewById(R.id.sampleRateSpinner);
        SetSpinner(sampleRateSpinner, R.array.sample_rate_input, String.valueOf(SettingsController.GetInstance().getSampleRate()));
    }

    private void SetChannelsNumberSpinner() {
        channelsSpinner = findViewById(R.id.channelsNumSpinner);
        SetSpinner(channelsSpinner, R.array.num_of_channels_input, String.valueOf(SettingsController.GetInstance().getChannelsNumber()));
    }
    private void HandleSubmitForm() {
        // submit button connect
        submitButton = findViewById(R.id.submitBtnSettings);
        // Set the submit button listener so user can submit the form
        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (BluetoothController.GetInstance().GetCurrentBTState() != BluetoothController.btState.CONNECTED)
                    BluetoothController.GetInstance().DisplayBluetoothDisconnectedText(getApplicationContext());
                else
                    UpdateNewSettings();
            }

            private void UpdateNewSettings() {
                String newSampleRateValue = sampleRateSpinner.getSelectedItem().toString();
                String newChannelsNumber = channelsSpinner.getSelectedItem().toString();
                String newNumFftPoints = fftPointsSpinner.getSelectedItem().toString();
                String newAmplitudeRange = amplitudeRangeSpinner.getSelectedItem().toString();
                //Units, SAMPLE RATE, CHANNELS NUM, FFT points, number of samples
                SettingsController.GetInstance().SetNewSettings(
                        Double.parseDouble(newAmplitudeRange),
                        Integer.parseInt(newSampleRateValue),
                        Integer.parseInt(newChannelsNumber),
                        Integer.parseInt(newNumFftPoints)
                );

                Toast.makeText(getApplicationContext(), "System Settings Controller Update", Toast.LENGTH_SHORT).show();

                SendNewSettingsToServer(newAmplitudeRange, newSampleRateValue, newChannelsNumber, newNumFftPoints);
            }

            private void SendNewSettingsToServer(String newAmplitudeRange, String newSampleRateValue, String newChannelsNumber, String newNumFftPoints) {
                String message = BluetoothController.GetInstance().CreateMessageFromInputParams(
                        newAmplitudeRange,
                        newSampleRateValue,
                        newChannelsNumber,
                        newNumFftPoints
                );
                BluetoothController.GetInstance().Write(message);
            }
        });
    }
}