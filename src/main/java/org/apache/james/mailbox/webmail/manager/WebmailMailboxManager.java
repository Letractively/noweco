/**
 * 
 */
package org.apache.james.mailbox.webmail.manager;

import org.apache.james.mailbox.MailboxException;
import org.apache.james.mailbox.MailboxPath;
import org.apache.james.mailbox.MailboxPathLocker;
import org.apache.james.mailbox.MailboxSession;
import org.apache.james.mailbox.store.Authenticator;
import org.apache.james.mailbox.store.StoreMailboxManager;
import org.apache.james.mailbox.store.StoreMessageManager;
import org.apache.james.mailbox.store.mail.MailboxMapperFactory;
import org.apache.james.mailbox.store.mail.MessageMapperFactory;
import org.apache.james.mailbox.store.mail.UidProvider;
import org.apache.james.mailbox.store.mail.model.Mailbox;
import org.apache.james.mailbox.util.MailboxEventDispatcher;
import org.apache.james.mailbox.webmail.WebmailConstants;
import org.apache.james.mailbox.webmail.model.WebmailMailbox;

/**
 * @author Pierre-Marie Dhaussy
 * 
 */
public class WebmailMailboxManager extends StoreMailboxManager<Integer> {

    /**
     * @param mailboxSessionMapperFactory
     * @param authenticator
     * @param uidProvider
     * @param locker
     */
    public WebmailMailboxManager(final MailboxMapperFactory<Integer> mailboxSessionMapperFactory, final Authenticator authenticator, final UidProvider<Integer> uidProvider,
            final MailboxPathLocker locker) {
        super(mailboxSessionMapperFactory, authenticator, uidProvider, locker);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.StoreMailboxManager#createMessageManager (org.apache.james.mailbox.store.mail.UidProvider, org.apache.james.mailbox.util.MailboxEventDispatcher,
     * org.apache.james.mailbox.store.mail.model.Mailbox, org.apache.james.mailbox.MailboxSession)
     */
    @Override
    @SuppressWarnings("unchecked")
    protected StoreMessageManager<Integer> createMessageManager(final UidProvider<Integer> uidProvider, final MailboxEventDispatcher dispatcher, final Mailbox<Integer> mailbox,
            final MailboxSession session) throws MailboxException {
        return new WebmailMessageManager((MessageMapperFactory<Integer>) mailboxSessionMapperFactory, uidProvider, dispatcher, mailbox);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.StoreMailboxManager#doCreateMailbox(org .apache.james.mailbox.MailboxPath, org.apache.james.mailbox.MailboxSession)
     */
    @Override
    protected Mailbox<Integer> doCreateMailbox(final MailboxPath mailboxPath, final MailboxSession session) throws MailboxException {
        return new WebmailMailbox(mailboxPath, session, randomUidValidity());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.StoreMailboxManager#getDelimiter()
     */
    @Override
    public char getDelimiter() {
        return WebmailConstants.ARBITRARY_FOLDER_SEPARATOR;
    }
}