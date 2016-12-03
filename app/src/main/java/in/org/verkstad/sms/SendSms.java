package in.org.verkstad.sms;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.EditText;

public class SendSms extends AppCompatActivity {
    EditText mobile,sms;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_sms);
        mobile = (EditText) findViewById(R.id.editText);
        sms = (EditText) findViewById(R.id.editText2);
    }

    public void SENDSMS(View view){
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(mobile.getText().toString(),null,sms.getText().toString(),null,null);

        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setType("vnd.android-dir/mms-sms");
        mobile.setText("");
        sms.setText("");
    }
}
