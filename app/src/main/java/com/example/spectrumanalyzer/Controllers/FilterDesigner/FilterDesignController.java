package com.example.spectrumanalyzer.Controllers.FilterDesigner;

import com.example.spectrumanalyzer.Controllers.SettingsController.SettingsController;

public class FilterDesignController {
    private static FilterDesignController instance = null;
    private boolean designFilterMode = false;
    private final String filterKeyword = "Filter";
    private String responseMethod;

    private String responseTechnique;
    private String responseType;
    private String fc1Value;
    private String fc2Value;
    private double frequencyResolution;

    private final String CommaSeparator = ",";

    private FilterDesignController() {
    }

    public static FilterDesignController GetInstance() {
        if (instance == null)
            instance = new FilterDesignController();

        return instance;
    }

    public boolean getDesignFilterMode() {
        return designFilterMode;
    }

    public void setDesignFilterMode(boolean mode) {
        designFilterMode = mode;
    }

    public double getFrequencyResolution() {
        frequencyResolution = Math.ceil(
                (double) SettingsController.GetInstance().getSampleRate() / (double) (2*(SettingsController.GetInstance().getFftPointsNumber()))
        );

        return frequencyResolution;
    }

    public void SetNewFilterDesigner(String responseMethod, String responseTechnique, String responseType, String fc1Value, String fc2Value) {
        this.responseMethod = responseMethod;
        this.responseTechnique = responseTechnique;
        this.responseType = responseType;
        this.fc1Value = fc1Value;
        this.fc2Value = fc2Value;
    }

    /**
     * Constructs a message representation that will be sent to server.
     * Final result would like something like this: "Filter,FIR,Hann,LP,0,30".
     *
     * @return String representation of all parameters
     */
    public String GetRequestRepresentation() {
        return filterKeyword +
                CommaSeparator +
                responseMethod +
                CommaSeparator +
                responseTechnique +
                CommaSeparator +
                responseType +
                CommaSeparator +
                fc1Value +
                CommaSeparator +
                fc2Value;
    }

    public String getResponseMethod() {
        return responseMethod;
    }

    public String getResponseTechnique() {
        return responseTechnique;
    }

    public String getResponseType() {
        return responseType;
    }

    public String getFc1Value() {
        return fc1Value;
    }

    public String getFc2Value() {
        return fc2Value;
    }
}
