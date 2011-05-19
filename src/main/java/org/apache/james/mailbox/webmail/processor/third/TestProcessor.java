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
import org.apache.james.mailbox.webmail.mail.model.WebmailMailbox;
import org.apache.james.mailbox.webmail.mail.model.WebmailMessage;
import org.apache.james.mailbox.webmail.processor.WebmailAbstractProcessor;
import org.apache.james.mailbox.webmail.user.model.WebmailSubscription;

/**
 * @author Pierre-Marie Dhaussy
 * 
 */
public class TestProcessor extends WebmailAbstractProcessor {
	
	/**
	 * 
	 */
	WebmailMailbox mailbox = null;
	
	/**
	 * 
	 */
	List<Mailbox<Integer>> mailboxesList = new ArrayList<Mailbox<Integer>>();
	
	/**
	 * 
	 */
	WebmailMessage message = null;
	
	/**
	 * 
	 */
	List<Message<Integer>> messageList = new ArrayList<Message<Integer>>();
	
	/**
	 * 
	 */
	WebmailSubscription subscription = null;
	
	/**
	 * 
	 */
	List<Subscription> subscriptionsList = new ArrayList<Subscription>();
	
	/**
	 * Constructor
	 */
	public TestProcessor() {
		mailbox = new WebmailMailbox(null, null, 0);
		mailbox.setName("INBOX");
		mailboxesList.add(mailbox);
		
		message = new WebmailMessage();
		message.setUid(0);
		
		subscription = new WebmailSubscription("test", mailbox.getName());
	}
	
	/*
	 * Log tools
	 */
	
	private String log(final Mailbox<Integer> mailbox) {
        return mailbox.toString();
    }

    private String log(final MailboxPath mailboxPath) {
        return mailboxPath.toString();
    }

    private String log(final Message<Integer> message) {
        return message.toString();
    }

    /*
     * AUTHENTICATION
     */

    public boolean isAuthentic(final String userid, final CharSequence passwd) {
        System.out.println("TestProcessor.isAuthentic(" + new Object[] { userid, passwd } + ")");
        return !passwd.equals("fail");
    }

    /*
     * MAILBOX
     */

    public Mailbox<Integer> findMailboxByPath(final MailboxPath mailboxPath) throws MailboxException, MailboxNotFoundException {
        System.out.println("TestProcessor.findMailboxByPath(" + log(mailboxPath) + ")");
        return mailbox;
    }

    public void save(final Mailbox<Integer> mailbox) throws MailboxException {
        System.out.println("TestProcessor.save(" + log(mailbox) + ")");
    }

    public void delete(final Mailbox<Integer> mailbox) throws MailboxException {
        System.out.println("TestProcessor.delete(" + log(mailbox) + ")");
    }

    public List<Mailbox<Integer>> findMailboxWithPathLike(final MailboxPath mailboxPath) throws MailboxException {
        System.out.println("TestProcessor.findMailboxWithPathLike(" + log(mailboxPath) + ")");
        return mailboxesList;
    }

    public boolean hasChildren(final Mailbox<Integer> mailbox, final char delimiter) throws MailboxException, MailboxNotFoundException {
        System.out.println("TestProcessor.hasChildren(" + new Object[] { log(mailbox), delimiter } + ")");
        return false;
    }

    public List<Mailbox<Integer>> list() throws MailboxException {
        System.out.println("TestProcessor.list()");
        return mailboxesList;
    }

    /*
     * MESSAGE
     */

    public void findInMailbox(final Mailbox<Integer> mailbox, final MessageRange set, final MailboxMembershipCallback<Integer> callback) throws MailboxException {
        System.out.println("TestProcessor.findInMailbox(" + new Object[] { log(mailbox), set, callback } + ")");
    }

    public Iterator<Long> expungeMarkedForDeletionInMailbox(final Mailbox<Integer> mailbox, final MessageRange set) throws MailboxException {
        System.out.println("TestProcessor.expungeMarkedForDeletionInMailbox(" + new Object[] { log(mailbox), set } + ")");
        return null;
    }

    public long countMessagesInMailbox(final Mailbox<Integer> mailbox) throws MailboxException {
        System.out.println("TestProcessor.countMessagesInMailbox(" + log(mailbox) + ")");
        return 1;
    }

    public long countUnseenMessagesInMailbox(final Mailbox<Integer> mailbox) throws MailboxException {
        System.out.println("TestProcessor.countUnseenMessagesInMailbox(" + log(mailbox) + ")");
        return 1;
    }

    public void delete(final Mailbox<Integer> mailbox, final Message<Integer> message) throws MailboxException {
        System.out.println("TestProcessor.delete(" + new Object[] { log(mailbox), log(message) } + ")");
    }

    public Long findFirstUnseenMessageUid(final Mailbox<Integer> mailbox) throws MailboxException {
        System.out.println("TestProcessor.findFirstUnseenMessageUid(" + log(mailbox) + ")");
        return 0L;
    }

    public List<Message<Integer>> findRecentMessagesInMailbox(final Mailbox<Integer> mailbox) throws MailboxException {
        System.out.println("TestProcessor.findRecentMessagesInMailbox(" + log(mailbox) + ")");
        return messageList;
    }

    public long add(final Mailbox<Integer> mailbox, final Message<Integer> message) throws MailboxException {
        System.out.println("TestProcessor.add(" + new Object[] { log(mailbox), log(message) } + ")");
        return 0;
    }

    public Iterator<UpdatedFlags> updateFlags(final Mailbox<Integer> mailbox, final Flags flags, final boolean value, final boolean replace, final MessageRange set) throws MailboxException {
        System.out.println("TestProcessor.updateFlags(" + new Object[] { log(mailbox), flags, value, replace, set } + ")");
        return null;
    }

    public long copy(final Mailbox<Integer> mailbox, final long uid, final Message<Integer> original) throws MailboxException {
        System.out.println("TestProcessor.copy(" + new Object[] { log(mailbox), uid, original } + ")");
        return 0;
    }

    /*
     * SUBSCRIPTION
     */

    public Subscription findMailboxSubscriptionForUser(final String user, final String mailbox) throws SubscriptionException {
        System.out.println("TestProcessor.findMailboxSubscriptionForUser(" + new Object[] { user, mailbox } + ")");
        return subscription;
    }

    public void save(final Subscription subscription) throws SubscriptionException {
        System.out.println("TestProcessor.save(" + subscription + ")");
    }

    public List<Subscription> findSubscriptionsForUser(final String user) throws SubscriptionException {
        System.out.println("TestProcessor.findSubscriptionsForUser(" + user + ")");
        return new ArrayList<Subscription>();
    }

    public void delete(final Subscription subscription) throws SubscriptionException {
        System.out.println("TestProcessor.delete(" + subscription + ")");
    }
}