package com.example.classrep.SingleActivity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.example.classrep.HomeActivity;
import com.example.classrep.R;
import com.example.classrep.adapter.PersonAdapter;
import com.example.classrep.adapter.PtaAdapter;
import com.example.classrep.adder.AddPtaActivity;
import com.example.classrep.database.ClassRepDB;
import com.example.classrep.database.entity.Child;
import com.example.classrep.database.entity.Event;
import com.example.classrep.database.entity.PTAmeeting;
import com.example.classrep.database.entity.Parent;
import com.example.classrep.fragment.PTAFragment;
import com.example.classrep.utilities.SingleToneClass;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileNotFoundException;
import java.lang.reflect.Type;
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

public class PtaActivity extends AppCompatActivity {

    private PTAmeeting pta;
    private List<Parent> parents = new ArrayList<>();

    private RecyclerView recycleParent;

    private PersonAdapter adapter;

    private EditText nome;
    private EditText cognome;
    private EditText date;
    private EditText data_inizio;
    private EditText data_fine;
    private EditText materie;

    private MaterialToolbar toolbar;
    private Button addButton;

    private int idInstitute;
    private int idEvent;
    int hours;
    int minutes;
    int hours1;
    int minutes1;
    private ClassRepDB db;
    int maxidParent;

    private boolean modify = false;
    private BlurView blurView;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pta);
        db = ClassRepDB.getDatabase(getBaseContext());
        AsyncTask.execute(()->{
            maxidParent = db.ClassRepDAO().getMaxIdParent()+1;
        });

        blurView = findViewById(R.id.blurViewPta);
        backgroundBlur();

        recycleParent = findViewById(R.id.putParent);

        toolbar = findViewById(R.id.ptaTopAppBar);

        nome = findViewById(R.id.nomeProf);
        cognome = findViewById(R.id.cognomeProf);
        date = findViewById(R.id.giornoPta);
        data_inizio = findViewById(R.id.oraInizio);
        data_fine = findViewById(R.id.oraFine);
        materie = findViewById(R.id.subjectPta);

        addButton = findViewById(R.id.ptaParent);

        Intent intent = this.getIntent();
        String jsParents;
        jsParents = intent.getStringExtra("parents");
        //System.out.println(jsParents);
        Type listTypeEvent = new TypeToken<List<Parent>>() {}.getType();
        parents = new Gson().fromJson(String.valueOf(jsParents), listTypeEvent);
        createRecycler();
        SingleToneClass singleToneClass = com.example.classrep.utilities.SingleToneClass.getInstance();
        idInstitute = singleToneClass.getData("institute");
        idEvent = singleToneClass.getData("pta");
        if(!singleToneClass.getImageBackground().contains("nada")){
            ConstraintLayout background = findViewById(R.id.backgroundPta);

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

        String jsPta = intent.getStringExtra("pta");
        //System.out.println(jsPta);
        Type listTypeEvent1 = new TypeToken<PTAmeeting>() {}.getType();
        pta = new Gson().fromJson(String.valueOf(jsPta), listTypeEvent1);
        setAll();

        addButton.setOnClickListener( view ->{
            for(int i= 0; i<parents.size(); i++){
                RecyclerView.ViewHolder view1 = recycleParent.findViewHolderForLayoutPosition(i);
                if(view1 == null){
                    view1 = adapter.holderHashMap.get(i);
                }
                EditText nomeGenitore = view1.itemView.findViewById(R.id.nome);
                EditText cognomeGenitore = view1.itemView.findViewById(R.id.cognome);
                EditText orarioGenitore = view1.itemView.findViewById(R.id.time);
                parents.get(i).setName(nomeGenitore.getText().toString());
                parents.get(i).setSurname(cognomeGenitore.getText().toString());
                String stringa = orarioGenitore.getText().toString();
                try {
                    if(stringa.isEmpty()){
                        parents.get(i).setTime(new SimpleDateFormat("HH:mm").parse("00:00"));
                    } else {
                        parents.get(i).setTime(new SimpleDateFormat("HH:mm").parse(stringa));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            parents.add(new  Parent(maxidParent, 0, pta.getId_pta(), "", "", Date.from(Calendar.getInstance().toInstant())));
            maxidParent++;
            adapter.notifyDataSetChanged();
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

        toolbar.setOnMenuItemClickListener(menuItem->{
            switch (menuItem.getItemId()){
                case R.id.send:
                    Intent intento = new Intent(Intent.ACTION_SEND);

                    intento.setType("text/plain");
                    intento.setPackage("com.whatsapp");
                    String messaggio=pta.toTesto()+"\n";
                    for (int i=0; i<parents.size(); i++){
                        messaggio = messaggio+parents.get(i).toTesto()+"\n";
                    }
                    intento.putExtra(Intent.EXTRA_TEXT, messaggio);
                    startActivity(intento);
                    break;
                case R.id.edit:
                    modify = true;
                    menuItem.setVisible(false);
                    MenuItem cancel = toolbar.getMenu().findItem(R.id.cancel);
                    cancel.setVisible(true);
                    MenuItem confirm = toolbar.getMenu().findItem(R.id.confirmEdit);
                    confirm.setVisible(true);
                    MenuItem send = toolbar.getMenu().findItem(R.id.send);
                    send.setVisible(false);
                    for (int i=0; i<parents.size(); i++){
                        adapter.addParents(parents);
                        RecyclerView.ViewHolder view = recycleParent.findViewHolderForLayoutPosition(i);
                        if(view == null){
                            view = adapter.holderHashMap.get(i);
                            System.out.println(view);
                        }
                        EditText nome = view.itemView.findViewById(R.id.nome);
                        nome.setEnabled(true);
                        EditText cognome = view.itemView.findViewById(R.id.cognome);
                        cognome.setEnabled(true);
                        EditText orario = view.itemView.findViewById(R.id.time);
                        orario.setEnabled(true);
                        Button delete = view.itemView.findViewById(R.id.trashPerson);
                        delete.setVisibility(View.VISIBLE);
                    }
                    modify = true;
                    modifyEnable(modify);
                    addButton.setVisibility(View.VISIBLE);
                    break;
                case R.id.cancel:
                    modify = false;
                    setAll();
                    menuItem.setVisible(false);
                    MenuItem cancel1 = toolbar.getMenu().findItem(R.id.confirmEdit);
                    cancel1.setVisible(false);
                    MenuItem edit = toolbar.getMenu().findItem(R.id.edit);
                    edit.setVisible(true);
                    MenuItem send1 = toolbar.getMenu().findItem(R.id.send);
                    send1.setVisible(true);

                    for (int i=0; i<parents.size(); i++){
                        RecyclerView.ViewHolder view = recycleParent.findViewHolderForLayoutPosition(i);
                        if(view == null){
                            view = adapter.holderHashMap.get(i);
                            System.out.println(view);
                        }
                        EditText nome = view.itemView.findViewById(R.id.nome);
                        nome.setEnabled(false);
                        EditText cognome = view.itemView.findViewById(R.id.cognome);
                        cognome.setEnabled(false);
                        EditText orario = view.itemView.findViewById(R.id.time);
                        orario.setEnabled(false);
                        Button delete = view.itemView.findViewById(R.id.trashPerson);
                        delete.setVisibility(View.GONE);
                    }
                    addButton.setVisibility(View.GONE);
                    nome.setEnabled(false);
                    cognome.setEnabled(false);
                    date.setEnabled(false);
                    data_inizio.setEnabled(false);
                    data_fine.setEnabled(false);
                    materie.setEnabled(false);
                    break;
                case R.id.confirmEdit:
                    System.out.println(recycleParent.getChildCount());

                    System.out.println(parents.size());

                    menuItem.setVisible(false);
                    MenuItem cancel2 = toolbar.getMenu().findItem(R.id.cancel);
                    cancel2.setVisible(false);
                    MenuItem edit1 = toolbar.getMenu().findItem(R.id.edit);
                    edit1.setVisible(true);
                    MenuItem send2 = toolbar.getMenu().findItem(R.id.send);
                    send2.setVisible(true);
                    modify = false;
                    modifyEnable(modify);
                    //update valori

                    AsyncTask.execute(()->{
                        System.out.println("genitori su recycle:" + recycleParent.getChildCount());
                        if(isAllOk()){
                            System.out.println("genitori su recycle:" + recycleParent.getChildCount());
                            List<Integer> idGenitori = adapter.getRemovedParent();
                            db.ClassRepDAO().deleteParents(idGenitori);
                            adapter.clearList();
                            PTAmeeting substitute = createPta();
                            if(substitute.getName()!=pta.getName()
                                    ||substitute.getSurname()!=pta.getSurname()
                                    ||!substitute.getStart_date().equals(pta.getStart_date())
                                    ||!substitute.getFinish_date().equals(pta.getFinish_date())
                                    ||substitute.getSubject()!=pta.getSubject())
                            {
                                pta = substitute;
                                db.ClassRepDAO().updatePtaMeeting(pta);
                            }


                            System.out.println(parents.size());
                            for (int i=0; i<parents.size(); i++){
                                List<Parent> trovato = db.ClassRepDAO().getSingleParent(parents.get(i).getId_parent());
                                if(!trovato.isEmpty()){
                                    RecyclerView.ViewHolder view = recycleParent.findViewHolderForLayoutPosition(i);
                                    if(view == null){
                                        view = adapter.holderHashMap.get(i);
                                    }
                                    EditText nomeGenitore = view.itemView.findViewById(R.id.nome);
                                    EditText cognomeGenitore = view.itemView.findViewById(R.id.cognome);
                                    EditText orarioGenitore = view.itemView.findViewById(R.id.time);
                                    System.out.println(parents.get(i).getName() + " " +nomeGenitore.getText().toString());
                                    try {
                                        if(!parents.get(i).getName().contains(nomeGenitore.getText().toString())
                                                || !parents.get(i).getSurname().contains(cognomeGenitore.getText().toString())
                                                || !parents.get(i).getTime().equals(new SimpleDateFormat("HH:mm").parse(orarioGenitore.getText().toString())))
                                        {
                                            parents.get(i).setName(nomeGenitore.getText().toString());
                                            parents.get(i).setSurname(cognomeGenitore.getText().toString());
                                            parents.get(i).setTime(new SimpleDateFormat("HH:mm").parse(orarioGenitore.getText().toString()));
                                            db.ClassRepDAO().updateParent(parents.get(i));
                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    RecyclerView.ViewHolder view = recycleParent.findViewHolderForLayoutPosition(i);
                                    if(view == null){
                                        view = adapter.holderHashMap.get(i);
                                        System.out.println(view);
                                    }
                                    EditText nomeGenitore = view.itemView.findViewById(R.id.nome);
                                    EditText cognomeGenitore = view.itemView.findViewById(R.id.cognome);
                                    EditText orarioGenitore = view.itemView.findViewById(R.id.time);

                                    if(!nomeGenitore.getText().toString().isEmpty() || !cognomeGenitore.getText().toString().isEmpty()){
                                        String ora = orarioGenitore.getText().toString();
                                        if(ora.isEmpty()){
                                            ora = "00:00";
                                        }

                                        try {
                                            Parent parente = new Parent(parents.get(i).getId_parent(), 0, pta.getId_pta(),
                                                    nomeGenitore.getText().toString(), cognomeGenitore.getText().toString(),
                                                    new SimpleDateFormat("HH:mm").parse(ora));
                                            db.ClassRepDAO().insertParent(parente);
                                            parents.set(i,parente);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                        this.runOnUiThread(()->{
                            for (int l =0; l<parents.size(); l++){
                                RecyclerView.ViewHolder view1 = recycleParent.findViewHolderForLayoutPosition(l);
                                if(view1 == null){
                                    view1 = adapter.holderHashMap.get(l);
                                    System.out.println(view1);
                                }
                                EditText nome = view1.itemView.findViewById(R.id.nome);
                                nome.setEnabled(false);
                                EditText cognome = view1.itemView.findViewById(R.id.cognome);
                                cognome.setEnabled(false);
                                EditText orario = view1.itemView.findViewById(R.id.time);
                                orario.setEnabled(false);
                                Button delete = view1.itemView.findViewById(R.id.trashPerson);
                                delete.setVisibility(View.GONE);
                            }
                            adapter.notifyDataSetChanged();
                            setAll();
                        });
                    });
                    break;
            }
            return false;
        });
    }


    private void createRecycler() {
        adapter = new PersonAdapter(getBaseContext(), "parent", true);
        adapter.addParents(parents);
        adapter.notifyDataSetChanged();
        this.runOnUiThread(()->recycleParent.setAdapter(adapter));
    }

    private void setAll(){

        nome.setText(pta.getName());
        cognome.setText(pta.getSurname());
        String pattern = "dd/MM/yyyy";
        SimpleDateFormat simpleDate = new SimpleDateFormat(pattern);
        String dateAll = simpleDate.format(pta.getFinish_date());
        date.setText(dateAll);
        String patternOra = "HH:mm";
        SimpleDateFormat simpleTime = new SimpleDateFormat(patternOra);
        String dataStart = simpleTime.format(pta.getStart_date());
        String dataFinish = simpleTime.format(pta.getFinish_date());
        data_inizio.setText(dataStart);
        data_fine.setText(dataFinish);
        materie.setText(pta.getSubject());
    }

    public void modifyEnable(boolean bool){
        nome.setEnabled(bool);
        cognome.setEnabled(bool);
        date.setEnabled(bool);
        data_inizio.setEnabled(bool);
        data_fine.setEnabled(bool);
        materie.setEnabled(bool);
        addButton.setVisibility(bool ? View.VISIBLE : View.GONE);
        adapter.setSingleOrAdd(!bool);
    }

    boolean rispostona;
    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean isAllOk(){
        rispostona = false;
        if(nome.getText().toString().isEmpty() ||
                date.getText().toString().isEmpty() ||
                data_inizio.getText().toString().isEmpty() ||
                data_fine.getText().toString().isEmpty() ||
                materie.getText().toString().isEmpty() ||
                cognome.getText().toString().isEmpty()){
            this.runOnUiThread(()->{new androidx.appcompat.app.AlertDialog.Builder(PtaActivity.this)
                    .setTitle("Ci sono alcuni elementi mancanti")
                    //se clicca ok
                    .setPositiveButton(android.R.string.yes, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .create()
                    .show();});
        } else if(parents.isEmpty()) {

            this.runOnUiThread(()->{new androidx.appcompat.app.AlertDialog.Builder(PtaActivity.this)
                    .setTitle("Non hai inserito nessun genitore")
                    .setMessage("")
                    //se clicca ok
                    .setPositiveButton("ok",null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .create()
                    .show();});
            rispostona = true;
        } else {
            rispostona = true;
        }
        return rispostona;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public PTAmeeting createPta(){

        Date daterrima = null;
        Date dataInizio = null;
        Date dataFine = null;
        try {
            daterrima = new SimpleDateFormat("dd/MM/yyyy").parse(date.getText().toString());
            dataInizio = new SimpleDateFormat("HH:mm").parse(data_inizio.getText().toString());
            dataFine = new SimpleDateFormat("HH:mm").parse(data_fine.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar calendarioInizio = Calendar.getInstance();
        calendarioInizio.setTime(daterrima);
        calendarioInizio.set(Calendar.HOUR, dataInizio.getHours());
        calendarioInizio.set(Calendar.MINUTE, dataInizio.getMinutes());

        Calendar calendarioFine = Calendar.getInstance();
        calendarioFine.setTime(daterrima);
        calendarioFine.set(Calendar.HOUR, dataFine.getHours());
        calendarioFine.set(Calendar.MINUTE, dataFine.getMinutes());

        return new PTAmeeting(pta.getId_pta(),
                pta.getForeign_institute(),
                nome.getText().toString(),
                cognome.getText().toString(),
                Date.from(calendarioInizio.toInstant()),
                Date.from(calendarioFine.toInstant()),
                materie.getText().toString());
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("fragment", "pta");
        startActivity(intent);
    }
}