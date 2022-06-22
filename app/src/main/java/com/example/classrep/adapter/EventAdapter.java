package com.example.classrep.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.classrep.R;
import com.example.classrep.database.entity.Event;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder>{

    private List<Event> events = new ArrayList<>();
    private List<Integer> parents;
    private List<Integer> children;
    private Context context;

    private onEventListener mOnEventListener;

    SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public EventAdapter(Context context, List<Event> events, List<Integer> parents, List<Integer> children, onEventListener onEventListener) {
        this.context = context;
        this.events = events;
        this.parents = parents;
        this.children = children;
        System.out.println(parents);
        System.out.println(children);

        this.mOnEventListener = onEventListener;
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_recycle, parent, false);
        EventViewHolder viewHolder = new EventViewHolder(view, mOnEventListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        holder.bindEvent(events.get(position), parents.get(position), children.get(position));
    }

    @Override
    public int getItemCount() {
        return events.size();
    }


    public class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        onEventListener onEventListener;

        @BindView(R.id.title_adapter) TextView title;
        @BindView(R.id.date) TextView date;
        @BindView(R.id.place) TextView place;
        @BindView(R.id.note) TextView note;
        @BindView(R.id.dateOrParent) TextView textParents;
        @BindView(R.id.dateOrChildren) TextView textChildren;
        private Context mContext;

        public EventViewHolder(View itemView, onEventListener onEventListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = itemView.getContext();
            this.onEventListener = onEventListener;

            itemView.setOnClickListener(this);
        }

        public void bindEvent(Event meeting, int parents, int children) {
            title.setText(meeting.getTitle());
            Calendar cal = Calendar.getInstance();
            cal.setTime(meeting.getDate());
            date.setText(format1.format(cal.getTime()));
            place.setText(meeting.getPlace());
            textParents.setVisibility(View.VISIBLE);
            textChildren.setVisibility(View.VISIBLE);
            textParents.setText("Genitori: "+String.valueOf(parents));
            textChildren.setText("Bambini: "+String.valueOf(parents));

            note.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onClick(View view) {
            onEventListener.onEventClick(getAdapterPosition());
        }
    }

    public interface onEventListener {
        void onEventClick(int position);
    }
}
