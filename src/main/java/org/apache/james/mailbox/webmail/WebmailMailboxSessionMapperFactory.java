/**
 * 
 */
package org.apache.james.mailbox.webmail;

import org.apache.james.mailbox.MailboxException;
import org.apache.james.mailbox.MailboxSession;
import org.apache.james.mailbox.SubscriptionException;
import org.apache.james.mailbox.store.MailboxSessionMapperFactory;
import org.apache.james.mailbox.store.mail.MailboxMapper;
import org.apache.james.mailbox.store.mail.MessageMapper;
import org.apache.james.mailbox.store.user.SubscriptionMapper;
import org.apache.james.mailbox.webmail.mail.WebmailMailboxMapper;
import org.apache.james.mailbox.webmail.mail.WebmailMessageMapper;
import org.apache.james.mailbox.webmail.processor.WebmailProcessorFactory;
import org.apache.james.mailbox.webmail.user.WebmailSubscriptionMapper;

/**
 * @author Pierre-Marie Dhaussy
 * 
 */
public class WebmailMailboxSessionMapperFactory extends MailboxSessionMapperFactory<Integer> {

    /**
     * Factory
     */
    private WebmailProcessorFactory processorFactory = null;

    /**
     * Constructor
     * 
     * @param processorFactory factory
     */
    public WebmailMailboxSessionMapperFactory(final WebmailProcessorFactory processorFactory) {
        this.processorFactory = processorFactory;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.james.mailbox.store.MailboxSessionMapperFactory#createMailboxMapper(org.apache.james.mailbox.MailboxSession)
     */
    @Override
    protected MailboxMapper<Integer> createMailboxMapper(final MailboxSession session) throws MailboxException {
        try {
            return new WebmailMailboxMapper(processorFactory.getProcessor(WebmailUtils.getProfileName(session)));
        } catch (WebmailException e) {
            throw new MailboxException("Unable to retrieve processor", e);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.james.mailbox.store.MailboxSessionMapperFactory#createMessageMapper(org.apache.james.mailbox.MailboxSession)
     */
    @Override
    protected MessageMapper<Integer> createMessageMapper(final MailboxSession session) throws MailboxException {
        try {
            return new WebmailMessageMapper(processorFactory.getProcessor(WebmailUtils.getProfileName(session)));
        } catch (WebmailException e) {
            throw new MailboxException("Unable to retrieve processor", e);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.james.mailbox.store.MailboxSessionMapperFactory#
     *      createSubscriptionMapper(org.apache.james.mailbox.MailboxSession)
     */
    @Override
    protected SubscriptionMapper createSubscriptionMapper(final MailboxSession session) throws SubscriptionException {
        try {
            return new WebmailSubscriptionMapper(processorFactory.getProcessor(WebmailUtils.getProfileName(session)));
        } catch (WebmailException e) {
            throw new SubscriptionException(e);
        }
    }
}