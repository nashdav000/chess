package dataaccess;

/**
 * Indicates there was an error connecting to the database
 */
public class DataAccessException extends Exception{

    public enum Type{
        AlreadyTaken,
        Unauthorized,
        DoesNotExist,
        BadRequest
    }

    private final Type type;

    public DataAccessException(Type type, String message) {
        super(message);
        this.type = type;
    }

    public int toHTTPResponse(){
        return switch (type){
            case BadRequest -> 400;
            case Unauthorized -> 401;
            case AlreadyTaken -> 403;
            case DoesNotExist -> 500;
        };
    }
}
