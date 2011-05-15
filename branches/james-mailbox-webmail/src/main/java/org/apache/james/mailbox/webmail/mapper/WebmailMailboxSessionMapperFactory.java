/**
 * 
 */
package org.apache.james.mailbox.webmail.mapper;

import org.apache.james.mailbox.MailboxException;
import org.apache.james.mailbox.MailboxSession;
import org.apache.james.mailbox.SubscriptionException;
import org.apache.james.mailbox.store.MailboxSessionMapperFactory;
import org.apache.james.mailbox.store.mail.MailboxMapper;
import org.apache.james.mailbox.store.mail.MessageMapper;
import org.apache.james.mailbox.store.user.SubscriptionMapper;
import org.apache.james.mailbox.webmail.WebmailUtils;
import org.apache.james.mailbox.webmail.processor.WebmailProcessorFactory;

/**
 * @author Pierre-Marie Dhaussy
 * 
 */
public class WebmailMailboxSessionMapperFactory extends MailboxSessionMapperFactory<Integer> {

    /**
     * Factory
     */
    private static final WebmailProcessorFactory processorFactory = WebmailProcessorFactory.getInstance();

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.MailboxSessionMapperFactory#createMailboxMapper(org.apache.james.mailbox.MailboxSession)
     */
    @Override
    protected MailboxMapper<Integer> createMailboxMapper(final MailboxSession session) throws MailboxException {
        return new WebmailMailboxMapper(processorFactory.getProcessor(WebmailUtils.getProfileName(session)));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.MailboxSessionMapperFactory#createMessageMapper(org.apache.james.mailbox.MailboxSession)
     */
    @Override
    protected MessageMapper<Integer> createMessageMapper(final MailboxSession session) throws MailboxException {
        return new WebmailMessageMapper(processorFactory.getProcessor(WebmailUtils.getProfileName(session)));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.MailboxSessionMapperFactory#createSubscriptionMapper(org.apache.james.mailbox.MailboxSession)
     */
    @Override
    protected SubscriptionMapper createSubscriptionMapper(final MailboxSession session) throws SubscriptionException {
        return new WebmailSubscriptionMapper(processorFactory.getProcessor(WebmailUtils.getProfileName(session)));
    }
}