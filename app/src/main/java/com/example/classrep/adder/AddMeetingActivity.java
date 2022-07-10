package com.example.classrep.adder;

import static com.example.classrep.utilities.NotificationService.NOTIFICATION_CHANNEL_ID;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.example.classrep.HomeActivity;
import com.example.classrep.MainActivity;
import com.example.classrep.R;
import com.example.classrep.SelectionActivity;
import com.example.classrep.database.ClassRepDB;
import com.example.classrep.database.entity.Adhesion;
import com.example.classrep.database.entity.Child;
import com.example.classrep.database.entity.Event;
import com.example.classrep.database.entity.Meeting;
import com.example.classrep.database.entity.PTAmeeting;
import com.example.classrep.database.entity.Parent;
import com.example.classrep.database.entity.Settings;
import com.example.classrep.utilities.NotificationService;
import com.example.classrep.utilities.SingleToneClass;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class AddMeetingActivity extends AppCompatActivity {
    private final static String default_notification_channel_id = "default" ;

    private ClassRepDB db;
    private String tipo = "scolastico";

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


    private MaterialToolbar topAppbar;
    private BlurView blurView;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meeting);

        SingleToneClass singleToneClass = com.example.classrep.utilities.SingleToneClass.getInstance();
        item = singleToneClass.getData("institute");

        if(!singleToneClass.getImageBackground().contains("nada")){
            ConstraintLayout background = findViewById(R.id.backgroundAddMeeting);

            Uri uri = Uri.parse(singleToneClass.getImageBackground());
            Bitmap bmImg = null;
            try {
                bmImg = BitmapFactory.decodeStream( getContentResolver().openInputStream(uri));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            BitmapDrawable background1 = new BitmapDrawable(bmImg);
            background.setBackground(background1);
        }

        db = ClassRepDB.getDatabase(AddMeetingActivity.this);
        blurView = findViewById(R.id.blurViewAddMeeting);
        backgroundBlur();

        title = findViewById(R.id.titoloMeeting);
        place = findViewById(R.id.postoMeeting);
        note = findViewById(R.id.notaMeeting);
        date = findViewById(R.id.giornoMeeting);
        report = findViewById(R.id.reportMeet);

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

        chipScolastico = findViewById(R.id.chip1);
        chipExtaScolastico = findViewById(R.id.chip2);

        chipScolastico.setOnClickListener(view->{
            tipo = "scolastico";
        });
        chipExtaScolastico.setOnClickListener(view->{
            tipo = "extra-scolastico";
        });

        topAppbar = findViewById(R.id.meetingTopAppBar);

        topAppbar.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()){
                case R.id.cancelAll:
                    title.setText("");
                    date.setText("");
                    place.setText("");
                    note.setText("");
                    chipExtaScolastico.setChecked(false);
                    chipScolastico.setChecked(true);
                    break;
                case R.id.confirm:
                    if(title.getText().toString().isEmpty() ||
                            date.getText().toString().isEmpty() ||
                            place.getText().toString().isEmpty() ||
                            note.getText().toString().isEmpty()){
                        new androidx.appcompat.app.AlertDialog.Builder(AddMeetingActivity.this)
                                .setTitle("Ci sono alcuni elementi mancanti")
                                //se clicca ok
                                .setPositiveButton(android.R.string.yes, null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .create()
                                .show();
                    } else {
                        verifyDate();
                    }
                    break;
            }
            return false;
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void verifyDate(){
        Instant instatNow = Calendar.getInstance().toInstant();
        Instant instatTaken = null;
        try {
            instatTaken = new SimpleDateFormat("dd/MM/yyyy").parse(date.getText().toString()).toInstant();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(instatNow.isAfter(instatTaken)){
            new androidx.appcompat.app.AlertDialog.Builder(AddMeetingActivity.this)
                    .setTitle("La data inserita è prima o corrispondente a quella odierna")
                    .setMessage("Vuoi comunque aggiungere la riunione?")
                    //se clicca ok
                    .setPositiveButton("Si", (dialog, which)->{
                        addEvent();
                    })
                    .setNegativeButton("No", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .create()
                    .show();
        } else {
            addEvent();
        }
    }

    private void addEvent(){
        System.out.println("entra");

        AsyncTask.execute(()->{
            int maxid = db.ClassRepDAO().getMaxIdMeeting() + 1;
            Meeting meeting;
            try {
                Date data = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(date.getText().toString());
                System.out.println(data);
                meeting = new Meeting(maxid, item, title.getText().toString(), tipo, data
                        , place.getText().toString(),note.getText().toString(), report.getText().toString());
                db.ClassRepDAO().insertMeeting(meeting);
            } catch (ParseException e) {
                System.out.println(e);
            }

            Settings setting = db.ClassRepDAO().getSetting(item);
            if(setting.isNotification()){
                Date daterrima = null;
                try {
                    daterrima = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(date.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                System.out.println(daterrima);

                scheduleNotification(getNotification() , daterrima.getTime()) ;
            }

            callIntent();
        });
    }

    private void scheduleNotification (Notification notification , long delay) {
        Intent notificationIntent = new Intent( this, NotificationService.class ) ;
        notificationIntent.putExtra(NotificationService.NOTIFICATION_ID , 0 ) ;
        notificationIntent.putExtra(NotificationService.NOTIFICATION , notification) ;
        PendingIntent pendingIntent = PendingIntent.getBroadcast ( this, 0 , notificationIntent , PendingIntent.FLAG_UPDATE_CURRENT ) ;
        AlarmManager alarmManager = (AlarmManager) getSystemService(getBaseContext().ALARM_SERVICE ) ;
        assert alarmManager != null;
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP , delay , pendingIntent);
    }
    private Notification getNotification () {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder( this, default_notification_channel_id ) ;
        builder.setContentTitle( "Scheduled Notification" ) ;
        builder.setContentText("L'incontro "+title.getText().toString()+" "+tipo+" sta per iniziare") ;
        builder.setSmallIcon(R.drawable.logo) ;
        builder.setAutoCancel( true ) ;
        builder.setContentIntent(pendingIntent);
        builder.setChannelId( NOTIFICATION_CHANNEL_ID ) ;
        return builder.build() ;
    }

    @Override
    public void onBackPressed() {
        new androidx.appcompat.app.AlertDialog.Builder(AddMeetingActivity.this)
                .setTitle("Stai tornando indietro")
                .setMessage("Sei sicuro di non voler più inserire una riunione?")
                //se clicca si
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    callIntent();
                })
                //se clicca no
                .setNegativeButton(android.R.string.no,null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .create()
                .show();

    }

    public void callIntent(){
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("fragment", "meeting");
        startActivity(intent);
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
}