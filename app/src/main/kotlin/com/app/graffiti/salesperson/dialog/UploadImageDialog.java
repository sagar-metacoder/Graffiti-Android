package com.app.graffiti.salesperson.dialog;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.app.graffiti.BuildConfig;
import com.app.graffiti.R;
import com.app.graffiti.utils.InternalStorageContentProvider;

import java.io.File;

public class UploadImageDialog extends BottomSheetDialog {

    private File mFileTemp;
    Button gallerybtn, camerabtn;
    private Activity context;
    public static final int selectFromGallery = 1;
    public static final int selectFromCamera = 2;
    public static final int REQUEST_CODE=123;

    public UploadImageDialog(@NonNull Activity context, File mFileTemp) {
        super(context);
        this.context = context;
        this.mFileTemp=mFileTemp;
    }

    public UploadImageDialog(@NonNull Activity context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_address);
        gallerybtn = findViewById(R.id.btn_gallery);
        camerabtn = findViewById(R.id.btn_camera);
        gallerybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                photoPickerIntent.setType("image/*");
                context.startActivityForResult(photoPickerIntent, selectFromGallery);
            }
        });

        camerabtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    try {
                        Uri mImageCaptureUri = null;
                        String state = Environment.getExternalStorageState();
                        if (Environment.MEDIA_MOUNTED.equals(state)) {
//                        mImageCaptureUri = Uri.fromFile(mFileTemp);
                            mImageCaptureUri = getUri(context,mFileTemp);
                        } else {
                            mImageCaptureUri = InternalStorageContentProvider.CONTENT_URI;
                        }
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                        intent.putExtra("return-data", true);

                        context.startActivityForResult(intent, selectFromCamera);
                    } catch (ActivityNotFoundException e) {
                        Log.d("cannot take picture", "cannot take picture", e);
                    }
                }
        });
    }
    public static Uri getUri(Context context, File photoFile) {
        if (Build.VERSION.SDK_INT >= 24) {
            return FileProvider.getUriForFile(context,
                    BuildConfig.APPLICATION_ID + ".provider",
                    photoFile);
        } else {
            return Uri.fromFile(photoFile);
        }
    }



}
