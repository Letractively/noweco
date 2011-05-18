/**
 * 
 */
package org.apache.james.mailbox.webmail;

/**
 * @author Pierre-Marie Dhaussy
 * 
 */
public class WebmailException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public WebmailException() {
        // TODO Auto-generated constructor stub
    }

    public WebmailException(final String message) {
        super(message);
    }

    public WebmailException(final Throwable throwable) {
        super(throwable);
    }

    public WebmailException(final String message, final Throwable throwable) {
        super(message, throwable);
    }
}