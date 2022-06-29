package com.example.classrep.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Build;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.classrep.R;
import com.example.classrep.database.entity.Adhesion;
import com.example.classrep.database.entity.Child;
import com.example.classrep.database.entity.Parent;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.PersonViewHolder>{

    private List<Integer> people = new ArrayList<>();
    private List<Parent> parents = new ArrayList<>();
    private List<Child> children = new ArrayList<>();
    private List<Adhesion> adhesion = new ArrayList<>();
    private List<Integer> removedParent = new ArrayList<>();
    private List<Integer> removedChild = new ArrayList<>();
    private Context context;
    private String type;
    private boolean singleOrAdd;

    SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public PersonAdapter(Context context, String type, boolean singleOrAdd) {
        this.context = context;
        this.type = type;
        this.singleOrAdd = singleOrAdd;
    }

    public void addPeople(List<Integer> people){
        this.people = people;
    }
    public void addParents(List<Parent> parents){
        this.parents = parents;
    }
    public void addChildren(List<Child> children){
        this.children = children;
    }
    public void addAdhesion(List<Adhesion> adhesion){
        this.adhesion = adhesion;
    }

    public void setSingleOrAdd(boolean bool){
        this.singleOrAdd = bool;
    }

    public List<Integer> getRemovedParent(){
        return removedParent;
    }
    public List<Integer> getRemovedChild(){
        return removedChild;
    }
    public void clearList(){
        removedParent.clear();
        removedChild.clear();
    }


    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_people, parent, false);
        PersonViewHolder viewHolder = new PersonViewHolder(view);
        return viewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(PersonViewHolder holder, int position) {
        holder.bindPta(position);
    }

    @Override
    public int getItemCount() {
        if(!parents.isEmpty()){
            return parents.size();
        } else if(!children.isEmpty()){
            return children.size();
        } else if(!adhesion.isEmpty()){
            return adhesion.size();
        }
        return people.size();
    }


    public class PersonViewHolder extends RecyclerView.ViewHolder{

        int hours;
        int minutes;

        @BindView(R.id.time) EditText time;
        @BindView(R.id.trashPerson) Button delete;
        @BindView(R.id.nome) EditText name;
        @BindView(R.id.cognome) EditText surname;

        private Context mContext;

        public PersonViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = itemView.getContext();
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public void bindPta(int ciao) {

            if (!parents.isEmpty()){
                name.setText(parents.get(ciao).getName());
                name.setEnabled(true);
                surname.setText(parents.get(ciao).getSurname());
                surname.setEnabled(true);
                String pattern = "HH:mm";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                String date = simpleDateFormat.format(parents.get(ciao).getTime());
                time.setText(date);
                time.setEnabled(true);
                delete.setVisibility(View.VISIBLE);
            } else if(!children.isEmpty()){
                name.setText(children.get(ciao).getName());
                name.setEnabled(true);
                surname.setText(children.get(ciao).getSurname());
                surname.setEnabled(true);
                delete.setVisibility(View.VISIBLE);
            } else if(!adhesion.isEmpty()){
                surname.setText(String.valueOf(adhesion.get(ciao).getMoney()));
                surname.setEnabled(true);
                delete.setVisibility(View.VISIBLE);
            }

            if(singleOrAdd){
                time.setClickable(false);
                time.setEnabled(false);

                name.setClickable(false);
                name.setEnabled(false);

                surname.setClickable(false);
                surname.setEnabled(false);

                delete.setVisibility(View.GONE);
            }

            if(type.contains("adhesion")){
                name.setVisibility(View.INVISIBLE);
                surname.setHint("Conto");
                surname.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
            }
            if(!type.contains("parent")){
                time.setVisibility(View.INVISIBLE);
            }

            time.setOnClickListener(view ->{
                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int Selectedhour, int Selectedminutes) {
                        hours= Selectedhour;
                        minutes = Selectedminutes;
                        time.setText(String.format(Locale.ITALY, "%02d:%02d", hours, minutes));
                    }
                };

                int style = AlertDialog.THEME_HOLO_DARK;

                TimePickerDialog timePickerDialog = new TimePickerDialog(time.getContext(), style, onTimeSetListener, hours, minutes, true);

                timePickerDialog.setTitle("Scegli l'orario");
                timePickerDialog.show();
            });

            delete.setOnClickListener(view ->{
                if (!parents.isEmpty()){
                    removedParent.add(parents.get(ciao).getId_parent());
                    parents.remove(ciao);
                } else if(!children.isEmpty()){
                    removedChild.add(children.get(ciao).getId_child());
                    children.remove(ciao);
                } else if(!adhesion.isEmpty()){
                    adhesion.remove(ciao);
                } else {
                    people.remove(ciao);
                }

                notifyDataSetChanged();
                if(type.contains("adhesion")){
                    Button button = ((Activity) context).findViewById(R.id.addAdhesion);
                    button.setVisibility(View.VISIBLE);
                }
            });
        }

    }

}
