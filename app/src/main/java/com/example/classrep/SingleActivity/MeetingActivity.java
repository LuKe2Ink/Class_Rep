package com.example.classrep.SingleActivity;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.classrep.HomeActivity;
import com.example.classrep.R;
import com.example.classrep.SelectionActivity;
import com.example.classrep.adder.AddMeetingActivity;
import com.example.classrep.database.ClassRepDB;
import com.example.classrep.database.entity.Meeting;
import com.example.classrep.database.entity.PTAmeeting;
import com.example.classrep.database.entity.Parent;
import com.example.classrep.utilities.SingleToneClass;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class MeetingActivity extends AppCompatActivity {

    private ClassRepDB db;
    private String tipo = "scolastico";
    private Meeting meeting;

    private TextInputEditText title;
    private TextInputEditText date;
    private TextInputEditText place;
    private TextInputEditText note;
    private TextInputEditText report;

    private Chip chipScolastico;
    private Chip chipExtaScolastico;
    int item;

    int hours;
    int minutes;
    boolean modify = false;


    private MaterialToolbar topAppbar;
    private BlurView blurView;


    private int pageHeight = 1120;
    private int pagewidth = 792;

    Bitmap bmp, scaledbmp;
    private static final int PERMISSION_REQUEST_CODE = 200;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting);


        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        scaledbmp = Bitmap.createScaledBitmap(bmp, 140, 140, false);


        SingleToneClass singleToneClass = com.example.classrep.utilities.SingleToneClass.getInstance();
        item = singleToneClass.getData("meeting");

        db = ClassRepDB.getDatabase(MeetingActivity.this);
        blurView = findViewById(R.id.blurViewMeeting);
        backgroundBlur();

        title = findViewById(R.id.titoloModificaMeeting);
        place = findViewById(R.id.postoModificaMeeting);
        note = findViewById(R.id.notaModificaMeeting);
        date = findViewById(R.id.giornoModificaMeeting);
        report = findViewById(R.id.reportMeetingModify);

        chipScolastico = findViewById(R.id.chipMeeting);
        chipExtaScolastico = findViewById(R.id.chipMeeting1);

        Intent intent = this.getIntent();
        String jsMeeting;
        jsMeeting = intent.getStringExtra("meeting");
        Type listTypeEvent = new TypeToken<Meeting>() {}.getType();
        meeting = new Gson().fromJson(String.valueOf(jsMeeting), listTypeEvent);

        setAll();
        modifyEnable(modify);

        date.setOnClickListener(view ->{
            DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                    date.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                    TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int Selectedhour, int Selectedminutes) {
                            int hours = Selectedhour;
                            int minutes = Selectedminutes;
                            date.setText(date.getText().toString()+" "+String.format(Locale.ITALY, "%02d:%02d", hours, minutes));
                        }
                    };

                    int style = AlertDialog.THEME_HOLO_DARK;

                    TimePickerDialog timePickerDialog = new TimePickerDialog(date.getContext(), style, onTimeSetListener, hours, minutes, true);

                    timePickerDialog.setTitle("Scegli l'orario");
                    timePickerDialog.show();
                }
            };
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, onDateSetListener,
                    Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH),
                    Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

            datePickerDialog.show();
        });

        chipScolastico.setOnClickListener(view->{
            tipo = "scolastico";
        });
        chipExtaScolastico.setOnClickListener(view->{
            tipo = "extra-scolastico";
        });

        topAppbar = findViewById(R.id.meetingTopBar);

        if(!meeting.getReport().isEmpty()){
            MenuItem pdf = topAppbar.getMenu().findItem(R.id.pdf);
            pdf.setVisible(true);
        }

        topAppbar.setOnMenuItemClickListener(menuItem->{
            switch (menuItem.getItemId()){
                case R.id.send:
                    Intent intento = new Intent(Intent.ACTION_SEND);

                    intento.setType("text/plain");
                    intento.setPackage("com.whatsapp");
                    String messaggio=meeting.toTesto()+"\n";
                    intento.putExtra(Intent.EXTRA_TEXT, messaggio);
                    startActivity(intento);
                    break;
                case R.id.edit:
                    modify = true;
                    menuItem.setVisible(false);
                    MenuItem cancel = topAppbar.getMenu().findItem(R.id.cancel);
                    cancel.setVisible(true);
                    MenuItem confirm = topAppbar.getMenu().findItem(R.id.confirmEdit);
                    confirm.setVisible(true);
                    MenuItem send = topAppbar.getMenu().findItem(R.id.send);
                    send.setVisible(false);

                    modify = true;
                    modifyEnable(modify);
                    break;
                case R.id.cancel:
                    modify = false;
                    setAll();
                    menuItem.setVisible(false);
                    MenuItem cancel1 = topAppbar.getMenu().findItem(R.id.confirmEdit);
                    cancel1.setVisible(false);
                    MenuItem edit = topAppbar.getMenu().findItem(R.id.edit);
                    edit.setVisible(true);
                    MenuItem send1 = topAppbar.getMenu().findItem(R.id.send);
                    send1.setVisible(true);

                    title.setEnabled(false);
                    place.setEnabled(false);
                    date.setEnabled(false);
                    note.setEnabled(false);
                    break;
                case R.id.confirmEdit:

                    menuItem.setVisible(false);
                    MenuItem cancel2 = topAppbar.getMenu().findItem(R.id.cancel);
                    cancel2.setVisible(false);
                    MenuItem edit1 = topAppbar.getMenu().findItem(R.id.edit);
                    edit1.setVisible(true);
                    MenuItem send2 = topAppbar.getMenu().findItem(R.id.send);
                    send2.setVisible(true);
                    modify = false;

                    AsyncTask.execute(()->{
                        if(isAllOk()){
                            Meeting substitute = createMeeting();
                            if(substitute.getTitle()!=meeting.getTitle()
                                    ||substitute.getNote()!=meeting.getNote()
                                    ||!substitute.getDate().equals(meeting.getDate())
                                    ||substitute.getPlace()!=meeting.getPlace()
                                    ||substitute.getReport()!=meeting.getReport())
                            {
                                meeting = substitute;
                                db.ClassRepDAO().updateMeeting(meeting);
                                this.runOnUiThread(()->{modifyEnable(modify);});
                            }
                        }
                        this.runOnUiThread(()->{setAll();});
                    });
                    break;
                case R.id.pdf:
                    String pdfName = generatePDF();
                    Intent intent1 = new Intent(Intent.ACTION_VIEW);
                    File pdfFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+pdfName);
                    Uri uriPdf = FileProvider.getUriForFile(getBaseContext(), getBaseContext().getApplicationContext().getPackageName() + ".provider", pdfFile);

                    intent1.setDataAndType(uriPdf, "application/pdf");
                    intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent1.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent1);
                    break;
            }
            return false;
        });
    }
    public void backgroundBlur(){
        float radius = 5f;

        View decorView = getWindow().getDecorView();
        //ViewGroup you want to start blur from. Choose root as close to BlurView in hierarchy as possible.
        ViewGroup rootView = decorView.findViewById(android.R.id.content);
        //Set drawable to draw in the beginning of each blurred frame (Optional).
        //Can be used in case your layout has a lot of transparent space and your content
        //gets kinda lost after after blur is applied.
        Drawable windowBackground = decorView.getBackground();

        blurView.setupWith(rootView)
                .setFrameClearDrawable(windowBackground)
                .setBlurAlgorithm(new RenderScriptBlur(this))
                .setBlurRadius(radius)
                .setHasFixedTransformationMatrix(true);
    }

    boolean rispostona;
    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean isAllOk(){
        rispostona = false;
        if(title.getText().toString().isEmpty() ||
                date.getText().toString().isEmpty() ||
                place.getText().toString().isEmpty() ||
                note.getText().toString().isEmpty()){
            this.runOnUiThread(()->{new androidx.appcompat.app.AlertDialog.Builder(MeetingActivity.this)
                    .setTitle("Ci sono alcuni elementi mancanti")
                    //se clicca ok
                    .setPositiveButton(android.R.string.yes, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .create()
                    .show();});
        } else {
            rispostona = true;
        }
        return rispostona;
    }

    private void setAll(){
        title.setText(meeting.getTitle());
        place.setText(meeting.getPlace());
        String pattern = "dd/MM/yyyy HH:mm";
        SimpleDateFormat simpleDate = new SimpleDateFormat(pattern);
        String dateAll = simpleDate.format(meeting.getDate());
        date.setText(dateAll);
        note.setText(meeting.getNote());
        if (tipo.contains("scolastico")) {
            chipScolastico.setChecked(true);
        } else {
            chipExtaScolastico.setChecked(true);
        }
        report.setText(meeting.getReport());
    }

    public void modifyEnable(boolean bool){
        title.setEnabled(bool);
        place.setEnabled(bool);
        date.setEnabled(bool);
        note.setEnabled(bool);
        chipScolastico.setEnabled(bool);
        chipExtaScolastico.setEnabled(bool);
        report.setEnabled(bool);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Meeting createMeeting(){

        Date daterrima = null;
        try {
            daterrima = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(date.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return new Meeting(meeting.getId_meeting(),
                meeting.getForeign_institute(),
                title.getText().toString(),
                tipo,
                daterrima,
                place.getText().toString(),
                note.getText().toString(),
                report.getText().toString());
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("fragment", "meeting");
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
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
        title.setColor(ContextCompat.getColor(this, R.color.purple_700));
        title.setTextSize(60);

        // below line is sued for setting color
        // of our text inside our PDF file.

        // below line is used to draw text in our PDF file.
        // the first parameter is our text, second parameter
        // is position from start, third parameter is position from top
        // and then we are passing our variable of paint which is title.
        canvas.drawText("ClassRep", 220, 90, title);

        // similarly we are creating another text and in this
        // we are aligning this text to center of our PDF file.
        title.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        title.setColor(ContextCompat.getColor(this, R.color.black));
        title.setTextSize(20);

        // below line is used for setting
        // our text to center of PDF.
        title.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(meeting.getTitle(), 396, 300, title);

        canvas.drawText(meeting.getType(), 396, 300, title);

        canvas.drawText(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(meeting.getDate()), 396, 300, title);

        canvas.drawText(meeting.getPlace(), 396, 300, title);

        canvas.drawText(meeting.getNote(), 396, 300, title);

        canvas.drawText(meeting.getTitle(), 396, 300, title);
        title.set


        // after adding all attributes to our
        // PDF file we will be finishing our page.
        pdfDocument.finishPage(myPage);

        // below line is used to set the name of
        // our PDF file and its path.
        String uniqueString = UUID.randomUUID().toString()+".pdf";
        File file = new File(Environment.getExternalStorageDirectory(), uniqueString);

        try {
            // after creating a file name we will
            // write our PDF file to that location.
            pdfDocument.writeTo(new FileOutputStream(file));

            // below line is to print toast message
            // on completion of PDF generation.
            Toast.makeText(MeetingActivity.this, "PDF file generated successfully.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            // below line is used
            // to handle error
            e.printStackTrace();
        }
        // after storing our pdf to that
        // location we are closing our PDF file.
        pdfDocument.close();

        return uniqueString; //ritornare il nome del file per poter prendere lo URI
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