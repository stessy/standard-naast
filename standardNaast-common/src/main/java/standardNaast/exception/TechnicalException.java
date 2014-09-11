/* Copyright 2011 BuyWay-Services */
package standardNaast.exception;

import java.util.Arrays;

/**
 * Description : Technical exception.
 *
 * @author BuyWay-Services: DWW<BR>
 * @version $Revision: $Date: $<BR> Created on 23 août 2011
 */
public class TechnicalException extends AbstractRuntimeException {

    /**
     * The serialVersionUID.
     */
    private static final long serialVersionUID = -7688155793691170593L;

    /**
     * Constructs a new exception with the specified detail message. The cause
     * is not initialized, and may subsequently be initialized by a call to
     * {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for later
     * retrieval by the {@link #getMessage()} method.
     */
    public TechnicalException(final String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     * <p> Note that the detail message associated with
     * <code>cause</code> is <i>not</i> automatically incorporated in this
     * exception's detail message.
     *
     * @param message the detail message (which is saved for later retrieval by
     * the {@link #getMessage()} method).
     * @param cause the cause (which is saved for later retrieval by the
     * {@link #getCause()} method). (A <tt>null</tt> value is permitted, and
     * indicates that the cause is nonexistent or unknown.)
     */
    public TechnicalException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified cause and a detail message
     * of <tt>(cause==null ? null : cause.toString())</tt> (which typically
     * contains the class and detail message of <tt>cause</tt>). This
     * constructor is useful for exceptions that are little more than wrappers
     * for other throwables (for example,
     * {@link java.security.PrivilegedActionException}).
     *
     * @param cause the cause (which is saved for later retrieval by the
     * {@link #getCause()} method). (A <tt>null</tt> value is permitted, and
     * indicates that the cause is nonexistent or unknown.)
     */
    public TechnicalException(final Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new exception with the specified detail message and the
     * errors. The cause is not initialized, and may subsequently be initialized
     * by a call to {@link #initCause}.
     *
     * @param message the detail message (which is saved for later retrieval by
     * the {@link #getMessage()} method).
     * @param errors list of errors.
     */
    public TechnicalException(final String message, final String... errors) {
        super(message + Arrays.asList(errors));
        this.setErrors(errors);
    }
}
