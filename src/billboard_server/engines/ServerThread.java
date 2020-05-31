package billboard_server.engines;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.TreeMap;

import billboard_server.Scheduler;
import billboard_server.types.*;
import com.sun.source.tree.Tree;

import static billboard_server.engines.Server.*;
import static billboard_server.engines.ServerFunctions.*;

/**
 * Server side: Thread to handle a new client connection
 * @author Max Ferguson
 */
public class ServerThread implements Runnable {
    ObjectOutputStream outStream = null;
    ObjectInputStream inStream = null;
    Socket socket = null;

    /**
     * Initialiser function to set the socket of the client to handle
     * @param socket The socket of the newly accepted client
     */
    public ServerThread(Socket socket) {
        this.socket = socket;
    }

    /**
     * Parses a client's request, and returns a relevant response based on the request
     * @param request The ClientRequest object sent by the client
     * @return ServerResponse The server's response to the request after parsing
     */
    private ServerResponse parseRequest(ClientRequest request) {

        ServerResponse response = null;
        String userId = null;

        // call upon a ServerFunction based on ClientRequest.cmd
        try {
            switch (request.cmd) {
                case GET_SESSION_ID:
                    response = getSessionId(request.data);
                    break;

                case ADD_BILLBOARDS:
                    response = addBillboards(request.sessionId, request.data);
                    break;

                case DELETE_BILLBOARDS:
                    response = deleteBillboards(request.sessionId, request.data);
                    break;

                case GET_CURRENT_BILLBOARD:
                    response = new ServerResponse();
                    System.out.println(scheduler.getCurrentBillboardData());
                    response.data.put("currentBillboard", scheduler.getCurrentBillboardData());
                    break;

                case GET_BILLBOARDS:
                    response = getBillboards(request.data);
                    break;

                case ADD_SCHEDULES:
                    response = addSchedules(request.sessionId, request.data);
                    break;

                case DELETE_SCHEDULES:
                    response = deleteSchedules(request.sessionId, request.data);
                    break;

                case GET_SCHEDULES:
                    response = getSchedules(request.sessionId, request.data);
                    break;

                case ADD_USERS:
                    response = addUsers(request.sessionId, request.data);
                    break;

                case DELETE_USERS:
                    response = deleteUsers(request.sessionId, request.data);
                    break;

                case GET_USERS:
                    response = getUsers(request.sessionId, request.data);
                    break;

                case NAME_TO_ID:
                    response = new ServerResponse();
                    response.data = new TreeMap<>();
                    response.data.put("userId", database.userNameToId((String) request.data.get("userName")));
                    break;

                case BOARD_TO_ID:
                    response = new ServerResponse();
                    response.data = new TreeMap<>();
                    response.data.put("billboardId", database.billboardNameToId((String) request.data.get("billboardName")));
                    break;

                case BOARD_TO_SCHEDULE:
                    response = new ServerResponse();
                    response.data = new TreeMap<>();
                    response.data.put("scheduleId", database.billboardToScheduleId((String) request.data.get("billboardId")));
                    break;
            }

        } catch (Exception e) {
            // if a function above threw an exception, catch it and send the message to the client
            response = new ServerResponse();
            e.printStackTrace();
            response.status = e.getMessage();
            response.success = false;
        }

        return response;
    }


    /**
     * Receives a request from the client, parses it and sends a response back
     */
    @Override
    public void run() {

        // create iostreams
        try {
            outStream = new ObjectOutputStream(socket.getOutputStream());
            inStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException ignored) {
            return;
        }

        // read from client
        ClientRequest request = null;
        try {
            request = (ClientRequest) inStream.readObject();
        } catch (IOException ignored) {
            return;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        // print request to terminal
        request.print();

        // parse msg
        ServerResponse response = parseRequest(request);

        // send response back to client
        try {
            outStream.writeObject(response);
        } catch (IOException ignored) {
            return;
        }
    }
}
