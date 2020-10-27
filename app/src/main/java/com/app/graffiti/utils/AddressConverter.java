package com.app.graffiti.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.app.graffiti.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Locale;

/**
 * {@link AddressConverter} : <p>
 * <p>
 * </p>
 *
 * @author Jeel Vankhede
 * @version 1.0.0
 * @see AsyncTask
 * @since 9/1/18
 */

public class AddressConverter extends AsyncTask<Void, Void, String> {
    public static final String TAG = AddressConverter.class.getSimpleName();
    private static final String JSON_KEY_STATUS = "status";
    private static final String JSON_KEY_OK = "OK";
    private static final String JSON_KEY_RESULTS = "results";
    private static final String JSON_KEY_FORMATTED_ADDR = "formatted_address";
    private WeakReference<Context> context;
    private Location location;
    private ProgressDialog progressDialog;

    Double lat;
    Double longs;
    private String Address1 = "", Address2 = "", City = "", State = "", Country = "", County = "", PIN = "";


    public AddressConverter(Context context, Location location) {
        this.context = new WeakReference<Context>(context);
        this.location = location;
        if (context instanceof Activity) {
            if (this.context.get() != null) {
                progressDialog = new ProgressDialog(this.context.get());
                progressDialog.setMessage("getting location");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setCancelable(false);
                if (!progressDialog.isShowing()) {
                    progressDialog.show();
                }
            }
        }
        this.execute();
    }

    @Override
    protected String doInBackground(Void... voids) {
        if (context.get() != null)
            return getlocation(context.get(), location);

            //return getAddress(context.get(), location.getLatitude(),location.getLongitude());
        else
            return "";
    }

    @Override
    protected void onPostExecute(String s) {
        if (context.get() != null && progressDialog != null) {
            if (context.get() instanceof Activity) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        }
    }


    public String getAddress(Context context, double lat, double lng) {
        String address = "";
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            String add = obj.getAddressLine(0);
            String currentAddress = obj.getSubAdminArea() + ","
                    + obj.getAdminArea();
            double latitude = obj.getLatitude();
            double longitude = obj.getLongitude();
            String currentCity = obj.getSubAdminArea();
            String currentState = obj.getAdminArea();
            add = add + "\n" + obj.getCountryName();
            add = add + "\n" + obj.getCountryCode();
            add = add + "\n" + obj.getAdminArea();
            add = add + "\n" + obj.getPostalCode();
            add = add + "\n" + obj.getSubAdminArea();
            add = add + "\n" + obj.getLocality();
            add = add + "\n" + obj.getSubThoroughfare();


            System.out.println("obj.getCountryName()" + obj.getCountryName());
            System.out.println("obj.getCountryCode()" + obj.getCountryCode());
            System.out.println("obj.getAdminArea()" + obj.getAdminArea());
            System.out.println("obj.getPostalCode()" + obj.getPostalCode());
            System.out.println("obj.getSubAdminArea()" + obj.getSubAdminArea());
            System.out.println("obj.getLocality()" + obj.getLocality());
            System.out.println("obj.getSubThoroughfare()" + obj.getSubThoroughfare());


            Log.v("IGA", "Address" + add);
            address = add;
            // Toast.makeText(this, "Address=>" + add,
            // Toast.LENGTH_SHORT).show();

            // TennisAppActivity.showDialog(add);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }
        return address;
    }


    public String getlocation(Context context, Location location) {

        Geocoder gc = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = gc.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            StringBuilder sb = new StringBuilder();
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
//                for (int i = 0; i < address.getMaxAddressLineIndex(); i++)
//                    sb.append(address.getAddressLine(i)).append("\n");
//                sb.append(address.getLocality()).append("\n");
//                sb.append(address.getPostalCode()).append("\n");
//                sb.append(address.getCountryName());

                return address.getAddressLine(0);
            }

        } catch (Exception e) {

        }

        return "";
    }

    private String convertPostCodeFromLatLong(Context context, Location location) {
        String address = "";
        try {
            StringBuilder addressUrl = new StringBuilder("");
            addressUrl
                    .append("https://maps.googleapis.com/maps/api/geocode/")
                    .append("json?latlng=")
                    .append(location.getLatitude())
                    .append(",")
                    .append(location.getLongitude())
                    .append("&sensor=true&key=")
                    .append(context.getString(R.string.place_api_key));
//            String addressUrl = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + location.getLatitude() + ","
//                    + location.getLongitude() + "&sensor=true&key=" + context.getString(R.string.geo_code_api_key);
            Logger.log(TAG, "convertPostCodeFromLatLong : url " + addressUrl, Logger.Level.VERBOSE);
            URL url = new URL(addressUrl.toString());
            URLConnection con = url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder response = new StringBuilder("");
            String temp;
            while ((temp = reader.readLine()) != null) {
                response.append(temp);
            }
            reader.close();
            JSONObject jsonObj = new JSONObject(response.toString());
            if (jsonObj.has(JSON_KEY_STATUS)) {
                String Status = jsonObj.getString(JSON_KEY_STATUS);
                if (Status.equalsIgnoreCase(JSON_KEY_OK)) {
                    if (jsonObj.has(JSON_KEY_RESULTS)) {
                        JSONArray Results = jsonObj.getJSONArray(JSON_KEY_RESULTS);
                        JSONObject firstResult = Results.getJSONObject(0);
                        if (firstResult.has(JSON_KEY_FORMATTED_ADDR))
                            address = firstResult.getString(JSON_KEY_FORMATTED_ADDR);
                    }
                }
            }
        } catch (IOException e) {
            Log.e(TAG, " convertAddressFromApi : IOException ", e);
        } catch (JSONException e) {
            Log.e(TAG, " convertAddressFromApi : JSONException ", e);
        } finally {
            return address;
        }
    }
}
