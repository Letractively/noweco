/**
 * 
 */
package org.apache.james.mailbox.webmail;

import org.apache.james.mailbox.store.Authenticator;
import org.apache.james.mailbox.webmail.processor.WebmailProcessor;
import org.apache.james.mailbox.webmail.processor.WebmailProcessorFactory;

/**
 * @author Pierre-Marie Dhaussy
 * 
 */
public class WebmailAuthenticator implements Authenticator {

    /**
     * Factory
     */
    private WebmailProcessorFactory processorFactory = null;

    /**
     * Constructor
     * 
     * @param processorFactory factory
     */
    public WebmailAuthenticator(final WebmailProcessorFactory processorFactory) {
        this.processorFactory = processorFactory;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.james.mailbox.store.Authenticator#isAuthentic(java.lang.String,
     *      java.lang.CharSequence)
     */
    public boolean isAuthentic(final String userid, final CharSequence passwd) {
        try {
            WebmailProcessor processor = processorFactory.getProcessor(WebmailUtils.getProfileName(userid));
            return processor.isAuthentic(userid, passwd);
        } catch (WebmailException e) {
            // FIXME ?
            return false;
        }
    }
}