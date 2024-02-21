package com.example.novigrad;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomerHome extends AppCompatActivity {

    ImageView profileIcon;
    ImageView serviceIcon;
    ImageView requestIcon;
    ImageView evaluationIcon;

    String customerFirstName;
    String customerId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home);

        profileIcon=(ImageView)findViewById(R.id.customerProfileIcon);
        serviceIcon=(ImageView)findViewById(R.id.customerServiceIcon);
        requestIcon=(ImageView)findViewById(R.id.customerRequestIcon);
        evaluationIcon=(ImageView)findViewById(R.id.rateIcon);

        Intent intent=getIntent();
        User userProfile= (User) intent.getSerializableExtra("userProfile");

        TextView welcomeTxt=(TextView) findViewById(R.id.customer_welcome);

        if(userProfile!=null){
            //Convert the name with upperCase
            customerId= userProfile.getId();
            customerFirstName= userProfile.getFirst_name();
            customerFirstName= Character.toUpperCase(customerFirstName.charAt(0))+customerFirstName.substring(1);


            welcomeTxt.setText("Welcome "+customerFirstName+". Happy to see you back as our favorite "+userProfile.getRole()+" !!");
        }else{
            welcomeTxt.setText("No user Found!");
        }



        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profilePage=new Intent(getApplicationContext(),EmployeeProfle.class);
                profilePage.putExtra("employeeId",customerId);
                startActivity(profilePage);
            }
        });
        serviceIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent servicePage=new Intent(getApplicationContext(), CustomerServices.class);
                servicePage.putExtra("customerId",customerId);
                startActivity(servicePage);
            }
        });
        requestIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //we gotta fix the EmployeeRequestAdapter so that it removes "reject" and "approve" button for customer
                Intent requestPage=new Intent(getApplicationContext(), CustomerRequests.class);
                requestPage.putExtra("customerId",customerId);
                startActivity(requestPage);
            }
        });
        evaluationIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent evaluationPage=new Intent(getApplicationContext(), CustomerEvaluation.class);
                evaluationPage.putExtra("customerId",customerId);
                startActivity(evaluationPage);
            }
        });
    }
}