/**
 * 
 */
package org.apache.james.mailbox.webmail.mapper;

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
     * 
     */
    private WebmailProcessor processor = null;
    
    /**
     * @param processor
     */
    public WebmailMailboxMapper(WebmailProcessor processor) {
        this.processor = processor;
    }
    
    /* (non-Javadoc)
     * @see org.apache.james.mailbox.store.transaction.Mapper#endRequest()
     */
    public void endRequest() {
        processor.endRequest();
    }

    /* (non-Javadoc)
     * @see org.apache.james.mailbox.store.transaction.Mapper#execute(org.apache.james.mailbox.store.transaction.Mapper.Transaction)
     */
    public <T> T execute(Transaction<T> transaction) throws MailboxException {
        return processor.execute(transaction);
    }

    /* (non-Javadoc)
     * @see org.apache.james.mailbox.store.mail.MailboxMapper#save(org.apache.james.mailbox.store.mail.model.Mailbox)
     */
    public void save(Mailbox<Integer> mailbox) throws MailboxException {
        processor.save(mailbox);
    }

    /* (non-Javadoc)
     * @see org.apache.james.mailbox.store.mail.MailboxMapper#delete(org.apache.james.mailbox.store.mail.model.Mailbox)
     */
    public void delete(Mailbox<Integer> mailbox) throws MailboxException {
        processor.delete(mailbox);
    }

    /* (non-Javadoc)
     * @see org.apache.james.mailbox.store.mail.MailboxMapper#findMailboxByPath(org.apache.james.mailbox.MailboxPath)
     */
    public Mailbox<Integer> findMailboxByPath(MailboxPath mailboxName) throws MailboxException, MailboxNotFoundException {
        return processor.findMailboxByPath(mailboxName);
    }

    /* (non-Javadoc)
     * @see org.apache.james.mailbox.store.mail.MailboxMapper#findMailboxWithPathLike(org.apache.james.mailbox.MailboxPath)
     */
    public List<Mailbox<Integer>> findMailboxWithPathLike(MailboxPath mailboxPath) throws MailboxException {
        return processor.findMailboxWithPathLike(mailboxPath);
    }

    /* (non-Javadoc)
     * @see org.apache.james.mailbox.store.mail.MailboxMapper#hasChildren(org.apache.james.mailbox.store.mail.model.Mailbox, char)
     */
    public boolean hasChildren(Mailbox<Integer> mailbox, char delimiter) throws MailboxException, MailboxNotFoundException {
        return processor.hasChildren(mailbox, delimiter);
    }

    /* (non-Javadoc)
     * @see org.apache.james.mailbox.store.mail.MailboxMapper#list()
     */
    public List<Mailbox<Integer>> list() throws MailboxException {
        return processor.list();
    }
}