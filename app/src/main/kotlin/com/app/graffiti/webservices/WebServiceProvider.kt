package com.app.graffiti.webservices

import `in`.freakylibs.easynetworkcall_redefined.core.ApiHandler
import `in`.freakylibs.easynetworkcall_redefined.core.RequestFlag
import com.app.graffiti.Graffiti
import com.app.graffiti.model.CreateOrder
import com.app.graffiti.model.SendExpense
import com.app.graffiti.utils.Common
import okhttp3.MultipartBody
import okhttp3.RequestBody

/**
 * [WebServiceProvider] : <p>
 *
 * </p>
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 5/4/18
 */

class WebServiceProvider {
    class LogIn(
            email: String,
            password: String
    ) : ApiHandler(
            RequestFlag(
                    Flag.LOG_IN.flag,
                    Flag.LOG_IN.flagName
            ),
            Graffiti.apiClient?.logIn(
                    email,
                    password
            )
    )

    class ForgotPassword(email: String) : ApiHandler(
            RequestFlag(
                    Flag.FORGOT_PASSWORD.flag,
                    Flag.FORGOT_PASSWORD.flagName
            ),
            Graffiti.apiClient?.forgotPassword(email)
    )

    class UserTarget(
            userId: Int,
            userType: Int
    ) : ApiHandler(
            RequestFlag(
                    Flag.USER_TARGET.flag,
                    "${Flag.USER_TARGET.flagName}(UserId$userId,UserType$userType)"
            ),
            Graffiti.apiClient?.getUserTarget(userId, userType)
    )

    class DistributorsOfSalesPerson(
            userId: Int,
            userType: Int
    ) : ApiHandler(
            RequestFlag(
                    WebServiceProvider.Flag.DISTRIBUTORS_OF_SALES_PERSON.flag,
                    WebServiceProvider.Flag.DISTRIBUTORS_OF_SALES_PERSON.flagName
            ),
            Graffiti.apiClient?.getUserListForParentUser(
                    userId,
                    userType
            )
    )

    class DealersOfSalesPerson(
            userId: Int,
            userType: Int
    ) : ApiHandler(
            RequestFlag(
                    WebServiceProvider.Flag.DEALERS_OF_SALES_PERSON.flag,
                    WebServiceProvider.Flag.DEALERS_OF_SALES_PERSON.flagName
            ),
            Graffiti.apiClient?.getUserListForParentUser(
                    userId,
                    userType
            )
    )

    class AddDistributor(
            userId: Int,
            userType: Int,
            firstName: String,
            lastName: String,
            firmName: String,
            address: String,
            location: String,
            state: String,
            city: String,
            zipCode: String,
            country: String,
            gstNumber: String,
            mobileNumber: String,
            email: String
    ) : ApiHandler(
            RequestFlag(
                    WebServiceProvider.Flag.ADD_DISTRIBUTOR.flag,
                    WebServiceProvider.Flag.ADD_DISTRIBUTOR.flagName
            ),
            Graffiti.apiClient?.addUser(
                    userId,
                    userType,
                    firstName,
                    lastName,
                    firmName,
                    address,
                    location,
                    state,
                    city,
                    zipCode,
                    country,
                    gstNumber,
                    mobileNumber,
                    email
            )
    )

    class AddDealer(
            userId: Int,
            userType: Int,
            firstName: String,
            lastName: String,
            firmName: String,
            address: String,
            location: String,
            state: String,
            city: String,
            zipCode: String,
            country: String,
            gstNumber: String,
            mobileNumber: String,
            email: String
    ) : ApiHandler(
            RequestFlag(
                    WebServiceProvider.Flag.ADD_DEALER.flag,
                    WebServiceProvider.Flag.ADD_DEALER.flagName
            ),
            Graffiti.apiClient?.addUser(
                    userId,
                    userType,
                    firstName,
                    lastName,
                    firmName,
                    address,
                    location,
                    state,
                    city,
                    zipCode,
                    country,
                    gstNumber,
                    mobileNumber,
                    email
            )
    )

