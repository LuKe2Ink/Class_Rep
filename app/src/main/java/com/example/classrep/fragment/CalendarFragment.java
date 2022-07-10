package com.example.classrep.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.classrep.ProfileActivity;
import com.example.classrep.R;
import com.example.classrep.SelectionActivity;
import com.example.classrep.SettingsActivity;
import com.example.classrep.SingleActivity.PtaActivity;
import com.example.classrep.adapter.EventAdapter;
import com.example.classrep.adapter.MeetingAdapter;
import com.example.classrep.adapter.PtaAdapter;
import com.example.classrep.database.ClassRepDB;
import com.example.classrep.database.entity.Event;
import com.example.classrep.database.entity.Institute;
import com.example.classrep.database.entity.Meeting;
import com.example.classrep.database.entity.PTAmeeting;
import com.example.classrep.database.entity.Parent;
import com.example.classrep.utilities.SingleToneClass;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.naishadhparmar.zcustomcalendar.CustomCalendar;
import org.naishadhparmar.zcustomcalendar.OnDateSelectedListener;
import org.naishadhparmar.zcustomcalendar.OnNavigationButtonClickedListener;
import org.naishadhparmar.zcustomcalendar.Property;

import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;


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
    private MaterialToolbar topAppBar;
    private SingleToneClass singleToneClass;

    private DrawerLayout drawerLayout;
    private NavigationView drawer;


    private View view;

    private Map<Integer, Object> mappaRecycler = new HashMap<>();
    private BlurView blurView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_calendar, container, false);

        db = ClassRepDB.getDatabase(getContext());
        singleToneClass = com.example.classrep.utilities.SingleToneClass.getInstance();

        drawerLayout = view.findViewById(R.id.drawerLayoutCalendar);
        drawer = view.findViewById(R.id.drawerCalendar);
        drawer.bringToFront();

        AsyncTask.execute(()->{
            String image = db.ClassRepDAO().getInstitute(singleToneClass.getData("institute")).getImage();
            if(!image.contains("nada")){
                Uri uri = Uri.parse(image);
                View hView =  drawer.inflateHeaderView(R.layout.headerlayout);
                CircleImageView immagine = hView.findViewById(R.id.headerIcon);
                immagine.setImageURI(uri);
            } else {
                View hView =  drawer.inflateHeaderView(R.layout.headerlayout);
            }
        });

        if(!singleToneClass.getImageBackground().contains("nada")){
            FrameLayout background = view.findViewById(R.id.backgroundCalendar);

            Uri uri = Uri.parse(singleToneClass.getImageBackground());
            Bitmap bmImg = null;
            try {
                bmImg = BitmapFactory.decodeStream( this.getActivity().getContentResolver().openInputStream(uri));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            BitmapDrawable background1 = new BitmapDrawable(bmImg);
            background.setBackground(background1);
        }

        eventRecycle = view.findViewById(R.id.recyclerCalendarEvent);
        meetingRecycle = view.findViewById(R.id.recyclerCalendarMeeting);
        ptaRecycle = view.findViewById(R.id.recyclerCalendarPta);

        topAppBar = view.findViewById(R.id.topAppBarHome);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this.getActivity(), drawerLayout, topAppBar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        drawer.setNavigationItemSelectedListener(menuItem->{
            int id = menuItem.getItemId();

            switch(id){
                case R.id.profile:
                    Intent intent1 = new Intent(getContext(), SettingsActivity.class);
                    startActivity(intent1);
                    break;
                case R.id.settings:
                    Intent intent2 = new Intent(getContext(), ProfileActivity.class);
                    startActivity(intent2);
                    break;
                case R.id.logOut:
                    Intent intent3 = new Intent(getContext(), SelectionActivity.class);
                    startActivity(intent3);
                    break;
            }

            return true;
        });

        customCalendar=view.findViewById(R.id.custom_calendar);

        blurView = view.findViewById(R.id.blurViewCalendar);
        backgroundBlur();

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
    public void backgroundBlur(){
        float radius = 5f;

        View decorView = getActivity().getWindow().getDecorView();
        //ViewGroup you want to start blur from. Choose root as close to BlurView in hierarchy as possible.
        ViewGroup rootView = decorView.findViewById(android.R.id.content);
        //Set drawable to draw in the beginning of each blurred frame (Optional).
        //Can be used in case your layout has a lot of transparent space and your content
        //gets kinda lost after after blur is applied.
        Drawable windowBackground = decorView.getBackground();

        blurView.setupWith(rootView)
                .setFrameClearDrawable(windowBackground)
                .setBlurAlgorithm(new RenderScriptBlur(getContext()))
                .setBlurRadius(radius)
                .setHasFixedTransformationMatrix(true);
    }

}