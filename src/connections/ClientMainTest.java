package connections;

import connections.testing.*;

public class ClientMainTest {
    public static void main(String[] args) throws Exception {

        AdminAddUsers.main(args);

        line();

        User1AddBillboards.main(args);

        line();

        User2EditBillboards.main(args);

        line();

        User3ScheduleBillboards.main(args);

        line();

        ViewerGetCurrent.main(args);

        line();

        AdminModifyUsers.main(args);

        line();


    }

    static void line() {
        System.out.println("\n==========================================================================\n");
    }
}
