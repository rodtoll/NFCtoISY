package troublex3.nfctoisy.app;

import java.net.*;
import java.io.*;
import android.util.*;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 * Created by rodtoll on 2/17/14.
 */
public class ISYHttpRequest implements Runnable {

    public ISYHttpRequest(
            String _isyAddress,
            String _userName,
            String _password,
            String _deviceAddress,
            boolean _stateToSet
    )
    {
        deviceAddress = _deviceAddress.replaceAll(" ", "%20");
        stateToSet = _stateToSet;
        isyAddress = _isyAddress;
        completed = false;
        userName = _userName;
        password = _password;
        resultCode = -2;
    }

    protected int GetResultCode(XmlPullParser parser) throws XmlPullParserException, IOException
    {
        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() == XmlPullParser.START_TAG) {
                String name = parser.getName();
                // Starts by looking for the entry tag
                if (name.equals("status"))
                {
                    String resultCodeText = parser.nextText();
                    if(resultCodeText == null)
                    {
                        return 500;
                    }
                    else
                    {
                        return Integer.parseInt(resultCodeText);
                    }
                }
            }
        }
        return -1;
    }

    @Override
    public void run()
    {
        String message;
        boolean completed = false;
        String url = "http://" + isyAddress + "/rest/nodes/" + deviceAddress + "/cmd/" + ((stateToSet) ? "DON" : "DOF");

        try
        {
            String userPassword = userName + ":" + password;
            String basicAuth = "Basic " + new String(Base64.encode(userPassword.getBytes(),0));

            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestProperty("Authorization", basicAuth);

            InputStream in = connection.getInputStream();
            BufferedReader rd  = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            XmlPullParser resultParser = Xml.newPullParser();
            resultParser.setInput(rd);
            resultParser.nextTag();

            synchronized (this)
            {
                resultCode = GetResultCode(resultParser);
            }
        }
        catch(MalformedURLException malformedException)
        {
            message = malformedException.toString();
        }
        catch(IOException ioException)
        {
            message = ioException.toString();
        }
        catch(XmlPullParserException xmlException)
        {
            message = xmlException.toString();
        }
        finally
        {
            connection.disconnect();
        }

        synchronized (this)
        {
            completed = true;
        }
    }

    public synchronized boolean getCompleted()
    {
        return completed;
    }

    public synchronized Integer getResult()
    {
        if(!completed)
        {
            throw new IllegalArgumentException("Cannot get result, system not finished executing");
        }

        return resultCode;
    }

    protected HttpURLConnection connection;
    protected String deviceAddress;
    protected String isyAddress;
    protected String userName;
    protected String password;
    protected boolean stateToSet;
    protected boolean completed;
    protected Integer resultCode;
}
