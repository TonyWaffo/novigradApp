package com.example.novigrad;


import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class AdminHome extends AppCompatActivity {

    ImageView serviceIcon;
    ImageView accountsIcon;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        serviceIcon=(ImageView)findViewById(R.id.EmployeeServiceIcon);
        accountsIcon=(ImageView)findViewById(R.id.accountsImages);

        Intent intent=getIntent();
        User userProfile= (User) intent.getSerializableExtra("userProfile");

        TextView welcomeTxt=(TextView) findViewById(R.id.employee_welcome);

        if(userProfile!=null){
            //Convert the name with upperCase
            String adminFirstName= userProfile.getFirst_name();
            adminFirstName= Character.toUpperCase(adminFirstName.charAt(0))+adminFirstName.substring(1);


            welcomeTxt.setText("Welcome "+adminFirstName+". Happy to see you back as our favorite "+userProfile.getRole()+" !!");
        }else{
            welcomeTxt.setText("No user Found!");
        }

        serviceIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent servicePage=new Intent(getApplicationContext(),AdminServices.class);
                startActivity(servicePage);
            }
        });
        accountsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent allUsersPage=new Intent(getApplicationContext(),AdminAccountManager.class);
                startActivity(allUsersPage);
            }
        });
    }

}