    class AddUser(
            userId: Int,
            userType: Int,
            firstName: String,
            lastName: String,
            firmName: String,
            address: String,
            location: String,
            state: String,
            city: String,
            zipCode: String,
            country: String,
            gstNumber: String,
            mobileNumber: String,
            email: String
    ) : ApiHandler(
            RequestFlag(
                    WebServiceProvider.Flag.ADD_USER.flag,
                    WebServiceProvider.Flag.ADD_USER.flagName
            ),
            Graffiti.apiClient?.addUser(
                    userId,
                    userType,
                    firstName,
                    lastName,
                    firmName,
                    address,
                    location,
                    state,
                    city,
                    zipCode,
                    country,
                    gstNumber,
                    mobileNumber,
                    email
            )
    )

    class UpdateDistributor(
            updateData: HashMap<String, Any>
    ) : ApiHandler(
            RequestFlag(
                    WebServiceProvider.Flag.UPDATE_DISTRIBUTOR.flag,
                    WebServiceProvider.Flag.UPDATE_DISTRIBUTOR.flagName
            ),
            Graffiti.apiClient?.updateUser(updateData)
    )

    class UpdateDealer(
            updateData: HashMap<String, Any>
    ) : ApiHandler(
            RequestFlag(
                    WebServiceProvider.Flag.UPDATE_DEALER.flag,
                    WebServiceProvider.Flag.UPDATE_DEALER.flagName
            ),
            Graffiti.apiClient?.updateUser(updateData)
    )

    class DeleteDistributor(
            userId: Int
    ) : ApiHandler(
            RequestFlag(
                    WebServiceProvider.Flag.DELETE_DISTRIBUTOR.flag,
                    WebServiceProvider.Flag.DELETE_DISTRIBUTOR.flagName
            ),
            Graffiti.apiClient?.deleteUser(userId)
    )

    class DeleteDealer(
            userId: Int
    ) : ApiHandler(
            RequestFlag(
                    WebServiceProvider.Flag.DELETE_DEALER.flag,
                    WebServiceProvider.Flag.DELETE_DEALER.flagName
            ),
            Graffiti.apiClient?.deleteUser(userId)
    )

    class GetCategories : ApiHandler(
            RequestFlag(
                    WebServiceProvider.Flag.GET_CATEGORIES.flag,
                    WebServiceProvider.Flag.GET_CATEGORIES.flagName
            ),
            Graffiti.apiClient?.getCategories()
    )

    class GetMembers (userId: Int) : ApiHandler(
            RequestFlag(
                    WebServiceProvider.Flag.GET_TEAM_MEMBERS.flag,
                    WebServiceProvider.Flag.GET_TEAM_MEMBERS.flagName
            ),
            Graffiti.apiClient?.getTeamMembers(userId)
    )

    class GetProducts(
            mainCategoryId: Int,
            subCategoryId: Int
    ) : ApiHandler(
            RequestFlag(
                    WebServiceProvider.Flag.GET_PRODUCTS.flag,
                    if (mainCategoryId == -1 && subCategoryId == -1) {
                        WebServiceProvider.Flag.GET_PRODUCTS.flagName
                    } else {
                        if (subCategoryId == -1) {
                            "${WebServiceProvider.Flag.GET_PRODUCTS.flagName}(MainId$mainCategoryId)"
                        } else {
                            "${WebServiceProvider.Flag.GET_PRODUCTS.flagName}(MainId$mainCategoryId,SubId$subCategoryId)"
                        }
                    }
            ),
            if (mainCategoryId == -1 && subCategoryId == -1) {
                Graffiti.apiClient?.getProductList()
            } else {
                if (subCategoryId == -1) {
                    Graffiti.apiClient?.getProductList(mainCategoryId)
                } else {
                    Graffiti.apiClient?.getProductList(mainCategoryId, subCategoryId)
                }
            }
    )

