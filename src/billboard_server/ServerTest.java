package billboard_server;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import java.sql.SQLException;

public class ServerTest {

    private Server sv;

    @BeforeEach @Test
    public void createServer() throws SQLException {
        sv = new Server();
    }

    @Test
    public void checkNonEmptyDb() throws SQLException{
        assertEquals("billboards schedule users ", sv.getTables());
    }

    @Test
    public void getUsers() throws SQLException{
        sv.addUser("jimmy");
        assertEquals("jimmy ", sv.getUsers());
    }

}
