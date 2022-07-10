package com.example.classrep.adder;

import static com.example.classrep.utilities.NotificationService.NOTIFICATION_CHANNEL_ID;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;

import com.example.classrep.HomeActivity;
import com.example.classrep.MainActivity;
import com.example.classrep.R;
import com.example.classrep.adapter.PersonAdapter;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class AddPtaActivity extends AppCompatActivity {
    private final static String default_notification_channel_id = "default" ;


    private PersonAdapter parentAdapter;
    private List<Integer> parents = new ArrayList<>();

    private int parentsCount = 1;

    private ClassRepDB db;

    private RecyclerView recycleParent;

    private TextInputEditText nome;
    private TextInputEditText cognome;
    private TextInputEditText date;
    private TextInputEditText data_inizio;
    private TextInputEditText data_fine;
    private TextInputEditText materie;

    private Button addParent;

    private MaterialToolbar topAppbar;

    int item;

    int hours;
    int minutes;
    int hours1;
    int minutes1;
    private BlurView blurView;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pta);

        db = ClassRepDB.getDatabase(AddPtaActivity.this);
        blurView = findViewById(R.id.blurViewAddPta);

        SingleToneClass singleToneClass = com.example.classrep.utilities.SingleToneClass.getInstance();
        item = singleToneClass.getData("institute");

        if(!singleToneClass.getImageBackground().contains("nada")){
            ConstraintLayout background = findViewById(R.id.backgroundAddPta);

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

        recycleParent = findViewById(R.id.putParentAdd);


        addParent = findViewById(R.id.addParent);

        topAppbar = findViewById(R.id.ptaAddTopAppBar);

        nome = findViewById(R.id.nomeProfAdd);
        cognome = findViewById(R.id.cognomeProfAdd);
        date = findViewById(R.id.giornoPtaAdd);
        data_inizio = findViewById(R.id.oraInizioAdd);
        data_fine = findViewById(R.id.oraFineAdd);
        materie = findViewById(R.id.subjectPtaAdd);

        addParent.setOnClickListener( view ->{
            //System.out.println(parents);
            parents.add(parentsCount);
            parentsCount++;
            parentAdapter.notifyDataSetChanged();
        });

        date.setOnClickListener(view ->{
            DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                    date.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                }
            };

            int style = AlertDialog.THEME_HOLO_DARK;

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, onDateSetListener,
                    Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH),
                    Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

            datePickerDialog.show();
        });

        data_inizio.setOnClickListener(view->{
            TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int Selectedhour, int Selectedminutes) {
                    hours = Selectedhour;
                    minutes = Selectedminutes;
                    data_inizio.setText(String.format(Locale.ITALY, "%02d:%02d", hours, minutes));
                }
            };

            int style = AlertDialog.THEME_HOLO_DARK;

            TimePickerDialog timePickerDialog = new TimePickerDialog(data_inizio.getContext(), style, onTimeSetListener, hours, minutes, true);

            timePickerDialog.setTitle("Scegli l'orario");
            timePickerDialog.show();
        });

        data_fine.setOnClickListener(view->{
            TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int Selectedhour, int Selectedminutes) {
                    hours1 = Selectedhour;
                    minutes1 = Selectedminutes;
                    data_fine.setText(String.format(Locale.ITALY, "%02d:%02d", hours1, minutes1));
                }
            };

            int style = AlertDialog.THEME_HOLO_DARK;

            TimePickerDialog timePickerDialog = new TimePickerDialog(data_fine.getContext(), style, onTimeSetListener, hours1, minutes1, true);

            timePickerDialog.setTitle("Scegli l'orario");
            timePickerDialog.show();
        });


        topAppbar.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()){
                case R.id.cancelAll:
                    parents.clear();
                    parentAdapter.notifyDataSetChanged();

                    nome.setText("");
                    cognome.setText("");
                    data_inizio.setText("");
                    data_fine.setText("");
                    materie.setText("");
                    break;
                case R.id.confirm:
                    if(nome.getText().toString().isEmpty() ||
                            date.getText().toString().isEmpty() ||
                            data_inizio.getText().toString().isEmpty() ||
                            data_fine.getText().toString().isEmpty() ||
                            materie.getText().toString().isEmpty() ||
                            cognome.getText().toString().isEmpty()){
                        new androidx.appcompat.app.AlertDialog.Builder(AddPtaActivity.this)
                                .setTitle("Ci sono alcuni elementi mancanti")
                                //se clicca ok
                                .setPositiveButton(android.R.string.yes, null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .create()
                                .show();
                    } else if(parents.isEmpty()) {
                        new androidx.appcompat.app.AlertDialog.Builder(AddPtaActivity.this)
                                .setTitle("Non hai inserito nessun genitore")
                                .setMessage("Vuoi comunque aggiungere l'evento?")
                                //se clicca ok
                                .setPositiveButton("Si", (dialog, which)->{
                                    verifyDate();
                                })
                                .setNegativeButton("No", null)
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

        createRecycler();
    }


    private void createRecycler() {
        parentAdapter = new PersonAdapter(this, "parent", false);
        parentAdapter.addPeople(parents);
        recycleParent.setAdapter(parentAdapter);
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
        //System.out.println(instatNow);
        //System.out.println(instatTaken);
        if(instatNow.isAfter(instatTaken)){
            new androidx.appcompat.app.AlertDialog.Builder(AddPtaActivity.this)
                    .setTitle("La data inserita è prima o corrispondente a quella odierna")
                    .setMessage("Vuoi comunque aggiungere il colloquio?")
                    //se clicca ok
                    .setPositiveButton("Si", (dialog, which)->{
                        addPta();
                    })
                    .setNegativeButton("No", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .create()
                    .show();
        } else {
            addPta();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addPta(){

        AsyncTask.execute(()->{
            int maxid = db.ClassRepDAO().getMaxIdPta() + 1;
            PTAmeeting pta;
            try {
                Date daterrima = new SimpleDateFormat("dd/MM/yyyy").parse(date.getText().toString());
                Date dataInizio = new SimpleDateFormat("HH:mm").parse(data_inizio.getText().toString());
                Date dataFine = new SimpleDateFormat("HH:mm").parse(data_fine.getText().toString());

                Calendar calendarioInizio = Calendar.getInstance();
                calendarioInizio.setTime(daterrima);
                calendarioInizio.set(Calendar.HOUR, dataInizio.getHours());
                calendarioInizio.set(Calendar.MINUTE, dataInizio.getMinutes());

                Settings setting = db.ClassRepDAO().getSetting(item);
                if(setting.isNotification()){
                    scheduleNotification(getNotification() , Date.from(calendarioInizio.getTime().toInstant()).getTime()) ;
                }

                Calendar calendarioFine = Calendar.getInstance();
                calendarioFine.setTime(daterrima);
                calendarioFine.set(Calendar.HOUR, dataFine.getHours());
                calendarioFine.set(Calendar.MINUTE, dataFine.getMinutes());

                //System.out.println(dataInizio.toString() +" " +dataFine.toString());

                pta = new PTAmeeting(maxid, item, nome.getText().toString(), cognome.getText().toString(),
                        Date.from(calendarioInizio.getTime().toInstant()), Date.from(calendarioFine.getTime().toInstant()),
                        materie.getText().toString());
                db.ClassRepDAO().insertPTAmeeting(pta);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if(!parents.isEmpty()){
                for (int i=0; i<parents.size(); i++){
                    View view = recycleParent.getChildAt(i);
                    EditText nome = view.findViewById(R.id.nome);
                    EditText cognome = view.findViewById(R.id.cognome);
                    EditText orario = view.findViewById(R.id.time);
                    int maxidParent = db.ClassRepDAO().getMaxIdParent()+1;
                    String ora = orario.getText().toString();
                    try {
                        if(ora.isEmpty()){
                            db.ClassRepDAO().insertParent(new Parent(maxidParent, 0, maxid,
                                    nome.getText().toString(), cognome.getText().toString(),
                                    new SimpleDateFormat("HH:mm").parse("00:00")));
                        } else {
                            db.ClassRepDAO().insertParent(new Parent(maxidParent, 0, maxid,
                                    nome.getText().toString(), cognome.getText().toString(),
                                    new SimpleDateFormat("HH:mm").parse(orario.getText().toString())));
                        }
                    } catch (ParseException e) {
                        System.out.println(e);
                    }
                }
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
        builder.setContentText("Il colloquio con "+nome.getText().toString()+" "+cognome.getText().toString()+" sta per iniziare") ;
        builder.setSmallIcon(R.drawable.logo) ;
        builder.setAutoCancel( true ) ;
        builder.setContentIntent(pendingIntent);
        builder.setChannelId( NOTIFICATION_CHANNEL_ID ) ;
        return builder.build() ;
    }

    public void onBackPressed() {
        new androidx.appcompat.app.AlertDialog.Builder(AddPtaActivity.this)
                .setTitle("Stai tornando indietro")
                .setMessage("Sei sicuro di non voler più inserire un colloquio?")
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
        intent.putExtra("fragment", "pta");
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
