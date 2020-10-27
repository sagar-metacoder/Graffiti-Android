package com.app.graffiti.webservices

import com.app.graffiti.model.CreateOrder
import com.app.graffiti.model.SendExpense
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

/**
 * [ApiClient] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 4/4/18
 */

interface ApiClient {
    @FormUrlEncoded
    @POST("api/login")
    fun logIn(
            @Field("username") userName: String,
            @Field("password") passWord: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("api/forgotPassword")
    fun forgotPassword(
            @Field("email") email: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("api/addDistributorDealer")
    fun addUser(
            @Field("parentUserId") parentUserId: Int,
            @Field("userType") userType: Int,
            @Field("firstName") firstName: String,
            @Field("lastName") lastName: String,
            @Field("firmName") firmName: String,
            @Field("address") address: String,
            @Field("location") location: String,
            @Field("state") state: String,
            @Field("city") city: String,
            @Field("zipCode") zipCode: String,
            @Field("country") country: String,
            @Field("gstNo") gstNo: String,
            @Field("mobileNo") mobileNo: String,
            @Field("email") email: String
    ): Call<ResponseBody>

    @PUT("api/updateDistributorDealer")
    fun updateUser(
            @Body userData: HashMap<String, Any>
    ): Call<ResponseBody>

    @GET("api/getUserListForParentUser")
    fun getUserListForParentUser(
            @Query("id") id: Int,
            @Query("userType") userType: Int
    ): Call<ResponseBody>

    @GET("api/getUserListForParentUser")
    fun getUserListForParentUser(
            @Query("id") id: Int,
            @Query("userType") userType: Int,
            @Query("status") status: String
    ): Call<ResponseBody>

    @GET("api/getUserListForParentUser")
    fun getUserListForParentUser(
            @Query("address") address: String,
            @Query("id") id: Int,
            @Query("userType") userType: Int
    ): Call<ResponseBody>

    @GET("api/getUserListForParentUser")
    fun getUserListForParentUser(
            @Query("id") id: Int,
            @Query("userType") userType: Int,
            @Query("status") status: String,
            @Query("address") address: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("api/addSalesPersonAttendance")
    fun addSalesPersonAttendance(
            @Field("id") userId: Int,
            @Field("location") location: String,
            @Field("latitude") latitude: Double,
            @Field("longitude") longitude: Double,
            @Field("dateTime") dateTime: String //yyyy-MM-dd hh:mm:ss
    ): Call<ResponseBody>

    @GET("api/getUserTarget")
    fun getUserTarget(
            @Query("userId") userId: Int,
            @Query("userType") userType: Int
    ): Call<ResponseBody>

    @GET("api/getCategoryList")
    fun getCategories(): Call<ResponseBody>

    @GET("api/getProductList")
    fun getProductList(): Call<ResponseBody>

    @GET("api/getProductList")
    fun getProductList(
            @Query("mainCategoryId") mainCategoryId: Int
    ): Call<ResponseBody>

    @GET("api/getProductList")
    fun getProductList(
            @Query("mainCategoryId") mainCategoryId: Int,
            @Query("subCategoryId") subCategoryId: Int
    ): Call<ResponseBody>

    @DELETE("api/deleteUser/{userId}")
    fun deleteUser(
            @Path("userId") id: Int
    ): Call<ResponseBody>

    @POST("api/addSalesPersonDailyExpenses")
    fun addSalesPersonDailyExpenses(
            @Header("Content-Type") contentType: String,
            @Body sendExpense: SendExpense
    ): Call<ResponseBody>

    @GET("api/getDsrUserList")
    fun  getUserSuggestions(
            @Query("creatorId") userId: Int
    ): Call<ResponseBody>

    @GET("api/getDsrUserList")
    fun getUserSuggestions(): Call<ResponseBody>

    @POST("api/placeOrder")
    fun placeOrder(
            @Header("Content-Type") contentType: String,
            @Body createOrder: CreateOrder
    ): Call<ResponseBody>

    @GET("api/getOrderList")
    fun getOrderList(
            @Query("creatorId") creatorId: Int
    ): Call<ResponseBody>

    @GET("api/getOrderList")
    fun getOrderList(
            @Query("creatorId") creatorId: Int,
            @Query("startDate") startDate: String,
            @Query("endDate") endDate: String
    ): Call<ResponseBody>

    @GET("api/getOrderList")
    fun getOrderList(
            @Query("creatorId") creatorId: Int,
            @Query("userId") userId: Int
    ): Call<ResponseBody>

    @GET("api/getOrderList")
    fun getOrderList(
            @Query("creatorId") creatorId: Int,
            @Query("userId") userId: Int,
            @Query("startDate") startDate: String,
            @Query("endDate") endDate: String
    ): Call<ResponseBody>

    @GET("api/getOrderList")
    fun getOrderListByEndDate(
            @Query("creatorId") creatorId: Int,
            @Query("endDate") endDate: String
    ): Call<ResponseBody>

    @GET("api/getOrderList")
    fun getOrderListByStartDate(
            @Query("creatorId") creatorId: Int,
            @Query("startDate") startDate: String
    ): Call<ResponseBody>

    @GET("api/getOrderList")
    fun getOrderListByEndDate(
            @Query("creatorId") creatorId: Int,
            @Query("userId") userId: Int,
            @Query("endDate") endDate: String
    ): Call<ResponseBody>

    @GET("api/getOrderList")
    fun getOrderListByStartDate(
            @Query("creatorId") creatorId: Int,
            @Query("userId") userId: Int,
            @Query("startDate") startDate: String
    ): Call<ResponseBody>

    @POST("api/DsrUserAddUpdate")
    fun addUpdateUserToDsr(
            @Header("Content-Type") contentType: String,
            @Body dealer: HashMap<String, Any>
    ): Call<ResponseBody>

    @GET("api/getDsrTempUserList")
    fun getDsrTempUserList(
            @Query("creatorId") creatorId: Int,
            @Query("date") date: String
    ): Call<ResponseBody>

    @GET("api/DsrUserDelete/{userId}")
    fun deleteUserFromDsr(
            @Path("userId") userId: Int
    ): Call<ResponseBody>

    @GET("api/getOrderPaymentHistory")
    fun getOrderPaymentHistory(
            @Query("userId") creatorId: Int
    ): Call<ResponseBody>

    @GET("api/getOrderPaymentHistory")
    fun getOrderPaymentHistory(
            @Query("creatorId") creatorId: Int,
            @Query("userId") userId: Int
    ): Call<ResponseBody>

    @POST("api/DsrSalesExpensesAddUpdate")
    fun dsrSalesExpenseAddUpdate(
            @Header("Content-Type") contentType: String,
            @Body dsrSalesExpense: HashMap<String, Any>
    ): Call<ResponseBody>

    @GET("api/getDsrSalesExpenses")
    fun getDsrSalesExpenses(
            @Query("creatorId") creatorId: Int,
            @Query("date") date: String
    ): Call<ResponseBody>

    @DELETE("api/DsrSalesExpensesDelete/{id}")
    fun deleteDsrSalesExpense(
            @Path("id") id: Int
    ): Call<ResponseBody>

    @GET("api/getNotification")
    fun getNotification(): Call<ResponseBody>

    @GET("api/getTargetScheme")
    fun getTargetScheme(
            @Query("userId") userId: Int,
            @Query("userType") userType: Int
    ): Call<ResponseBody>

    @GET("api/getUserLedger")
    fun getUserLedger(
            @Query("userId") userId: Int
    ): Call<ResponseBody>

    @GET("api/getTeamMemberList")
    fun getTeamMembers(
            @Query("userId") userId: Int
    ): Call<ResponseBody>

    @Multipart
    @POST("api/addCustomerCare")
    fun sendUserComplaint(@Part("name") name: RequestBody,
                          @Part("email") email: RequestBody,
                          @Part("contact") contact: RequestBody,
                          @Part("address") address: RequestBody,
                          @Part("comment") comment: RequestBody,
                          @Part("purchase_date") date: RequestBody,
                          @Part photo: MultipartBody.Part?): Call<ResponseBody>
    /*@POST("api/addToCart")
    fun addToCart(
            @Header("Content-Type") contentType: String,
            @Body cart: CartData
    ): Call<ResponseBody>

    @GET("api/getCartList")
    fun getCartList(
            @Query("creatorId") creatorId: Int
    ): Call<ResponseBody>

    @GET("api/getCartList")
    fun getCartList(
            @Query("creatorId") creatorId: Int,
            @Query("userId") userId: Int
    ): Call<ResponseBody>

    @DELETE("api/removeCart/{cartId}")
    fun removeCartItem(
            @Path("cartId") cartId: Int
    ): Call<ResponseBody>

    @DELETE("api/removeCartProduct/{creatorId}/{userId}/{productId}")
    fun removeCartProduct(
            @Path("creatorId") creatorId: Int,
            @Path("userId") userId: Int,
            @Path("productId") productId: Int
    ): Call<ResponseBody>

    @GET("api/getOrderList")
    fun getOrderList(
            @Query("creatorId") creatorId: Int,
            @Query("userId") userId: Int,
            @Query("orderType") orderType: String
    ): Call<ResponseBody>

    @GET("api/getOrderList")
    fun getOrderList(
            @Query("creatorId") creatorId: Int,
            @Query("orderType") orderType: String
    ): Call<ResponseBody>

    @PUT("api/updateCart")
    fun updateCart(
            @Header("Content-Type") contentType: String,
            @Body updateCart: HashMap<String, Any>
    ): Call<ResponseBody>*/
}
