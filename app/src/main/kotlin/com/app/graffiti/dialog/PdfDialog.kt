package com.app.graffiti.dialog

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.support.v4.content.ContextCompat.startActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.app.graffiti.R
import com.google.gson.JsonSyntaxException
import kotlinx.android.synthetic.main.dialog_pdf.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class PdfDialog(context: Context?) : Dialog(context) {

    constructor(context: Context?, id: String?):this(context){
        this.id=id
    }

    var id: String? = null
    var url:String?=null
    var call: Call<ResponseBody>? = null
    val myCalendar = Calendar.getInstance()
    val myCalendar1 = Calendar.getInstance()
    var startDate: String? = null
    var endDate: String? = null

    var apiInterface: ApiInterface? = null
    var myResponse: MyResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_pdf)
        apiInterface = ApiClient.Companion.getClient()?.create(ApiInterface::class.java)

        val startDate = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateStartLabel()
        }
        val endDate = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            // TODO Auto-generated method stub
            myCalendar1.set(Calendar.YEAR, year)
            myCalendar1.set(Calendar.MONTH, monthOfYear)
            myCalendar1.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateEndLabel()
        }
        et_startDate.setOnClickListener {
            DatePickerDialog(context, startDate, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show()
        }
        et_endDate.setOnClickListener {
            DatePickerDialog(context, endDate, myCalendar1
                    .get(Calendar.YEAR), myCalendar1.get(Calendar.MONTH),
                    myCalendar1.get(Calendar.DAY_OF_MONTH)).show()
        }
        btn_export.setOnClickListener {
            callApi()
        }
    }

    private fun callApi() {

        val queue = Volley.newRequestQueue(context)
        val postRequest = object : StringRequest(Request.Method.POST, "http://graffitibath.com/graffiti/api/getSalesPersonExpenseReport", object : com.android.volley.Response.Listener<String> {
            override fun onResponse(response: String?) {
                Log.d("response", response)
                try {
                    /* val data:String= response.body()?.string().toString()
                     Toast.makeText(context,data,Toast.LENGTH_SHORT).show()*/




                    val jsonObject = JSONObject(response);
                    url = jsonObject.getJSONObject("data").getString("url")

                    val format = "http://docs.google.com/gview?embedded=true&url="+ url
                   // val fullPath = String.format(Locale.ENGLISH, format);
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(format));
                    context.startActivity(intent)
                    //DownloadFile().execute()
                    //Toast.makeText(context, url, Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    e.printStackTrace()
                } catch (e: JsonSyntaxException) {
                    e.printStackTrace()
                }
            }

        }, object : com.android.volley.Response.ErrorListener {
            override fun onErrorResponse(error: VolleyError?) {

            }
        }) {

            override fun parseNetworkError(volleyError: VolleyError): VolleyError {
                var volleyError = volleyError
                // TODO Auto-generated method stub
                if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                    val error = VolleyError(String(volleyError.networkResponse.data))
                    volleyError = error
                }
                return volleyError
            }

            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                Log.d("ID IN",""+id)
                params.put("user_id", id!!)
                params.put("start_date", startDate!!)
                params.put("end_date", endDate!!)
                //params.put("PreParture-APIKEY", "QWERTYUIOPASDFGHJKLZXCVBNMQWERTYUIOPASDF");
                //params.put("CONNECTEX-APIKEY", "QWERTYUIOPASDFGHJKLZXCVBNMQWERTYUIOPASDF");
                println("forgot password parameter--> " + params.toString())
                return params
            }
        }
        postRequest.tag = "Grafity"
        postRequest.retryPolicy = DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        queue.add(postRequest)


        /*call = apiInterface?.EXPORT_PDF_CALL(Graffiti.userId.toString(),startDate,endDate)
        call?.enqueue(object :Callback<ResponseBody>{
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                if (response != null && response.isSuccessful){
                    Log.d("sample",response.body()?.string())
                    try {
                        val data:String= response.body()?.string().toString()
                        Toast.makeText(context,data,Toast.LENGTH_SHORT).show()

                        val jsonObject=JSONObject(response.body()?.string());
                        val url=jsonObject.getJSONObject("data").getString("url");
                        *//*myResponse=Gson().fromJson(response.body()?.string(),MyResponse::class.java)
                        if (myResponse?.status==1){
//                        DownloadFile().execute(myResponse?.data?.url, "Graffiti.pdf")
                            Toast.makeText(context,myResponse?.message?.success+myResponse?.data?.url,Toast.LENGTH_LONG).show()
                        }else{
                            //Toast.makeText(context,myResponse?.message?.success,Toast.LENGTH_LONG).show()
                        }*//*
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } catch (e: JsonSyntaxException) {
                        e.printStackTrace()
                    }


                }
            }

        })*/
    }

    private fun updateStartLabel() {
        val myFormat = "dd-MM-yyyy" //In which you need put here
        val sdf = SimpleDateFormat(myFormat, Locale.US)

        et_startDate.setText("From " + sdf.format(myCalendar.getTime()))
        startDate = sdf.format(myCalendar.getTime())
    }

    private fun updateEndLabel() {
        val myFormat = "dd-MM-yyyy" //In which you need put here
        val sdf = SimpleDateFormat(myFormat, Locale.US)

        et_endDate.setText("To " + sdf.format(myCalendar1.getTime()))
        endDate = sdf.format(myCalendar1.getTime())
    }

    private inner class DownloadFile : AsyncTask<Void?, Void?, Void?>() {

        override fun onPreExecute() {
            super.onPreExecute()
            //showDialog()
        }

        override fun doInBackground(vararg strings: Void?): Void? {

            val fileUrl = url
            // -> https://letuscsolutions.files.wordpress.com/2015/07/five-point-someone-chetan-bhagat_ebook.pdf
            val fileName = "Graffiti.xlsx"
            // ->five-point-someone-chetan-bhagat_ebook.pdf
            val extStorageDirectory = Environment.getExternalStorageDirectory().toString()
            val folder = File(extStorageDirectory, "Graffiti")
            folder.mkdir()

            val pdfFile = File(folder, fileName)

            try {
                pdfFile.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            FileDownloader.downloadFile(fileUrl, pdfFile)
            return null

        }

        override fun onPostExecute(aVoid: Void?) {
            super.onPostExecute(aVoid)
            //hideDialog()
            Toast.makeText(context, "Download PDf successfully", Toast.LENGTH_SHORT).show()

            Log.d("Download complete", "----------")
        }
    }

    private fun showDialog() {
        if (pDialog.visibility == View.GONE)
            pDialog.visibility = View.VISIBLE
    }

    private fun hideDialog() {
        if (pDialog.visibility == View.VISIBLE)
            pDialog.visibility = View.GONE
    }
}