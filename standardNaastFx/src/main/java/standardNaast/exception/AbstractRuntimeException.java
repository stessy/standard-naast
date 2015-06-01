/* Copyright 2012 BuyWay-Services */
package standardNaast.exception;

import java.util.Arrays;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import standardNaast.utils.ExceptionUtils;

/**
 * Description: base exception class.
 *
 * @author BuyWay-Services: DWW<BR>
 *         Created on 12 juin 2012
 */
public abstract class AbstractRuntimeException extends RuntimeException {

	/**
	 * The serialVersionUID.
	 */
	private static final long serialVersionUID = -3375070979845105063L;

	/**
	 * The LOG.
	 */
	private static final Logger LOG = LogManager.getLogger(AbstractRuntimeException.class);

	/**
	 * The uniqueId.
	 */
	private final UUID uniqueId = UUID.randomUUID();

	/**
	 * The businessId.
	 */
	private final String businessId = ExceptionUtils.getTimestampAndServerName();

	/**
	 * The errors.
	 */
	private String[] errors;

	/**
	 * Constructs a new exception with the specified detail message. The cause
	 * is not initialized, and may subsequently be initialized by a call to
	 * {@link #initCause}.
	 *
	 * @param message
	 *            the detail message. The detail message is saved for later
	 *            retrieval by the {@link #getMessage()} method.
	 */
	protected AbstractRuntimeException(final String message) {
		super(message);
		this.log();
	}

	/**
	 * Constructs a new exception with the specified detail message and cause.
	 * <p>
	 * Note that the detail message associated with <code>cause</code> is
	 * <i>not</i> automatically incorporated in this exception's detail message.
	 *
	 * @param message
	 *            the detail message (which is saved for later retrieval by the
	 *            {@link #getMessage()} method).
	 * @param cause
	 *            the cause (which is saved for later retrieval by the
	 *            {@link #getCause()} method). (A <tt>null</tt> value is
	 *            permitted, and indicates that the cause is nonexistent or
	 *            unknown.)
	 */
	protected AbstractRuntimeException(final String message, final Throwable cause) {
		super(message, cause);
		this.log();
	}

	/**
	 * Constructs a new exception with the specified cause and a detail message
	 * of <tt>(cause==null ? null : cause.toString())</tt> (which typically
	 * contains the class and detail message of <tt>cause</tt>). This
	 * constructor is useful for exceptions that are little more than wrappers
	 * for other throwables (for example,
	 * {@link java.security.PrivilegedActionException}).
	 *
	 * @param cause
	 *            the cause (which is saved for later retrieval by the
	 *            {@link #getCause()} method). (A <tt>null</tt> value is
	 *            permitted, and indicates that the cause is nonexistent or
	 *            unknown.)
	 */
	protected AbstractRuntimeException(final Throwable cause) {
		super(cause);
		this.log();
	}

	/**
	 * Constructs a new exception with the specified detail message and the
	 * errors. The cause is not initialized, and may subsequently be initialized
	 * by a call to {@link #initCause}.
	 *
	 * @param message
	 *            the detail message (which is saved for later retrieval by the
	 *            {@link #getMessage()} method).
	 * @param errors
	 *            list of errors.
	 */
	protected AbstractRuntimeException(final String message, final String[] errors) {
		super(message + Arrays.asList(errors).toString());
		this.errors = errors;
		this.log();
	}

	/**
	 * @return Returns the errors.
	 */
	public String[] getErrors() {
		return this.errors;
	}

	/**
	 * @param errors
	 *            The errors to set.
	 */
	public void setErrors(final String[] errors) {
		this.errors = errors;
	}

	/**
	 * @return Returns the uniqueId.
	 */
	public UUID getUniqueId() {
		return this.uniqueId;
	}

	@Override
	public String toString() {
		return this.createUniqueIdMessage(super.toString());
	}

	/**
	 * @return <code>true</code> or <code>false</code>.
	 */
	protected boolean log() {
		AbstractRuntimeException.LOG.error(this.toString(), this);
		return true;
	}

	/**
	 * @param message
	 *            The message to prefix with the unique ID and business ID.
	 * @return A new String containing the unique ID and business ID of the
	 *         exception and the message passed as parameter.
	 */
	private String createUniqueIdMessage(final String message) {
		return "[" + this.uniqueId + "] [" + this.businessId + "] " + message;
	}

	/**
	 * @return Returns the businessId.
	 */
	public String getBusinessId() {
		return this.businessId;
	}
}