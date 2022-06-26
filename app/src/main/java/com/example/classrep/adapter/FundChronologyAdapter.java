package com.example.classrep.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.classrep.R;
import com.example.classrep.database.entity.FundChronology;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FundChronologyAdapter extends RecyclerView.Adapter<FundChronologyAdapter.FundChronologyViewHolder>{

    private List<FundChronology> fundChronologies = new ArrayList<>();
    private Context context;

    SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public FundChronologyAdapter(Context context, List<FundChronology> fundChronologies) {
        this.context = context;
        this.fundChronologies = fundChronologies;
    }



    @Override
    public FundChronologyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_recycle, parent, false);
        FundChronologyViewHolder viewHolder = new FundChronologyViewHolder(view);
        return viewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(FundChronologyViewHolder holder, int position) {
        holder.bindPta(position);
    }

    @Override
    public int getItemCount() {
        return fundChronologies.size();
    }


    public class FundChronologyViewHolder extends RecyclerView.ViewHolder{



        @BindView(R.id.title_adapter) TextView title;
        @BindView(R.id.date) TextView date;
        @BindView(R.id.note) TextView note;
        @BindView(R.id.place) TextView place;

        private Context mContext;

        public FundChronologyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = itemView.getContext();
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public void bindPta(int position) {


            title.setText(String.valueOf(fundChronologies.get(position).getQuantity()));
            title.setTextColor(fundChronologies.get(position).getAction().contains("add") ? Color.parseColor("#1ca212") : Color.parseColor("#Bf1e21"));
            date.setText(format1.format(fundChronologies.get(position).getDate()));
            note.setText(fundChronologies.get(position).getCausal());

            place.setVisibility(View.INVISIBLE);

        }

    }
}
