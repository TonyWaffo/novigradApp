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

public class EmployeeRequests extends AppCompatActivity {

    DatabaseReference dbRequest;
    ListView requestView;
    List<Request> requests;
    String employeeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_requests);
        dbRequest= FirebaseDatabase.getInstance().getReference("requests");
        requestView=(ListView)findViewById(R.id.requestView);
        requests=new ArrayList<>();
        Intent intent=getIntent();
        employeeId=intent.getStringExtra("employeeId");
    }

    @Override
    protected void onStart() {
        super.onStart();

        //attaching a value event listener and display all the branches' requests
        dbRequest.addValueEventListener(new ValueEventListener(){
            @Override
            public  void onDataChange(DataSnapshot dataSnapshot){

                try{
                    //deleting the previous list
                    requests.clear();

                    //iterating through all the nodes
                    for(DataSnapshot postSnapshot:dataSnapshot.getChildren()){

                        // Getting data as a map
                        Map<String, Object> data = (Map<String, Object>) postSnapshot.getValue();

                        //retrieve all pending request associated to the current employee
                        if (data != null && data.get("branchId").equals(employeeId) && data.get("status").equals("pending")) {
                            // Extracting data from the map
                            String id = (String) data.get("requestId");
                            String associatedService= (String) data.get("associatedService");
                            String associatedBranch= (String) data.get("branchId");
                            String associatedCustomer= (String) data.get("requesterId");
                            String status=(String) data.get("status");
                            List<String> files = (List<String>) data.get("files");

                            // Creating a Service object
                            Request request = new Request(id, associatedService, associatedCustomer, associatedBranch,status,files);

                            // Adding the service to the list
                            requests.add(request);
                        }

                    }

                    //creating adapter
                    EmployeeRequestAdapter adapter=new EmployeeRequestAdapter(EmployeeRequests.this, requests);
                    //attaching adapter to the list view and put all the requests on the screen
                    requestView.setAdapter(adapter);
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