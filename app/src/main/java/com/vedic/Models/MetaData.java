package com.vedic.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MetaData {

    @SerializedName("response_code")
    @Expose
    private int responseCode;
    @SerializedName("response_text")
    @Expose
    private String responseText;

    public String getResponseText() {
        return responseText;
    }

    public int getResponseCode() {
        return responseCode;
    }


}