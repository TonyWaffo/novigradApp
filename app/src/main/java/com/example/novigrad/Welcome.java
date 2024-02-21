// SEG 2505 - groupe 21
// Projet Service Novigrade

package com.example.novigrad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class Welcome extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome); // Set the content view to the layout defined in activity_welcome.xml

        Intent intent=getIntent();
        User userProfile= (User) intent.getSerializableExtra("userProfile");

        TextView welcomeTxt=(TextView) findViewById(R.id.welcomeText);
        welcomeTxt.setText("Welcome "+userProfile.getFirst_name()+" as a new "+userProfile.getRole());

    }

}