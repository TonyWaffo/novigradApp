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
public class ServiceAdapter extends ArrayAdapter<Service> {
    private Activity context;
    List<Service> services;
    DatabaseReference dbServices;



    public ServiceAdapter(Activity context,List<Service> services) {
        super(context, R.layout.layout_info_and_docs,services);
        this.context = context;
        this.services = services;
    }

    public ServiceAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.layout_services_adapter, null, true);

        Service service = services.get(position);

        TextView textViewName = (TextView) listViewItem.findViewById(R.id.viewServiceName);
        textViewName.setText(service.get_name());

        //creating another adapter to insert the list of relevant information to the big adapter
        InfoAndDocList infosAdapter=new InfoAndDocList(context, service.get_infos());
        //attaching adapter to the list view
        ListView listViewInfos=(ListView)listViewItem.findViewById(R.id.relevantInfosView);
        listViewInfos.setAdapter( infosAdapter);

        //creating another adapter to insert the list of relevant documents to the big adapter
        InfoAndDocList documentsAdapter=new InfoAndDocList( context, service.get_documents());
        //attaching adapter to the list view
        ListView listViewDocs=(ListView)listViewItem.findViewById(R.id.docsToSubmitView);
        listViewDocs.setAdapter( documentsAdapter);


        dbServices= FirebaseDatabase.getInstance().getReference("services");

        //add the delete button and remove the service from the database
        Button deleteButton=(Button)listViewItem.findViewById(R.id.deleteServiceButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbServices.child(service.get_id()).removeValue();
                Toast.makeText(context.getApplicationContext(),"You just deleted the service"+ service.get_name(),Toast.LENGTH_SHORT).show();
            }
        });


        //add an info to the current information's list
        Button updateInfos=(Button)listViewItem.findViewById(R.id.updateInfosbutton);
        updateInfos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUpdateDialog(service,0);
            }
        });


        //add a required document's name to the current document's list
        Button updateDocs=(Button)listViewItem.findViewById(R.id.updateDocsButton);
        updateDocs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUpdateDialog(service, 1);
            }
        });

        return listViewItem;
    }



    private void showUpdateDialog(Service service, int info_or_doc){
        //build a dialog popup box
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        View dialogView = context.getLayoutInflater().inflate(R.layout.update_service_dialog, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Add a new element");

        AlertDialog b = dialogBuilder.create();
        b.show();

        //Retrieve details from the popup box
        EditText textToAdd=(EditText)dialogView.findViewById(R.id.updateTextField);
        Button confirmAddingButton=(Button)dialogView.findViewById(R.id.confirmUpdateButton);
        Button cancelUpdateButton=(Button)dialogView.findViewById(R.id.cancelUpdateButton);
        confirmAddingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty((textToAdd.getText().toString()))){
                    //add elements in the list of information or document depending on the value of info_or_doc ( 0 is for info and 1 is for documents)
                    if(info_or_doc==0){
                        service.add_infos(textToAdd.getText().toString());
                    }else{
                        service.add_documents(textToAdd.getText().toString());
                    }
                    dbServices.child(service.get_id()).setValue(service);
                    b.dismiss();
                    Toast.makeText(getContext(),(info_or_doc==0)?"New information added":"New document added",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getContext(),"Add a text",Toast.LENGTH_SHORT).show();
                }
            }
        });


        cancelUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b.dismiss();
            }
        });


    }



}

