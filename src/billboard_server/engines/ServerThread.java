package billboard_server.engines;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.TreeMap;

import billboard_server.types.*;

import static billboard_server.engines.Server.*;
import static billboard_server.engines.ServerFunctions.*;

public class ServerThread implements Runnable {
    ObjectOutputStream outStream = null;
    ObjectInputStream inStream = null;
    Socket socket = null;

    public ServerThread(Socket socket) {
        this.socket = socket;
    }

    private ServerResponse parseRequest(ClientRequest request) {

        ServerResponse response = null;
        String userId = null;

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
                    response = getSchedules(request.data);
                    break;

                case ADD_USERS:
                    response = addUsers(request.sessionId, request.data);
                    break;

                case DELETE_USERS:
                    response = deleteUsers(request.sessionId, request.data);
                    break;

                case GET_USERS:
                    response = getUsers(request.data);
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
            response = new ServerResponse();
            e.printStackTrace();
            response.status = e.getMessage();
            response.success = false;
        }

        return response;
    }


    @Override
    public void run() {

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

        request.print();

        // parse msg
        ServerResponse response = parseRequest(request);

        // send response
        try {
            outStream.writeObject(response);
        } catch (IOException ignored) {
            return;
        }
    }
}
