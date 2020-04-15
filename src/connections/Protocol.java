package connections;

public class Protocol
{
    public static final String LOCALHOST = "localHost";

    public static final String PERMISSION = "permission";
    public class Permission
    {
        public static final String EDIT_ALL_BILLBOARDS = "EDIT_ALL_BILLBOARDS";
        public static final String CREATE_BILLBOARDS = "CREATE_BILLBOARDS";
        public static final String SCHEDULE_BILLBOARDS = "SCHEDULE_BILLBOARDS";
        public static final String EDIT_USERS = "EDIT_USERS";
    }

    public static final String PATH = "path";
    public class Path
    {
        public static final String USERS = "/users/";
        public static final String BILLBOARDS = "/billboards/";
        public static final String NEW_SESSION_ID = "/cmd/newSessionId";
    }

    public class Type
    {
        public static final String POST = "POST";
        public static final String GET = "GET";
        public static final String DELETE = "DELETE";
    }

    public class Params
    {
        public static final String SESSION_ID = "sessionId";
        public static final String CURRENT_SCHEDULED = "currentScheduled";
    }

    public static final String HASH = "hash";
    public static final String SALT = "salt";
    public static final String USER = "user";

}

