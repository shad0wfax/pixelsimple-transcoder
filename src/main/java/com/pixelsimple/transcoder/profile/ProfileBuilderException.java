/**
 * � PixelSimple 2011-2012.
 */
package com.pixelsimple.transcoder.profile;

/**
 *
 * @author Akshay Sharma
 * Dec 16, 2011
 */
public class ProfileBuilderException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 588445143175265873L;

	/** Constructs a new runtime exception with the specified detail message.
	 * The cause is not initialized, and may subsequently be initialized by a
	 * call to {@link #initCause}.
	 *
	 * @param   message   the detail message. The detail message is saved for 
	 *          later retrieval by the {@link #getMessage()} method.
	 */
	public ProfileBuilderException(String message) {
		super(message);
	}
	
	/**
	 * Constructs a new runtime exception with the specified detail message and
	 * cause.  <p>Note that the detail message associated with
	 * <code>cause</code> is <i>not</i> automatically incorporated in
	 * this runtime exception's detail message.
	 *
	 * @param  message the detail message (which is saved for later retrieval
	 *         by the {@link #getMessage()} method).
	 * @param  cause the cause (which is saved for later retrieval by the
	 *         {@link #getCause()} method).  (A <tt>null</tt> value is
	 *         permitted, and indicates that the cause is nonexistent or
	 *         unknown.)
	 */
	public ProfileBuilderException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
