package billboard_server.types;

import billboard_server.Protocol;

import java.io.Serializable;
import java.util.TreeMap;

/**
 * Encapsulation class to send data from the client to the server
 *
 * @author Max Ferguson
 */
public class ClientRequest implements Serializable {
    public Protocol.Cmd cmd = Protocol.Cmd.NONE;
    public long sessionId = 0;
    public TreeMap<String, Object> data = null;

    public ClientRequest() {
        this.data = new TreeMap<>();
    }

    // debugging
    public String toString() {
        return "\ncmd: " + this.cmd +
                "\nsessionId: " + this.sessionId +
                "\ndata: " + this.data +
                "\n---------------------------";
    }

    public void print() {
        System.out.println(this.toString());
    }
}
