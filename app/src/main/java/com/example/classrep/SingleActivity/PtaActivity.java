package com.example.classrep.SingleActivity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.example.classrep.R;
import com.example.classrep.adapter.PersonAdapter;
import com.example.classrep.adapter.PtaAdapter;
import com.example.classrep.adder.AddPtaActivity;
import com.example.classrep.database.ClassRepDB;
import com.example.classrep.database.entity.Event;
import com.example.classrep.database.entity.PTAmeeting;
import com.example.classrep.database.entity.Parent;
import com.example.classrep.fragment.PTAFragment;
import com.example.classrep.utilities.SingleToneClass;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pta);
        db = ClassRepDB.getDatabase(getBaseContext());
        AsyncTask.execute(()->{
            maxidParent = db.ClassRepDAO().getMaxIdParent()+1;
        });

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

        String jsPta = intent.getStringExtra("pta");
        //System.out.println(jsPta);
        Type listTypeEvent1 = new TypeToken<PTAmeeting>() {}.getType();
        pta = new Gson().fromJson(String.valueOf(jsPta), listTypeEvent1);
        setAll();

        addButton.setOnClickListener( view ->{
            parents.add(new  Parent(maxidParent, 0, pta.getId_pta(), "", "", Date.from(Calendar.getInstance().toInstant())));
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
                        View view = recycleParent.getChildAt(i);
                        EditText nome = view.findViewById(R.id.nome);
                        nome.setFocusable(true);
                        EditText cognome = view.findViewById(R.id.cognome);
                        cognome.setFocusable(true);
                        EditText orario = view.findViewById(R.id.time);
                        orario.setFocusable(true);
                        Button delete = view.findViewById(R.id.trashPerson);
                        delete.setVisibility(View.VISIBLE);
                    }
                    modify = true;
                    modifyEnable(modify);
                    adapter.notifyDataSetChanged();
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
                    send1.setVisible(false);

                    addButton.setVisibility(View.GONE);
                    nome.setEnabled(false);
                    cognome.setEnabled(false);
                    date.setEnabled(false);
                    data_inizio.setEnabled(false);
                    data_fine.setEnabled(false);
                    materie.setEnabled(false);
                    break;
                case R.id.confirmEdit:

                    menuItem.setVisible(false);
                    MenuItem cancel2 = toolbar.getMenu().findItem(R.id.cancel);
                    cancel2.setVisible(false);
                    MenuItem edit1 = toolbar.getMenu().findItem(R.id.edit);
                    edit1.setVisible(true);
                    MenuItem send2 = toolbar.getMenu().findItem(R.id.send);
                    send2.setVisible(false);
                    modify = false;
                    modifyEnable(modify);

                    AsyncTask.execute(()->{
                        System.out.println(isAllOk());
                        System.out.println(verifyDate());
                        if(isAllOk() && verifyDate()){
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

                            for (int i=0; i<parents.size(); i++){
                                if(!db.ClassRepDAO().getSingleParent(parents.get(i).getId_parent()).isEmpty()){
                                    View view = recycleParent.getChildAt(i);
                                    EditText nomeGenitore = view.findViewById(R.id.nome);
                                    EditText cognomeGenitore = view.findViewById(R.id.cognome);
                                    EditText orarioGenitore = view.findViewById(R.id.time);
                                    try {
                                        if(parents.get(i).getName() != nomeGenitore.getText().toString()
                                                || parents.get(i).getSurname() != cognomeGenitore.getText().toString()
                                                || parents.get(i).getTime().equals(new SimpleDateFormat("HH:mm").parse(orarioGenitore.getText().toString())))
                                        {
                                            System.out.println(i);
                                            parents.get(i).setName(nomeGenitore.getText().toString());
                                            parents.get(i).setSurname(cognomeGenitore.getText().toString());
                                            parents.get(i).setTime(new SimpleDateFormat("HH:mm").parse(orarioGenitore.getText().toString()));
                                            db.ClassRepDAO().updateParent(parents.get(i));
                                            System.out.println(i);
                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    View view = recycleParent.getChildAt(i);
                                    EditText nomeGenitore = view.findViewById(R.id.nome);
                                    EditText cognomeGenitore = view.findViewById(R.id.cognome);
                                    EditText orarioGenitore = view.findViewById(R.id.time);

                                    if(!nomeGenitore.getText().toString().isEmpty() || !cognomeGenitore.getText().toString().isEmpty()){
                                        String ora = orarioGenitore.getText().toString();
                                        if(ora.isEmpty()){
                                            ora = "00:00";
                                        }

                                        try {
                                            Parent parente = new Parent(maxidParent, 0, pta.getId_pta(),
                                                    nomeGenitore.getText().toString(), cognomeGenitore.getText().toString(),
                                                    new SimpleDateFormat("HH:mm").parse(ora));
                                            db.ClassRepDAO().insertParent(parente);
                                            parents.set(i,parente);
                                            this.runOnUiThread(()->{
                                                adapter.notifyDataSetChanged();
                                            });
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                        this.runOnUiThread(()->{setAll();});
                    });
                    break;
            }
            return false;
        });
    }


    private void createRecycler() {
        System.out.println(parents);
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
                    .setMessage("Vuoi comunque aggiungere l'evento?")
                    //se clicca ok
                    .setPositiveButton("Si", (dialog, which)->{
                        rispostona = true;
                    })
                    .setNegativeButton("No", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .create()
                    .show();});
        } else {
            rispostona = true;
        }
        return rispostona;
    }

    boolean risposta;
    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean verifyDate(){
        risposta = false;
        Instant instatNow = Calendar.getInstance().toInstant();
        Instant instatTaken = null;
        try {
            instatTaken = new SimpleDateFormat("dd/MM/yyyy").parse(date.getText().toString()).toInstant();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println(instatNow);
        System.out.println(instatTaken);
        System.out.println(instatNow.isAfter(instatTaken));
        if(instatNow.isAfter(instatTaken)){
            this.runOnUiThread(()->{
                new androidx.appcompat.app.AlertDialog.Builder(PtaActivity.this)
                    .setTitle("La data inserita è prima di quella odierna")
                    .setMessage("Vuoi comunque aggiungere l'evento?")
                    //se clicca ok
                    .setPositiveButton("Si", (dialog, which)->{
                        risposta = true;
                    })
                    .setNegativeButton("No", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .create()
                    .show();
            });
        } else {
            risposta = true;
        }

        return risposta;
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, PTAFragment.class);
        startActivity(intent);
    }
}