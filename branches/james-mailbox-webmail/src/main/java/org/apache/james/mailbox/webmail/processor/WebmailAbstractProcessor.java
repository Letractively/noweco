/**
 * 
 */
package org.apache.james.mailbox.webmail.processor;

import java.util.Iterator;
import java.util.List;

import javax.mail.Flags;

import org.apache.james.mailbox.MailboxException;
import org.apache.james.mailbox.MailboxNotFoundException;
import org.apache.james.mailbox.MailboxPath;
import org.apache.james.mailbox.MessageRange;
import org.apache.james.mailbox.SubscriptionException;
import org.apache.james.mailbox.UpdatedFlags;
import org.apache.james.mailbox.store.mail.model.Mailbox;
import org.apache.james.mailbox.store.mail.model.Message;
import org.apache.james.mailbox.store.user.model.Subscription;

/**
 * @author Pierre-Marie Dhaussy
 * 
 */
public abstract class WebmailAbstractProcessor implements WebmailProcessor {

    /**
     * 
     */
    protected WebmailProcessorConfiguration configuration = null;

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.webmail.processor.WebmailProcessor#getConfiguration()
     */
    public WebmailProcessorConfiguration getConfiguration() {
        return configuration;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.webmail.processor.WebmailProcessor#setConfiguration(org.apache.james.mailbox.webmail.processor.WebmailProcessorConfiguration)
     */
    public void setConfiguration(final WebmailProcessorConfiguration configuration) {
        this.configuration = configuration;
    }

    /*
     * ------------------------------------------------------
     */

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.transaction.Mapper#endRequest()
     */
    public void endRequest() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.transaction.Mapper#execute(org.apache.james.mailbox.store.transaction.Mapper.Transaction)
     */
    public <T> T execute(final Transaction<T> transaction) throws MailboxException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.Authenticator#isAuthentic(java.lang.String, java.lang.CharSequence)
     */
    public boolean isAuthentic(final String userid, final CharSequence passwd) {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.mail.MessageMapper#findInMailbox(org.apache.james.mailbox.store.mail.model.Mailbox, org.apache.james.mailbox.MessageRange,
     * org.apache.james.mailbox.store.transaction.Mapper.MailboxMembershipCallback)
     */
    public void findInMailbox(final Mailbox<Integer> mailbox, final MessageRange set, final MailboxMembershipCallback<Integer> callback) throws MailboxException {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.mail.MessageMapper#expungeMarkedForDeletionInMailbox(org.apache.james.mailbox.store.mail.model.Mailbox, org.apache.james.mailbox.MessageRange)
     */
    public Iterator<Long> expungeMarkedForDeletionInMailbox(final Mailbox<Integer> mailbox, final MessageRange set) throws MailboxException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.mail.MessageMapper#countMessagesInMailbox(org.apache.james.mailbox.store.mail.model.Mailbox)
     */
    public long countMessagesInMailbox(final Mailbox<Integer> mailbox) throws MailboxException {
        // TODO Auto-generated method stub
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.mail.MessageMapper#countUnseenMessagesInMailbox(org.apache.james.mailbox.store.mail.model.Mailbox)
     */
    public long countUnseenMessagesInMailbox(final Mailbox<Integer> mailbox) throws MailboxException {
        // TODO Auto-generated method stub
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.mail.MessageMapper#delete(org.apache.james.mailbox.store.mail.model.Mailbox, org.apache.james.mailbox.store.mail.model.Message)
     */
    public void delete(final Mailbox<Integer> mailbox, final Message<Integer> message) throws MailboxException {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.mail.MessageMapper#findFirstUnseenMessageUid(org.apache.james.mailbox.store.mail.model.Mailbox)
     */
    public Long findFirstUnseenMessageUid(final Mailbox<Integer> mailbox) throws MailboxException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.mail.MessageMapper#findRecentMessagesInMailbox(org.apache.james.mailbox.store.mail.model.Mailbox)
     */
    public List<Message<Integer>> findRecentMessagesInMailbox(final Mailbox<Integer> mailbox) throws MailboxException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.mail.MessageMapper#add(org.apache.james.mailbox.store.mail.model.Mailbox, org.apache.james.mailbox.store.mail.model.Message)
     */
    public long add(final Mailbox<Integer> mailbox, final Message<Integer> message) throws MailboxException {
        // TODO Auto-generated method stub
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.mail.MessageMapper#updateFlags(org.apache.james.mailbox.store.mail.model.Mailbox, javax.mail.Flags, boolean, boolean, org.apache.james.mailbox.MessageRange)
     */
    public Iterator<UpdatedFlags> updateFlags(final Mailbox<Integer> mailbox, final Flags flags, final boolean value, final boolean replace, final MessageRange set) throws MailboxException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.mail.MessageMapper#copy(org.apache.james.mailbox.store.mail.model.Mailbox, long, org.apache.james.mailbox.store.mail.model.Message)
     */
    public long copy(final Mailbox<Integer> mailbox, final long uid, final Message<Integer> original) throws MailboxException {
        // TODO Auto-generated method stub
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.user.SubscriptionMapper#findMailboxSubscriptionForUser(java.lang.String, java.lang.String)
     */
    public Subscription findMailboxSubscriptionForUser(final String user, final String mailbox) throws SubscriptionException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.user.SubscriptionMapper#save(org.apache.james.mailbox.store.user.model.Subscription)
     */
    public void save(final Subscription subscription) throws SubscriptionException {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.user.SubscriptionMapper#findSubscriptionsForUser(java.lang.String)
     */
    public List<Subscription> findSubscriptionsForUser(final String user) throws SubscriptionException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.user.SubscriptionMapper#delete(org.apache.james.mailbox.store.user.model.Subscription)
     */
    public void delete(final Subscription subscription) throws SubscriptionException {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.mail.MailboxMapper#save(org.apache.james.mailbox.store.mail.model.Mailbox)
     */
    public void save(final Mailbox<Integer> mailbox) throws MailboxException {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.mail.MailboxMapper#delete(org.apache.james.mailbox.store.mail.model.Mailbox)
     */
    public void delete(final Mailbox<Integer> mailbox) throws MailboxException {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.mail.MailboxMapper#findMailboxByPath(org.apache.james.mailbox.MailboxPath)
     */
    public Mailbox<Integer> findMailboxByPath(final MailboxPath mailboxName) throws MailboxException, MailboxNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.mail.MailboxMapper#findMailboxWithPathLike(org.apache.james.mailbox.MailboxPath)
     */
    public List<Mailbox<Integer>> findMailboxWithPathLike(final MailboxPath mailboxPath) throws MailboxException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.mail.MailboxMapper#hasChildren(org.apache.james.mailbox.store.mail.model.Mailbox, char)
     */
    public boolean hasChildren(final Mailbox<Integer> mailbox, final char delimiter) throws MailboxException, MailboxNotFoundException {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.mail.MailboxMapper#list()
     */
    public List<Mailbox<Integer>> list() throws MailboxException {
        // TODO Auto-generated method stub
        return null;
    }
}