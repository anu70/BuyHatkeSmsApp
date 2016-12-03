package in.org.verkstad.sms;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by anu on 12/3/2016.
 */
public class IncomingSms extends BroadcastReceiver {
    final SmsManager sms = SmsManager.getDefault();
    @Override
    public void onReceive(Context context, Intent intent) {
        final Bundle bundle = intent.getExtras();

        try {

            if (bundle != null) {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                    String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody();

                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
                    mBuilder.setSmallIcon(R.mipmap.ic_launcher);
                    mBuilder.setContentTitle(senderNum);
                    mBuilder.setContentText(message);

                    NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                    mNotificationManager.notify(1, mBuilder.build());

                }
            }

        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" + e);

        }
    }
}
