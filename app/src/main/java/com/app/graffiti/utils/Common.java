package com.app.graffiti.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import com.app.graffiti.R;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * {@link Common} : <p>
 * <p>
 * </p>
 *
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 27/3/18
 */

public class Common {
    public static final String TAG = Common.class.getSimpleName();
    public static final String BASE_URL = "http://52.66.53.230/graffiti/";
    //    public static final String BASE_URL = "http://192.168.2.4/graffiti/";
    public static final long SPLASH_TIMEOUT = 1000 * 2;
    public static final long REFRESH_DELAY = 3000;
    public static final long POST_FRAGMENT_DELAY = 50;
    public static final String EXTRA_FRAGMENT = "fragment";
    public static final String EXTRA_FRAGMENT_TYPE = "fragment_type";
    public static final String EXTRA_DASHBOARD_ITEM = "dashboard_item";
    public static final String EXTRA_CATEGORY_ID = "category_id";
    public static final String EXTRA_SUB_CATEGORY_ID = "sub_category_id";
    public static final String EXTRA_REDIRECT_TAG = "login_redirection_tag";
    public static final String EXTRA_USER_ID = "user_id";
    public static final String EXTRA_USER_TYPE = "user_type";
    public static final String EXTRA_CREATOR_ID = "creator_id";
    public static final String EXTRA_IS_USER_ACTIVE = "is_user_active";
    public static final String EXTRA_USER_LIST = "user_list";
    public static final String EXTRA_USER = "user";
    public static final String EXTRA_CATEGORY = "category_item";
    public static final String EXTRA_SUB_CATEGORY = "sub_category_item";
    public static final String EXTRA_PRODUCT_LIST_TITLE = "product_list_title";
    public static final String EXTRA_PRODUCT = "product_item";
    public static final String EXTRA_CART = "cart_item";
    public static final String EXTRA_ITEM_ID = "item_id";
    public static final String EXTRA_QUANTITY = "quantity";
    public static final String EXTRA_DSR_DEALER = "dsr_dealer_data";
    public static final String EXTRA_EXPENSE = "expense_data";
    public static final String EXTRA_IMG_RES = "image_res_id";
    public static final String EXTRA_ADDRESS = "current_address";
    public static final String EXTRA_LATITUDE = "current_latitude";
    public static final String EXTRA_LONGITUDE = "current_longitude";
    public static final String EXTRA_DOWNLOAD_URL = "download_url";
    public static final long DEBUG_CONNECTION_TIMEOUT = 1000 * 10;
    public static final long RELEASE_CONNECTION_TIMEOUT = 1000 * 5;
    public static final long DEBUG_READ_TIMEOUT = 1000 * 20;
    public static final long RELEASE_READ_TIMEOUT = 1000 * 10;
    public static final long DEBUG_WRITE_TIMEOUT = 1000 * 20;
    public static final long RELEASE_WRITE_TIMEOUT = 1000 * 10;

    public static final int REQUEST_AUTO_COMPLETE_PLACE = 200;

    public static final String JSON_KEY_ID = "id";
    public static final String JSON_KEY_FIRST_NAME = "firstName";
    public static final String JSON_KEY_LAST_NAME = "lastName";
    public static final String JSON_KEY_FIRM_NAME = "firmName";
    public static final String JSON_KEY_GST_NUMBER = "gstNo";
    public static final String JSON_KEY_MOBILE_NUMBER = "mobileNo";
    public static final String JSON_KEY_EMAIL = "email";
    public static final String JSON_KEY_ADDRESS = "address";
    public static final String JSON_KEY_LOCATION = "location";
    public static final String JSON_KEY_DELETE_USER_ID = "userId";
    public static final String JSON_KEY_ORDER_ID = "orderId";
    public static final String JSON_KEY_PRODUCT_ID = "productId";
    public static final String JSON_KEY_QUANTITY = "quantity";
    public static final String JSON_KEY_CREATOR_ID = "creator_id";
    public static final String JSON_KEY_USER_ID = "user_id";
    public static final String JSON_KEY_DESCRIPTION = "description";
    public static final String JSON_KEY_DATE = "date";
    public static final String JSON_KEY_USER_TYPE = "user_type";
    public static final String JSON_KEY_FROM_ADDRESS = "from_address";
    public static final String JSON_KEY_TO_ADDRESS = "to_address";
    public static final String JSON_KEY_MODE = "mode";
    public static final String JSON_KEY_FARE = "fare";
    public static final String JSON_KEY_LOGGING = "logging";
    public static final String JSON_KEY_BOARDING = "boarding";
    public static final String JSON_KEY_EXTRA = "extra";
    public static final String JSON_KEY_MISCELLANEOUS = "miscellaneous";
    public static final String JSON_KEY_TOTAL = "total";

