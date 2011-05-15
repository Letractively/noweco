/**
 * 
 */
package org.apache.james.mailbox.webmail.mapper;

import java.util.Iterator;
import java.util.List;

import javax.mail.Flags;

import org.apache.james.mailbox.MailboxException;
import org.apache.james.mailbox.MessageRange;
import org.apache.james.mailbox.UpdatedFlags;
import org.apache.james.mailbox.store.mail.MessageMapper;
import org.apache.james.mailbox.store.mail.model.Mailbox;
import org.apache.james.mailbox.store.mail.model.Message;
import org.apache.james.mailbox.webmail.processor.WebmailProcessor;

/**
 * @author Pierre-Marie Dhaussy
 * 
 */
public class WebmailMessageMapper implements MessageMapper<Integer> {

    /**
     * The processor
     */
    private WebmailProcessor processor = null;

    /**
     * @param processor
     */
    public WebmailMessageMapper(final WebmailProcessor processor) {
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
     * @see org.apache.james.mailbox.store.mail.MessageMapper#findInMailbox(org.apache.james.mailbox.store.mail.model.Mailbox, org.apache.james.mailbox.MessageRange,
     * org.apache.james.mailbox.store.transaction.Mapper.MailboxMembershipCallback)
     */
    public void findInMailbox(final Mailbox<Integer> mailbox, final MessageRange set, final MailboxMembershipCallback<Integer> callback) throws MailboxException {
        processor.findInMailbox(mailbox, set, callback);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.mail.MessageMapper#expungeMarkedForDeletionInMailbox(org.apache.james.mailbox.store.mail.model.Mailbox, org.apache.james.mailbox.MessageRange)
     */
    public Iterator<Long> expungeMarkedForDeletionInMailbox(final Mailbox<Integer> mailbox, final MessageRange set) throws MailboxException {
        return processor.expungeMarkedForDeletionInMailbox(mailbox, set);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.mail.MessageMapper#countMessagesInMailbox(org.apache.james.mailbox.store.mail.model.Mailbox)
     */
    public long countMessagesInMailbox(final Mailbox<Integer> mailbox) throws MailboxException {
        return processor.countMessagesInMailbox(mailbox);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.mail.MessageMapper#countUnseenMessagesInMailbox(org.apache.james.mailbox.store.mail.model.Mailbox)
     */
    public long countUnseenMessagesInMailbox(final Mailbox<Integer> mailbox) throws MailboxException {
        return processor.countUnseenMessagesInMailbox(mailbox);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.mail.MessageMapper#delete(org.apache.james.mailbox.store.mail.model.Mailbox, org.apache.james.mailbox.store.mail.model.Message)
     */
    public void delete(final Mailbox<Integer> mailbox, final Message<Integer> message) throws MailboxException {
        processor.delete(mailbox, message);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.mail.MessageMapper#findFirstUnseenMessageUid(org.apache.james.mailbox.store.mail.model.Mailbox)
     */
    public Long findFirstUnseenMessageUid(final Mailbox<Integer> mailbox) throws MailboxException {
        return processor.findFirstUnseenMessageUid(mailbox);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.mail.MessageMapper#findRecentMessagesInMailbox(org.apache.james.mailbox.store.mail.model.Mailbox)
     */
    public List<Message<Integer>> findRecentMessagesInMailbox(final Mailbox<Integer> mailbox) throws MailboxException {
        return processor.findRecentMessagesInMailbox(mailbox);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.mail.MessageMapper#add(org.apache.james.mailbox.store.mail.model.Mailbox, org.apache.james.mailbox.store.mail.model.Message)
     */
    public long add(final Mailbox<Integer> mailbox, final Message<Integer> message) throws MailboxException {
        return processor.add(mailbox, message);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.mail.MessageMapper#updateFlags(org.apache.james.mailbox.store.mail.model.Mailbox, javax.mail.Flags, boolean, boolean, org.apache.james.mailbox.MessageRange)
     */
    public Iterator<UpdatedFlags> updateFlags(final Mailbox<Integer> mailbox, final Flags flags, final boolean value, final boolean replace, final MessageRange set) throws MailboxException {
        return processor.updateFlags(mailbox, flags, value, replace, set);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.mail.MessageMapper#copy(org.apache.james.mailbox.store.mail.model.Mailbox, long, org.apache.james.mailbox.store.mail.model.Message)
     */
    public long copy(final Mailbox<Integer> mailbox, final long uid, final Message<Integer> original) throws MailboxException {
        return processor.copy(mailbox, uid, original);
    }
}