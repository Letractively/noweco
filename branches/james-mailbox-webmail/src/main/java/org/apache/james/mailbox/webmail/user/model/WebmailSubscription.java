/**
 * 
 */
package org.apache.james.mailbox.webmail.user.model;

import org.apache.james.mailbox.store.user.model.Subscription;

/**
 * @author Pierre-Marie Dhaussy
 * 
 */
public class WebmailSubscription implements Subscription {

    /**
     * 
     */
    private final String user;

    /**
     * 
     */
    private final String mailbox;

    public WebmailSubscription(final String user, final String mailbox) {
        this.user = user;
        this.mailbox = mailbox;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.user.model.Subscription#getMailbox()
     */
    public String getMailbox() {
        return mailbox;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.user.model.Subscription#getUser()
     */
    public String getUser() {
        return user;
    }
}