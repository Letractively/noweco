/**
 * 
 */
package org.apache.james.mailbox.webmail.manager;

import org.apache.james.mailbox.MailboxSession;
import org.apache.james.mailbox.store.StoreSubscriptionManager;
import org.apache.james.mailbox.store.user.SubscriptionMapperFactory;
import org.apache.james.mailbox.store.user.model.Subscription;
import org.apache.james.mailbox.webmail.model.WebmailSubscription;

/**
 * @author Pierre-Marie Dhaussy
 * 
 */
public class WebmailSubscriptionManager extends StoreSubscriptionManager {

    /**
     * @param mapperFactory
     */
    public WebmailSubscriptionManager(final SubscriptionMapperFactory mapperFactory) {
        super(mapperFactory);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.StoreSubscriptionManager#createSubscription(org.apache.james.mailbox.MailboxSession, java.lang.String)
     */
    @Override
    protected Subscription createSubscription(final MailboxSession session, final String mailbox) {
        return new WebmailSubscription(session.getUser().getUserName(), mailbox);
    }
}