    class SendDailyExpense(
            expense: SendExpense
    ) : ApiHandler(
            RequestFlag(
                    WebServiceProvider.Flag.SEND_DAILY_EXPENSE.flag,
                    WebServiceProvider.Flag.SEND_DAILY_EXPENSE.flagName
            ),
            Graffiti.apiClient?.addSalesPersonDailyExpenses(
                    Common.CONTENT_TYPE_JSON,
                    expense
            )
    )

    class GetUserSuggestions(
            userId: Int?
    ) : ApiHandler(
            RequestFlag(
                    WebServiceProvider.Flag.GET_USER_SUGGESTIONS.flag,
                    if (userId != null) "${WebServiceProvider.Flag.GET_PRODUCTS.flagName}(UserId$userId)"
                    else WebServiceProvider.Flag.GET_PRODUCTS.flagName
            ),
            if (userId != null) Graffiti.apiClient?.getUserSuggestions(userId)
            else Graffiti.apiClient?.getUserSuggestions()
    )

    class AddUserAttendance(
            userId: Int,
            location: String,
            latitude: Double,
            longitude: Double,
            dateTime: String
    ) : ApiHandler(
            RequestFlag(
                    WebServiceProvider.Flag.ADD_ATTENDANCE.flag,
                    WebServiceProvider.Flag.ADD_ATTENDANCE.flagName
            ),
            Graffiti.apiClient?.addSalesPersonAttendance(
                    userId,
                    location,
                    latitude,
                    longitude,
                    dateTime
            )
    )

    // TODO : [ 28/5/18/Jeel Vankhede ] : WebServiceProvider   Remove once unused
    /*class AddToCart(
            cartData: CartData
    ) : ApiHandler(
            RequestFlag(
                    WebServiceProvider.Flag.ADD_TO_CART.flag,
                    WebServiceProvider.Flag.ADD_TO_CART.flagName
            ),
            Graffiti.apiClient?.addToCart(
                    Common.CONTENT_TYPE_JSON,
                    cartData
            )
    )

    class GetCartList(
            creatorId: Int,
            userId: Int
    ) : ApiHandler(
            RequestFlag(
                    WebServiceProvider.Flag.GET_CART_LIST.flag,
                    if (userId != 0) "${WebServiceProvider.Flag.GET_CART_LIST.flagName}(CreatorId$creatorId,userId$userId)"
                    else "${WebServiceProvider.Flag.GET_CART_LIST.flagName}(CreatorId$creatorId)"
            ),
            if (userId != 0) Graffiti.apiClient?.getCartList(creatorId, userId)
            else Graffiti.apiClient?.getCartList(creatorId)
    )

    class RemoveCartItem(
            cartId: Int
    ) : ApiHandler(
            RequestFlag(
                    WebServiceProvider.Flag.REMOVE_CART_ITEM.flag,
                    WebServiceProvider.Flag.REMOVE_CART_ITEM.flagName
            ),
            Graffiti.apiClient?.removeCartItem(cartId)
    )

    class RemoveCartProduct(
            creatorId: Int,
            userId: Int,
            productId: Int
    ) : ApiHandler(
            RequestFlag(
                    WebServiceProvider.Flag.REMOVE_CART_PRODUCT.flag,
                    WebServiceProvider.Flag.REMOVE_CART_PRODUCT.flagName
            ),
            Graffiti.apiClient?.removeCartProduct(
                    creatorId,
                    userId,
                    productId
            )
    )*/

    class PlaceOrder(
            createOrder: CreateOrder
    ) : ApiHandler(
            RequestFlag(
                    WebServiceProvider.Flag.PLACE_ORDER.flag,
                    WebServiceProvider.Flag.PLACE_ORDER.flagName
            ),
            Graffiti.apiClient?.placeOrder(
                    Common.CONTENT_TYPE_JSON,
                    createOrder
            )
    )

