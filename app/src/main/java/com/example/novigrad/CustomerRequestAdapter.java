package com.example.novigrad;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
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


/*
 *In summary, this InfoAndDocList class is an adapter used to
 * convert a list of String objects into a set of views for display
 * in a ListView or a similar Android AdapterView.
 * */
public class CustomerRequestAdapter extends ArrayAdapter<Request> {
    private Activity context;
    List<Request> requests;
    DatabaseReference dbRequest= FirebaseDatabase.getInstance().getReference("requests");;
    DatabaseReference dbUsers=FirebaseDatabase.getInstance().getReference("profiles");
    DatabaseReference dbServices=FirebaseDatabase.getInstance().getReference("services");



    public CustomerRequestAdapter(Activity context,List<Request> requests) {
        super(context, R.layout.layout_info_and_docs,requests);
        this.context = context;
        this.requests = requests;
    }

    public CustomerRequestAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.customer_request_adapter, null, true);

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
        serviceTextView.setText(request.getAssociatedService());
        statusView.setText(request.getStatus());


        //display all the uploaded files using the adapter
        InfoAndDocList adapter=new InfoAndDocList(context, request.getFiles());
        //attaching adapter to the list view
        ListView listViewInfos=(ListView)listViewItem.findViewById(R.id.filesForRequest);
        listViewInfos.setAdapter( adapter);


        return listViewItem;
    }




}



