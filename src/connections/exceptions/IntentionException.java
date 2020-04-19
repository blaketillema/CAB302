package connections.exceptions;

public class IntentionException extends ServerException {
    public IntentionException(String user, String permission, String needed, String intention) {
        super(String.format("user '%s' intends to %s (requires %s) with only %s", user, intention, needed, permission));
    }
}
