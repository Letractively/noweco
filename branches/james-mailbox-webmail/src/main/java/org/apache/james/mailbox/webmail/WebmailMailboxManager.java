/**
 * 
 */
package org.apache.james.mailbox.webmail;

import org.apache.james.mailbox.MailboxException;
import org.apache.james.mailbox.MailboxPath;
import org.apache.james.mailbox.MailboxPathLocker;
import org.apache.james.mailbox.MailboxSession;
import org.apache.james.mailbox.store.Authenticator;
import org.apache.james.mailbox.store.JVMMailboxPathLocker;
import org.apache.james.mailbox.store.StoreMailboxManager;
import org.apache.james.mailbox.store.StoreMessageManager;
import org.apache.james.mailbox.store.mail.MailboxMapperFactory;
import org.apache.james.mailbox.store.mail.MessageMapperFactory;
import org.apache.james.mailbox.store.mail.UidProvider;
import org.apache.james.mailbox.store.mail.model.Mailbox;
import org.apache.james.mailbox.util.MailboxEventDispatcher;
import org.apache.james.mailbox.webmail.mail.WebmailUidProvider;
import org.apache.james.mailbox.webmail.mail.model.WebmailMailbox;

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

    /**
     * @param mailboxSessionMapperFactory
     * @param authenticator
     * @param uidProvider
     */
    public WebmailMailboxManager(final MailboxMapperFactory<Integer> mailboxSessionMapperFactory, final Authenticator authenticator) {
        this(mailboxSessionMapperFactory, authenticator, new WebmailUidProvider(), new JVMMailboxPathLocker());
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.james.mailbox.store.StoreMailboxManager#createMessageManager
     *      (org.apache.james.mailbox.store.mail.UidProvider,
     *      org.apache.james.mailbox.util.MailboxEventDispatcher,
     *      org.apache.james.mailbox.store.mail.model.Mailbox,
     *      org.apache.james.mailbox.MailboxSession)
     */
    @Override
    @SuppressWarnings("unchecked")
    protected StoreMessageManager<Integer> createMessageManager(final UidProvider<Integer> uidProvider, final MailboxEventDispatcher dispatcher, final Mailbox<Integer> mailbox,
            final MailboxSession session) throws MailboxException {
        return new WebmailMessageManager((MessageMapperFactory<Integer>) mailboxSessionMapperFactory, uidProvider, dispatcher, mailbox);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.james.mailbox.store.StoreMailboxManager#doCreateMailbox(org
     *      .apache.james.mailbox.MailboxPath,
     *      org.apache.james.mailbox.MailboxSession)
     */
    @Override
    protected Mailbox<Integer> doCreateMailbox(final MailboxPath mailboxPath, final MailboxSession session) throws MailboxException {
        return new WebmailMailbox(mailboxPath, session, randomUidValidity());
    }
}