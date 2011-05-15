/**
 * 
 */
package org.apache.james.mailbox.webmail.mapper;

import java.util.List;

import org.apache.james.mailbox.MailboxException;
import org.apache.james.mailbox.SubscriptionException;
import org.apache.james.mailbox.store.user.SubscriptionMapper;
import org.apache.james.mailbox.store.user.model.Subscription;
import org.apache.james.mailbox.webmail.processor.WebmailProcessor;

/**
 * @author Pierre-Marie Dhaussy
 * 
 */
public class WebmailSubscriptionMapper implements SubscriptionMapper {

    /**
     * The processor
     */
    private WebmailProcessor processor = null;

    /**
     * @param processor
     */
    public WebmailSubscriptionMapper(final WebmailProcessor processor) {
        this.processor = processor;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.transaction.Mapper#endRequest()
     */
    public void endRequest() {
        processor.endRequest();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.transaction.Mapper#execute(org.apache.james.mailbox.store.transaction.Mapper.Transaction)
     */
    public <T> T execute(final Transaction<T> transaction) throws MailboxException {
        return processor.execute(transaction);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.user.SubscriptionMapper#findMailboxSubscriptionForUser(java.lang.String, java.lang.String)
     */
    public Subscription findMailboxSubscriptionForUser(final String user, final String mailbox) throws SubscriptionException {
        return processor.findMailboxSubscriptionForUser(user, mailbox);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.user.SubscriptionMapper#save(org.apache.james.mailbox.store.user.model.Subscription)
     */
    public void save(final Subscription subscription) throws SubscriptionException {
        processor.save(subscription);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.user.SubscriptionMapper#findSubscriptionsForUser(java.lang.String)
     */
    public List<Subscription> findSubscriptionsForUser(final String user) throws SubscriptionException {
        return processor.findSubscriptionsForUser(user);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.user.SubscriptionMapper#delete(org.apache.james.mailbox.store.user.model.Subscription)
     */
    public void delete(final Subscription subscription) throws SubscriptionException {
        processor.delete(subscription);
    }
}