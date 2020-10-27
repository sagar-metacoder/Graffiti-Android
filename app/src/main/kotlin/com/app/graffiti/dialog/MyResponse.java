package com.app.graffiti.dialog;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MyResponse {

    @Expose
    @SerializedName("data")
    public DataEntity data;
    @Expose
    @SerializedName("message")
    public MessageEntity message;
    @Expose
    @SerializedName("status")
    public int status;

    public static class DataEntity {
        @Expose
        @SerializedName("url")
        public String url;
    }

    public static class MessageEntity {
        @Expose
        @SerializedName("success")
        public String success;
    }
}
