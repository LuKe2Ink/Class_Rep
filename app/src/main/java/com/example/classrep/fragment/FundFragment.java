package com.example.classrep.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.classrep.ProfileActivity;
import com.example.classrep.R;
import com.example.classrep.SelectionActivity;
import com.example.classrep.SettingsActivity;
import com.example.classrep.SingleActivity.FundChronologyActivity;
import com.example.classrep.SingleActivity.PtaActivity;
import com.example.classrep.database.ClassRepDB;
import com.example.classrep.database.entity.Fund;
import com.example.classrep.database.entity.FundChronology;
import com.example.classrep.utilities.SingleToneClass;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class FundFragment extends Fragment {

    private ClassRepDB db;
    private List<FundChronology> fundChronologies;
    private Fund fundDB;

    private DrawerLayout drawerLayout;
    private NavigationView drawer;
    private MaterialToolbar toolbar;
    private TextView fund;
    private TextView lastFund;
    private TextView previousFund;
    private TextView casual;
    private EditText cash;
    private Button add;
    private Button minus;
    private BlurView blurView;

    private int addColor = Color.parseColor("#1ca212");
    private int minusColor = Color.parseColor("#Bf1e21");

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fund, container, false);

        db = ClassRepDB.getDatabase(this.getContext());

        String jsFunds;
        jsFunds = getArguments().getString("funds");

        Type listTypeEvent = new TypeToken<List<FundChronology>>() {}.getType();
        fundChronologies = new Gson().fromJson(String.valueOf(jsFunds), listTypeEvent);

        String jsFund;
        jsFund = getArguments().getString("fund");

        Type listTypeEvent1 = new TypeToken<Fund>() {}.getType();
        fundDB = new Gson().fromJson(String.valueOf(jsFund), listTypeEvent1);

        drawerLayout = view.findViewById(R.id.drawerLayoutFund);
        drawer = view.findViewById(R.id.drawerFund);
        drawer.bringToFront();
        toolbar = view.findViewById(R.id.fundAppBar);
        cash = view.findViewById(R.id.cash);
        fund = view.findViewById(R.id.fundMoney);
        lastFund = view.findViewById(R.id.prev);
        previousFund = view.findViewById(R.id.prev1);
        casual = view.findViewById(R.id.casuality);
        blurView = view.findViewById(R.id.blurViewFund);

        fund.setText(" "+fundDB.getMoney()+" € ");

        backgroundBlur();

        if (fundChronologies.isEmpty()) {
            lastFund.setText("");
            previousFund.setText("");
        } else if(fundChronologies.size() == 1){
            FundChronology quantity = fundChronologies.get(0);
            String stringa = ""+ quantity.getQuantity() +"€ ";
            lastFund.setText(quantity.getAction().contains("add") ? " +"+stringa : " -"+stringa);
            lastFund.setTextColor(quantity.getAction().contains("add") ? addColor : minusColor);
            previousFund.setText("");
        } else {
            FundChronology quantity1 = fundChronologies.get(0);
            FundChronology quantity2 = fundChronologies.get(1);
            String stringa1 = ""+ quantity1.getQuantity() +"€ ";
            lastFund.setText(quantity1.getAction().contains("add") ? " +"+stringa1 : " -"+stringa1);
            lastFund.setTextColor(quantity1.getAction().contains("add") ? addColor : minusColor);
            String stringa2 = ""+ quantity2.getQuantity() +"€ ";
            previousFund.setText(quantity2.getAction().contains("add") ? " +"+stringa2 : " -"+stringa2);
            previousFund.setTextColor(quantity2.getAction().contains("add") ? addColor : minusColor);
        }

        add = view.findViewById(R.id.addMoney);
        minus = view.findViewById(R.id.minusMoney);

        add.setOnClickListener(view1->{
            if(!casual.getText().toString().isEmpty()){
                AsyncTask.execute(()->{
                    int fundChron = db.ClassRepDAO().getMaxFundChron()+1;
                    fundDB.setMoney(fundDB.getMoney()+Double.parseDouble(cash.getText().toString()));
                    this.getActivity().runOnUiThread(()->{
                        fund.setText(" "+ fundDB.getMoney() +" € ");
                    });
                    db.ClassRepDAO().updateFund(fundDB);
                    FundChronology chron = new FundChronology(fundChron,
                            fundDB.getId_fund(),
                            Double.parseDouble(cash.getText().toString().replace("€", "")),
                            "add",
                            Date.from(Calendar.getInstance().toInstant()),
                            casual.getText().toString());
                    db.ClassRepDAO().insertFundChronology(chron);

                    this.getActivity().runOnUiThread(()->{
                        if (fundChronologies.isEmpty()) {
                            fundChronologies.add(chron);
                            String stringa1 = ""+ chron.getQuantity() +"€ ";
                            lastFund.setText(chron.getAction().contains("add") ? " +"+stringa1 : " -"+stringa1);
                            lastFund.setTextColor(chron.getAction().contains("add") ? addColor : minusColor);
                        } else if(fundChronologies.size() == 1){
                            fundChronologies.add(fundChronologies.get(0));
                            fundChronologies.set(0, chron);
                            String stringa1 = ""+ chron.getQuantity() +"€ ";
                            lastFund.setText(chron.getAction().contains("add") ? " +"+stringa1 : " -"+stringa1);
                            lastFund.setTextColor(chron.getAction().contains("add") ? addColor : minusColor);
                            String stringa2 = ""+ fundChronologies.get(1).getQuantity() +"€ ";
                            previousFund.setText(fundChronologies.get(1).getAction().contains("add") ? " +"+stringa2 : " -"+stringa2);
                            previousFund.setTextColor(fundChronologies.get(1).getAction().contains("add") ? addColor : minusColor);
                        } else {
                            fundChronologies.set(1, fundChronologies.get(0));
                            fundChronologies.set(0, chron);
                            String stringa1 = ""+ chron.getQuantity() +"€ ";
                            lastFund.setText(chron.getAction().contains("add") ? " +"+stringa1 : " -"+stringa1);
                            lastFund.setTextColor(chron.getAction().contains("add") ? addColor : minusColor);
                            String stringa2 = ""+ fundChronologies.get(1).getQuantity() +"€ ";
                            previousFund.setText(fundChronologies.get(1).getAction().contains("add") ? " +"+stringa2 : " -"+stringa2);
                            previousFund.setTextColor(fundChronologies.get(1).getAction().contains("add") ? addColor : minusColor);
                        }
                    });
                });
            } else {
                this.getActivity().runOnUiThread(()->{new androidx.appcompat.app.AlertDialog.Builder(getContext())
                        .setTitle("Manca la causale")
                        //se clicca ok
                        .setPositiveButton(android.R.string.yes, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .create()
                        .show();});
            }
        });

        minus.setOnClickListener(view1->{
            if(!casual.getText().toString().isEmpty()){
                AsyncTask.execute(()->{
                    int fundChron = db.ClassRepDAO().getMaxFundChron()+1;
                    fundDB.setMoney(fundDB.getMoney()-Double.parseDouble(cash.getText().toString()));
                    this.getActivity().runOnUiThread(()->{
                        fund.setText(" "+ fundDB.getMoney() +" € ");
                    });
                    db.ClassRepDAO().updateFund(fundDB);
                    FundChronology chron = new FundChronology(fundChron,
                            fundDB.getId_fund(),
                            Double.parseDouble(cash.getText().toString().replace("€", "")),
                            "minus",
                            Date.from(Calendar.getInstance().toInstant()),
                            casual.getText().toString());
                    db.ClassRepDAO().insertFundChronology(chron);

                    this.getActivity().runOnUiThread(()->{
                        if (fundChronologies.isEmpty()) {
                            fundChronologies.add(chron);
                            String stringa1 = ""+ chron.getQuantity() +"€ ";
                            lastFund.setText(chron.getAction().contains("add") ? " +"+stringa1 : " -"+stringa1);
                            lastFund.setTextColor(chron.getAction().contains("add") ? addColor : minusColor);
                        } else if(fundChronologies.size() == 1){
                            fundChronologies.add(fundChronologies.get(0));
                            fundChronologies.set(0, chron);
                            String stringa1 = ""+ chron.getQuantity() +"€ ";
                            lastFund.setText(chron.getAction().contains("add") ? " +"+stringa1 : " -"+stringa1);
                            lastFund.setTextColor(chron.getAction().contains("add") ? addColor : minusColor);
                            String stringa2 = ""+ fundChronologies.get(1).getQuantity() +"€ ";
                            previousFund.setText(fundChronologies.get(1).getAction().contains("add") ? " +"+stringa2 : " -"+stringa2);
                            previousFund.setTextColor(fundChronologies.get(1).getAction().contains("add") ? addColor : minusColor);
                        } else {
                            fundChronologies.set(1, fundChronologies.get(0));
                            fundChronologies.set(0, chron);
                            String stringa1 = ""+ chron.getQuantity() +"€ ";
                            lastFund.setText(chron.getAction().contains("add") ? " +"+stringa1 : " -"+stringa1);
                            lastFund.setTextColor(chron.getAction().contains("add") ? addColor : minusColor);
                            String stringa2 = ""+ fundChronologies.get(1).getQuantity() +"€ ";
                            previousFund.setText(fundChronologies.get(1).getAction().contains("add") ? " +"+stringa2 : " -"+stringa2);
                            previousFund.setTextColor(fundChronologies.get(1).getAction().contains("add") ? addColor : minusColor);
                        }
                    });
                });
            } else {
                this.getActivity().runOnUiThread(()->{new androidx.appcompat.app.AlertDialog.Builder(getContext())
                        .setTitle("Manca la causale")
                        //se clicca ok
                        .setPositiveButton(android.R.string.yes, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .create()
                        .show();});
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this.getActivity(), drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        toolbar.setOnMenuItemClickListener(menuItem->{

            SingleToneClass singleToneClass = com.example.classrep.utilities.SingleToneClass.getInstance();
            singleToneClass.setData("fund", fundDB.getId_fund());

            Intent intent = new Intent(this.getContext(), FundChronologyActivity.class);
            startActivity(intent);
            return false;
        });
        backgroundBlur();

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

        return view;
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