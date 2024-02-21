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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


/*
 *In summary, this InfoAndDocList class is an adapter used to
 * convert a list of String objects into a set of views for display
 * in a ListView or a similar Android AdapterView.
 * */
public class PickServicesAdapter extends ArrayAdapter<Service> {
    private Activity context;
    List<Service> services;
    String userId;
    DatabaseReference dbUsers;



    public PickServicesAdapter(Activity context,List<Service> services,String userId) {
        super(context, R.layout.layout_info_and_docs,services);
        this.context = context;
        this.services = services;
        this.userId=userId;
    }

    public PickServicesAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.employee_pick_services_adapter, null, true);

        Service service = services.get(position);

        //Extracting the name of the service
        TextView textViewName = (TextView) listViewItem.findViewById(R.id.servNameField);
        textViewName.setText(service.get_name());



        dbUsers= FirebaseDatabase.getInstance().getReference("profiles");

        //add the delete button and remove the service from the database
        Button addServiceButton=(Button)listViewItem.findViewById(R.id.employeeAddServiceButton);
        addServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add a service in the database, on the employee profile
                dbUsers.child(userId).child("myServices").child(service.get_id()).setValue(service);

                //Notification fwhen a service is added
                Toast.makeText(context.getApplicationContext(),"Service "+ service.get_name()+" added to the branch",Toast.LENGTH_SHORT).show();
            }
        });


        return listViewItem;
    }




}

