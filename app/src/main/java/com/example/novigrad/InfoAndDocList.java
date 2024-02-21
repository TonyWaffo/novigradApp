package com.example.novigrad;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;


/*
 *In summary, this InfoAndDocList class is an adapter used to
 * convert a list of String objects into a set of views for display
 * in a ListView or a similar Android AdapterView.
 * */
public class InfoAndDocList extends ArrayAdapter<String> {
    private Activity context;
    List<String> info_docs;

    public InfoAndDocList(Activity context, List<String> info_docs) {
        super(context, R.layout.layout_info_and_docs, info_docs);
        this.context = context;
        this.info_docs = info_docs;
    }

    public InfoAndDocList(@NonNull Context context, int resource) {
        super(context, resource);
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.layout_info_and_docs, null, true);

        TextView textViewName = (TextView) listViewItem.findViewById(R.id.textViewName);

        String info_doc = info_docs.get(position);
        textViewName.setText(info_doc);


        /*Button deleteDetailButton= (Button) listViewItem.findViewById(R.id.deleteDetailButton);
        deleteDetailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                info_docs.remove(position);
                notifyDataSetChanged();// notify adapter when data set has changed so it can remove the current position before returning the view
            }
        });
                 */
        return listViewItem;
    }
}
