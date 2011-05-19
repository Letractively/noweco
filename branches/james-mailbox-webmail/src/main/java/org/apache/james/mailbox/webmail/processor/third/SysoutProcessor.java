/**
 * 
 */
package org.apache.james.mailbox.webmail.processor.third;

import java.util.ArrayList;
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
import org.apache.james.mailbox.webmail.processor.WebmailAbstractProcessor;

/**
 * @author Pierre-Marie Dhaussy
 * 
 */
public class SysoutProcessor extends WebmailAbstractProcessor {

    private String log(final Mailbox<Integer> mailbox) {
        return mailbox.toString();
    }

    private String log(final MailboxPath mailboxPath) {
        return mailboxPath.toString();
    }

    private String log(final Message<Integer> message) {
        return message.toString();
    }

    public boolean isAuthentic(final String userid, final CharSequence passwd) {
        System.out.println("SysoutProcessor.isAuthentic(" + new Object[] { userid, passwd } + ")");
        return passwd.equals("true");
    }

    public void save(final Mailbox<Integer> mailbox) throws MailboxException {
        System.out.println("SysoutProcessor.save(" + log(mailbox) + ")");
    }

    public void delete(final Mailbox<Integer> mailbox) throws MailboxException {
        System.out.println("SysoutProcessor.delete(" + log(mailbox) + ")");
    }

    public Mailbox<Integer> findMailboxByPath(final MailboxPath mailboxPath) throws MailboxException, MailboxNotFoundException {
        System.out.println("SysoutProcessor.findMailboxByPath(" + log(mailboxPath) + ")");
        return null;
    }

    public List<Mailbox<Integer>> findMailboxWithPathLike(final MailboxPath mailboxPath) throws MailboxException {
        System.out.println("SysoutProcessor.findMailboxWithPathLike(" + log(mailboxPath) + ")");
        return new ArrayList<Mailbox<Integer>>();
    }

    public boolean hasChildren(final Mailbox<Integer> mailbox, final char delimiter) throws MailboxException, MailboxNotFoundException {
        System.out.println("SysoutProcessor.hasChildren(" + new Object[] { log(mailbox), delimiter } + ")");
        return false;
    }

    public List<Mailbox<Integer>> list() throws MailboxException {
        System.out.println("SysoutProcessor.list()");
        return new ArrayList<Mailbox<Integer>>();
    }

    @Override
    public void endRequest() {
        System.out.println("SysoutProcessor.endRequest()");
    }

    @Override
    public <T> T execute(final Transaction<T> transaction) throws MailboxException {
        System.out.println("SysoutProcessor.execute(" + transaction + ")");
        return null;
    }

    public void findInMailbox(final Mailbox<Integer> mailbox, final MessageRange set, final MailboxMembershipCallback<Integer> callback) throws MailboxException {
        System.out.println("SysoutProcessor.findInMailbox(" + new Object[] { log(mailbox), set, callback } + ")");
    }

    public Iterator<Long> expungeMarkedForDeletionInMailbox(final Mailbox<Integer> mailbox, final MessageRange set) throws MailboxException {
        System.out.println("SysoutProcessor.expungeMarkedForDeletionInMailbox(" + new Object[] { log(mailbox), set } + ")");
        return null;
    }

    public long countMessagesInMailbox(final Mailbox<Integer> mailbox) throws MailboxException {
        System.out.println("SysoutProcessor.countMessagesInMailbox(" + log(mailbox) + ")");
        return 0;
    }

    public long countUnseenMessagesInMailbox(final Mailbox<Integer> mailbox) throws MailboxException {
        System.out.println("SysoutProcessor.countUnseenMessagesInMailbox(" + log(mailbox) + ")");
        return 0;
    }

    public void delete(final Mailbox<Integer> mailbox, final Message<Integer> message) throws MailboxException {
        System.out.println("SysoutProcessor.delete(" + new Object[] { log(mailbox), log(message) } + ")");
    }

    public Long findFirstUnseenMessageUid(final Mailbox<Integer> mailbox) throws MailboxException {
        System.out.println("SysoutProcessor.findFirstUnseenMessageUid(" + log(mailbox) + ")");
        return null;
    }

    public List<Message<Integer>> findRecentMessagesInMailbox(final Mailbox<Integer> mailbox) throws MailboxException {
        System.out.println("SysoutProcessor.findRecentMessagesInMailbox(" + log(mailbox) + ")");
        return new ArrayList<Message<Integer>>();
    }

    public long add(final Mailbox<Integer> mailbox, final Message<Integer> message) throws MailboxException {
        System.out.println("SysoutProcessor.add(" + new Object[] { log(mailbox), log(message) } + ")");
        return 0;
    }

    public Iterator<UpdatedFlags> updateFlags(final Mailbox<Integer> mailbox, final Flags flags, final boolean value, final boolean replace, final MessageRange set) throws MailboxException {
        System.out.println("SysoutProcessor.updateFlags(" + new Object[] { log(mailbox), flags, value, replace, set } + ")");
        return null;
    }

    public long copy(final Mailbox<Integer> mailbox, final long uid, final Message<Integer> original) throws MailboxException {
        System.out.println("SysoutProcessor.copy(" + new Object[] { log(mailbox), uid, original } + ")");
        return 0;
    }

    public Subscription findMailboxSubscriptionForUser(final String user, final String mailbox) throws SubscriptionException {
        System.out.println("SysoutProcessor.findMailboxSubscriptionForUser(" + new Object[] { user, mailbox } + ")");
        return null;
    }

    public void save(final Subscription subscription) throws SubscriptionException {
        System.out.println("SysoutProcessor.save(" + subscription + ")");
    }

    public List<Subscription> findSubscriptionsForUser(final String user) throws SubscriptionException {
        System.out.println("SysoutProcessor.findSubscriptionsForUser(" + user + ")");
        return new ArrayList<Subscription>();
    }

    public void delete(final Subscription subscription) throws SubscriptionException {
        System.out.println("SysoutProcessor.delete(" + subscription + ")");
    }
}