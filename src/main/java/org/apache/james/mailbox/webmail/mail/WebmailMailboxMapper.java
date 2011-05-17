/**
 * 
 */
package org.apache.james.mailbox.webmail.mail;

import java.util.List;

import org.apache.james.mailbox.MailboxException;
import org.apache.james.mailbox.MailboxNotFoundException;
import org.apache.james.mailbox.MailboxPath;
import org.apache.james.mailbox.store.mail.MailboxMapper;
import org.apache.james.mailbox.store.mail.model.Mailbox;
import org.apache.james.mailbox.webmail.processor.WebmailProcessor;

/**
 * @author Pierre-Marie Dhaussy
 * 
 */
public class WebmailMailboxMapper implements MailboxMapper<Integer> {

    /**
     * The processor
     */
    private WebmailProcessor processor = null;

    /**
     * @param processor
     */
    public WebmailMailboxMapper(final WebmailProcessor processor) {
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
     * @see org.apache.james.mailbox.store.mail.MailboxMapper#save(org.apache.james.mailbox.store.mail.model.Mailbox)
     */
    public void save(final Mailbox<Integer> mailbox) throws MailboxException {
        processor.save(mailbox);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.mail.MailboxMapper#delete(org.apache.james.mailbox.store.mail.model.Mailbox)
     */
    public void delete(final Mailbox<Integer> mailbox) throws MailboxException {
        processor.delete(mailbox);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.mail.MailboxMapper#findMailboxByPath(org.apache.james.mailbox.MailboxPath)
     */
    public Mailbox<Integer> findMailboxByPath(final MailboxPath mailboxName) throws MailboxException, MailboxNotFoundException {
        return processor.findMailboxByPath(mailboxName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.mail.MailboxMapper#findMailboxWithPathLike(org.apache.james.mailbox.MailboxPath)
     */
    public List<Mailbox<Integer>> findMailboxWithPathLike(final MailboxPath mailboxPath) throws MailboxException {
        return processor.findMailboxWithPathLike(mailboxPath);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.mail.MailboxMapper#hasChildren(org.apache.james.mailbox.store.mail.model.Mailbox, char)
     */
    public boolean hasChildren(final Mailbox<Integer> mailbox, final char delimiter) throws MailboxException, MailboxNotFoundException {
        return processor.hasChildren(mailbox, delimiter);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.mail.MailboxMapper#list()
     */
    public List<Mailbox<Integer>> list() throws MailboxException {
        return processor.list();
    }
}