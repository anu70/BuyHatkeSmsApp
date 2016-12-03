package in.org.verkstad.sms;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class Message extends AppCompatActivity {
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        listView = (ListView) findViewById(R.id.listView);
        Intent intent = getIntent();
        ArrayList readMsg = intent.getStringArrayListExtra("readMsg");

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, readMsg);
        listView.setAdapter(adapter);
    }

}
