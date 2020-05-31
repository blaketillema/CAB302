package billboard_server;

import com.sun.source.tree.Tree;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class TestDatabase {

    private Database db;

    @BeforeEach
    void newDb() throws SQLException{
        db = new Database();
    }

    @Test
    void addUser() throws SQLException{
        String userId = java.util.UUID.randomUUID().toString();
        TreeMap<String, Object> user = new TreeMap<>();
        TreeMap<String, Object> data = new TreeMap<>();
        data.put("userName", "test");
        data.put("permissions", 8);
        user.put(userId, data);
        db.addUser(userId, "test", "hash", "salt", 8);
        Assertions.assertEquals(user.toString(), db.getUser(userId).toString());
    }

    @Test
    void addUsers() throws SQLException{
        String userId1 = java.util.UUID.randomUUID().toString();
        String userId2 = java.util.UUID.randomUUID().toString();

        TreeMap<String, Object> data1 = new TreeMap<>();
        TreeMap<String, Object> data2 = new TreeMap<>();
        TreeMap<String, Object> users = new TreeMap<>();

        data1.put("userName", "test1");
        data1.put("permissions", 1);

        data2.put("userName", "test2");
        data2.put("permissions", 2);

        users.put(userId1, data1);
        users.put(userId2, data2);

        db.addUser(userId1, "test1", "hash1", "salt1", 1);
        db.addUser(userId2, "test2", "hash2", "salt1", 2);

        TreeMap<String, Object> getUsers = db.getUsers();

        //this loops finds and removes the admin user
        String adminId = ""; //key needed to be stored outside of the loop to prevent Concurrent Modification Exception
        for(Map.Entry<String, Object> user : getUsers.entrySet()){
            if(user.getValue().toString().contains("admin")){
                adminId = user.getKey();
            }
        }
        if(!adminId.equals("")) getUsers.remove(adminId);

        Assertions.assertEquals(users.toString(), getUsers.toString());
    }

    @Test
    void removeUser() throws SQLException{
        String userId = java.util.UUID.randomUUID().toString();
        TreeMap<String, Object> emptyTree = new TreeMap<>();

        db.addUser(userId, "name", "hash", "salt", 8);

        db.deleteUser(userId);

        TreeMap<String, Object> getUsers = db.getUsers();

        String adminId = "";
        for(Map.Entry<String, Object> user : getUsers.entrySet()){
            if(user.getValue().toString().contains("admin")){
                adminId = user.getKey();
            }
        }
        if(!adminId.equals("")) getUsers.remove(adminId);

        Assertions.assertEquals(emptyTree.toString(), getUsers.toString());
    }

    @Test
    void addBillboard() throws SQLException{
        String billboardId = java.util.UUID.randomUUID().toString();
        String userId = java.util.UUID.randomUUID().toString();

        ArrayList<String> bilbs = new ArrayList<>();
        bilbs.add(billboardId);

        db.addUser(userId, "test", "test", "test", 8);

        TreeMap<String, Object> data = new TreeMap<>();
        TreeMap<String, Object> bilb = new TreeMap<>();

        data.put("billboardName", "name"); data.put("billboardCreator", userId);
        data.put("message", "message"); data.put("information", "info");
        data.put("pictureData", "pd"); data.put("pictureUrl", "pu");
        data.put("billboardBackground", "bg"); data.put("messageColour", "mc");
        data.put("informationColour", "ic"); bilb.put(billboardId, data);

        db.addBillboard(billboardId, "name", userId, "message", "info", "pd", "pu", "bg", "mc", "ic");

        Assertions.assertEquals(bilb.toString(), db.getBillboards(bilbs).toString());
    }

    @Test
    void addBillboards() throws SQLException{
        String billboardId1 = java.util.UUID.randomUUID().toString();
        String billboardId2 = java.util.UUID.randomUUID().toString();
        String userId = java.util.UUID.randomUUID().toString();

        ArrayList<String> bilbsStr = new ArrayList<>();
        bilbsStr.add(billboardId1);
        bilbsStr.add(billboardId2);

        db.addUser(userId, "test", "test", "test", 8);

        TreeMap<String, Object> data = new TreeMap<>();
        TreeMap<String, Object> bilbs = new TreeMap<>();

        data.put("billboardName", "name"); data.put("billboardCreator", userId);
        data.put("message", "message"); data.put("information", "info");
        data.put("pictureData", "pd"); data.put("pictureUrl", "pu");
        data.put("billboardBackground", "bg"); data.put("messageColour", "mc");
        data.put("informationColour", "ic");

        bilbs.put(billboardId1, data);
        bilbs.put(billboardId2, data);

        db.addBillboard(billboardId1, "name", userId, "message", "info", "pd", "pu", "bg", "mc", "ic");
        db.addBillboard(billboardId2, "name", userId, "message", "info", "pd", "pu", "bg", "mc", "ic");

        Assertions.assertEquals(bilbs.toString(), db.getBillboards(bilbsStr).toString());
    }

    @Test
    void removeBillboard() throws SQLException{
        String userId = java.util.UUID.randomUUID().toString();
        String billboardId = java.util.UUID.randomUUID().toString();
        TreeMap<String, Object> emptyTree = new TreeMap<>();

        db.addUser(userId, "name", "hash", "salt", 8);

        db.addBillboard(billboardId, "name", userId, "message", "info", "pictureD", "pictureU", "bg", "msgC", "infoC");
        db.deleteBillboard(billboardId);

        Assertions.assertEquals(emptyTree.toString(), db.getBillboards().toString());
    }
}