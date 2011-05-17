/**
 * 
 */
package org.apache.james.mailbox.webmail.mail;

import org.apache.james.mailbox.MailboxException;
import org.apache.james.mailbox.MailboxSession;
import org.apache.james.mailbox.store.mail.UidProvider;
import org.apache.james.mailbox.store.mail.model.Mailbox;

/**
 * @author Pierre-Marie Dhaussy
 * 
 */
public class WebmailUidProvider implements UidProvider<Integer> {

    /**
     * The last UID
     */
    private int lastUid = 0;

    /*
     * (non-Javadoc)
     * @see
     * org.apache.james.mailbox.store.mail.UidProvider#nextUid(org.apache.james
     * .mailbox.MailboxSession,
     * org.apache.james.mailbox.store.mail.model.Mailbox)
     */
    public long nextUid(final MailboxSession session, final Mailbox<Integer> mailbox) throws MailboxException {
        return ++lastUid;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.apache.james.mailbox.store.mail.UidProvider#lastUid(org.apache.james
     * .mailbox.MailboxSession,
     * org.apache.james.mailbox.store.mail.model.Mailbox)
     */
    public long lastUid(final MailboxSession session, final Mailbox<Integer> mailbox) throws MailboxException {
        return lastUid;
    }

}
