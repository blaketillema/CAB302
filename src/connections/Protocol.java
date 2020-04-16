package connections;

public class Protocol
{
    public static final String LOCALHOST = "localHost";

    public static final String PERMISSION = "permission";

    public static class Permission
    {
        // add permissions together
        public static String combine(String ... permissions)
        {
            int total = 0;
            for(String permission : permissions)
            {
                total += Integer.parseInt(permission);
            }
            return String.valueOf(total);
        }

        // check if permissions match exactly
        public static boolean hasAll(String toCompare, String needed)
        {
            return toCompare.equals(needed);
        }

        // check if toCompare has more or equal to the permissions of needed
        public static boolean hasOne(String toCompare, String needed)
        {
            int a = Integer.parseInt(toCompare, 2);
            int b = Integer.parseInt(needed, 2);

            return (a & b) >= b;
        }

        public static final String NONE = "0";
        public static final String EDIT_ALL_BILLBOARDS = "1";
        public static final String CREATE_BILLBOARDS = "10";
        public static final String SCHEDULE_BILLBOARDS = "100";
        public static final String EDIT_USERS = "1000";
        public static final String ALL = "1111";
    }

    public static final String PATH = "path";

    public static class Path
    {
        public static final String USERS = "/users/";
        public static final String BILLBOARDS = "/billboards/";
        public static final String NEW_SESSION_ID = "/cmd/newSessionId";
    }

    public static class Type
    {
        public static final String POST = "POST";
        public static final String GET = "GET";
        public static final String DELETE = "DELETE";
    }

    public static class Params
    {
        public static final String SESSION_ID = "sessionId";
        public static final String CURRENT_SCHEDULED = "currentScheduled";
    }

    public static final String HASH = "hash";
    public static final String SALT = "salt";
    public static final String USER = "user";

}

