package com.example.novigrad;

import androidx.appcompat.app.AppCompatActivity;

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

public class CustomerEvaluation extends AppCompatActivity {
    List<User> branches;
    DatabaseReference dbUsers;
    ListView listViewBranches;
    String customerId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_customer_evaluation);

        customerId= (String) getIntent().getStringExtra("customerId");

        dbUsers= FirebaseDatabase.getInstance().getReference("profiles");
        branches=new ArrayList<>();
        listViewBranches=(ListView) findViewById(R.id.allBranchView);
    }

    @Override
    protected void onStart() {
        super.onStart();

        dbUsers.addValueEventListener(new ValueEventListener(){
            @Override
            public  void onDataChange(DataSnapshot dataSnapshot){

                try{
                    //deleting the previous list
                    branches.clear();

                    //retrieve user data in the database
                    for(DataSnapshot postSnapshot:dataSnapshot.getChildren()) {
                        User user = postSnapshot.getValue(User.class);
                        // Categorize users based on their role and add them to the respective lists
                        if(user.getRole().equals("Employee")) {
                            branches.add(user);
                            Log.w("firebase error again",user.getFirst_name());
                        }
                    }

                    // Create adapters for both lists and set them to the ListViews
                    BranchRatingAdapter branchRatingAdapter=new BranchRatingAdapter(CustomerEvaluation.this, branches,customerId);
                    //attaching adapter to the list view and put all the services on the screen
                    listViewBranches.setAdapter(branchRatingAdapter);

                }catch (Exception e){
                    Log.e("users","can't refresh users",e);
                }
            }
            @Override
            public  void onCancelled(DatabaseError databaseError){
                // Handle potential cancellation of the data retrieval
            }
        });
    }
}