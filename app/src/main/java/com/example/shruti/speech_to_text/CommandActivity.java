package com.example.shruti.speech_to_text;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

/**
 * Created by Shruti on 07/03/18.
 */

public class CommandActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.command);
        EditText edt= (EditText) findViewById(R.id.editText);
        populatelistView();
    }

    private void populatelistView(){
        // create list of modes
        String [] modes={"User","Auto Pilot","Angle(Others)"};

        //Build Adapter (convert these items into appropiate views to work with)
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, // context for activity
                R.layout.modes, // layout to use (create)
                modes); // items to be displayed

        //configure list view...
        ListView list = (ListView)findViewById(R.id.listItem);
        list.setAdapter(adapter);
    }
}
