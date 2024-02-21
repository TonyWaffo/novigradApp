package com.example.novigrad;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class EmployeeHome extends AppCompatActivity {

    ImageView profileIcon;
    ImageView serviceIcon;
    ImageView requestIcon;
    ImageView calendarIcon;

    String employeeFirstName;
    String employeeId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_home);

        profileIcon=(ImageView)findViewById(R.id.employeeProfileIcon);
        serviceIcon=(ImageView)findViewById(R.id.EmployeeServiceIcon);
        requestIcon=(ImageView)findViewById(R.id.employeeRequestIcon);
        calendarIcon=(ImageView)findViewById(R.id.calendarIcon);

        Intent intent=getIntent();
        User userProfile= (User) intent.getSerializableExtra("userProfile");

        TextView welcomeTxt=(TextView) findViewById(R.id.employee_welcome);

        if(userProfile!=null){
            //Convert the name with upperCase
            employeeId= userProfile.getId();
            employeeFirstName= userProfile.getFirst_name();
            employeeFirstName= Character.toUpperCase(employeeFirstName.charAt(0))+employeeFirstName.substring(1);


            welcomeTxt.setText("Welcome "+employeeFirstName+". Happy to see you back as our favorite "+userProfile.getRole()+" !!");
        }else{
            welcomeTxt.setText("No user Found!");
        }



        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profilePage=new Intent(getApplicationContext(),EmployeeProfle.class);
                profilePage.putExtra("employeeId",employeeId);
                startActivity(profilePage);
            }
        });
        serviceIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent servicePage=new Intent(getApplicationContext(), EmployeeServices.class);
                servicePage.putExtra("employeeId",employeeId);
                startActivity(servicePage);
            }
        });
        requestIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent requestPage=new Intent(getApplicationContext(),EmployeeRequests.class);
                requestPage.putExtra("employeeId",employeeId);
                startActivity(requestPage);
            }
        });
        calendarIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent schedulePage=new Intent(getApplicationContext(), EmployeeSchedule.class);
                schedulePage.putExtra("employeeId",employeeId);
                startActivity(schedulePage);
            }
        });
    }
}