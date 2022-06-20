package com.example.classrep.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.classrep.R;
import com.example.classrep.database.entity.PTAmeeting;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PtaAdapter extends  RecyclerView.Adapter<PtaAdapter.PtaViewHolder> {

    private List<PTAmeeting> ptaMeetings = new ArrayList<>();
    private Context context;

    private onPtaListener mOnPtaListener;

    SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public PtaAdapter(Context context, List<PTAmeeting> ptaMeetings, onPtaListener onPtaListener) {
        this.context = context;
        this.ptaMeetings = ptaMeetings;

        this.mOnPtaListener = onPtaListener;
    }

    @Override
    public PtaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_recycle, parent, false);
        PtaViewHolder viewHolder = new PtaViewHolder(view, mOnPtaListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PtaViewHolder holder, int position) {
        holder.bindPta(ptaMeetings.get(position));
    }

    @Override
    public int getItemCount() {
        return ptaMeetings.size();
    }


    public class PtaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        onPtaListener onPtaListener;

        @BindView(R.id.title_adapter) TextView title;
        @BindView(R.id.date) TextView date;
        @BindView(R.id.note) TextView note;
        @BindView(R.id.place) TextView place;
        @BindView(R.id.dateOrParent) TextView start;
        @BindView(R.id.dateOrChildren) TextView finish;

        private Context mContext;

        public PtaViewHolder(View itemView, onPtaListener onPtaListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = itemView.getContext();
            this.onPtaListener = onPtaListener;

            itemView.setOnClickListener(this);
        }

        public void bindPta(PTAmeeting pta) {
            title.setText(pta.getName()+pta.getSurname());
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(pta.getStart_date());
            start.setVisibility(View.VISIBLE);
            start.setText(format1.format(cal1));
            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(pta.getStart_date());
            finish.setVisibility(View.VISIBLE);
            finish.setText(format1.format(cal1));
            date.setText(pta.getSubject());

            note.setVisibility(View.INVISIBLE);
            place.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onClick(View view) {
            onPtaListener.onPtaClick(getAdapterPosition());
        }
    }

    public interface onPtaListener {
        void onPtaClick(int position);
    }
}
