package in.org.verkstad.sms;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView lViewSMS;
    //AIzaSyD1INNl-lUJh_hDNXFQRnQY85E625D5BMA
    //AIzaSyBK0_07SKRmJh_4b3JE6p9I7tHK7tE1Ih0
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openInbox(View view){
        Intent intent = new Intent(MainActivity.this,Inbox.class);
        startActivity(intent);
    }

    public void sendSMS(View view){
        Intent intent = new Intent(MainActivity.this,SendSms.class);
        startActivity(intent);
        finish();
    }

}
