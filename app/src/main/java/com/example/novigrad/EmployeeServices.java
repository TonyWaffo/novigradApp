package com.example.novigrad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EmployeeServices extends AppCompatActivity {

    DatabaseReference dbAllServices;  //will point the service's database
    DatabaseReference dbEmployeeServices; //will point the profile's database

    ListView servicesToPickView;  //listView to display the all the service's where an employee will be able to pick some
    ListView servicesOfferedView;  ////listView to display the branch's services
    List<Service> services;
    List<Service> allServices;
    List<Service> myServices;
    String employeeId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_services);
        Intent intent=getIntent();
        employeeId= (String) intent.getStringExtra("employeeId");
        dbAllServices= FirebaseDatabase.getInstance().getReference("services");
        dbEmployeeServices= FirebaseDatabase.getInstance().getReference("profiles");
        servicesToPickView=(ListView) findViewById(R.id.serviceBrowserView);
        servicesOfferedView=(ListView) findViewById(R.id.customerServices);

        allServices=new ArrayList<>();
        myServices=new ArrayList<>();
    }



    @Override
    protected void onStart() {
        super.onStart();

        //attaching a value event listener and display all the available services that can be added by a branch
        dbAllServices.addValueEventListener(new ValueEventListener(){
            @Override
            public  void onDataChange(DataSnapshot dataSnapshot){

                try{
                    //deleting the previous list
                    allServices.clear();

                    //iterating through all the nodes
                    for(DataSnapshot postSnapshot:dataSnapshot.getChildren()){

                        // Getting data as a map
                        Map<String, Object> serviceData = (Map<String, Object>) postSnapshot.getValue();

                        if (serviceData != null) {
                            // Extracting data from the map
                            String id = (String) serviceData.get("_id");
                            String serviceName = (String) serviceData.get("_name");
                            List<String> serviceInfos = (List<String>) serviceData.get("_infos");
                            List<String> serviceDocuments = (List<String>) serviceData.get("_documents");

                            // Creating a Service object
                            Service service = new Service(id, serviceName, serviceInfos, serviceDocuments);

                            // Adding the service to the list
                            allServices.add(service);
                        }

                    }

                    //creating adapter
                    PickServicesAdapter serviceAdapter=new PickServicesAdapter(EmployeeServices.this, allServices,employeeId);
                    //attaching adapter to the list view and put all the services on the screen
                    servicesToPickView.setAdapter(serviceAdapter);
                }catch (Exception e){
                    Log.e("firebase error","can't refresh data",e);
                }
            }
            @Override
            public  void onCancelled(DatabaseError databaseError){
                //
            }
        });


        //attaching a value event listener and display all the user' services

        dbEmployeeServices.child(employeeId).child("myServices").addValueEventListener(new ValueEventListener(){
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
                    EmployeeServicesAdapter serviceAdapter=new EmployeeServicesAdapter(EmployeeServices.this, myServices, employeeId,"",true);
                    //attaching adapter to the list view and put all the services on the screen
                    servicesOfferedView.setAdapter(serviceAdapter);
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
}