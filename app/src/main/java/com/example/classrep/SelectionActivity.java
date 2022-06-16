package com.example.classrep;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.classrep.adapter.InstituteAdapter;
import com.example.classrep.database.ClassRepDB;
import com.example.classrep.database.entity.Institute;
import com.example.classrep.utilities.SingleToneClass;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class SelectionActivity extends AppCompatActivity implements InstituteAdapter.onInstituteListener {

    private List<Institute> listInstitutes;
    private InstituteAdapter adapter;
    private RecyclerView recycle;
    private MaterialToolbar topAppbar;

    private ClassRepDB db;

    private FloatingActionButton add;

    private int pageHeight = 1120;
    private int pagewidth = 792;

    Bitmap bmp, scaledbmp;

    private static final int PERMISSION_REQUEST_CODE = 200;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        topAppbar = findViewById(R.id.topAppBar);

        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.hqdefault);
        scaledbmp = Bitmap.createScaledBitmap(bmp, 140, 140, false);
        //listInstitutes.add(new Institute(1, "bruh1", "bruhone", ""));

        db = ClassRepDB.getDatabase(SelectionActivity.this);

        recycle = findViewById(R.id.institute);

        add = findViewById(R.id.addInstitute);

        AsyncTask.execute(()->{
            //db.ClassRepDAO().insertInstitute(new Institute(1, "bruh", "bruh1", "", Date.from(Calendar.getInstance().toInstant())));
            //db.ClassRepDAO().insertMeeting(new Meeting(1, "Prova", "scolastica", Date.from(Calendar.getInstance().toInstant()),"", ""));
            listInstitutes = db.ClassRepDAO().getAllInstitute();
            createRecycler();
        });

        add.setOnClickListener(view -> {
            //daterrima = riunione.get(0).getDate();
            //Calendar calendario = Calendar.getInstance();
            //calendario.setTime(daterrima);
            Intent intent = new Intent(this, AddInstituteActivity.class);
            startActivity(intent);
            //Toast.makeText(getBaseContext(), calendario.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ITALY), Toast.LENGTH_SHORT).show();
//             AsyncTask.execute(()->{
//                 db.ClassRepDAO().insertInstitute(new Institute(1, "bruh", "bruh1", "", Date.from(Calendar.getInstance().toInstant())));
//             });
//            Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
//
//            String nomePDF = generatePDF();
//            File pdfFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+nomePDF);
//            Uri uriPdf = FileProvider.getUriForFile(getBaseContext(), getBaseContext().getApplicationContext().getPackageName() + ".provider", pdfFile);
//
//            pdfIntent.setDataAndType(uriPdf, "application/pdf");
//            pdfIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            startActivity(pdfIntent);

            System.out.println(listInstitutes.isEmpty());
        });

        topAppbar.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()){
                case R.id.trash:
                    MenuItem deselectAll = topAppbar.getMenu().findItem(R.id.deselectAll);
                    deselectAll.setVisible(true);
                    MenuItem selectAll = topAppbar.getMenu().findItem(R.id.selectAll);
                    selectAll.setVisible(true);

                    add.setImageResource(R.drawable.ic_open_trashcan);
                    break;
                case R.id.deselectAll:
                    break;
                case R.id.selectAll:
                    break;
            }
            return false;
        });

        //Toast.makeText(getBaseContext(), String.valueOf(singleToneClass.getData("institute")), Toast.LENGTH_SHORT).show();

    }

    private void createRecycler(){

        if(listInstitutes.isEmpty()){
            TextView empty = findViewById(R.id.empty);
            empty.setVisibility(View.VISIBLE);
        } else {
            adapter = new InstituteAdapter(getApplicationContext(), listInstitutes, this);
            recycle.setAdapter(adapter);
        }
    }

    @Override
    public void onInstituteClick(int position) {
        Institute item = listInstitutes.get(position);

        SingleToneClass singleToneClass = com.example.classrep.utilities.SingleToneClass.getInstance();
        singleToneClass.setData("institute",item.getId_institute());

        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    //PER LA CREAZIONE DEL PDF
    //POTREI FARE CHE ACCETTA COME INGRESSO UNA MAPPA CON CHIAVI E VALORI
    //TITOLO, NOTE(?), DATA, RAPPORTO

    public String generatePDF(){

        // creating an object variable
        // for our PDF document.
        PdfDocument pdfDocument = new PdfDocument();

        // two variables for paint "paint" is used
        // for drawing shapes and we will use "title"
        // for adding text in our PDF file.
        Paint paint = new Paint();
        Paint title = new Paint();

        // we are adding page info to our PDF file
        // in which we will be passing our pageWidth,
        // pageHeight and number of pages and after that
        // we are calling it to create our PDF.
        PdfDocument.PageInfo mypageInfo = new PdfDocument.PageInfo.Builder(pagewidth, pageHeight, 1).create();

        // below line is used for setting
        // start page for our PDF file.
        PdfDocument.Page myPage = pdfDocument.startPage(mypageInfo);

        // creating a variable for canvas
        // from our page of PDF.
        Canvas canvas = myPage.getCanvas();

        // below line is used to draw our image on our PDF file.
        // the first parameter of our drawbitmap method is
        // our bitmap
        // second parameter is position from left
        // third parameter is position from top and last
        // one is our variable for paint.
        canvas.drawBitmap(scaledbmp, 56, 40, paint);

        // below line is used for adding typeface for
        // our text which we will be adding in our PDF file.
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));

        // below line is used for setting text size
        // which we will be displaying in our PDF file.
        title.setTextSize(15);

        // below line is sued for setting color
        // of our text inside our PDF file.
        title.setColor(ContextCompat.getColor(this, R.color.black));

        // below line is used to draw text in our PDF file.
        // the first parameter is our text, second parameter
        // is position from start, third parameter is position from top
        // and then we are passing our variable of paint which is title.
        canvas.drawText("oka-san.....", 209, 100, title);
        canvas.drawText("Anya samishi", 209, 80, title);

        // similarly we are creating another text and in this
        // we are aligning this text to center of our PDF file.
        title.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        title.setColor(ContextCompat.getColor(this, R.color.purple_200));
        title.setTextSize(15);

        // below line is used for setting
        // our text to center of PDF.
        title.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("This is Anya, say hi to samishi Anya", 396, 560, title);

        // after adding all attributes to our
        // PDF file we will be finishing our page.
        pdfDocument.finishPage(myPage);

        // below line is used to set the name of
        // our PDF file and its path.
        File file = new File(Environment.getExternalStorageDirectory(), "GFG.pdf");

        try {
            // after creating a file name we will
            // write our PDF file to that location.
            pdfDocument.writeTo(new FileOutputStream(file));

            // below line is to print toast message
            // on completion of PDF generation.
            Toast.makeText(SelectionActivity.this, "PDF file generated successfully.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            // below line is used
            // to handle error
            e.printStackTrace();
        }
        // after storing our pdf to that
        // location we are closing our PDF file.
        pdfDocument.close();

        return "GFG.pdf"; //ritornare il nome del file per poter prendere lo URI
    }





    //PER LE RICHIESTE DI SCRITTURA E LETTURA

    private boolean checkPermission() {
        // checking of permissions.
        int permission1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        // requesting permissions if not provided.
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {

                // after requesting permissions we are showing
                // users a toast message of permission granted.
                boolean writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (writeStorage && readStorage) {
                    Toast.makeText(this, "Permission Granted..", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Denined.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

}