package com.example.classrep.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.recyclerview.widget.RecyclerView;

import com.example.classrep.R;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.PersonViewHolder>{

    private List<Integer> people = new ArrayList<>();
    private Context context;
    private String type;

    SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public PersonAdapter(Context context, List<Integer> people, String type) {
        this.context = context;
        this.people = people;
        this.type = type;
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_people, parent, false);
        PersonViewHolder viewHolder = new PersonViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PersonViewHolder holder, int position) {
        holder.bindPta(position);
    }

    @Override
    public int getItemCount() {
        return people.size();
    }


    public class PersonViewHolder extends RecyclerView.ViewHolder{

        int hours;
        int minutes;

        @BindView(R.id.time) Button time;
        @BindView(R.id.trashPerson) Button delete;
        @BindView(R.id.nome) EditText name;
        @BindView(R.id.cognome) EditText surname;

        private Context mContext;

        public PersonViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = itemView.getContext();
        }

        public void bindPta(int ciao) {

            if(type.contains("adhesion")){
                name.setVisibility(View.INVISIBLE);
                surname.setHint("Conto");
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
                people.remove(ciao);
                notifyDataSetChanged();
                if(type.contains("adhesion")){
                    Button button = ((Activity) context).findViewById(R.id.addAdhesion);
                    button.setVisibility(View.VISIBLE);
                }
            });
        }

    }
}
