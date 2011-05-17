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
     * 
     */
    private static final WebmailProcessorFactory processorRegistry = WebmailProcessorFactory.getInstance();

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.Authenticator#isAuthentic(java.lang.String, java.lang.CharSequence)
     */
    public boolean isAuthentic(final String userid, final CharSequence passwd) {
        WebmailProcessor processor = processorRegistry.getProcessor(WebmailUtils.getProfileName(userid));
        return processor.isAuthentic(userid, passwd);
    }

}
