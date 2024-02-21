// SEG 2505 - groupe 21
// Projet Service Novigrade


package com.example.novigrad;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    DatabaseReference databaseUsers;

    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Set the content view to the layout defined in activity_main.xml
        databaseUsers= FirebaseDatabase.getInstance().getReference("profiles");
        mAuth= FirebaseAuth.getInstance();
    }


    public void createAccount(View view){
        Intent intent= new Intent(getApplicationContext(), SignUp.class);
        startActivityForResult(intent,0);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) return;

        String accountCreationMsg = data.getStringExtra("signUpMsg");


        TextView displayMsg=(TextView)findViewById(R.id.signUpMessage);

        //display a message when an account is created successfully
        displayMsg.setText(accountCreationMsg);


    }

    public void login(View view){

        //Intent toWelcomePage;
        EditText login_email= (EditText) findViewById(R.id.loginEmail);
        EditText login_password= (EditText) findViewById(R.id.loginPassword);

        TextView displayMsg=(TextView)findViewById(R.id.signUpMessage);

        String email_input=login_email.getText().toString().trim();
        String password_input=login_password.getText().toString().trim();

        mAuth.signInWithEmailAndPassword(email_input, password_input)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success and take user credentials
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            displayMsg.setText("Authentication passed");

                            //retrieve user's information via his id
                            databaseUsers.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    try {
                                        //retrieve user data in the database
                                        if (dataSnapshot.exists()) {
                                            User userAccount = dataSnapshot.getValue(User.class);
                                            // Access the user's data and pass it to one of the  welcome page
                                            Intent toWelcomePage;
                                            if(userAccount.getRole().equals("Administrator")){
                                                toWelcomePage=new Intent(getApplicationContext(), AdminHome.class);
                                            }else if(userAccount.getRole().equals("Employee")){
                                                toWelcomePage=new Intent(getApplicationContext(), EmployeeHome.class);
                                            }else{
                                                toWelcomePage=new Intent(getApplicationContext(), CustomerHome.class);
                                            }
                                            toWelcomePage.putExtra("userProfile",userAccount);
                                            startActivity(toWelcomePage);
                                        } else {
                                            // User data not found
                                        }
                                    }catch (Exception e){
                                        Log.e("Firebase","data arrival error"+ e.getMessage());
                                        displayMsg.setText("An error occurred while retrieving user data");
                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    // Handle any errors that occur during the data retrieval
                                    Log.e("Firebase", "Data retrieval failed: " + databaseError.getMessage());
                                }
                            });
                        } else {
                            // If authentication fails, display a message to the user.
                            Toast.makeText(getApplicationContext(),"Invalid credentials",Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }
}