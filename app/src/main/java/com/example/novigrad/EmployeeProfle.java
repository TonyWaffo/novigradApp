package com.example.novigrad;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class EmployeeProfle extends AppCompatActivity {

    DatabaseReference dbUsers;    // Reference to the Firebase Database
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    User employee;
    EditText firstName;
    EditText lastName;
    EditText postalCode;
    EditText phoneNumber;
    EditText email;
    EditText password;
    String employeeId;

    Button updateProfile;
    Button updateEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_profle);
        dbUsers= FirebaseDatabase.getInstance().getReference("profiles");
        mAuth= FirebaseAuth.getInstance();
        currentUser=null;
        Intent intent=getIntent();
        employeeId= (String) intent.getStringExtra("employeeId");
        firstName= (EditText)findViewById(R.id.employeeFirstName);
        lastName= (EditText)findViewById(R.id.employeeLastName);
        phoneNumber= (EditText)findViewById(R.id.employeePhoneNumber);
        postalCode= (EditText)findViewById(R.id.employeePostalCode);
        //email= (EditText)findViewById(R.id.employeeEmail);


        //add the click event to update the profile
        updateProfile=(Button) findViewById(R.id.updateEmployeeProfile);

        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    /*boolean emptyField= firstName.getText().toString().trim().isEmpty() || lastName.getText().toString().trim().isEmpty() ||
                            phoneNumber.getText().toString().trim().isEmpty() || postalCode.getText().toString().trim().isEmpty();*/
                    //check if fields are empty
                    if(checkField(firstName,lastName,phoneNumber,postalCode)){
                        Toast.makeText(getApplicationContext(),"One or more empty fields",Toast.LENGTH_SHORT).show();
                        return;
                    } else{
                        //check the phone number format
                        if(!checkPhoneNumber(phoneNumber.getText().toString().trim())){
                            Toast.makeText(getApplicationContext(),"Wrong phone number format",Toast.LENGTH_SHORT).show();
                            return;
                        }

                        dbUsers.child(employeeId).child("first_name").setValue(firstName.getText().toString().trim());
                        dbUsers.child(employeeId).child("last_name").setValue(lastName.getText().toString().trim());
                        dbUsers.child(employeeId).child("phone_number").setValue(Long.parseLong(phoneNumber.getText().toString().trim()));
                        dbUsers.child(employeeId).child("postal_code").setValue(postalCode.getText().toString().trim());
                        //dbUsers.child(employeeId).setValue(employee);
                        Toast.makeText(getApplicationContext(),"Profile updated",Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Log.e("Firebase","update error",e);
                }
            }
        });


        //add the click event to update the email
        /*
        updateEmail=(Button) findViewById(R.id.updateEmployeeEmail);

        updateEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    if(email.getText().toString().trim().isEmpty()){
                        Toast.makeText(getApplicationContext(),"Empty field",Toast.LENGTH_SHORT).show();
                    }else{
                        //this functionality isn't working properly. I will work on that later
                        //authenticate the user before updating
                       // authenticationAndEmailUpdate();

                    }
                }catch (Exception e){
                    Log.e("Firebase","update email error",e);
                }
            }
        });
        */


    }


    @Override
    protected void onStart() {
        super.onStart();

        // Attach a value event listener to the database reference
        dbUsers.addValueEventListener(new ValueEventListener(){
            @Override
            public  void onDataChange(DataSnapshot dataSnapshot){

                try{
                    //retrieve user data in the database
                    for(DataSnapshot postSnapshot:dataSnapshot.getChildren()) {
                        employee = postSnapshot.getValue(User.class);
                        // Categorize users based on their role and add them to the respective lists
                        if(employee.getId().equals(employeeId)){
                            firstName.setText(employee.getFirst_name());
                            lastName.setText(employee.getLast_name());
                            phoneNumber.setText(String.valueOf(employee.getPhone_number()));
                            postalCode.setText(employee.getPostal_code());
                            email.setText(employee.getEmail_address());

                            //stop the loop at the current login user
                            break;
                        }
                        Log.w("user",employee.getFirst_name());
                    }

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

    public void authenticationAndEmailUpdate(){
        //we authenticate the current user before any update
        mAuth.signInWithEmailAndPassword(employee.getEmail_address(), employee.getPassword())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success and take user credentials
                            currentUser = mAuth.getCurrentUser();
                            //update the email address on the firebase authenticator
                            handleEmailUpdate();
                        } else {
                            // If authentication fails, display a message to the user.
                            Toast.makeText(getApplicationContext(),"authentication failed",Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    private void handleEmailUpdate(){
        if(currentUser==null){
            Toast.makeText(getApplicationContext(),"Null current user",Toast.LENGTH_SHORT).show();
            return;
        }
        currentUser.updateEmail(email.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        try {
                            if (task.isSuccessful()) {
                                //update email address in the profile database as well
                                employee.setEmail_address(email.getText().toString());
                                dbUsers.child(employee.getId()).setValue(employee);
                                Log.d("firebase", "User email address updated." + currentUser.getEmail());
                                //Toast.makeText(getApplicationContext(),"Email updated",Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            Log.e("firebase", "email address not updated.", e);
                            Toast.makeText(getApplicationContext(),"Email not updated",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public boolean checkField(EditText fName,EditText lName,EditText number,EditText pCode){
        return fName.getText().toString().trim().isEmpty() || lName.getText().toString().trim().isEmpty() ||
                number.getText().toString().trim().isEmpty() || pCode.getText().toString().trim().isEmpty();
    }

    public boolean checkPhoneNumber(String phone){
        try{
            Long contact= Long.parseLong(phone);
        }catch (NumberFormatException e){
            return  false;
        }
        return true;
    }

}