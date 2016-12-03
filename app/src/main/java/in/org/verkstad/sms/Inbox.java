package in.org.verkstad.sms;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;

public class Inbox extends AppCompatActivity /**implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener**/ {
    ListView lViewSMS;
    private GoogleApiClient mGoogleApiClient;
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

   /** @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }

        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Toast.makeText(getApplicationContext(),"Connected",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(getApplicationContext(),"Connection Suspended",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(),"Connection Failed: "+connectionResult,Toast.LENGTH_SHORT).show();
    }**/
}
