package billboard_server;

import java.io.Serializable;

public class TCPClass implements Serializable{
    public int num = 1;
    public String msg = "ahhhh";
    public boolean muteReceiver = false;

    public TCPClass() {}

    public String toString()
    {
        return String.format("num = %d, msg = %s", this.num, this.msg);
    }
}
