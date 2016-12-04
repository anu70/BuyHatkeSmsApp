package in.org.verkstad.sms;

import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi.DriveContentsResult;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.plus.Plus;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;

public class Inbox extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    ListView lViewSMS;
    private GoogleApiClient mGoogleApiClient;
    private boolean fileOperation = false;
    SearchView.OnQueryTextListener listener;
    SearchView search;
    LinkedHashSet<String> name;
    ArrayList<ArrayList<String>> sms;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        search = (SearchView) findViewById(R.id.searchView);
        lViewSMS = (ListView) findViewById(R.id.listViewSMS);
        fetchInbox();
        showInListView(new ArrayList<String>(name));
        listener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String newText) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.equals("")){
                    //Toast.makeText(getApplicationContext(),"Empty",Toast.LENGTH_SHORT).show();
                    showInListView(new ArrayList<String>(name));
                }
                else if(sms.size()>0){
                    newText = newText.toLowerCase();
                    ArrayList<String> filteredList = new ArrayList<>();
                    Iterator<String> iterator = name.iterator();
                    for(int i=0;i<sms.size();i++){
                        String sender = iterator.next();
                        for(int j=0;j<sms.get(i).size();j++){
                            String text = sms.get(i).get(j).toLowerCase();
                            if(text.contains(newText))
                                filteredList.add(sender +"\n"+text);
                        }
                    }

                    showInListView(filteredList);
                }

                return true;
            }
        };
        search.setOnQueryTextListener(listener);
    }

    public void showInListView(ArrayList<String> list){
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        lViewSMS.setAdapter(adapter);
    }

    public void fetchInbox()
    {
        name = new LinkedHashSet<String>();
        sms = new ArrayList<ArrayList<String>>();

        Uri uriSms = Uri.parse("content://sms/inbox");
        Cursor cursor = getContentResolver().query(uriSms, new String[]{"_id", "address", "date", "body"},null,null,null);

        cursor.moveToFirst();
        while  (cursor.moveToNext())
        {
            String address = cursor.getString(1);
            final String body = cursor.getString(3);

            name.add(address);
            Iterator<String> iterator = name.iterator();
            int p = 0;
            while(iterator.hasNext()){
                if(iterator.next().equals(address))
                    break;
                p++;
            }
            if(p<sms.size())
                sms.get(p).add(body);
            else {
                sms.add(new ArrayList<String>());
                sms.get(p).add(body);
            }
            lViewSMS.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(Inbox.this, Message.class);
                    intent.putStringArrayListExtra("readMsg", sms.get(position));
                    startActivity(intent);
                }
            });
        }
    }

    public void takeBackUp(View view){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addScope(Plus.SCOPE_PLUS_PROFILE)
                .addApi(Drive.API).addScope(Drive.SCOPE_FILE)
                .addScope(Drive.SCOPE_APPFOLDER)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
        fileOperation = true;

        // create new contents resource
        Drive.DriveApi.newDriveContents(mGoogleApiClient)
                .setResultCallback(driveContentsCallback);
    }

    final ResultCallback<DriveContentsResult> driveContentsCallback =
            new ResultCallback<DriveContentsResult>() {
        @Override
        public void onResult(DriveContentsResult result) {

            if (result.getStatus().isSuccess()) {

                if (fileOperation == true) {

                    CreateFileOnGoogleDrive(result);

                }
            }

        }
    };

    public void CreateFileOnGoogleDrive(DriveContentsResult result){

        final DriveContents driveContents = result.getDriveContents();

        // Perform I/O off the UI thread.
        new Thread() {
            @Override
            public void run() {
                // write content to DriveContents
                OutputStream outputStream = driveContents.getOutputStream();
                Writer writer = new OutputStreamWriter(outputStream);

                try {
                    writer.write("Message backup here:"+"\n"+"\n");
                   Iterator<String> iterator = name.iterator();
                    int p =0;
                    while (iterator.hasNext()){
                        writer.write("SENDER: "+iterator.next() +"\n");
                        if(p<sms.size()){
                            for(int i=0;i<sms.get(p).size();i++){
                                writer.write(sms.get(p).get(i)+"\n"+"\n");
                            }
                        }

                        writer.write("\n"+"\n"+"\n");
                        p++;
                    }
                    writer.close();
                } catch (IOException e) {
                    Log.e("ExceptionInWrittingMsgs",e.getMessage());
                }

                MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                        .setTitle("SMSAPP")
                .setMimeType("text/plain")
                .setStarred(true).build();

                // create a file in root folder
                Drive.DriveApi.getRootFolder(mGoogleApiClient)
                        .createFile(mGoogleApiClient, changeSet, driveContents)
                        .setResultCallback(fileCallback);
            }
        }.start();
    }

    final private ResultCallback<DriveFolder.DriveFileResult> fileCallback = new
    ResultCallback<DriveFolder.DriveFileResult>() {
        @Override
        public void onResult(DriveFolder.DriveFileResult result) {
            if (result.getStatus().isSuccess()) {

                Toast.makeText(getApplicationContext(), "created file "+
                        result.getDriveFile().getDriveId(), Toast.LENGTH_LONG).show();

            }

            return;

        }
    };



    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!result.hasResolution()) {

            // show the localized error dialog.
            GoogleApiAvailability.getInstance().getErrorDialog(this, result.getErrorCode(), 0).show();
            return;
        }

        try {

            result.startResolutionForResult(this, 5);

        } catch (IntentSender.SendIntentException e) {

            Log.e("Exception", e.toString());
        }
    }
}
