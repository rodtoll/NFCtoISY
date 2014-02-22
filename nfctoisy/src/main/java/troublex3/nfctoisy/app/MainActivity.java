package troublex3.nfctoisy.app;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.nfc.*;
import android.os.Parcelable;
import android.os.Build;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URI;
import android.net.Uri;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import android.speech.RecognizerIntent;
import android.app.Activity;
import android.widget.Toast;

import java.net.HttpURLConnection;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        device = new ISYDevice("", "admin", "");
        database = new DeviceMappingDatabaseHandler(this);
    }

    protected URI getUriFromNdef(Intent intent)
    {
        NdefMessage msgs[];

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction()))
        {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            if (rawMsgs != null)
            {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++)
                {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }

                if(rawMsgs.length == 0)
                {
                    showToastMessage("No message content in NFC tag.");
                    return null;
                }

                NdefRecord[] records = msgs[0].getRecords();

                for( int index = 0; index < msgs.length; index++)
                {
                    if(records[index].getTnf() == NdefRecord.TNF_WELL_KNOWN)
                    {
                        byte [] payload = records[index].getPayload();
                        byte [] relevantPayload = new byte[payload.length-1];
                        System.arraycopy(payload, 1, relevantPayload, 0, payload.length-1);

                        String uriText = new String(relevantPayload, Charset.forName("US-ASCII"));

                        // Comment

                        try
                        {
                            URI uri = new URI(uriText);
                            return uri;
                        }
                        catch(URISyntaxException except)
                        {
                            showToastMessage(except.getMessage());
                            return null;
                        }
                    }
                }
            }
        }

        return null;
    }

    public void onNfcAction(Intent intent)
    {
        // If there is already an action in progress
        if(currentRequest != null)
        {
            return;
        }

        URI uri = getUriFromNdef(intent);

        if(uri != null)
        {
            DeviceMapEntry entry = database.getDeviceMapEntry(uri.getHost());

            if(entry == null)
            {
                showToastMessage("Unknown device:" + uri.getHost());
            }
            else
            {
                showToastMessage( "Executing command: [" + uri.getPath() +
                        "] for device: [" + uri.getHost() +
                        "] address: [" + entry.getAddress() + "]");

                if(uri.getPath().compareToIgnoreCase("/off") == 0)
                {
                    currentRequest = device.ExecuteRequest(entry.getAddress(), false);
                }
                else if(uri.getPath().compareToIgnoreCase("/on") == 0)
                {
                    currentRequest = device.ExecuteRequest(entry.getAddress(), true);
                }
            }
        }
        else
        {
            showToastMessage("Unable to get Uri from the Nfc action");
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        Intent intent = getIntent();

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction()))
        {
            onNfcAction(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
    }

    void showToastMessage(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings)
        {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

    protected ISYDevice device;
    protected ISYHttpRequest request;
    protected DeviceMappingDatabaseHandler database;
    protected ISYHttpRequest currentRequest;
}


