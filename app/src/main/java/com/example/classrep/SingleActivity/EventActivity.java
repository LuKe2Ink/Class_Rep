package com.example.classrep.SingleActivity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.EditText;
import android.widget.Switch;

import com.example.classrep.HomeActivity;
import com.example.classrep.R;
import com.example.classrep.adapter.PersonAdapter;
import com.example.classrep.database.ClassRepDB;
import com.example.classrep.database.entity.Adhesion;
import com.example.classrep.database.entity.Child;
import com.example.classrep.database.entity.Event;
import com.example.classrep.database.entity.PTAmeeting;
import com.example.classrep.database.entity.Parent;
import com.example.classrep.utilities.SingleToneClass;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class EventActivity extends AppCompatActivity {
    
    private ClassRepDB db;
    private BlurView blurView;
    private Event event;
    private List<Parent> parents = new ArrayList<>();
    private List<Child> children = new ArrayList<>();
    private List<Adhesion> adhesion = new ArrayList<>();

    private TextInputEditText titolo;
    private TextInputEditText data;
    private TextInputEditText posto;
    private TextInputEditText note;

    private Switch childSwitch;
    private Switch adhesionSwitch;

    private Button addParent;
    private Button addChild;
    private Button addAdhesion;

    private RecyclerView parentRecycle;
    private PersonAdapter parentAdapter;
    private RecyclerView childRecycle;
    private PersonAdapter childAdapter;
    private RecyclerView adhesionRecycler;
    private PersonAdapter adhesionAdapter;

    private ConstraintLayout expandableChild;
    private ConstraintLayout expandableAdhesion;

    private MaterialToolbar topAppbar;

    private int idInstitute;
    private int idEvent;
    private int maxIdParent;
    private boolean modify = false;
    private int maxIdChild;
    private boolean isThereChild = false;
    private boolean isThereAdhesion = false;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        db = ClassRepDB.getDatabase(getBaseContext());

        Intent intent = this.getIntent();
        String jsEvent = intent.getStringExtra("event");
        Type listTypeEvent1 = new TypeToken<Event>() {}.getType();
        event = new Gson().fromJson(String.valueOf(jsEvent), listTypeEvent1);

        SingleToneClass singleToneClass = com.example.classrep.utilities.SingleToneClass.getInstance();

        if(!singleToneClass.getImageBackground().contains("nada")){
            ConstraintLayout background = findViewById(R.id.backgroundEvent);

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

        AsyncTask.execute(()->{
            maxIdParent = db.ClassRepDAO().getMaxIdParent()+1;
            maxIdChild = db.ClassRepDAO().getMaxIdChild()+1;
        });

        blurView = findViewById(R.id.blurViewModifyEvent);
        backgroundBlur();
        parentRecycle = findViewById(R.id.putParentModify);
        childRecycle = findViewById(R.id.putChildModify);
        adhesionRecycler = findViewById(R.id.putAdhesionModify);

        expandableChild = findViewById(R.id.expandableLayoutChildModify);
        expandableAdhesion = findViewById(R.id.expandableLayoutAdhesionModify);

        addParent = findViewById(R.id.addParentModify);
        addChild = findViewById(R.id.addChildModify);
        addAdhesion = findViewById(R.id.addAdhesion);

        addParent.setOnClickListener( view ->{
            for(int i= 0; i<parents.size(); i++){
                RecyclerView.ViewHolder view1 = parentRecycle.findViewHolderForLayoutPosition(i);
                if(view1 == null){
                    view1 = parentAdapter.holderHashMap.get(i);
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
            parents.add(new  Parent(maxIdParent, event.getId_event(), 0, "", "", Date.from(Calendar.getInstance().toInstant())));
            maxIdParent++;
            parentAdapter.notifyDataSetChanged();
        });

        addChild.setOnClickListener( view ->{
            for(int i= 0; i<children.size(); i++){
                RecyclerView.ViewHolder view1 = childRecycle.findViewHolderForLayoutPosition(i);
                if(view1 == null){
                    view1 = childAdapter.holderHashMap.get(i);
                }
                EditText nomeBambino = view1.itemView.findViewById(R.id.nome);
                EditText cognomeBambino = view1.itemView.findViewById(R.id.cognome);
                children.get(i).setName(nomeBambino.getText().toString());
                children.get(i).setSurname(cognomeBambino.getText().toString());
            }
            children.add(new Child(maxIdChild, event.getId_event(), "", ""));
            maxIdChild++;
            childAdapter.notifyDataSetChanged();
        });

        addChild.setVisibility(View.GONE);

        addAdhesion.setOnClickListener( view ->{
            adhesion.add(new Adhesion(event.getId_event(), 0.0));
            adhesionAdapter.notifyDataSetChanged();
            addAdhesion.setVisibility(View.GONE);
        });

        addAdhesion.setVisibility(View.GONE);

        childSwitch = findViewById(R.id.childSwitchModify);
        adhesionSwitch = findViewById(R.id.adhesionSwitchModify);

        topAppbar = findViewById(R.id.eventModifyTopAppBar);

        titolo = findViewById(R.id.titoloModify);
        data = findViewById(R.id.giornoModify);
        posto = findViewById(R.id.postoModify);
        note = findViewById(R.id.notaModify);

        String jsParents;
        jsParents = intent.getStringExtra("parents");
        //System.out.println(jsParents);
        Type listTypeEvent = new TypeToken<List<Parent>>() {}.getType();
        parents = new Gson().fromJson(String.valueOf(jsParents), listTypeEvent);

        String jsChildren;
        jsChildren = intent.getStringExtra("children");
        Type listTypeEvent2 = new TypeToken<List<Child>>() {}.getType();
        children = new Gson().fromJson(String.valueOf(jsChildren), listTypeEvent2);
        if(event.isChildren()){
            childSwitch.setChecked(true);
            expandableChild.setVisibility(View.VISIBLE);
            isThereChild = true;
        }

        String jsAdhesion;
        jsAdhesion = intent.getStringExtra("adhesion");
        Type listTypeEvent3 = new TypeToken<List<Adhesion>>() {}.getType();
        adhesion = new Gson().fromJson(String.valueOf(jsAdhesion), listTypeEvent3);
        if(event.isAdhesions()){
            adhesionSwitch.setChecked(true);
            expandableAdhesion.setVisibility(View.VISIBLE);
            isThereAdhesion = true;
        }

        adhesionSwitch.setOnCheckedChangeListener((view, checked)->{
            isThereAdhesion = checked;
            if(!checked){
                expandableAdhesion.setVisibility(View.GONE);
            } else{
                expandableAdhesion.setVisibility(View.VISIBLE);
            }
        });

        childSwitch.setOnCheckedChangeListener((view, checked)->{
            isThereChild = checked;
            if(!checked){
                expandableChild.setVisibility(View.GONE);
            } else{
                expandableChild.setVisibility(View.VISIBLE);
            }
        });

        createRecycler();

        idInstitute = singleToneClass.getData("institute");
        idEvent = singleToneClass.getData("event");
        modifyEnable(modify);
        setAll();

        topAppbar.setOnMenuItemClickListener(menuItem->{
            switch (menuItem.getItemId()){
                case R.id.send:
                    Intent intento = new Intent(Intent.ACTION_SEND);

                    intento.setType("text/plain");
                    intento.setPackage("com.whatsapp");
                    String messaggio=event.toTesto()+"\n";
                    for (int i=0; i<parents.size(); i++){
                        messaggio = messaggio+parents.get(i).toTesto()+"\n";
                    }
                    for (int i=0; i<children.size(); i++){
                        messaggio = messaggio+children.get(i).toTesto()+"\n";
                    }
                    for (int i=0; i<adhesion.size(); i++){
                        messaggio = messaggio+adhesion.get(i).toTesto()+"\n";
                    }
                    intento.putExtra(Intent.EXTRA_TEXT, messaggio);
                    startActivity(intento);
                    break;
                case R.id.edit:
                    if(isThereChild){
                        addChild.setVisibility(View.VISIBLE);
                    }
                    if(isThereAdhesion){
                        addAdhesion.setVisibility(View.VISIBLE);
                    }

                    modify = true;
                    menuItem.setVisible(false);
                    MenuItem cancel = topAppbar.getMenu().findItem(R.id.cancel);
                    cancel.setVisible(true);
                    MenuItem confirm = topAppbar.getMenu().findItem(R.id.confirmEdit);
                    confirm.setVisible(true);
                    MenuItem send = topAppbar.getMenu().findItem(R.id.send);
                    send.setVisible(false);
                    for (int i=0; i<parents.size(); i++){
                        RecyclerView.ViewHolder view = parentRecycle.findViewHolderForLayoutPosition(i);
                        if(view == null){
                            view = parentAdapter.holderHashMap.get(i);
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
                    if(event.isChildren()){
                        for (int i=0; i<children.size(); i++){
                            RecyclerView.ViewHolder view = childRecycle.findViewHolderForLayoutPosition(i);
                            if(view == null){
                                view = childAdapter.holderHashMap.get(i);
                            }
                            EditText nome = view.itemView.findViewById(R.id.nome);
                            nome.setEnabled(true);
                            EditText cognome = view.itemView.findViewById(R.id.cognome);
                            cognome.setEnabled(true);
                            Button delete = view.itemView.findViewById(R.id.trashPerson);
                            delete.setVisibility(View.VISIBLE);
                        }
                    }
                    if(event.isAdhesions()){
                        for (int i=0; i<adhesion.size(); i++){
                            View view = adhesionRecycler.getChildAt(i);
                            EditText cognome = view.findViewById(R.id.cognome);
                            cognome.setEnabled(true);
                            Button delete = view.findViewById(R.id.trashPerson);
                            delete.setVisibility(View.VISIBLE);
                        }
                    }
                    modify = true;
                    modifyEnable(modify);
                    addParent.setVisibility(View.VISIBLE);
                    addChild.setVisibility(View.VISIBLE);
                    addAdhesion.setVisibility(View.VISIBLE);
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
                    for (int i=0; i<parents.size(); i++){
                        RecyclerView.ViewHolder view = parentRecycle.findViewHolderForLayoutPosition(i);
                        if(view == null){
                            view = parentAdapter.holderHashMap.get(i);
                        }
                        EditText nome = view.itemView.findViewById(R.id.nome);
                        nome.setEnabled(false);
                        EditText cognome = view.itemView.findViewById(R.id.cognome);
                        cognome.setEnabled(false);
                        EditText orario = view.itemView.findViewById(R.id.time);
                        orario.setEnabled(false);
                        Button delete = view.itemView.findViewById(R.id.trashPerson);
                        delete.setVisibility(View.VISIBLE);
                    }
                    if(event.isChildren()){
                        for (int i=0; i<children.size(); i++){
                            RecyclerView.ViewHolder view = childRecycle.findViewHolderForLayoutPosition(i);
                            if(view == null){
                                view = childAdapter.holderHashMap.get(i);
                            }
                            EditText nome = view.itemView.findViewById(R.id.nome);
                            nome.setEnabled(false);
                            EditText cognome = view.itemView.findViewById(R.id.cognome);
                            cognome.setEnabled(false);
                            Button delete = view.itemView.findViewById(R.id.trashPerson);
                            delete.setVisibility(View.VISIBLE);
                        }
                    }
                    if(event.isAdhesions()){
                        for (int i=0; i<adhesion.size(); i++){
                            View view = adhesionRecycler.getChildAt(i);
                            EditText cognome = view.findViewById(R.id.cognome);
                            cognome.setEnabled(false);
                            Button delete = view.findViewById(R.id.trashPerson);
                            delete.setVisibility(View.VISIBLE);
                        }
                    }

                    addParent.setVisibility(View.GONE);
                    addChild.setVisibility(View.GONE);
                    addAdhesion.setVisibility(View.GONE);
                    modifyEnable(modify);
                    break;
                case R.id.confirmEdit:
                    addChild.setVisibility(View.GONE);
                    addAdhesion.setVisibility(View.GONE);
                    for (int i=0; i<parents.size(); i++){
                        RecyclerView.ViewHolder view = parentRecycle.findViewHolderForLayoutPosition(i);
                        if(view == null){
                            view = parentAdapter.holderHashMap.get(i);
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
                    if(event.isChildren()){
                        for (int i=0; i<children.size(); i++){
                            RecyclerView.ViewHolder view = childRecycle.findViewHolderForLayoutPosition(i);
                            if(view == null){
                                view = childAdapter.holderHashMap.get(i);
                            }
                            EditText nome = view.itemView.findViewById(R.id.nome);
                            nome.setEnabled(true);
                            EditText cognome = view.itemView.findViewById(R.id.cognome);
                            cognome.setEnabled(true);
                            Button delete = view.itemView.findViewById(R.id.trashPerson);
                            delete.setVisibility(View.VISIBLE);
                        }
                    }
                    if(event.isAdhesions()){
                        for (int i=0; i<adhesion.size(); i++){
                            View view = adhesionRecycler.getChildAt(i);
                            EditText cognome = view.findViewById(R.id.cognome);
                            cognome.setEnabled(true);
                            Button delete = view.findViewById(R.id.trashPerson);
                            delete.setVisibility(View.VISIBLE);
                        }
                    }

                    menuItem.setVisible(false);
                    MenuItem cancel2 = topAppbar.getMenu().findItem(R.id.cancel);
                    cancel2.setVisible(false);
                    MenuItem edit1 = topAppbar.getMenu().findItem(R.id.edit);
                    edit1.setVisible(true);
                    MenuItem send2 = topAppbar.getMenu().findItem(R.id.send);
                    send2.setVisible(true);
                    modify = false;
                    modifyEnable(modify);

                    AsyncTask.execute(()->{
                        System.out.println(isAllOk());
                        if(isAllOk()){
                            List<Integer> idGenitori = parentAdapter.getRemovedParent();
                            db.ClassRepDAO().deleteParents(idGenitori);
                            parentAdapter.clearList();
                            Event substitute = createEvent();
                            if(substitute.getTitle()!=event.getTitle()
                                    ||substitute.getNote()!=event.getNote()
                                    ||!substitute.getDate().equals(event.getDate())
                                    ||substitute.getPlace()!=event.getPlace())
                            {
                                event = substitute;
                                db.ClassRepDAO().updateEvent(event);
                            }

                            for (int i=0; i<parents.size(); i++){
                                if(!db.ClassRepDAO().getSingleParent(parents.get(i).getId_parent()).isEmpty()){
                                    RecyclerView.ViewHolder view = parentRecycle.findViewHolderForLayoutPosition(i);
                                    if(view == null){
                                        view = parentAdapter.holderHashMap.get(i);
                                    }
                                    EditText nomeGenitore = view.itemView.findViewById(R.id.nome);
                                    EditText cognomeGenitore = view.itemView.findViewById(R.id.cognome);
                                    EditText orarioGenitore = view.itemView.findViewById(R.id.time);
                                    try {
                                        if(parents.get(i).getName() != nomeGenitore.getText().toString()
                                                || parents.get(i).getSurname() != cognomeGenitore.getText().toString()
                                                || parents.get(i).getTime().equals(new SimpleDateFormat("HH:mm").parse(orarioGenitore.getText().toString())))
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
                                    View view = parentRecycle.getChildAt(i);
                                    EditText nomeGenitore = view.findViewById(R.id.nome);
                                    EditText cognomeGenitore = view.findViewById(R.id.cognome);
                                    EditText orarioGenitore = view.findViewById(R.id.time);

                                    if(!nomeGenitore.getText().toString().isEmpty() || !cognomeGenitore.getText().toString().isEmpty()){
                                        String ora = orarioGenitore.getText().toString();
                                        if(ora.isEmpty()){
                                            ora = "00:00";
                                        }

                                        try {
                                            Parent parente = new Parent(parents.get(i).getId_parent(), event.getId_event(), 0,
                                                    nomeGenitore.getText().toString(), cognomeGenitore.getText().toString(),
                                                    new SimpleDateFormat("HH:mm").parse(ora));
                                            db.ClassRepDAO().insertParent(parente);
                                            parents.set(i,parente);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        parents.remove(i);
                                    }
                                }
                            }

                            List<Integer> idChild = childAdapter.getRemovedChild();
                            db.ClassRepDAO().deleteChild(idChild);
                            if(isThereChild){
                                for (int i=0; i<children.size(); i++){
                                    if(!db.ClassRepDAO().getSingleChild(children.get(i).getId_child()).isEmpty()){
                                        RecyclerView.ViewHolder view = childRecycle.findViewHolderForLayoutPosition(i);
                                        if(view == null){
                                            view = childAdapter.holderHashMap.get(i);
                                        }
                                        EditText nomeBambino = view.itemView.findViewById(R.id.nome);
                                        EditText cognomeBambino = view.itemView.findViewById(R.id.cognome);
                                        if(children.get(i).getName() != nomeBambino.getText().toString()
                                                || children.get(i).getSurname() != cognomeBambino.getText().toString())
                                        {
                                            children.get(i).setName(nomeBambino.getText().toString());
                                            children.get(i).setSurname(cognomeBambino.getText().toString());
                                            db.ClassRepDAO().updateChild(children.get(i));
                                        }
                                    } else {
                                        View view = childRecycle.getChildAt(i);
                                        EditText nomeBambino = view.findViewById(R.id.nome);
                                        EditText cognomeBambino = view.findViewById(R.id.cognome);

                                        if(!nomeBambino.getText().toString().isEmpty() || !cognomeBambino.getText().toString().isEmpty()){
                                            Child bambino = new Child(children.get(i).getId_child(), event.getId_event(),
                                                    nomeBambino.getText().toString(), cognomeBambino.getText().toString());
                                            db.ClassRepDAO().insertChild(bambino);
                                            children.set(i,bambino);
                                        } else {
                                            children.remove(i);
                                        }
                                    }
                                }
                            }
                            if(adhesionAdapter.isAdhesionEmpty()){
                                db.ClassRepDAO().deleteAdhesion(event.getId_event());
                            } else {
                                if(isThereAdhesion){
                                    if(!db.ClassRepDAO().getEventAdhesion(adhesion.get(0).getForeign_event()).isEmpty()){
                                        View view = adhesionRecycler.getChildAt(0);
                                        EditText soldi = view.findViewById(R.id.cognome);
                                        if(adhesion.get(0).getMoney() != Double.parseDouble(soldi.getText().toString()))
                                        {
                                            adhesion.get(0).setMoney(Double.parseDouble(soldi.getText().toString()));
                                            db.ClassRepDAO().updateAdhesion(adhesion.get(0));
                                        }
                                    } else {
                                        View view = adhesionRecycler.getChildAt(0);
                                        EditText soldi = view.findViewById(R.id.cognome);

                                        if(!soldi.getText().toString().isEmpty()){
                                            Adhesion adhesion1 = new Adhesion(event.getId_event(), Double.parseDouble(soldi.getText().toString()));
                                            db.ClassRepDAO().insertAdhesion(adhesion1);
                                            adhesion.set(0,adhesion1);
                                        }
                                    }
                                }
                            }

                        }
                        this.runOnUiThread(()->{

                            for (int l =0; l<parents.size(); l++){
                                RecyclerView.ViewHolder view1 = parentRecycle.findViewHolderForLayoutPosition(l);
                                if(view1 == null){
                                    view1 = parentAdapter.holderHashMap.get(l);
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


                            if(event.isChildren()){
                                for (int l =0; l<children.size(); l++){
                                    RecyclerView.ViewHolder view1 = childRecycle.findViewHolderForLayoutPosition(l);
                                    if(view1 == null){
                                        view1 = childAdapter.holderHashMap.get(l);
                                        System.out.println(view1);
                                    }
                                    EditText nome = view1.itemView.findViewById(R.id.nome);
                                    nome.setEnabled(false);
                                    EditText cognome = view1.itemView.findViewById(R.id.cognome);
                                    cognome.setEnabled(false);
                                    Button delete = view1.itemView.findViewById(R.id.trashPerson);
                                    delete.setVisibility(View.GONE);
                                }
                            }


                            if(event.isAdhesions()){
                                for (int l =0; l<adhesion.size(); l++){
                                    RecyclerView.ViewHolder view1 = adhesionRecycler.findViewHolderForLayoutPosition(l);
                                    if(view1 == null){
                                        view1 = adhesionAdapter.holderHashMap.get(l);
                                        System.out.println(view1);
                                    }
                                    EditText cognome = view1.itemView.findViewById(R.id.cognome);
                                    cognome.setEnabled(false);
                                    Button delete = view1.itemView.findViewById(R.id.trashPerson);
                                    delete.setVisibility(View.GONE);
                                }
                            }

                            setAll();});
                    });
                    break;
            }
            return false;
        });
    }
    private void createRecycler() {
        System.out.println(children);
        parentAdapter = new PersonAdapter(this, "parent", true);
        parentAdapter.addParents(parents);
        parentAdapter.notifyDataSetChanged();
        this.runOnUiThread(()->parentRecycle.setAdapter(parentAdapter));

        childAdapter = new PersonAdapter(this, "child", true);
        childAdapter.addChildren(children);
        childAdapter.notifyDataSetChanged();
        this.runOnUiThread(()->childRecycle.setAdapter(childAdapter));

        adhesionAdapter = new PersonAdapter(this, "adhesion", true);
        adhesionAdapter.addAdhesion(adhesion);
        adhesionAdapter.notifyDataSetChanged();
        this.runOnUiThread(()->adhesionRecycler.setAdapter(adhesionAdapter));
    }

    private void setAll(){
        System.out.println(event.getTitle());
        titolo.setText(event.getTitle());
        String pattern = "dd/MM/yyyy HH:mm";
        SimpleDateFormat simpleDate = new SimpleDateFormat(pattern);
        String dateAll = simpleDate.format(event.getDate());
        data.setText(dateAll);
        posto.setText(event.getPlace());
        note.setText(event.getNote());
    }

    public void modifyEnable(boolean bool){
        titolo.setEnabled(bool);
        data.setEnabled(bool);
        posto.setEnabled(bool);
        note.setEnabled(bool);
        childSwitch.setEnabled(bool);
        adhesionSwitch.setEnabled(bool);
        addParent.setVisibility(bool ? View.VISIBLE : View.GONE);
        parentAdapter.setSingleOrAdd(!bool);
        childAdapter.setSingleOrAdd(!bool);
        adhesionAdapter.setSingleOrAdd(!bool);
    }



    boolean rispostona;
    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean isAllOk(){
        rispostona = false;
        if(titolo.getText().toString().isEmpty() ||
                data.getText().toString().isEmpty() ||
                posto.getText().toString().isEmpty() ||
                note.getText().toString().isEmpty()){
            this.runOnUiThread(()->{new androidx.appcompat.app.AlertDialog.Builder(EventActivity.this)
                    .setTitle("Ci sono alcuni elementi mancanti")
                    //se clicca ok
                    .setPositiveButton(android.R.string.yes, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .create()
                    .show();});
        } else if(parents.isEmpty()) {

            this.runOnUiThread(()->{new androidx.appcompat.app.AlertDialog.Builder(EventActivity.this)
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
    public Event createEvent(){

        Date daterrima = null;
        try {
            daterrima = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(data.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return new Event(event.getId_event(),
                event.getForeign_institute(),
                titolo.getText().toString(),
                isThereChild,
                isThereAdhesion,
                daterrima,
                note.getText().toString(),
                posto.getText().toString()
                );
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
                .setBlurAlgorithm(new RenderScriptBlur(getBaseContext()))
                .setBlurRadius(radius)
                .setHasFixedTransformationMatrix(true);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("fragment", "event");
        startActivity(intent);
    }
}