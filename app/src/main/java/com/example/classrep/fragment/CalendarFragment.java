package com.example.classrep.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.classrep.R;
import com.example.classrep.SingleActivity.PtaActivity;
import com.example.classrep.adapter.EventAdapter;
import com.example.classrep.adapter.MeetingAdapter;
import com.example.classrep.adapter.PtaAdapter;
import com.example.classrep.database.ClassRepDB;
import com.example.classrep.database.entity.Event;
import com.example.classrep.database.entity.Meeting;
import com.example.classrep.database.entity.PTAmeeting;
import com.example.classrep.database.entity.Parent;
import com.example.classrep.utilities.SingleToneClass;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.naishadhparmar.zcustomcalendar.CustomCalendar;
import org.naishadhparmar.zcustomcalendar.OnDateSelectedListener;
import org.naishadhparmar.zcustomcalendar.OnNavigationButtonClickedListener;
import org.naishadhparmar.zcustomcalendar.Property;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class CalendarFragment extends Fragment implements OnNavigationButtonClickedListener {
    private CustomCalendar customCalendar;
    private ClassRepDB db;

    private List<Event> events;
    private List<Meeting> meetings;
    private List<PTAmeeting> ptameetings;
    private EventAdapter eventAdapter;
    private MeetingAdapter meetingAdapter;
    private PtaAdapter ptaAdapter;

    private Calendar calendar;
    private RecyclerView eventRecycle;
    private RecyclerView meetingRecycle;
    private RecyclerView ptaRecycle;
    private SingleToneClass singleToneClass;


    private View view;

    private Map<Integer, Object> mappaRecycler = new HashMap<>();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_calendar, container, false);

        db = ClassRepDB.getDatabase(getContext());
        singleToneClass = com.example.classrep.utilities.SingleToneClass.getInstance();

        eventRecycle = view.findViewById(R.id.recyclerCalendarEvent);
        meetingRecycle = view.findViewById(R.id.recyclerCalendarMeeting);
        ptaRecycle = view.findViewById(R.id.recyclerCalendarPta);

        customCalendar=view.findViewById(R.id.custom_calendar);

        Intent intent = this.getActivity().getIntent();
        String jsEvent;
        String jsMeet;
        String jsPta;
        jsEvent = getArguments().getString("events");
        jsMeet = getArguments().getString("meetings");
        jsPta = getArguments().getString("ptas");

        Type listTypeEvent = new TypeToken<List<Event>>() {}.getType();
        events = new Gson().fromJson(String.valueOf(jsEvent), listTypeEvent);

        Type listTypeMeet = new TypeToken<List<Meeting>>() {}.getType();
        meetings = new Gson().fromJson(String.valueOf(jsMeet), listTypeMeet);

        Type listTypePta = new TypeToken<List<PTAmeeting>>() {}.getType();
        ptameetings = new Gson().fromJson(String.valueOf(jsPta), listTypePta);



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
        Property eventProperty=new Property();
        eventProperty.layoutResource=R.layout.calendar_event;
        eventProperty.dateTextViewResource=R.id.text_view;
        descHashMap.put("event",eventProperty);

        // for meeting
        Property meetingProperty=new Property();
        meetingProperty.layoutResource=R.layout.calendar_meeting;
        meetingProperty.dateTextViewResource=R.id.text_view;
        descHashMap.put("meeting",meetingProperty);

        //for pta
        Property ptaProperty=new Property();
        ptaProperty.layoutResource=R.layout.calendar_pta;
        ptaProperty.dateTextViewResource=R.id.text_view;
        descHashMap.put("pta",ptaProperty);

        // set desc hashmap on custom calendar
        customCalendar.setMapDescToProp(descHashMap);

        // Initialize date hashmap
        HashMap<Integer,Object> dateHashmap=new HashMap<>();

        // initialize calendar
        calendar =  Calendar.getInstance();
        // per giorno corrente
        //dateHashmap.put(calendar.get(Calendar.DAY_OF_MONTH),"event");

        for (int i=0; i<events.size(); i++){
            System.out.println(i);
            Date date = events.get(i).getDate();
            Calendar dateCalendar = Calendar.getInstance();
            dateCalendar.setTime(date);
            if(dateCalendar.get(Calendar.MONTH)
                    == calendar.get(Calendar.MONTH)){
                dateHashmap.put(dateCalendar.get(Calendar.DAY_OF_MONTH), "event");
                mappaRecycler.put(dateCalendar.get(Calendar.DAY_OF_MONTH), events.get(i));
            }
        }
        for (int i=0; i<meetings.size(); i++){
            Date date = meetings.get(i).getDate();
            Calendar dateCalendar = Calendar.getInstance();
            dateCalendar.setTime(date);
            if(dateCalendar.get(Calendar.MONTH)
                    == calendar.get(Calendar.MONTH)){
                dateHashmap.put(dateCalendar.get(Calendar.DAY_OF_MONTH), "meeting");
                mappaRecycler.put(dateCalendar.get(Calendar.DAY_OF_MONTH),meetings.get(i));
            }
        }
        for (int i=0; i<ptameetings.size(); i++){
            Date date = ptameetings.get(i).getStart_date();
            Calendar dateCalendar = Calendar.getInstance();
            dateCalendar.setTime(date);
            if(dateCalendar.get(Calendar.MONTH)
                    == calendar.get(Calendar.MONTH)){
                dateHashmap.put(dateCalendar.get(Calendar.DAY_OF_MONTH), "pta");
                mappaRecycler.put(dateCalendar.get(Calendar.DAY_OF_MONTH), ptameetings.get(i));
            }
        }

        // set date
        customCalendar.setOnNavigationButtonClickedListener(CustomCalendar.PREVIOUS, this);
        customCalendar.setOnNavigationButtonClickedListener(CustomCalendar.NEXT, this);

        customCalendar.setDate(calendar,dateHashmap);

        customCalendar.setOnDateSelectedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(View view, Calendar selectedDate, Object desc) {
                // get string date
                int giorno=selectedDate.get(Calendar.DAY_OF_MONTH);
                if(mappaRecycler.get(giorno) instanceof Event){
                    eventRecycle.setVisibility(View.VISIBLE);
                    meetingRecycle.setVisibility(View.GONE);
                    ptaRecycle.setVisibility(View.GONE);
                    setEventRecycle((Event) mappaRecycler.get(giorno));
                }else if(mappaRecycler.get(giorno) instanceof Meeting){
                    meetingRecycle.setVisibility(View.VISIBLE);
                    eventRecycle.setVisibility(View.GONE);
                    ptaRecycle.setVisibility(View.GONE);
                    setMeetingRecycle((Meeting) mappaRecycler.get(giorno));
                }else if(mappaRecycler.get(giorno) instanceof PTAmeeting){
                    ptaRecycle.setVisibility(View.VISIBLE);
                    eventRecycle.setVisibility(View.GONE);
                    meetingRecycle.setVisibility(View.GONE);
                    setPtaRecycle((PTAmeeting) mappaRecycler.get(giorno));
                } else{
                    eventRecycle.setVisibility(View.GONE);
                    meetingRecycle.setVisibility(View.GONE);
                    ptaRecycle.setVisibility(View.GONE);
                }
            }
        });

        return view;
    }

    public void setEventRecycle(Event event){
        List<Event> ciao = new ArrayList<>();
        ciao.add(event);
        List<Integer> ciao1 = new ArrayList<>();
        List<Integer> ciao2 = new ArrayList<>();
        AsyncTask.execute(()->{
            ciao1.add(db.ClassRepDAO().getEventParents(event.getId_event()).size());
            ciao2.add(db.ClassRepDAO().getEventChildren(event.getId_event()).size());
            eventAdapter = new EventAdapter(getContext(), ciao, ciao1, ciao2, new EventAdapter.onEventListener() {
                @Override
                public void onEventClick(int position) {

                }
            });
            this.getActivity().runOnUiThread(()->{
                eventRecycle.setAdapter(eventAdapter);
            });
        });

    }
    public void setMeetingRecycle(Meeting meeting){
        List<Meeting> ciao = new ArrayList<>();
        ciao.add(meeting);
        meetingAdapter = new MeetingAdapter(getContext(), ciao, new MeetingAdapter.onMeetingListener() {
            @Override
            public void onMeetingClick(int position) {

            }
        });
        this.getActivity().runOnUiThread(()->{
            meetingRecycle.setAdapter(meetingAdapter);
        });

    }
    public void setPtaRecycle(PTAmeeting pta){
        List<PTAmeeting> ciao = new ArrayList<>();
        ciao.add(pta);
        ptaAdapter = new PtaAdapter(getContext(), ciao, new PtaAdapter.onPtaListener() {
            @Override
            public void onPtaClick(int position) {
                int item = pta.getId_pta();

                singleToneClass.setData("pta", item);

                AsyncTask.execute(()->{
                    Intent intent = new Intent(getContext(), PtaActivity.class);
                    List<Parent> parents = db.ClassRepDAO().getPTAmeetingParents(item);
                    String jsParent = new Gson().toJson(parents);
                    System.out.println(jsParent);
                    intent.putExtra("parents", jsParent);
                    String jsPta = new Gson().toJson(db.ClassRepDAO().getSinglePta(item));
                    intent.putExtra("pta", jsPta);
                    System.out.println(jsPta);
                    startActivity(intent);
                });

            }
        });
        ptaRecycle.setAdapter(ptaAdapter);

    }

    @Override
    public Map<Integer, Object>[] onNavigationButtonClicked(int whichButton, Calendar newMonth) {
        mappaRecycler.clear();
        Map<Integer, Object>[] arr = new Map[2];
        arr[0] = new HashMap<>();
        for (int i=0; i<events.size(); i++) {
            Date date = events.get(i).getDate();
            Calendar dateCalendar = Calendar.getInstance();
            dateCalendar.setTime(date);
            if (dateCalendar.get(Calendar.MONTH)
                    == newMonth.get(Calendar.MONTH)) {
                arr[0].put(dateCalendar.get(Calendar.DAY_OF_MONTH), "event");
                mappaRecycler.put(dateCalendar.get(Calendar.DAY_OF_MONTH), events.get(i));
            }
        }
        for (int i=0; i<meetings.size(); i++) {
            Date date = meetings.get(i).getDate();
            Calendar dateCalendar = Calendar.getInstance();
            dateCalendar.setTime(date);
            if (dateCalendar.get(Calendar.MONTH)
                    == newMonth.get(Calendar.MONTH)) {
                arr[0].put(dateCalendar.get(Calendar.DAY_OF_MONTH), "meeting");
                mappaRecycler.put(dateCalendar.get(Calendar.DAY_OF_MONTH),meetings.get(i));
            }
        }
        for (int i=0; i<ptameetings.size(); i++) {
            Date date = ptameetings.get(i).getStart_date();
            Calendar dateCalendar = Calendar.getInstance();
            dateCalendar.setTime(date);
            if (dateCalendar.get(Calendar.MONTH)
                    == newMonth.get(Calendar.MONTH)) {
                arr[0].put(dateCalendar.get(Calendar.DAY_OF_MONTH), "pta");
                mappaRecycler.put(dateCalendar.get(Calendar.DAY_OF_MONTH), ptameetings.get(i));
            }
        }
        arr[1] = null;
        return arr;
    }
}