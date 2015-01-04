package nu.wasis.jdocstat.exception;

public class ArgumentParsingException extends IllegalArgumentException {

    private static final long serialVersionUID = 1L;

    public ArgumentParsingException() {
    }

    public ArgumentParsingException(final String s) {
        super(s);
    }

    public ArgumentParsingException(final Throwable cause) {
        super(cause);
    }

    public ArgumentParsingException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
