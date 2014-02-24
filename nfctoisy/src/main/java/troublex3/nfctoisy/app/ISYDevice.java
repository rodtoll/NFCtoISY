package troublex3.nfctoisy.app;

/**
 * Created by rodtoll on 2/17/14.
 */

import java.net.*;
import java.io.*;
import java.util.concurrent.ThreadFactory;
import android.os.Handler;
import android.os.Message;

import android.util.*;

public class ISYDevice {

    public ISYDevice(String _address, String _userName, String _password)
    {
        address = _address;
        userName = _userName;
        password = _password;
    }

    public ISYHttpRequest ExecuteRequest(String deviceAddress, boolean stateToSet, Handler resultHandler)
    {
        ISYHttpRequest request = new ISYHttpRequest(address, userName, password, deviceAddress, stateToSet, resultHandler);
        new Thread(request).start();
        return request;
    }

    protected String address;
    protected String userName;
    protected String password;
}