    public static final int REQUEST_UPDATE = 1000;
    public static final int REQUEST_DETAIL = 1001;
    public static final int RESULT_UPDATE_OK = 100;
    public static final int RESULT_DELETE_OK = 101;
    public static final int REQUEST_PERMISSION_LOCATION = 9001;
    public static final int REQUEST_PERMISSION_STORAGE = 9002;
    public static final int REQUEST_OPEN_SETTINGS = 2001;

    public static final String USER_LOGIN = "login";
    public static final String USER_LOGOUT = "logout";

    public static final String LOCATION_WORK_TAG = "location_update_worker";

    public static final String CONTENT_TYPE_JSON = "application/json";

    public static final String PREF_FILE_WORK_ID = "work_registry";
    public static final String PREF_KEY_WORK_REQ_ID = "Work_unique_id";

    public static final String DIR_DOWNLOADS = "download";
    public static final String DIR_PDF = "pdf";
    public static final String FILE_CATALOGUE_PDF = "catalogue.pdf";


    public static boolean isColorDark(int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        if (darkness < 0.5) {
            return false;
        } else {
            return true;
        }
    }

    public static float pixelToDp(Context context, float pixels) {
        return pixels / (context.getResources().getDisplayMetrics().density / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static float pixelToSp(Context context, float pixels) {
        return pixels / (context.getResources().getDisplayMetrics().scaledDensity / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static float dpToPixel(Context context, float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
//        return dp * (context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static float spToPixel(Context context, float sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }

    public static void showShortToast(Context context, String message) {
        Toast
                .makeText(context, message, Toast.LENGTH_SHORT)
                .show();
    }

    public static void showLongToast(Context context, String message) {
        Toast
                .makeText(context, message, Toast.LENGTH_LONG)
                .show();
    }

    public static void showShortSnackBar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
    }

    public static void showLongSnackBar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }

    public static void showIndefiniteSnackBar(View view, String message, String action, View.OnClickListener listener) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(action, listener);
        snackbar.show();
    }

    public static String formatDateTime(String inputDate, String inputFormat, String outputFormat) {
        SimpleDateFormat inputDateFormat = new SimpleDateFormat(inputFormat);
        SimpleDateFormat outputDateFormat = new SimpleDateFormat(outputFormat);
        Date date;
        try {
            date = inputDateFormat.parse(inputDate);
            return outputDateFormat.format(date);
        } catch (ParseException e) {
            Logger.log(TAG, "ParseException\n", e);
            return "";
        }
    }

    public static String formatTimeInMillis(long inputTime, String outputFormat) {
        SimpleDateFormat outputDateFormat = new SimpleDateFormat(outputFormat);
        Date date = new Date();
        date.setTime(inputTime);
        return outputDateFormat.format(date);
    }

    public static String formatDouble(Double value) {
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        return decimalFormat.format(value);
    }

    public static void changeToolbarTheme(@Nullable Toolbar toolbar, ColorDrawable drawable) {
        if (toolbar != null) {
            if (Common.isColorDark(drawable.getColor())) {
                toolbar.getContext().setTheme(R.style.ThemeOverlay_AppCompat_Dark_ActionBar);
            } else {
                toolbar.getContext().setTheme(R.style.ThemeOverlay_AppCompat_Light);
            }
        } else {
            Logger.log(TAG, "changeToolbarTheme : Toolbar is null");
        }
    }

    public static void changeToolbarTheme(@Nullable Toolbar toolbar, int color) {
        if (toolbar != null) {
            if (Common.isColorDark(color)) {
                toolbar.getContext().setTheme(R.style.ThemeOverlay_AppCompat_Dark_ActionBar);
            } else {
                toolbar.getContext().setTheme(R.style.ThemeOverlay_AppCompat_Light);
            }
        } else {
            Logger.log(TAG, "changeToolbarTheme : Toolbar is null");
        }
    }

    public enum UserType {
        SUPER_ADMIN(1, "Super Admin"),
        SALES_PERSON(2, "Sales Person"),
        DISTRIBUTOR(3, "Distributor"),
        DEALER(4, "Dealer"),
        ARCHITECTURE(5, "Architecture"),
        BUILDER(6, "Builder"),
        REGINAL_SALES_PERSON(7, "Reginal Sales Person"),
        SALES_HEAD(8, "Sales Head");

        private int userType;
        private String user;

        UserType(int userType, String user) {
            this.userType = userType;
            this.user = user;
        }

        public int getUserType() {
            return userType;
        }

        public String getUser() {
            return user;
        }

        public static String getUserType(int type) {
            if (UserType.values().length > 0) {
                for (UserType user : UserType.values()) {
                    if (user.userType == type) {
                        return user.user;
                    }
                }
            }
            return "";
        }

        public static int getUserType(String type) {
            if (UserType.values().length > 0) {
                for (UserType user : UserType.values()) {
                    if (user.user.equals(type)) {
                        return user.userType;
                    }
                }
            }
            return 0;
        }
    }
}