    class GetOrderList(
            creatorId: Int,
            userId: Int = 0,
            startDate: String = "",
            endDate: String = ""
    ) : ApiHandler(
            RequestFlag(
                    WebServiceProvider.Flag.GET_ORDER_LIST.flag,
                    if (userId == 0) {
                        if (startDate == "" && endDate == "") {
                            "${WebServiceProvider.Flag.GET_ORDER_LIST.flagName}(CreatorId$creatorId)"
                        } else if (startDate != "" && endDate == "") {
                            "${WebServiceProvider.Flag.GET_ORDER_LIST.flagName}(CreatorId$creatorId,StartDate$startDate)"
                        } else if (startDate == "" && endDate != "") {
                            "${WebServiceProvider.Flag.GET_ORDER_LIST.flagName}(CreatorId$creatorId,EndDate$endDate)"
                        } else {
                            "${WebServiceProvider.Flag.GET_ORDER_LIST.flagName}(CreatorId$creatorId,StartDate$startDate,EndDate$endDate)"
                        }
                    } else {
                        if (startDate == "" && endDate == "") {
                            "${WebServiceProvider.Flag.GET_ORDER_LIST.flagName}(CreatorId$creatorId,UserId$userId)"
                        } else if (startDate != "" && endDate == "") {
                            "${WebServiceProvider.Flag.GET_ORDER_LIST.flagName}(CreatorId$creatorId,UserId$userId,StartDate$startDate)"
                        } else if (startDate == "" && endDate != "") {
                            "${WebServiceProvider.Flag.GET_ORDER_LIST.flagName}(CreatorId$creatorId,UserId$userId,EndDate$endDate)"
                        } else {
                            "${WebServiceProvider.Flag.GET_ORDER_LIST.flagName}(CreatorId$creatorId,UserId$userId,StartDate$startDate,EndDate$endDate)"
                        }
                    }
            ),
            if (userId == 0) {
                if (startDate == "" && endDate == "") {
                    Graffiti.apiClient?.getOrderList(creatorId)
                } else if (startDate != "" && endDate == "") {
                    Graffiti.apiClient?.getOrderListByStartDate(creatorId, startDate)
                } else if (startDate == "" && endDate != "") {
                    Graffiti.apiClient?.getOrderListByEndDate(creatorId, endDate)
                } else {
                    Graffiti.apiClient?.getOrderList(creatorId, startDate, endDate)
                }
            } else {
                if (startDate == "" && endDate == "") {
                    Graffiti.apiClient?.getOrderList(creatorId, userId)
                } else if (startDate != "" && endDate == "") {
                    Graffiti.apiClient?.getOrderListByStartDate(creatorId, userId, startDate)
                } else if (startDate == "" && endDate != "") {
                    Graffiti.apiClient?.getOrderListByEndDate(creatorId, userId, endDate)
                } else {
                    Graffiti.apiClient?.getOrderList(creatorId, userId, startDate, endDate)
                }
            }
    )

    // TODO : [ 28/5/18/Jeel Vankhede ] : WebServiceProvider   Remove once unused
    /*class UpdateCartProduct(
            cartProduct: HashMap<String, Any>
    ) : ApiHandler(
            RequestFlag(
                    WebServiceProvider.Flag.UPDATE_CART_PRODUCT.flag,
                    WebServiceProvider.Flag.UPDATE_CART_PRODUCT.flagName
            ),
            Graffiti.apiClient?.updateCart(
                    Common.CONTENT_TYPE_JSON,
                    cartProduct
            )
    )*/

    class AddUpdateUserToDsr(
            dealer: HashMap<String, Any>
    ) : ApiHandler(
            RequestFlag(
                    WebServiceProvider.Flag.USER_TO_DSR.flag,
                    WebServiceProvider.Flag.USER_TO_DSR.flagName
            ),
            Graffiti.apiClient?.addUpdateUserToDsr(
                    Common.CONTENT_TYPE_JSON,
                    dealer
            )
    )

    class GetDsrTempUserList(
            creatorId: Int,
            date: String
    ) : ApiHandler(
            RequestFlag(
                    WebServiceProvider.Flag.GET_DSR_TEMP_USER_LIST.flag,
                    "${WebServiceProvider.Flag.GET_DSR_TEMP_USER_LIST.flagName}(UserId$creatorId,Date$date)"
            ),
            Graffiti.apiClient?.getDsrTempUserList(creatorId, date)
    )

