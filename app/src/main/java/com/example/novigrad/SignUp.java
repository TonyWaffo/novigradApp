// SEG 2505 - groupe 21
// Projet Service Novigrade


package com.example.novigrad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import android.util.Patterns;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {

    FirebaseAuth mAuth;
    DatabaseReference databaseUsers;

    // Intent to be used for returning data to the calling activity
    Intent returnIntent;


    // UI elements
    TextView error_message;
    EditText firstName;
    EditText lastName;
    EditText phone;
    EditText address;
    EditText email;
    EditText password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);  // Set the content view to the layout defined in activity_sign_up.xml
        mAuth = FirebaseAuth.getInstance();
    }


    //use the email pattern provided by android to check the input's validity
    public boolean checkEmail(String emailAddress){
        //String emailAddress=(String) email.getText().toString().trim();
        if(Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()){
            return true;
        }else{
            return false;
        }
    }


    public void signUp(View view){
        //method called when the user try to sign up

        int signUpAuthorization=0;  // 0 means we can return to the login page, 1 means we can not

        //UI elements
        error_message=(TextView) findViewById(R.id.errorMessage);
        firstName= (EditText)findViewById(R.id.employeeFirstName);
        lastName= (EditText)findViewById(R.id.employeeLastName);
        phone= (EditText)findViewById(R.id.employeePhoneNumber);
        address= (EditText)findViewById(R.id.employeePostalCode);
        email= (EditText)findViewById(R.id.employeeEmail);
        password= (EditText)findViewById(R.id.employeePassword);
        
        String role = null;
        // Check if any of the required field are empty
        boolean emptyField= firstName.getText().toString().trim().isEmpty() || lastName.getText().toString().trim().isEmpty() ||
                phone.getText().toString().trim().isEmpty() || address.getText().toString().trim().isEmpty() ||
                email.getText().toString().trim().isEmpty() || password.getText().toString().trim().isEmpty();

        //notify is there is at least one empty field
        if(emptyField) {
            error_message.setText("One or more field are empty");
            Toast.makeText(getApplicationContext(),"One or more field are empty", Toast.LENGTH_LONG).show();
            signUpAuthorization=1;
        }

        /*
        * Notify if the user checked a role
        * */
        RadioGroup roleChoices= (RadioGroup) findViewById(R.id.roleRadio);

        int selectedRoleId= roleChoices.getCheckedRadioButtonId();  //id of the selected role
        if(selectedRoleId !=-1){
            RadioButton selectedRole= (RadioButton) findViewById(selectedRoleId);
            role= (String) selectedRole.getText();
        }else{
            error_message.setText("Invalid role");
            Toast.makeText(getApplicationContext(),"Invalid role", Toast.LENGTH_LONG).show();
            signUpAuthorization=1;
        }


        // Verify the email validity using the method create above
        if(!checkEmail(email.getText().toString().trim())){
            error_message.setText("Invalid email");
            //create a mini popup alert
            Toast.makeText(getApplicationContext(),"Invalid email", Toast.LENGTH_LONG).show();
            signUpAuthorization=1;
        }


        if(signUpAuthorization==0){

            String finalRole = role;

            //user firebase authenticator to create user with email and password
            mAuth.createUserWithEmailAndPassword(email.getText().toString().trim(), password.getText().toString().trim())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //registration successfully done and store user data
                                FirebaseUser firebaseUser  = mAuth.getCurrentUser();

                                //create my user class
                                User newUser=new User(firebaseUser.getUid(),firstName.getText().toString().trim(),lastName.getText().toString().trim(),
                                        email.getText().toString().trim(),address.getText().toString().trim(),password.getText().toString().trim(), finalRole,
                                        Long.parseLong(phone.getText().toString()));

                                try{
                                    //add the user to the database
                                    databaseUsers= FirebaseDatabase.getInstance().getReference("profiles");
                                    databaseUsers.child(newUser.getId()).setValue(newUser);
                                    Log.e("signup successful","big issue with registration");
                                }catch (Exception e){
                                    Log.e("signup error","can't sign up",e);
                                }


                                //create returning activity
                                returnIntent=new Intent();

                                //add a message to the return element
                                returnIntent.putExtra("signUpMsg","You account was created successfully");
                                //returnIntent.putExtra("newUser", newUser);           //we have to implements the serializable interface to pass User class via the intent
                                setResult(RESULT_OK,returnIntent);



                                //finish activity and return to the login page
                                finish();

                            } else {
                               //user registration failed
                                Log.e("Firebase", "Registration failed: " + task.getException());
                            }
                        }
                    });

        }


    }

    public void backTologin(View view){
        //go back to the login page
        Intent toLoginPage=new Intent(getApplicationContext(), MainActivity.class);
        startActivity(toLoginPage);
    }
}