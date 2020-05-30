package billboard_server;

public final class Protocol {
    public static final String TYPE = "type";

    public static final String DATABASE_CMD = "dbcmd";
    public static final String SERVER_CMD = "svcmd";

    public enum Cmd {
        NONE,
        ADD_USERS,
        DELETE_USERS,
        GET_USERS,
        ADD_BILLBOARDS,
        DELETE_BILLBOARDS,
        GET_BILLBOARDS,
        GET_CURRENT_BILLBOARD,
        ADD_SCHEDULES,
        DELETE_SCHEDULES,
        GET_SCHEDULES,
        GET_SESSION_ID,
        NAME_TO_ID,
        BOARD_TO_ID,
        BOARD_TO_SCHEDULE,
        SCHEDULE_COMMAND
    }

    public static final String PERMISSION = "permissions";

    public static final class Permission {
        public static final int NONE = 0b0;
        public static final int EDIT_ALL_BILLBOARDS = 0b1;
        public static final int CREATE_BILLBOARDS = 0b10;
        public static final int SCHEDULE_BILLBOARDS = 0b100;
        public static final int EDIT_USERS = 0b1000;
        public static final int ALL = 0b1111;

        public static String toString(int permission) {
            if (permission == 0) {
                return "None";
            }

            String permissions = "'";

            if ((permission & EDIT_ALL_BILLBOARDS) != 0) {
                permissions += "Edit all billboards, ";
            }

            if ((permission & CREATE_BILLBOARDS) != 0) {
                permissions += "Create billboards, ";
            }

            if ((permission & SCHEDULE_BILLBOARDS) != 0) {
                permissions += "Schedule Billboards, ";
            }

            if ((permission & EDIT_USERS) != 0) {
                permissions += "Edit users, ";
            }

            permissions = permissions.substring(0, permissions.length() - 2) + '\'';

            return permissions;
        }
    }

    public static final String HASH = "hash";
    public static final String SALT = "salt";
    public static final String USERNAME = "userName";
    public static final String USERID = "userId";
    public static final String BOARDNAME = "billboardName";
    public static final String BOARDCREATOR = "billboardCreator";
    public static final String BOARDID = "billboardId";
    public static final String SESSIONID = "sessionId";
}
