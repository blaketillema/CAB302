package connections.exceptions;

import java.util.List;

public class ExistingUserException extends ServerException {
    public ExistingUserException(List<String> s) {
        super("tried to add existing users: " + s.toString());
    }
}
