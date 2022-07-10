package com.example.classrep;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.classrep.adder.AddInstituteActivity;
import com.example.classrep.database.ClassRepDB;
import com.example.classrep.database.entity.Settings;
import com.example.classrep.utilities.SingleToneClass;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private int idInstitute;
    private ClassRepDB db;
    private Settings settings;
    private String fileUriString;
    private SingleToneClass singleToneClass;


    private Switch notification;
    private Switch notificationYear;
    private Button changBackground;
    private CircleImageView icon;
    private ExtendedFloatingActionButton fab;


    private static final int CAMERA_REQUEST = 100;
    private static final int STORAGE_REQUEST = 200;
    String cameraPermission[];
    String storagePermission[];
    private MaterialToolbar topAppbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        singleToneClass = com.example.classrep.utilities.SingleToneClass.getInstance();
        idInstitute = singleToneClass.getData("institute");
        db = ClassRepDB.getDatabase(this);
        icon = findViewById(R.id.profileIconChange);
        notification = findViewById(R.id.notificheSwitch);
        notificationYear = findViewById(R.id.notificaAnnoSwitch);

        fab = findViewById(R.id.fabReset);

        AsyncTask.execute(()->{
            settings = db.ClassRepDAO().getSetting(idInstitute);
            this.runOnUiThread(()->{
                notification.setChecked(settings.isNotification());
                notificationYear.setChecked(settings.isLast_notification());
            });
            String uriString = db.ClassRepDAO().getInstitute(idInstitute).getImage();
            if(uriString.contains("nada")){
                icon.setImageResource(R.drawable.splashscreen);
            } else {
                icon.setImageURI(Uri.parse(uriString));
            }
        });

        topAppbar = findViewById(R.id.topAppBarSetting);
        changBackground = findViewById(R.id.changeBackground);

        notificationYear.setOnCheckedChangeListener((view, check)->{
            settings.setLast_notification(check);
            AsyncTask.execute(()->{
                db.ClassRepDAO().updateSetting(settings);
            });
        });

        notification.setOnCheckedChangeListener((view, check)->{
            settings.setNotification(check);
            AsyncTask.execute(()->{
                db.ClassRepDAO().updateSetting(settings);
            });
        });


        changBackground.setOnClickListener(view->{
            lunchPhoto();
        });


        topAppbar.setNavigationOnClickListener(view->{
            Intent intent = new Intent(this, HomeActivity.class);
            intent.putExtra("fragment", "calendar");
            startActivity(intent);
        });

        fab.setOnClickListener(view ->{
            fileUriString = "nada";
            settings.setImage(fileUriString);
            singleToneClass.setImageBackground(fileUriString);
            settings.setNotification(true);
            settings.setLast_notification(true);
            AsyncTask.execute(()->{
                db.ClassRepDAO().updateSetting(settings);
            });
        });
    }


    private void lunchPhoto() {
        if (!checkCameraPermission()) {
            requestCameraPermission();
        } else if (!checkStoragePermission()){
            requestStoragePermission();
        } else {
            pickFromGallery();
        }
    }

    // checking storage permissions
    private Boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    // Requesting  gallery permission
    private void requestStoragePermission() {
        requestPermissions(storagePermission, STORAGE_REQUEST);
    }

    // checking camera permissions
    private Boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    // Requesting camera permission
    private void requestCameraPermission() {
        requestPermissions(cameraPermission, CAMERA_REQUEST);
    }

    // Requesting camera and gallery
    // permission if not given
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_REQUEST: {
                if (grantResults.length > 0) {
                    boolean camera_accepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageaccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (camera_accepted && writeStorageaccepted) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(this, "Please Enable Camera and Storage Permissions", Toast.LENGTH_LONG).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST: {
                if (grantResults.length > 0) {
                    boolean writeStorageaccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageaccepted) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(this, "Please Enable Storage Permissions", Toast.LENGTH_LONG).show();
                    }
                }
            }
            break;
        }
    }

    // Here we will pick image from gallery or camera
    private void pickFromGallery() {
        CropImage.activity().start(ProfileActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                fileUriString = result.getUri().toString();
                singleToneClass.setImageBackground(fileUriString);
                settings.setImage(fileUriString);
                AsyncTask.execute(()->{
                    db.ClassRepDAO().updateSetting(settings);
                });

                if (!fileUriString.contains("nada")) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(fileUriString));
                        ContextWrapper cw = new ContextWrapper(getApplicationContext());
                        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
                        File file = new File(directory, Calendar.getInstance().getTimeInMillis() + ".png");

                        fileUriString = file.toURI().toString();

                        if (!file.exists()) {
                            Log.d("path", file.toString());
                            FileOutputStream fos = null;
                            fos = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                            fos.flush();
                            fos.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    fileUriString = "nada";
                }

                Toast.makeText(getBaseContext(), "Il background è stato applicato", Toast.LENGTH_SHORT).show();

            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("fragment", "calendar");
        startActivity(intent);
    }
}