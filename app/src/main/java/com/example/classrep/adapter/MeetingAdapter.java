package com.example.classrep.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.classrep.R;
import com.example.classrep.database.entity.Meeting;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MeetingAdapter extends RecyclerView.Adapter<MeetingAdapter.MeetingViewHolder> {

    private List<Meeting> meetings = new ArrayList<>();
    private Context context;

    private onMeetingListener mOnMeetingListener;

    SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public MeetingAdapter(Context context, List<Meeting> meetings, onMeetingListener onMeetingListener) {
        this.context = context;
        this.meetings = meetings;

        this.mOnMeetingListener = onMeetingListener;
    }

    @Override
    public MeetingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_recycle, parent, false);
        MeetingViewHolder viewHolder = new MeetingViewHolder(view, mOnMeetingListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MeetingViewHolder holder, int position) {
        holder.bindMeeting(meetings.get(position));
    }

    @Override
    public int getItemCount() {
        return meetings.size();
    }

    public class MeetingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        onMeetingListener onMeetingListener;

        @BindView(R.id.title_adapter) TextView meetingTitle;
        @BindView(R.id.date) TextView meetingDate;
        @BindView(R.id.place) TextView meetingPlace;
        @BindView(R.id.note) TextView meetingNote;
        private Context mContext;

        public MeetingViewHolder(View itemView, onMeetingListener onMeetingListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = itemView.getContext();
            this.onMeetingListener = onMeetingListener;

            itemView.setOnClickListener(this);
        }

        public void bindMeeting(Meeting meeting) {
            meetingTitle.setText(meeting.getTitle());
            Calendar cal = Calendar.getInstance();
            cal.setTime(meeting.getDate());
            meetingDate.setText(format1.format(cal.getTime()));
            meetingPlace.setText(meeting.getPlace());
            meetingNote.setText(meeting.getNote());
        }

        @Override
        public void onClick(View view) {
            onMeetingListener.onMeetingClick(getAdapterPosition());
        }
    }

    public interface onMeetingListener{
        void onMeetingClick(int position);
    }
}
