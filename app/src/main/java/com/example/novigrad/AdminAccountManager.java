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

public class AdminAccountManager extends AppCompatActivity {

    // Lists to store user data for branches and clients
    List<User> branches;
    List<User> clients;
    DatabaseReference dbUsers;    // Reference to the Firebase Database

    // ListViews to display branches and clients
    ListView listViewBranches;
    ListView listViewClients;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_account_manager);


        // Initialize the Firebase database reference
        dbUsers=FirebaseDatabase.getInstance().getReference("profiles");
        // Initialize lists for storing branches and clients
        branches=new ArrayList<>();
        clients=new ArrayList<>();
        // Link the ListViews with their respective UI elements
        listViewBranches=(ListView) findViewById(R.id.branchesView);
        listViewClients=(ListView) findViewById(R.id.clientsView);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Attach a value event listener to the database reference and retrieve all the users
        dbUsers.addValueEventListener(new ValueEventListener(){
            @Override
            public  void onDataChange(DataSnapshot dataSnapshot){

                try{
                    //deleting the previous list
                    branches.clear();
                    clients.clear();

                    //retrieve user data in the database
                    for(DataSnapshot postSnapshot:dataSnapshot.getChildren()) {
                        User user = postSnapshot.getValue(User.class);
                        // Categorize users based on their role and add them to the respective lists
                        if(user.getRole().equals("Customer")){
                            clients.add(user);
                        }else if(user.getRole().equals("Employee")){
                            branches.add(user);
                        }
                        Log.w("user",user.getFirst_name());
                    }

                    // Create adapters for both lists and set them to the ListViews
                    AllUserAdapter userClientAdapter=new AllUserAdapter(AdminAccountManager.this, clients,"",true);
                    AllUserAdapter userBranchesAdapter=new AllUserAdapter(AdminAccountManager.this, branches,"",true);
                    //attaching adapter to the list view and put all the services on the screen
                    listViewBranches.setAdapter(userBranchesAdapter);
                    listViewClients.setAdapter(userClientAdapter);

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

}
