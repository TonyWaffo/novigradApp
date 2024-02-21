package com.example.novigrad;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RatingBar;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class BranchRatingAdapter extends ArrayAdapter<User> {

    private Activity context;
    private List<User> users;
    private DatabaseReference dbUsers;
    private String customerId;

    public BranchRatingAdapter(Activity context, List<User> users, String customerId) {
        super(context, R.layout.layout_info_and_docs,users);
        this.context = context;
        this.users = users;
        this.customerId = customerId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewBranchRating = inflater.inflate(R.layout.layout_branch_rating_adapter, null, true);
        dbUsers= FirebaseDatabase.getInstance().getReference("profiles");

        User user = users.get(position);

        TextView textViewName = (TextView) listViewBranchRating.findViewById(R.id.branchName);
        textViewName.setText(user.getFirst_name() + "' branch");

        TextView textViewRating = (TextView) listViewBranchRating.findViewById(R.id.note);
        textViewRating.setText(user.getRate() + "/5");

        RatingBar ratingBar = (RatingBar) listViewBranchRating.findViewById(R.id.ratingBar);
        ratingBar.setRating((float)user.getRate());

        Button submitButton=(Button)listViewBranchRating.findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float rating = ratingBar.getRating();
                dbUsers.child(user.getId()).child("rating").child(customerId).setValue(rating);
                Toast.makeText(context.getApplicationContext(),"review submitted",Toast.LENGTH_SHORT).show();
            }
        });
        return listViewBranchRating;
    }
}
