package connections.engines;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import connections.Protocol;
import connections.exceptions.ServerException;
import connections.types.*;
import connections.Protocol.*;
import connections.tools.UserAuth;

import static connections.engines.Server.*;

public class ServerThread implements Runnable {
    ObjectOutputStream outStream = null;
    ObjectInputStream inStream = null;
    Socket socket = null;

    public ServerThread(Socket socket) {

        this.socket = socket;

        long sessionId = new Random().nextLong();

        UserInfo newUser = new UserInfo();
        newUser.userId = "b220a053-91f1-48ee-acea-d1a145376e57";
        newUser.createdAt = Long.MAX_VALUE - ONE_DAY_MS;

        sessionIds.put(sessionId, newUser);
    }

    private long newSessionId(String userId, String hash) throws ServerException {
        String dbSalt = null;
        String dbHash = null;

        if (userId == null || hash == null) {
            throw new ServerException("userId and/or hash not provided");
        }

        try {
            dbHash = database.getHash(userId);
            dbSalt = database.getSalt(userId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerException(e.getMessage());
        }

        long sessionId;

        if (UserAuth.hashAndSalt(hash, dbSalt).equals(dbHash)) {

            for (Map.Entry<Long, UserInfo> userInfo : sessionIds.entrySet()) {
                if (userInfo.getValue().userId.equals(userId)) {
                    return userInfo.getKey();
                }
            }

            sessionId = new Random().nextLong();

            UserInfo newUser = new UserInfo();
            newUser.userId = userId;
            newUser.createdAt = System.currentTimeMillis();

            sessionIds.put(sessionId, newUser);
        } else {
            System.out.println(hash + ' ' + dbSalt + '\n' + UserAuth.hashAndSalt(hash, dbSalt) + '\n' + dbHash);
            throw new ServerException("couldn't get session id - invalid username or password");
        }

        return sessionId;
    }

    private String sessionToUserId(long sessionId) throws ServerException {

        if (!sessionIds.containsKey(sessionId)) {
            throw new ServerException("user doesn't have session ID");
        }

        UserInfo info = sessionIds.get(sessionId);

        if (info.createdAt + ONE_DAY_MS < System.currentTimeMillis()) {
            throw new ServerException("user session ID has expired");
        }

        return sessionIds.get(sessionId).userId;
    }

    private ServerResponse parseRequest(ClientRequest request) {

        ServerResponse response = new ServerResponse();
        response.status = "OK";

        String userId = null;

        if (request.cmd == Cmd.GET_SESSION_ID) {
            try {
                userId = (String) request.data.get(Protocol.USERID);
                String hash = (String) request.data.get(Protocol.HASH);

                long sessionId = newSessionId(userId, hash);
                response.data = new TreeMap<>();
                response.data.put("sessionId", sessionId);
            } catch (ServerException e) {
                e.printStackTrace();
                response.status = e.getMessage();
            }

            return response;
        }

        try {
            switch (request.cmd) {
                case ADD_BILLBOARDS:
                    userId = sessionToUserId(request.sessionId);
                    database.addBillboards(userId, request.data);
                    break;

                case DELETE_BILLBOARDS:
                    userId = sessionToUserId(request.sessionId);
                    database.deleteBillboards(userId, request.data);
                    break;

                case GET_CURRENT_BILLBOARD:
                case GET_BILLBOARDS:
                    response.data = database.getBillboards();
                    break;

                case ADD_SCHEDULES:
                    userId = sessionToUserId(request.sessionId);
                    database.addSchedule(userId, request.data);
                    break;

                case DELETE_SCHEDULES:
                    userId = sessionToUserId(request.sessionId);
                    database.deleteSchedules(userId, request.data);
                    break;

                case GET_SCHEDULES:
                    response.data = database.getSchedules();
                    break;

                case ADD_USERS:
                    userId = sessionToUserId(request.sessionId);
                    database.addUsers(userId, request.data);
                    break;

                case DELETE_USERS:
                    userId = sessionToUserId(request.sessionId);
                    database.deleteUsers(userId, request.data);
                    break;

                case GET_USERS:
                    response.data = database.getUsers();
                    break;

                case NAME_TO_ID:
                    String getUserId = database.userNameToId((String) request.data.get(Protocol.USERNAME));
                    response.data = new TreeMap<>();
                    response.data.put(Protocol.USERID, getUserId);
                    break;
            }
        } catch (Exception e) {
            response.status = e.getMessage();
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
