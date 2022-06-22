package com.example.classrep.fragment;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.classrep.R;
import com.example.classrep.database.ClassRepDB;
import com.example.classrep.database.entity.Event;
import com.example.classrep.database.entity.Meeting;
import com.example.classrep.database.entity.PTAmeeting;

import org.naishadhparmar.zcustomcalendar.CustomCalendar;
import org.naishadhparmar.zcustomcalendar.OnNavigationButtonClickedListener;
import org.naishadhparmar.zcustomcalendar.Property;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalendarFragment extends Fragment implements OnNavigationButtonClickedListener {
    private CustomCalendar customCalendar;
    private ClassRepDB db;

    private List<Event> events;
    private List<Meeting> meetings;
    private List<PTAmeeting> ptameetings;
    Calendar calendar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        customCalendar=view.findViewById(R.id.custom_calendar);
        db = ClassRepDB.getDatabase(getContext());

        // Initialize description hashmap
        HashMap<Object, Property> descHashMap=new HashMap<>();

        // Initialize default property
        Property defaultProperty=new Property();

        // Initialize default resource
        defaultProperty.layoutResource=R.layout.calendar_default;

        // Initialize and assign variable
        defaultProperty.dateTextViewResource=R.id.text_view;

        // Put object and property
        descHashMap.put("default",defaultProperty);

        // for event
        Property currentProperty=new Property();
        currentProperty.layoutResource=R.layout.calendar_event;
        currentProperty.dateTextViewResource=R.id.text_view;
        descHashMap.put("event",currentProperty);

        // for meeting
        Property presentProperty=new Property();
        presentProperty.layoutResource=R.layout.calendar_meeting;
        presentProperty.dateTextViewResource=R.id.text_view;
        descHashMap.put("meeting",presentProperty);

        // set desc hashmap on custom calendar
        customCalendar.setMapDescToProp(descHashMap);

        // Initialize date hashmap
        HashMap<Integer,Object> dateHashmap=new HashMap<>();

        // initialize calendar
        calendar =  Calendar.getInstance();
        // per giorno corrente
        //dateHashmap.put(calendar.get(Calendar.DAY_OF_MONTH),"event");

        //prende tutte le date
        AsyncTask.execute(()->{
            //Eventi
            events = db.ClassRepDAO().getAllEvent(1);
            for (int i=0; i<events.size(); i++){
                Date date = events.get(i).getDate();
                Calendar dateCalendar = Calendar.getInstance();
                dateCalendar.setTime(date);
                if(dateCalendar.get(Calendar.MONTH)
                        == calendar.get(Calendar.MONTH)){
                    dateHashmap.put(dateCalendar.get(Calendar.DAY_OF_MONTH), "event");
                }
            }
            //Riunioni
            meetings = db.ClassRepDAO().getAllMeeting(1);
            for (int i=0; i<meetings.size(); i++){
                Date date = meetings.get(i).getDate();
                Calendar dateCalendar = Calendar.getInstance();
                dateCalendar.setTime(date);
                if(dateCalendar.get(Calendar.MONTH)
                        == calendar.get(Calendar.MONTH)){
                    dateHashmap.put(dateCalendar.get(Calendar.DAY_OF_MONTH), "meeting");
                }
            }
            //Colloqui
            ptameetings = db.ClassRepDAO().getAllPTAmeeting(1);
            for (int i=0; i<ptameetings.size(); i++){
                Date date = ptameetings.get(i).getStart_date();
                Calendar dateCalendar = Calendar.getInstance();
                dateCalendar.setTime(date);
                if(dateCalendar.get(Calendar.MONTH)
                        == calendar.get(Calendar.MONTH)){
                    dateHashmap.put(dateCalendar.get(Calendar.DAY_OF_MONTH), "pta");
                }
            }
        });

        // set date
        customCalendar.setOnNavigationButtonClickedListener(CustomCalendar.PREVIOUS, this);
        customCalendar.setOnNavigationButtonClickedListener(CustomCalendar.NEXT, this);

        customCalendar.setDate(calendar,dateHashmap);

        return view;
    }

    @Override
    public Map<Integer, Object>[] onNavigationButtonClicked(int whichButton, Calendar newMonth) {
        Map<Integer, Object>[] arr = new Map[2];
        arr[0] = new HashMap<>();
        for (int i=0; i<events.size(); i++) {
            Date date = events.get(i).getDate();
            Calendar dateCalendar = Calendar.getInstance();
            dateCalendar.setTime(date);
            if (dateCalendar.get(Calendar.MONTH)
                    == newMonth.get(Calendar.MONTH)) {
                arr[0].put(dateCalendar.get(Calendar.DAY_OF_MONTH), "event");
            }
        }
        for (int i=0; i<meetings.size(); i++) {
            Date date = meetings.get(i).getDate();
            Calendar dateCalendar = Calendar.getInstance();
            dateCalendar.setTime(date);
            if (dateCalendar.get(Calendar.MONTH)
                    == newMonth.get(Calendar.MONTH)) {
                arr[0].put(dateCalendar.get(Calendar.DAY_OF_MONTH), "meeting");
            }
        }
        for (int i=0; i<ptameetings.size(); i++) {
            Date date = ptameetings.get(i).getStart_date();
            Calendar dateCalendar = Calendar.getInstance();
            dateCalendar.setTime(date);
            if (dateCalendar.get(Calendar.MONTH)
                    == newMonth.get(Calendar.MONTH)) {
                arr[0].put(dateCalendar.get(Calendar.DAY_OF_MONTH), "pta");
            }
        }
        arr[1] = null;
        return arr;
    }
}