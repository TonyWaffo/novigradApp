package com.example.novigrad;

import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


public class EmployeeServicesAdapter extends ArrayAdapter<Service> {
    private Activity context;
    List<Service> services;   //list of services
    DatabaseReference dbUsers;
    DatabaseReference dbRequest;

    String employeeId;
    String customerId;
    boolean deleteOrUpload; //true for delete and false for upload
    private static final int PICK_FILE_REQUEST_CODE = 1;
    private List<String> fileList=new ArrayList<>();  //list of all files
    private List<Uri> uriList=new ArrayList<>(); //list of all uri






    public EmployeeServicesAdapter(Activity context,List<Service> services,String employeeId,String customerId,boolean deleteOrUpload) {
        super(context, R.layout.layout_info_and_docs,services);
        this.context = context;
        this.services = services;
        this.employeeId=employeeId;
        this.customerId=customerId;
        this.deleteOrUpload=deleteOrUpload;
    }

    public EmployeeServicesAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        dbRequest = FirebaseDatabase.getInstance().getReference("requests");

        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.employee_services_adapter, null, true);

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


        dbUsers= FirebaseDatabase.getInstance().getReference("profiles");
        dbRequest=FirebaseDatabase.getInstance().getReference("requests");

        //add the delete button and remove the service from the database
        Button deleteOrUploadButton=(Button)listViewItem.findViewById(R.id.deleteServiceOrUploadFile);

        if(deleteOrUpload==true){
            //edit button's text and remove the service from the database
            deleteOrUploadButton.setText("Delete service");
            deleteOrUploadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dbUsers.child(employeeId).child("myServices").child(service.get_id()).removeValue();
                    Toast.makeText(context.getApplicationContext(),"You just deleted the service "+ service.get_name(),Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            //edit button's text and create a popup
            deleteOrUploadButton.setText("Upload files");
            deleteOrUploadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopup(service);
                }
            });
        }


        return listViewItem;
    }


    private void showPopup(Service service){
        //build a dialog popup box
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        View dialogView = context.getLayoutInflater().inflate(R.layout.upload_popup, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Upload Files");

        AlertDialog b = dialogBuilder.create();
        b.show();

        //Retrieve details from the popup box


        ///set up the adapter to display all the uploaded files
        InfoAndDocList allFilesAdapter=new InfoAndDocList((Activity) context,fileList);
        ListView allFilesField= (ListView)dialogView.findViewById(R.id.filesToSubmit);
        allFilesField.setAdapter(allFilesAdapter);

        //EditText textToAdd=(EditText)dialogView.findViewById(R.id.updateTextField);
        Button submitApplicationButton=(Button)dialogView.findViewById(R.id.submitApplicationButton);
        Button cancelApplicationButton=(Button)dialogView.findViewById(R.id.cancelApplicationButton);
        Button uploadFileButton=(Button)dialogView.findViewById(R.id.uploadFileButton);
        /*confirmAddingButton.setOnClickListener(new View.OnClickListener() {
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
        });*/

        //upload file from the phone
        uploadFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // notify adapter when data has changed
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fileList.add("newFile.txt");
                        allFilesAdapter.notifyDataSetChanged(); //notify the right adapter for the changes
                        Toast.makeText(context.getApplicationContext(), "File uploaded",Toast.LENGTH_SHORT).show();
                    }
                });
                //Open file picker
                /*Intent uploadIntent= new Intent(Intent.ACTION_GET_CONTENT);
                uploadIntent.setType("/*");
                startActivityForResult(context,uploadIntent,PICK_FILE_REQUEST_CODE,null);
                */
            }
        });


        //remove the popup from the screen
        cancelApplicationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b.dismiss();
                fileList.clear();
                uriList.clear();
            }
        });

        //submit application and remove the popup from the screen
        submitApplicationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //uploadFileToStorage(uriList);  // upload all files to the FirebaseStorage
                //create id for the request
                String requestId=dbRequest.push().getKey();

                //upload file from the storage
                try{
                    //uploadFileToStorage(uriList,requestId);

                    //submit the request in the database
                    Request request=new Request(requestId,service.get_name(),customerId,employeeId,"pending",fileList);
                    dbRequest.child(requestId).setValue(request);
                }catch (Exception e){
                    Log.e("Storage","Can't upload files",e);
                }

                //remove the popup
                b.dismiss();
                fileList.clear();
                uriList.clear();

                //notification
                Toast.makeText(context.getApplicationContext(), "A request for service "+service.get_name()+" has be sent to the branch",Toast.LENGTH_SHORT).show();
            }
        });


    }

    /*because i can't create onActivityresult here, this method would the handle
    *the response and will be called by the class using this adapter
    */
    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null && data.getData()!=null) {
            //handle the selected file
            Uri fileUri = data.getData();
            //String fileName= employeeId+fileUri.getLastPathSegment();            //add the file to the list of all files
            uriList.add(fileUri); // Add the picked file's uri to the list
        }
    }


    // Upload the file to Firebase Storage
    private void uploadFileToStorage(List<Uri> uriList,String reqId) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("requests");
        StorageReference fileRef = storageRef.child(reqId);


         for(Uri fileUri:uriList){
            // Upload each file to Firebase Storage
              fileRef.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get the download URL and save it in your database if needed
                    fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String downloadUrl = uri.toString();
                        //update the file's list
                        fileList.add(downloadUrl);
                    });
                })
                .addOnFailureListener(e -> {
                    // Handle errors during the upload
                });
         }

    }

    //proceed to the update in the database "requests"
    private void updateDatabase(List<String> files) {
        // Update your database with the download URL or any other relevant information
        dbRequest.child("requestId").child("files").setValue(files);
    }

}


