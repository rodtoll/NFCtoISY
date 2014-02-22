package troublex3.nfctoisy.app;

/**
 * Created by rodtoll on 2/20/14.
 */
public class DeviceMapEntry
{
    public DeviceMapEntry()
    {

    }

    protected int id;
    protected String alias;
    protected String address;

    public int getID()
    {
        return id;
    }

    public void setID(int _id)
    {
        id = _id;
    }

    public String getAlias()
    {
        return alias;
    }

    public void setAlias(String _alias)
    {
        alias = _alias;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String _address)
    {
        address = _address;
    }
}
