package com.example.novigrad;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomerServices extends AppCompatActivity {

    DatabaseReference dbServices;  //will point the service's database
    DatabaseReference dbUsers; //will point the profile's database

    ListView myServicesView;  //listView to display all myservices
    ListView allBranchesView;  ////listView to display all the branches
    //List<Service> services;
    List<User> allBranches;  //list of all the services offered by a branch
    List<Service> myServices;  //list of all my services
    String customerId;

    EditText findByAddress;
    EditText findByServiceName;
    Button findFromTimeBtn;
    Button findToTimeBtn;
    Button findBranchesBtn;
    String address;
    String serviceName;
    String minInterval;
    String maxInterval;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_services);
        Intent intent=getIntent();
        customerId= (String) intent.getStringExtra("customerId"); //don't forget put it properly in the intent
        dbServices= FirebaseDatabase.getInstance().getReference("services");
        dbUsers= FirebaseDatabase.getInstance().getReference("profiles");
        allBranchesView=(ListView) findViewById(R.id.serviceBrowserView);
        myServicesView=(ListView) findViewById(R.id.customerServices);

        findByAddress=(EditText)findViewById(R.id.findByAddress);
        findByServiceName=(EditText)findViewById(R.id.findByName);
        findFromTimeBtn=(Button)findViewById(R.id.findFrom);
        findToTimeBtn=(Button)findViewById(R.id.findTo);
        findBranchesBtn=(Button)findViewById(R.id.findBranchesBtn);


        allBranches=new ArrayList<>();
        myServices=new ArrayList<>();
    }



    @Override
    protected void onStart() {
        super.onStart();


        findBranchesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //get the text from the fields
                address=findByAddress.getText().toString();
                serviceName=findByServiceName.getText().toString();
                minInterval=findFromTimeBtn.getText().toString();
                maxInterval=findToTimeBtn.getText().toString();
                //attaching a value event listener and display all the branches
                dbUsers.addValueEventListener(new ValueEventListener(){
                    @Override
                    public  void onDataChange(DataSnapshot dataSnapshot){

                        try{
                            //deleting the previous list
                            allBranches.clear();

                            //retrieve user data in the database
                            for(DataSnapshot postSnapshot:dataSnapshot.getChildren()) {
                                //Map<String, Object> userData = (Map<String, Object>) postSnapshot.getValue();
                                //User user = customDeserializeUser(userData);
                                User user = postSnapshot.getValue(User.class);
                                Map<String, Object> userData = (Map<String, Object>) postSnapshot.getValue();
                                //User user = customDeserializeUser(userData);
                                Map<String, Object> userServices = (Map<String, Object>) userData.get("myServices");


                                //conditions for finding a branch
                                Boolean conditions;

                                if(user!=null){

                                    //conditions for finding a branch

                                    conditions=user.getRole().equals("Employee");
                                    if (!address.equals("")){
                                        conditions = conditions && user.getPostal_code().equals(address);
                                    }

                                    if (!serviceName.equals("")){
                                        conditions = conditions && findServiceName(userServices,serviceName);
                                    }

                                    if(!minInterval.equals("from") && !maxInterval.equals("to")) {
                                        conditions =conditions && findServiceByTimeInterval(user.getSchedule());
                                    }

                                    if(conditions){
                                        allBranches.add(user);
                                    }
                                    Log.w("user",user.getFirst_name());
                                }

                            }

                            // Create adapters for both lists and set them to the ListViews
                            AllUserAdapter allUserAdapter=new AllUserAdapter(CustomerServices.this, allBranches,customerId,false);
                            //attaching adapter to the list view and put all the services on the screen
                            allBranchesView.setAdapter(allUserAdapter);

                        }catch (Exception e){
                            Log.e("firebase error again","can't refresh users",e);
                        }
                    }
                    @Override
                    public  void onCancelled(DatabaseError databaseError){
                        // Handle potential cancellation of the data retrieval
                    }
                });
            }
        });

        //attaching a value event listener and display all the user' services

        dbUsers.child(customerId).child("myServices").addValueEventListener(new ValueEventListener(){
            @Override
            public  void onDataChange(DataSnapshot myServicesSnapshot){

                try{
                    //deleting the previous list
                    myServices.clear();

                    if(myServicesSnapshot.exists()){
                        for(DataSnapshot serviceSnapshot:myServicesSnapshot.getChildren()){
                            Service service = serviceSnapshot.getValue(Service.class);
                            // Adding the service to the list
                            myServices.add(service);

                        }
                    }

                    //creating adapter
                    EmployeeServicesAdapter serviceAdapter=new EmployeeServicesAdapter(CustomerServices.this, myServices, customerId,"",true);
                    //attaching adapter to the list view and put all the services on the screen
                    myServicesView.setAdapter(serviceAdapter);
                }catch (Exception e){
                    Log.e("firebase error","can't refresh data",e);
                }
            }
            @Override
            public  void onCancelled(DatabaseError databaseError){
                //
            }
        });

        //create the time picker pop up box
        TimePickerDialog.OnTimeSetListener fromTimeListener = (view, hourOfDay, minute) -> {
            if (view.isShown()) {

                String hour = String.format("%02d", hourOfDay);
                String min = String.format("%02d", minute);
                String time = hour + ":" + min;
                findFromTimeBtn.setText(time);

            }
        };

        //Set time with the button
        findFromTimeBtn.setOnClickListener(view -> new TimePickerDialog(this, fromTimeListener, 0, 0, true).show());


        //create  another time picker pop up box
        TimePickerDialog.OnTimeSetListener toTimeListener = (view, hourOfDay, minute) -> {
            if (view.isShown()) {

                String hour = String.format("%02d", hourOfDay);
                String min = String.format("%02d", minute);
                String time = hour + ":" + min;
                findToTimeBtn.setText(time);

            }
        };

        //Set time with the button
        findToTimeBtn.setOnClickListener(view -> new TimePickerDialog(this, toTimeListener, 0, 0, true).show());

    }


    //check if the service's name entered exists
    public Boolean findServiceName(Map<String,Object> services,String serviceName) {

        //iterate through the customer's list of service
        if(services!=null && services.size()!=0){
            for(Map.Entry<String, Object> entry:services.entrySet()){
                Map<String, Object> service = (Map<String, Object>) entry.getValue();

                // Check if "_name" key exists in the map
                if (service.containsKey("_name")) {
                    Object nameObject = service.get("_name");

                    // Check if the value associated with "_name" is a String
                    if (nameObject instanceof String) {
                        String serviceNameInMap = (String) nameObject;

                        //check if the input matches any service offered by the branches
                        Pattern pattern = Pattern.compile(serviceName, Pattern.CASE_INSENSITIVE);
                        Matcher matcher = pattern.matcher(serviceNameInMap);
                        boolean matchFound = matcher.find();
                        if (matchFound) {
                            return true;
                        }
                    } else {
                        Log.w("service","Not a string");
                    }
                } else {
                    Log.w("service", "_name doesn't exist");
                }
            }
        }
        return false;
    }



    //find availabilities within the week schedule
    public Boolean findServiceByTimeInterval(Map<String,Object> weekSchedule){

        //extract time from the button
        String fromTimeText = findFromTimeBtn.getText().toString();
        String toTimeText = findToTimeBtn.getText().toString();

        // Check if the time fields are not empty
        if (fromTimeText.equals("from") || toTimeText.equals("to")) {
            Toast.makeText(getApplicationContext(), "Please set both from and to times", Toast.LENGTH_SHORT).show();
            return false;
        }

        //convert time set by the customer
        int customerMinHours = Integer.parseInt(fromTimeText.split(":")[0]);
        int customerMinMinutes = Integer.parseInt(fromTimeText.split(":")[1]);

        int customerMaxHours = Integer.parseInt(toTimeText.split(":")[0]);
        int customerMaxMinutes = Integer.parseInt(toTimeText.split(":")[1]);

        Time customerMax = new Time(customerMaxHours, customerMaxMinutes, 0);
        Time customerMin = new Time(customerMinHours, customerMinMinutes, 0);

        //check if the customer enter a proper interval
        if(customerMax.compareTo(customerMin)<0){
            Toast.makeText(getApplicationContext(),"The time interval is wrong", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(weekSchedule != null){
            //iterate through all the week's days
            for(Map.Entry<String, Object> entry:weekSchedule.entrySet()){
                String day=entry.getKey();
                Map<String, String> singleDay = (Map<String, String>) entry.getValue();
                //check if there is no null values
                if(day!=null && singleDay!=null) {
                    String fromTime = singleDay.get("from");
                    String toTime = singleDay.get("to");

                    //check if there is no null values
                    if(!fromTime.toLowerCase().equals("null")  && !toTime.toLowerCase().equals("null")){
                        int minHours = Integer.parseInt(fromTime.split(":")[0]);
                        int minMinutes = Integer.parseInt(fromTime.split(":")[1]);

                        int maxHours = Integer.parseInt(toTime.split(":")[0]);
                        int maxMinutes = Integer.parseInt(toTime.split(":")[1]);

                        Time max = new Time(maxHours, maxMinutes, 0);
                        Time min = new Time(minHours, minMinutes, 0);

                        //compare to find if there is a match for the customer
                        if(min.compareTo(customerMin)<0 && max.compareTo(customerMax)>0){
                            return true;
                        }
                    }
                }
            }

        }
        return  false;
    }

}