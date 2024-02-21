package com.example.novigrad;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;


/*
 *In summary, this InfoAndDocList class is an adapter used to
 * convert a list of String objects into a set of views for display
 * in a ListView or a similar Android AdapterView.
 * */
public class AllUserAdapter extends ArrayAdapter<User> {


    private Activity context;
    List<User> users;
    String customerId;
    boolean customerOrAdmin;  //true for admin and false for customer
    DatabaseReference dbUsers;



    public AllUserAdapter(Activity context,List<User> users,String customerId,boolean response) {
        super(context, R.layout.layout_info_and_docs,users);
        this.context = context;
        this.users = users;
        this.customerId=customerId;
        this.customerOrAdmin=response;
    }

    public AllUserAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewUser = inflater.inflate(R.layout.layout_all_users_adapter, null, true);

        User user = users.get(position);

        TextView textViewName = (TextView) listViewUser.findViewById(R.id.userNameField);
        textViewName.setText(user.getFirst_name());



        dbUsers= FirebaseDatabase.getInstance().getReference("profiles");
        //delete button and redirect button
        Button deleteOrRedirectButton=(Button)listViewUser.findViewById(R.id.deleteOrRedirectUser);

        if(customerOrAdmin==true){
            //change the text in the button
            deleteOrRedirectButton.setText("Remove");
            //remove the user from the database
            deleteOrRedirectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dbUsers.child(user.getId()).removeValue();
                    Toast.makeText(context.getApplicationContext(),"You just deleted the user "+user.getFirst_name(),Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            //change the text in the button
            deleteOrRedirectButton.setText("Lookup");
            deleteOrRedirectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent browseServices=new Intent(context.getApplicationContext(), CustomerServiceBrowser.class);
                    browseServices.putExtra("branch",user); //send the branch's information to next page
                    browseServices.putExtra("customerId",customerId); //send the current customerId to the next page
                    context.startActivity(browseServices);
                }
            });
        }


        return listViewUser;
    }





}


