package com.svoemesto.ivfx.utils;

import com.google.gson.annotations.SerializedName;

public class Embeddings {

    @SerializedName("embeddings")
    public double[][] vectors;
    @SerializedName("names")
    public String[] tags;


}
