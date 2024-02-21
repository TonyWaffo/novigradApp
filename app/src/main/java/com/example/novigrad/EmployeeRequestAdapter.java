package com.example.novigrad;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/*
 *In summary, this InfoAndDocList class is an adapter used to
 * convert a list of String objects into a set of views for display
 * in a ListView or a similar Android AdapterView.
 * */
public class EmployeeRequestAdapter extends ArrayAdapter<Request> {
    private Activity context;
    List<Request> requests;
    DatabaseReference dbRequest= FirebaseDatabase.getInstance().getReference("requests");;
    DatabaseReference dbUsers=FirebaseDatabase.getInstance().getReference("profiles");
    DatabaseReference dbServices=FirebaseDatabase.getInstance().getReference("services");

    String serviceName;



    public EmployeeRequestAdapter(Activity context,List<Request> requests) {
        super(context, R.layout.layout_info_and_docs,requests);
        this.context = context;
        this.requests = requests;
    }

    public EmployeeRequestAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.employee_request_adapter, null, true);

        Request request = requests.get(position);

        TextView serviceTextView = (TextView) listViewItem.findViewById(R.id.associatedServiceText);
        TextView branchTextView = (TextView) listViewItem.findViewById(R.id.associatedBranchText);
        TextView customertextView = (TextView) listViewItem.findViewById(R.id.associatedCustomerText);
        TextView statusView = (TextView) listViewItem.findViewById(R.id.statusText);


        //extract the associated branch using the branchId associated
        dbUsers.child(request.getBranchId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String branchName = dataSnapshot.child("first_name").getValue(String.class);
                    // Use the branchName as needed
                    branchTextView.setText(branchName);
                }else{
                    customertextView.setText("Branch doesn\'t exists anymore");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle potential errors here
            }
        });


        //extract the requester name
        dbUsers.child(request.getRequesterId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String customerName = dataSnapshot.child("first_name").getValue(String.class);
                    // Use the branchName as needed
                    customertextView.setText(customerName);
                }else{
                    customertextView.setText("Customer doesn\'t exists anymore");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle potential errors here
            }
        });
        //Write the request name and status in the appropriate field
        serviceName=request.getAssociatedService();
        serviceTextView.setText(serviceName);
        statusView.setText(request.getStatus());


        //display all the uploaded files using the adapter
        InfoAndDocList adapter=new InfoAndDocList(context, request.getFiles());
        //attaching adapter to the list view
        ListView listViewInfos=(ListView)listViewItem.findViewById(R.id.filesForRequest);
        listViewInfos.setAdapter( adapter);


        //add buttons to approve and reject request
        Button approveRequestButton=(Button)listViewItem.findViewById(R.id.approveRequestButton);
        Button rejectRequestButton=(Button)listViewItem.findViewById(R.id.rejectRequestButton);

        List<Service> services = new ArrayList<>();
        //update the status of the request
        approveRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Update the service list of the customer
                FirebaseDatabase.getInstance().getReference("services").addValueEventListener(new ValueEventListener(){
                    @Override
                    public  void onDataChange(DataSnapshot dataSnapshot){

                        try{
                            Service serviceToAdd = null;

                            //iterating through all the nodes
                            for(DataSnapshot postSnapshot:dataSnapshot.getChildren()){

                                // Getting data as a map
                                Map<String, Object> serviceData = (Map<String, Object>) postSnapshot.getValue();


                                //look for the service with the associated name
                                if (serviceData != null && serviceData.get("_name").equals(serviceName)) {
                                    // Extracting data from the map
                                    String id = (String) serviceData.get("_id");
                                    String serviceName = (String) serviceData.get("_name");
                                    List<String> serviceInfos = (List<String>) serviceData.get("_infos");
                                    List<String> serviceDocuments = (List<String>) serviceData.get("_documents");

                                    serviceToAdd = new Service(id, serviceName, serviceInfos, serviceDocuments);
                                    break;

                                }
                            }

                            //add a new service to the customer current list of services
                            dbUsers.child(request.getRequesterId()).child("myServices").child(serviceToAdd.get_id()).setValue(serviceToAdd);

                        }catch (Exception e){
                            Log.e("firebase error","issue founding the service",e);
                        }
                    }
                    @Override
                    public  void onCancelled(DatabaseError databaseError){
                        //
                    }
                });




                //update the status
                dbRequest.child(request.getRequestId()).child("status").setValue("approved");
                Toast.makeText(context.getApplicationContext(),"A request has just been accepted",Toast.LENGTH_SHORT).show();
            }
        });

        //update the status of the request
        rejectRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //i might put on data change here
                dbRequest.child(request.getRequestId()).child("status").setValue("rejected");
                Toast.makeText(context.getApplicationContext(),"A request has just been rejected",Toast.LENGTH_SHORT).show();
            }
        });

        return listViewItem;
    }

    private Service extractServiceInfo(String associatedService) {
        //check one service among all the available services
        final Service[] serviceToExtract = {new Service()};// Declare as final array to avoid the error
        FirebaseDatabase.getInstance().getReference("services").addValueEventListener(new ValueEventListener(){
            @Override
            public  void onDataChange(DataSnapshot dataSnapshot){

                try{
                    //iterating through all the nodes
                    for(DataSnapshot postSnapshot:dataSnapshot.getChildren()){

                        // Getting data as a map
                        Map<String, Object> serviceData = (Map<String, Object>) postSnapshot.getValue();


                        //look for the service with the associated name
                        if (serviceData != null && serviceData.get("_name").equals(associatedService)) {
                            // Extracting data from the map
                            String id = (String) serviceData.get("_id");
                            String serviceName = (String) serviceData.get("_name");
                            List<String> serviceInfos = (List<String>) serviceData.get("_infos");
                            List<String> serviceDocuments = (List<String>) serviceData.get("_documents");

                            serviceToExtract[0] = new Service(id, serviceName, serviceInfos, serviceDocuments);

                        }
                    }

                }catch (Exception e){
                    Log.e("firebase error","issue founding the service",e);
                }
            }
            @Override
            public  void onCancelled(DatabaseError databaseError){
                //
            }
        });
        return serviceToExtract[0];
    }


}


