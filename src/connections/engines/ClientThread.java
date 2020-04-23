package connections.engines;


import connections.Protocol;
import connections.tools.Tools;
import connections.tools.UserAuth;
import connections.types.ClientRequest;
import connections.types.ServerResponse;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static connections.engines.Server.*;
import static connections.engines.Server.database;

public class ClientThread implements Runnable {
    ObjectOutputStream outStream = null;
    ObjectInputStream inStream = null;
    Socket socket = null;

    public ClientThread(Socket socket) {
        this.socket = socket;
    }

    private static String genSessionId() {
        return java.util.UUID.randomUUID().toString();
    }

    private static ServerResponse getSessionId(String user, String hash) {
        String dbSalt = null;
        String dbHash = null;

        ServerResponse response = new ServerResponse();
        response.status = "OK";

        try {
            dbHash = database.getUserValue(user, Protocol.HASH);
            dbSalt = database.getUserValue(user, Protocol.SALT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        if (UserAuth.hashAndSalt(hash, dbSalt).equals(dbHash)) {
            String sessionId = genSessionId();
            String unixTime = String.valueOf(System.currentTimeMillis());
            sessionIds.put(sessionId, new String[]{user, unixTime});
            response.data.put("data", Tools.tMap(Protocol.Params.SESSION_ID, sessionId));
        } else {
            response.status = "invalid username or password";
        }

        return response;
    }

    private static String getUser(String sessionId) throws Exception {

        String[] userAndTime;

        userAndTime = sessionIds.get(sessionId);

        long timeDiff = System.currentTimeMillis() - Long.parseLong(userAndTime[1]);

        if (timeDiff >= ONE_DAY_MS) {
            sessionIds.remove(sessionId);
            throw new Exception("session id has expired");
        }

        return userAndTime[0];
    }

    private static ServerResponse parseRequest(ClientRequest request) {
        String type = request.type;
        String path = request.path;
        String sessionId = request.sessionId;
        TreeMap<String, String> params = request.params;
        TreeMap<String, TreeMap<String, String>> data = request.data;

        ServerResponse response = new ServerResponse();
        response.status = "OK";

        // GET
        switch (type) {
            case Protocol.Type.GET:
                // ** get new session id **
                if (path.equals(Protocol.Path.NEW_SESSION_ID)) {
                    try {
                        String user = params.get(Protocol.USER);
                        String hash = params.get(Protocol.HASH);
                        return getSessionId(user, hash);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        response.status = "user and/or hash not in database";
                    }
                }

                // ** get billboards (all or current scheduled) **
                else if (path.equals(Protocol.Path.BILLBOARDS)) {
                    if (params != null && params.containsKey(Protocol.Params.CURRENT_SCHEDULED) &&
                            params.get(Protocol.Params.CURRENT_SCHEDULED).equals("true")) {
                        try {
                            response.data = database.getCurrentBillboard();
                        } catch (Exception e) {
                            e.printStackTrace();
                            response.status = e.getMessage();
                        }
                    } else {
                        response.data = database.getAllBillboards();
                    }
                }

                // ** get users **
                else if (path.equals(Protocol.Path.USERS)) {
                    try {
                        response.data = database.getAllUsers(getUser(sessionId));
                    } catch (Exception e) {
                        e.printStackTrace();
                        response.status = e.getMessage();
                    }
                }
                break;

            // POST
            case Protocol.Type.POST:
                // ** add new user **
                if (path.equals(Protocol.Path.USERS)) {
                    try {
                        if (params != null && params.containsKey(Protocol.Params.INTENT) &&
                                params.get(Protocol.Params.INTENT).equals(Protocol.Params.Intent.EDIT_USERS)) {
                            System.out.println(params);

                            for (Map.Entry<String, TreeMap<String, String>> user : data.entrySet()) {

                                // make sure admin cant destroy itself
                                if (user.getKey().equals("admin")) {
                                    data.get("admin").remove(Protocol.PERMISSION);
                                    data.get("admin").remove("renameTo");
                                }

                                System.out.println(data);

                                database.modifyUsers(getUser(sessionId), data);

                                if (user.getValue().containsKey("renameTo")) {
                                    String newUser = user.getValue().get("renameTo");

                                    for (Map.Entry<String, String[]> session : sessionIds.entrySet()) {
                                        try {
                                            if (user.getKey().equals(session.getValue()[0])) {
                                                sessionIds.put(session.getKey(), new String[]{newUser, session.getValue()[1]});
                                                break;
                                            }
                                        } catch (Exception e) {
                                        }
                                    }
                                }
                            }
                        } else {
                            String user;
                            if (sessionIds.isEmpty() && data.firstKey().equals("admin")) {
                                user = "admin";
                            } else {
                                user = getUser(sessionId);
                            }
                            database.addUsers(user, data);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        response.status = e.getMessage();
                    }
                }

                // ** add new billboard **
                else if (request.path.equals(Protocol.Path.BILLBOARDS)) {

                    try {
                        String intention = request.params.get(Protocol.Params.INTENT);
                        database.addBillboards(getUser(sessionId), data, intention);
                    } catch (Exception e) {
                        e.printStackTrace();
                        response.status = e.getMessage();
                    }
                }
                break;

            // DELETE
            case Protocol.Type.DELETE:
                if (path.equals(Protocol.Path.BILLBOARDS)) {
                    try {
                        database.removeBillboards(getUser(sessionId), data.keySet());
                    } catch (Exception e) {
                        e.printStackTrace();
                        response.status = e.getMessage();
                    }
                }
                break;
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

        // finished
    }
}

