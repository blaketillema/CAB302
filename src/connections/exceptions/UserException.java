package connections.exceptions;

import java.util.List;

public class UserException extends ServerException {
    public UserException(String s) {
        super("user " + s + " already exists");
    }
}
