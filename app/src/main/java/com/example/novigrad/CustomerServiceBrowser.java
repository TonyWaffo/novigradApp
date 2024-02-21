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

public class CustomerServiceBrowser extends AppCompatActivity {

    DatabaseReference dbServices;  //will point the service's database
    DatabaseReference dbUsers; //will point the profile's database

    ListView servicesView;  //listView to display all services offered by the selected branch

    List<Service> services;  //list of all my services
    EmployeeServicesAdapter serviceAdapter;  //adapter we will use to render all the services

    User branch;
    String customerId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_service_browser);
        Intent intent=getIntent();
        branch= (User) intent.getSerializableExtra("branch"); //get the info about the branch where to extract data
        customerId=intent.getStringExtra("customerId");
        dbServices= FirebaseDatabase.getInstance().getReference("services");
        dbUsers= FirebaseDatabase.getInstance().getReference("profiles");
        servicesView=(ListView) findViewById(R.id.serviceBrowserView);

        services=new ArrayList<>();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (serviceAdapter != null) {
            serviceAdapter.handleActivityResult(requestCode, resultCode, data);
        }
    }
    @Override
    protected void onStart() {
        super.onStart();

        //attaching a value event listener and display all the user' services

        dbUsers.child(branch.getId()).child("myServices").addValueEventListener(new ValueEventListener(){
            @Override
            public  void onDataChange(DataSnapshot servicesSnapshot){

                try{
                    //deleting the previous list
                    services.clear();

                    if(servicesSnapshot.exists()){
                        for(DataSnapshot serviceSnapshot:servicesSnapshot.getChildren()){
                            Service service = serviceSnapshot.getValue(Service.class);
                            // Adding the service to the list
                            services.add(service);

                        }
                    }

                    //creating adapter
                    serviceAdapter=new EmployeeServicesAdapter(CustomerServiceBrowser.this, services, branch.getId(),customerId,false);
                    //attaching adapter to the list view and put all the services on the screen
                    servicesView.setAdapter(serviceAdapter);
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