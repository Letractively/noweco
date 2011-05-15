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

    /* (non-Javadoc)
     * @see org.apache.james.mailbox.store.Authenticator#isAuthentic(java.lang.String, java.lang.CharSequence)
     */
    public boolean isAuthentic(String userid, CharSequence passwd) {
        System.out.println("WebmailFakeProcessor.isAuthentic(" + userid + ", " + passwd + ")");
        return true;
    }
    
    /* (non-Javadoc)
     * @see org.apache.james.mailbox.webmail.processor.WebmailAbstractProcessor#getFolderDelimiter()
     */
    @Override
    public char getFolderDelimiter() {
        System.out.println("WebmailFakeProcessor.getFolderDelimiter()");
        return super.getFolderDelimiter();
    }
}
