package com.example.novigrad;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EmployeeSchedule extends AppCompatActivity {
    private DatabaseReference dbSchedule;
    private DatabaseReference dbUsers;//new
    private ArrayList<Schedule> weekSchedule;

    private String employeeId;//new

    private boolean dataRendered = false;
    Button updateSchedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_schedule);
        Intent intent=getIntent();
        employeeId= intent.getStringExtra("employeeId");//new

        dbSchedule = FirebaseDatabase.getInstance().getReference("schedule");
        dbUsers=FirebaseDatabase.getInstance().getReference("profiles");//new
        weekSchedule = new ArrayList<>();

        updateSchedule=(Button) findViewById(R.id.updateSchedule);

        updateSchedule.setOnClickListener(v -> {
                ArrayList<String> daysFailed = new ArrayList<>();
                for (Schedule s : weekSchedule) {
                    if (!timeIsCorrect(s)) {
                        daysFailed.add(s.day);
                    } else {
                        //old version
                        //dbSchedule.child(s.day).child("from").setValue(s.from);
                        //dbSchedule.child(s.day).child("to").setValue(s.to);
                        dbUsers.child(employeeId).child("schedule").child(s.day).child("from").setValue(s.from);
                        dbUsers.child(employeeId).child("schedule").child(s.day).child("to").setValue(s.to);
                        dbUsers.child(employeeId).child("schedule").child(s.day).child("id").setValue(s.id);
                    }
                }

                if(!daysFailed.isEmpty())
                {
                    StringBuffer sb = new StringBuffer();
                    sb.append("Please set a correct from and to time on these days: ");
                    for (String s : daysFailed) {
                        sb.append(s);
                        sb.append(" ");
                    }
                    Toast.makeText(getApplicationContext(), sb.toString(),Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Schedule updated", Toast.LENGTH_SHORT).show();
                }
        });
    }

    private boolean timeIsCorrect(Schedule s) {
        if(s.from.equals("null") && s.to.equals("null")){
            return true;
        }

        if(s.from.equals("null") || s.to.equals("null")){
            return false;
        }

        int fHour = Integer.parseInt(s.from.split(":")[0]);
        int fMinutes = Integer.parseInt(s.from.split(":")[1]);

        int tHour = Integer.parseInt(s.to.split(":")[0]);
        int tMinutes = Integer.parseInt(s.to.split(":")[1]);

        Time tFrom = new Time(fHour, fMinutes, 0);
        Time tTo = new Time(tHour, tMinutes, 0);

        return tTo.compareTo(tFrom) > 0;
    }

    @Override
    protected void onStart() {
        super.onStart();

        //put dbUsers.child(employeeId).child("schedule").addValueEventListener instead  of dbSchedule.
        dbUsers.child(employeeId).child("schedule").addValueEventListener(new ValueEventListener(){
            @Override
            public  void onDataChange(DataSnapshot dataSnapshot){

                try{
                    //deleting the previous list
                    weekSchedule.clear();

                    if(dataSnapshot.exists()){
                        //iterating through all the nodes
                        for(DataSnapshot daySnapshot:dataSnapshot.getChildren()){

                            // Getting data as a map
                            Map<String, Object> daySchedule = (Map<String, Object>) daySnapshot.getValue();

                            if (daySchedule != null) {
                                String day = daySnapshot.getKey();
                                String from = (String) daySchedule.get("from");
                                String to = (String) daySchedule.get("to");
                                int id = Integer.parseInt(daySchedule.get("id").toString());
                                Schedule service = new Schedule(day, from, to, id);
                                weekSchedule.add(service);
                            }
                        }
                        render();
                    }else{
                        //create a render when it's the first time to set up a schedule
                        otherRender();
                    }
                }catch (Exception e){
                    Log.e("firebase error","can't refresh data",e);
                }
            }
            @Override
            public  void onCancelled(DatabaseError databaseError){
                //
            }
        });
    }

    private void render() {
        if(!dataRendered) {
            Collections.sort(weekSchedule);

            LinearLayout layout = findViewById(R.id.linearLayout);
            layout.removeAllViews();//clear all existing views

            for(Schedule s: this.weekSchedule) {

                LinearLayout hLayout = new LinearLayout(this);

                hLayout.addView(createTxt(s.day));
                hLayout.addView(createTxt("from"));

                Button fromBtn = createSetTimeBtn(s, "from");
                Button toBtn = createSetTimeBtn(s, "to");
                Button clearBtn = createClearBtn(s, fromBtn, toBtn);
                clearBtn.setLayoutParams(layout.getLayoutParams());
                toBtn.setLayoutParams(layout.getLayoutParams());
                fromBtn.setLayoutParams(layout.getLayoutParams());

                hLayout.addView(fromBtn);
                hLayout.addView(createTxt("to"));
                hLayout.addView(toBtn);
                hLayout.addView(clearBtn);

                layout.addView(hLayout);
            }

        }
    }

    private void otherRender() {
        if(!dataRendered) {
            weekSchedule.clear();
            String[] days={"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};

            LinearLayout layout = findViewById(R.id.linearLayout);
            layout.removeAllViews(); //clear all existing views
            for(int i=0; i<7;i++) {
                Schedule schedule=new Schedule(days[i],"null","null",i+1);

                LinearLayout hLayout = new LinearLayout(this);

                hLayout.addView(createTxt(days[i]));
                hLayout.addView(createTxt("from"));

                Button fromBtn = createSetTimeBtn(schedule, "from");
                Button toBtn = createSetTimeBtn(schedule, "to");
                Button clearBtn = createClearBtn(schedule, fromBtn, toBtn);
                clearBtn.setLayoutParams(layout.getLayoutParams());
                toBtn.setLayoutParams(layout.getLayoutParams());
                fromBtn.setLayoutParams(layout.getLayoutParams());

                hLayout.addView(fromBtn);
                hLayout.addView(createTxt("to"));
                hLayout.addView(toBtn);
                hLayout.addView(clearBtn);

                layout.addView(hLayout);
                weekSchedule.add(schedule);
            }

        }
    }

    private Button createClearBtn(Schedule s, Button from, Button to) {
        Button btn = new Button(this);
        btn.setText("clear");
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10,10,10,10);
        btn.setLayoutParams(params);

        btn.setOnClickListener(view -> {
            from.setText("Set time");
            to.setText("Set time");

            s.from = "null";
            s.to = "null";
            weekSchedule.set(weekSchedule.indexOf(s), s);
        });

        return btn;
    }

    private Button createSetTimeBtn(Schedule s, String tag) {
        Button btn = new Button(this);
        try{

            String btnTxt = tag.equals("from")
                    ? (s.from.toLowerCase().equals("null") ?  "Set time" : s.from)
                    : (s.to.toLowerCase().equals("null") ?  "Set time" : s.to);

            btn.setText(btnTxt);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(10,10,10,10);
            btn.setLayoutParams(params);

            TimePickerDialog.OnTimeSetListener myTimeListener = (view, hourOfDay, minute) -> {
                if (view.isShown()) {

                    String hToSting = String.format("%02d", hourOfDay);
                    String mToSting = String.format("%02d", minute);
                    String timeToSting = hToSting + ":" + mToSting;

                    if(tag == "from"){
                        s.from = timeToSting;
                        btn.setText(s.from.equals("null") ? "Set time" : s.from);
                    } else {
                        s.to = timeToSting;
                        btn.setText(s.to.equals("null") ? "Set time" : s.to);
                    }

                    weekSchedule.set(weekSchedule.indexOf(s), s);
                }
            };

            TimePickerDialog timePicker = new TimePickerDialog(this, myTimeListener, 0, 0, true);
            btn.setOnClickListener(view -> timePicker.show());

        }catch (Exception e){
            Log.e("button","button error",e);
        }
        return btn;
    }

    private TextView createTxt(String content) {

        TextView txt = new TextView(this);
        txt.setText(content);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10,10,10,10);
        txt.setLayoutParams(params);

        return txt;
    }
}