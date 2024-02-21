package com.example.novigrad;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import android.view.View;

import android.widget.AdapterView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdminServices extends AppCompatActivity {
    DatabaseReference databaseServices;
    List<String> serviceInfos;
    List<String> serviceDocuments;
    List<Service> services;


    EditText serviceName;
    EditText infoService;
    EditText docService;
    ListView listViewInfos;
    ListView listViewDocs;
    ListView listViewServices;


    Button buttonAddInfo;
    Button buttonAddDocument;
    Button buttonAddService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_services);

        serviceName=(EditText)findViewById(R.id.serviceNametextid);
        infoService=(EditText)findViewById(R.id.infoServiceText);
        docService=(EditText)findViewById(R.id.docServiceText);
        listViewInfos = (ListView) findViewById(R.id.infoListView);
        listViewDocs = (ListView) findViewById(R.id.docListView);
        listViewServices = (ListView) findViewById(R.id.listViewServices);
        buttonAddInfo = (Button) findViewById(R.id.addInfoButton);
        buttonAddDocument = (Button) findViewById(R.id.addDocButton);
        buttonAddService = (Button) findViewById(R.id.addServiceButton);

        serviceInfos=new ArrayList<>();
        serviceDocuments=new ArrayList<>();
        services=new ArrayList<>();

        databaseServices= FirebaseDatabase.getInstance().getReference("services");

        buttonAddInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!TextUtils.isEmpty((infoService.getText().toString()))){
                    //add the single info to the info's list
                    serviceInfos.add(infoService.getText().toString());
                    //creating adapter
                    InfoAndDocList infoAdapter=new InfoAndDocList(AdminServices.this, serviceInfos);
                    //attaching adapter to the list view
                    listViewInfos.setAdapter(infoAdapter);

                    //empty the field
                    infoService.setText("");
                }else{
                    //if nothing was written in the field, generate a popup
                    Toast.makeText(getApplicationContext(),"Please enter some informations about the service",Toast.LENGTH_SHORT).show();
                }

            }
        });

        buttonAddDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!TextUtils.isEmpty((docService.getText().toString()))){
                    //add the documents to the document's list
                    serviceDocuments.add(docService.getText().toString());
                    //creating adapter
                    InfoAndDocList documentsAdapter=new InfoAndDocList(AdminServices.this, serviceDocuments);
                    //attaching adapter to the list view
                    listViewDocs.setAdapter(documentsAdapter);

                    //empty the field
                    docService.setText("");
                }else{
                    //if nothing was written in the field, generate a popup
                    Toast.makeText(getApplicationContext(),"Please enter some required documents",Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonAddService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    if(!TextUtils.isEmpty((serviceName.getText().toString())) && !serviceInfos.isEmpty() && !serviceDocuments.isEmpty()){

                        //check if the servce's name already exists
                        for(Service serv:services){
                            if (serv.get_name().equals(serviceName.getText().toString())) {
                                Toast.makeText(getApplicationContext(),"This service already exists",Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        String id=databaseServices.push().getKey();
                        //create a service with the name, the informations and the documents
                        Service service=new Service(id,serviceName.getText().toString(),serviceInfos,serviceDocuments);

                        //add the service to the list
                        services.add(service);

                        //add to database
                        databaseServices.child(id).setValue(service);

                        //creating adapter
                        //ServiceAdapter serviceAdapter=new ServiceAdapter(AdminServices.this, services);
                        //attaching adapter to the list view and put all the services on the screen
                        //listViewServices.setAdapter(serviceAdapter);

                        //clear the input text name and the list containing the information and the documents
                        serviceName.setText("");
                        infoService.setText("");
                        docService.setText("");
                        serviceInfos=new ArrayList<>();
                        serviceDocuments=new ArrayList<>();

                        //Clear all  the info's and documents list views
                        listViewDocs.setAdapter(new InfoAndDocList(AdminServices.this, new ArrayList<>()));
                        listViewInfos.setAdapter(new InfoAndDocList(AdminServices.this, new ArrayList<>()));
                        Toast.makeText(getApplicationContext(),"New Service added",Toast.LENGTH_SHORT).show();

                    }else{
                        //if nothing was written in the field, generate a popup
                        Toast.makeText(getApplicationContext(),"One or several empty fields",Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Log.e("adapter","adapter not working properly",e);
                }

            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

        //attaching a value event listener
        databaseServices.addValueEventListener(new ValueEventListener(){
            @Override
            public  void onDataChange(DataSnapshot dataSnapshot){

                try{
                    //deleting the previous list
                    services.clear();

                    //iterating through all the nodes
                    for(DataSnapshot postSnapshot:dataSnapshot.getChildren()){

                        //getting product

                        //option1
                        //Service service=postSnapshot.getValue(Service.class);

                        /*Firebase sometimes can't fetch data without using postSnapshot.getValue(Service.class);
                         * use instead those different options
                         */

                        //option 2
                        //Service service=postSnapshot.getValue(new GenericTypeIndicator<Service>() {});

                        //option 3
                        //String json = postSnapshot.getValue().toString();  //getting the raw data as a JSON string
                        //Service service = new Gson().fromJson(json, Service.class);  // converting the JSON string to your desired object type using a custom deserializer


                        // Getting data as a map beacuse none of these methods work
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
                            services.add(service);
                        }
                    }

                    //creating adapter
                    ServiceAdapter serviceAdapter=new ServiceAdapter(AdminServices.this, services);
                    //attaching adapter to the list view and put all the services on the screen
                    listViewServices.setAdapter(serviceAdapter);
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
