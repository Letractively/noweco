/**
 * 
 */
package org.apache.james.mailbox.webmail.mail.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.james.mailbox.store.mail.model.Header;
import org.apache.mailet.base.RFC2822Headers;

/**
 * @author Pierre-Marie Dhaussy
 * 
 */
public abstract class WebmailMessageFactory {

    /**
     * Constructor
     */
    private WebmailMessageFactory() {
        // Utility class
    }

    /**
     * Create a new message
     * 
     * @param from sender
     * @param to recipient
     * @param subject subject
     * @param body body content
     * @return a new message
     */
    public static WebmailMessage createMessage(final String from, final String to, final String subject, final String body) {
        WebmailMessage message = new WebmailMessage();
        message.setHeaders(createHeaders(from, to, subject));
        // FIXME
        // message.setBodyContent(WebmailUtils.bodyToInputStream(body));
        return message;
    }

    public static List<Header> createHeaders(final String from, final String to) {
        List<Header> headers = new ArrayList<Header>();
        headers.add(new WebmailHeader(RFC2822Headers.FROM, from));
        headers.add(new WebmailHeader(RFC2822Headers.TO, to));
        return headers;
    }

    public static List<Header> createHeaders(final String from, final String to, final String subject) {
        List<Header> headers = new ArrayList<Header>();
        headers.add(new WebmailHeader(RFC2822Headers.FROM, from));
        headers.add(new WebmailHeader(RFC2822Headers.TO, to));
        headers.add(new WebmailHeader(RFC2822Headers.SUBJECT, subject));
        return headers;
    }
}