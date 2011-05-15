/**
 * 
 */
package org.apache.james.mailbox.webmail.processor;

/**
 * @author Pierre-Marie Dhaussy
 * 
 */
public abstract class WebmailAbstractProcessor implements WebmailProcessor {

    /**
     * 
     */
    protected WebmailProfile profile = null;

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.webmail.processor.WebmailProcessor#setProfile(org.apache.james.mailbox.webmail.processor.WebmailProfile)
     */
    public void setProfile(final WebmailProfile profile) {
        this.profile = profile;
    }
}