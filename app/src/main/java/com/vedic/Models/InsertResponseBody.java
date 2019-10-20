package com.vedic.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class InsertResponseBody {

    @SerializedName("data")
    @Expose
    private User data;
    @SerializedName("urls")
    @Expose
    private List<String> url;
    @SerializedName("metadata")
    @Expose
    private MetaData metadata;

    public User getData() {
        return data;
    }

    public void setData(User data) {
        this.data = data;
    }

    public MetaData getMetadata() {
        return metadata;
    }

    public void setMetadata(MetaData metadata) {
        this.metadata = metadata;
    }

    public List<String> getUrl() {
        return url;
    }

    public void setUrl(List<String> url) {
        this.url = url;
    }
}