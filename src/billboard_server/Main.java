package billboard_server;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class Main {
    public static void main(String[] args) throws SQLException, IOException {

        try{
            Properties props = new Properties();
            FileInputStream in = new FileInputStream("./db.props");
            props.load(in);
            in.close();

            String url = props.getProperty("jdbc.url");
            String schema = props.getProperty("jdbc.schema");
            String username = props.getProperty("jdbc.username");
            String password = props.getProperty("jdbc.password");

            Connection connection = DriverManager.getConnection(url + "/" + schema, username, password);

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM users");

            resultSet.next();
            int personId = resultSet.getInt(1);
            String personName = resultSet.getString(2);
            float someNum = resultSet.getFloat(3);
            System.out.print(personId + " " + personName + " " + someNum + "\n");

            connection.close();
        }
        catch(SQLException sqle){
            sqle.printStackTrace();
        }
        catch(IOException ioe){
            ioe.printStackTrace();
        }

    }
}
