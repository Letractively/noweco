/**
 * 
 */
package org.apache.james.mailbox.webmail.mapper;

import org.apache.james.mailbox.store.Authenticator;
import org.apache.james.mailbox.webmail.WebmailUtils;
import org.apache.james.mailbox.webmail.processor.WebmailProcessor;
import org.apache.james.mailbox.webmail.processor.WebmailProcessorFactory;

/**
 * @author Pierre-Marie Dhaussy
 * 
 */
public class AuthenticatorMapper implements Authenticator {

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
        WebmailProcessor processor = processorRegistry.getProcessor(WebmailUtils.getProfile(userid));
        return processor.isAuthentic(userid, passwd);
    }

}
