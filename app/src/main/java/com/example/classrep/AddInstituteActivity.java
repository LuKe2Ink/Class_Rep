package com.example.classrep;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.classrep.database.ClassRepDB;
import com.example.classrep.database.entity.Institute;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddInstituteActivity extends AppCompatActivity {

    private int institute_index;
    private ClassRepDB db;

    private ImageView imageView;
    private CircleImageView showcase;
    private String fileUriString = "nada";

    private FloatingActionButton add;

    private static final int CAMERA_REQUEST = 100;
    private static final int STORAGE_REQUEST = 200;
    String cameraPermission[];
    String storagePermission[];


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_institute);

        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        add = findViewById(R.id.insertInstitute);
        imageView = findViewById(R.id.creationIcon);
        showcase = (CircleImageView) findViewById(R.id.showcase);

        db = ClassRepDB.getDatabase(AddInstituteActivity.this);
        //Toast.makeText(getBaseContext(), String.valueOf(globalVariables.getData("institute")), Toast.LENGTH_SHORT).show();

        AsyncTask.execute(()->{
            institute_index = db.ClassRepDAO().getMaxIdInstitute();
            institute_index++;
            //db.ClassRepDAO().insertInstitute(new Institute(institute_index, "bruh1", "bruh2", ""));
        });

        add.setOnClickListener( view ->{
            EditText editInstituteName = findViewById(R.id.editInstituteName);
            EditText editInstituteGrade = findViewById(R.id.editInstituteGrade);
            String instituteName = editInstituteName.getText().toString();
            String instituteGrade = editInstituteGrade.getText().toString();

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

            AsyncTask.execute(()->{
                db.ClassRepDAO().insertInstitute(new Institute(institute_index, instituteName, instituteGrade, fileUriString, Date.from(Calendar.getInstance().toInstant())));
                Intent intent = new Intent(getBaseContext(), SelectionActivity.class);
                startActivity(intent);
            });
        });

        imageView.setOnClickListener(view -> lunchPhoto());

    }


    public void onBackPressed() {

        new AlertDialog.Builder(AddInstituteActivity.this)
            .setTitle("Stai tornando indietro")
            .setMessage("Sei sicuro di non voler più inserire una classe?")

                //se clicca si
            .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                Intent intent = new Intent(getBaseContext(), SelectionActivity.class);
                startActivity(intent);
            })
                //se clicca no
            .setNegativeButton(android.R.string.no,null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .create()
            .show();
        //alert box
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
        CropImage.activity().start(AddInstituteActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                fileUriString = result.getUri().toString();
                showcase.setVisibility(View.VISIBLE);
                Picasso.with(this).load(Uri.parse(fileUriString)).into(showcase);
                imageView.setVisibility(View.INVISIBLE);
            }
        }
    }
}