    class DeleteDsrDealer(
            userId: Int
    ) : ApiHandler(
            RequestFlag(
                    WebServiceProvider.Flag.DELETE_DEALER_FROM_DSR.flag,
                    WebServiceProvider.Flag.DELETE_DEALER_FROM_DSR.flagName
            ),
            Graffiti.apiClient?.deleteUserFromDsr(userId)
    )

    class GetPaymentHistory(
            userId: Int,
            creatorId: Int = 0
    ) : ApiHandler(
            RequestFlag(
                    WebServiceProvider.Flag.PAYMENT_HISTORY.flag,
                    if (creatorId == 0)
                        "${WebServiceProvider.Flag.PAYMENT_HISTORY.flagName}(UserId$userId)"
                    else
                        "${WebServiceProvider.Flag.PAYMENT_HISTORY.flagName}(CreatorId$creatorId,UserId$userId)"
            ),
            if (creatorId == 0)
                Graffiti.apiClient?.getOrderPaymentHistory(userId)
            else
                Graffiti.apiClient?.getOrderPaymentHistory(creatorId, userId)
    )

    class AddUpdateExpense(
            isUpdate: Boolean,
            salesExpense: HashMap<String, Any>
    ) : ApiHandler(
            if (isUpdate)
                RequestFlag(
                        WebServiceProvider.Flag.UPDATE_SALES_EXPENSE.flag,
                        WebServiceProvider.Flag.UPDATE_SALES_EXPENSE.flagName
                )
            else
                RequestFlag(
                        WebServiceProvider.Flag.ADD_SALES_EXPENSE.flag,
                        WebServiceProvider.Flag.ADD_SALES_EXPENSE.flagName
                ),
            Graffiti.apiClient?.dsrSalesExpenseAddUpdate(
                    Common.CONTENT_TYPE_JSON,
                    salesExpense
            )
    )

    class GetSalesExpenseList(
            creatorId: Int,
            date: String
    ) : ApiHandler(
            RequestFlag(
                    WebServiceProvider.Flag.GET_SALES_EXPENSE_LIST.flag,
                    "${WebServiceProvider.Flag.GET_SALES_EXPENSE_LIST.flagName}(UserId$creatorId,Date$date)"
            ),
            Graffiti.apiClient?.getDsrSalesExpenses(creatorId, date)
    )

    class DeleteDsrExpense(
            id: Int
    ) : ApiHandler(
            RequestFlag(
                    WebServiceProvider.Flag.DELETE_DSR_EXPENSE.flag,
                    WebServiceProvider.Flag.DELETE_DSR_EXPENSE.flagName
            ),
            Graffiti.apiClient?.deleteDsrSalesExpense(id)
    )

    class DealersOfDistributor(
            userId: Int,
            userType: Int
    ) : ApiHandler(
            RequestFlag(
                    WebServiceProvider.Flag.DEALERS_OF_DISTRIBUTOR.flag,
                    WebServiceProvider.Flag.DEALERS_OF_DISTRIBUTOR.flagName
            ),
            Graffiti.apiClient?.getUserListForParentUser(
                    userId,
                    userType
            )
    )

    class GetNotifications : ApiHandler(
            RequestFlag(
                    WebServiceProvider.Flag.GET_NOTIFICATIONS.flag,
                    WebServiceProvider.Flag.GET_NOTIFICATIONS.flagName
            ),
            Graffiti.apiClient?.getNotification()
    )

    class GetTargetScheme(
            userId: Int,
            userType: Int
    ) : ApiHandler(
            RequestFlag(
                    WebServiceProvider.Flag.GET_TARGET_SCHEME.flag,
                    "${WebServiceProvider.Flag.GET_TARGET_SCHEME.flagName}(userId=$userId,userType=$userType)"
            ),
            Graffiti.apiClient?.getTargetScheme(
                    userId,
                    userType
            )
    )

    class GetUserLedger(
            userId: Int
    ) : ApiHandler(
            RequestFlag(
                    WebServiceProvider.Flag.GET_USER_LEDGER.flag,
                    "${WebServiceProvider.Flag.GET_USER_LEDGER.flagName}(userId=$userId)"
            ),
            Graffiti.apiClient?.getUserLedger(userId)
    )

