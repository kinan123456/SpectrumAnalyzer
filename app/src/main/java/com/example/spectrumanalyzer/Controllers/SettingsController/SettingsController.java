package com.example.spectrumanalyzer.Controllers.SettingsController;

public class SettingsController {
    private int sampleRate;
    private int channelsNumber;
    private int fftPointsNumber;
    private int maxFreqInterval;
    private double amplitudeRange;

    private static SettingsController instance = null;

    private SettingsController(){
        SetDefaultSettings();
    }

    public static SettingsController GetInstance() {
        if (instance == null)
            instance = new SettingsController();

        return instance;
    }

    public void SetNewSettings(double newAmplitudeRange, int newSampleRate, int newChannelsNumbers, int newFftPointsNumber) {
        sampleRate = newSampleRate;
        channelsNumber = newChannelsNumbers;
        fftPointsNumber = newFftPointsNumber;
        amplitudeRange = newAmplitudeRange;
    }

    public void SetDefaultSettings() {
        sampleRate = 1000000;
        channelsNumber = 1;
        fftPointsNumber = 256;
        maxFreqInterval = 500000;
        amplitudeRange = 3.3;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public int getChannelsNumber() {
        return channelsNumber;
    }

    public int getFftPointsNumber() {
        return fftPointsNumber;
    }

    public double getAmplitudeRange() {
        return amplitudeRange;
    }
}
