/**
 * 
 */
package org.apache.james.mailbox.webmail.processor.third;

import org.apache.james.mailbox.webmail.processor.WebmailAbstractProcessor;

/**
 * @author Pierre-Marie Dhaussy
 * 
 */
public class FakeProcessor extends WebmailAbstractProcessor {

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.Authenticator#isAuthentic(java.lang.String, java.lang.CharSequence)
     */
    @Override
    public boolean isAuthentic(final String userid, final CharSequence passwd) {
        System.out.println("WebmailFakeProcessor.isAuthentic(" + userid + ", " + passwd + ")");
        return passwd.equals("true");
    }
}