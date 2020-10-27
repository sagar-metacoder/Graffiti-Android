package com.app.graffiti.dialog;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface {

    @FormUrlEncoded
    @POST("getSalesPersonExpenseReport")
    Call<ResponseBody> EXPORT_PDF_CALL(@Field("user_id") String user_id,
                                       @Field("start_date") String start_date,
                                       @Field("end_date") String end_date);

}
