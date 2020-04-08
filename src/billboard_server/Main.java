package billboard_server;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class Main {
    public static void main(String[] args) throws SQLException{

        Server sv = new Server();

        System.out.println(sv.getTables());
    }
}