    class SendUserComplaint(
            name: RequestBody,
            email: RequestBody,
            contact: RequestBody,
            address: RequestBody,
            comment: RequestBody,
            date: RequestBody,
            photo: MultipartBody.Part?
    ) : ApiHandler(
            RequestFlag(
                    WebServiceProvider.Flag.SEND_USER_COMPLAINT.flag,
                    WebServiceProvider.Flag.SEND_USER_COMPLAINT.flagName
            ),
            Graffiti.apiClient?.sendUserComplaint(name,email, contact, address, comment,date, photo?:null)
    )

    public enum class Flag(public var flag: Int, public var flagName: String) {
        LOG_IN(
                1,
                "logIn"
        ),
        USER_TARGET(
                2,
                "getUserTarget"
        ),
        DISTRIBUTORS_OF_SALES_PERSON(
                3,
                "distributorsOfSalesPerson"
        ),
        DEALERS_OF_SALES_PERSON(
                4,
                "dealersOfSalesPerson"
        ),
        ADD_DISTRIBUTOR(
                5,
                "addDistributor"
        ),
        ADD_DEALER(
                6,
                "addDealer"
        ),
        FORGOT_PASSWORD(
                7,
                "forgotPassword"
        ),
        UPDATE_DISTRIBUTOR(
                8,
                "updateDistributor"
        ),
        UPDATE_DEALER(
                9,
                "updateDealer"
        ),
        DELETE_DISTRIBUTOR(
                10,
                "deleteDistributor"
        ),
        DELETE_DEALER(
                11,
                "deleteDealer"
        ),
        GET_CATEGORIES(
                12,
                "getCategories"
        ),
        GET_PRODUCTS(
                13,
                "productList"
        ),
        SEND_DAILY_EXPENSE(
                14,
                "sendDailyExpense"
        ),
        GET_USER_SUGGESTIONS(
                15,
                "userSuggestionData"
        ),
        ADD_ATTENDANCE(
                16,
                "userAttendance"
        ),
        ADD_TO_CART(
                17,
                "addToCart"
        ),
        GET_CART_LIST(
                18,
                "cartList"
        ),
        REMOVE_CART_ITEM(
                19,
                "removeCartItem"
        ),
        REMOVE_CART_PRODUCT(
                20,
                "removeCartProduct"
        ),
        PLACE_ORDER(
                21,
                "placeOrder"
        ),
        GET_ORDER_LIST(
                22,
                "orderList"
        ),
        UPDATE_CART_PRODUCT(
                23,
                "updateCartProduct"
        ),
        USER_TO_DSR(
                24,
                "addUpdateUserToDsr"
        ),
        GET_DSR_TEMP_USER_LIST(
                25,
                "dsrTempUserList"
        ),
        DELETE_DEALER_FROM_DSR(
                26,
                "deleteDsrDealer"
        ),
        PAYMENT_HISTORY(
                27,
                "paymentHistory"
        ),
        ADD_SALES_EXPENSE(
                28,
                "add_sales_expense"
        ),
        UPDATE_SALES_EXPENSE(
                29,
                "update_sales_expense"
        ),
        ADD_USER(
                30,
                "addUser"
        ),
        GET_SALES_EXPENSE_LIST(
                31,
                "dsrExpenseList"
        ),
        DELETE_DSR_EXPENSE(
                32,
                "deleteDsrExpense"
        ),
        DEALERS_OF_DISTRIBUTOR(
                33,
                "dealersOfDistributor"
        ),
        GET_NOTIFICATIONS(
                34,
                "getNotifications"
        ),
        GET_TARGET_SCHEME(
                35,
                "getTargetScheme"
        ),
        GET_USER_LEDGER(
                36,
                "getUserLedger"
        ),
        SEND_USER_COMPLAINT(
                37,
                "addCustomerCare"
        ),
        GET_TEAM_MEMBERS(
                38,
                "getTeamMemberList"
        );
    }
}