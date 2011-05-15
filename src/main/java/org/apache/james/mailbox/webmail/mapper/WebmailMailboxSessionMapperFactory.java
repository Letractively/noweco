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
import org.apache.james.mailbox.webmail.processor.WebmailProcessorFactory;

/**
 * @author Pierre-Marie Dhaussy
 * 
 */
public class WebmailMailboxSessionMapperFactory extends MailboxSessionMapperFactory<Integer> {

    /**
     * 
     */
    private static final WebmailProcessorFactory processorRegistry = WebmailProcessorFactory.getInstance();

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.MailboxSessionMapperFactory#createMailboxMapper(org.apache.james.mailbox.MailboxSession)
     */
    @Override
    protected MailboxMapper<Integer> createMailboxMapper(MailboxSession session) throws MailboxException {
        return new WebmailMailboxMapper(processorRegistry.getProcessor(session));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.MailboxSessionMapperFactory#createMessageMapper(org.apache.james.mailbox.MailboxSession)
     */
    @Override
    protected MessageMapper<Integer> createMessageMapper(MailboxSession session) throws MailboxException {
        return new WebmailMessageMapper(processorRegistry.getProcessor(session));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.MailboxSessionMapperFactory#createSubscriptionMapper(org.apache.james.mailbox.MailboxSession)
     */
    @Override
    protected SubscriptionMapper createSubscriptionMapper(MailboxSession session) throws SubscriptionException {
        return new WebmailSubscriptionMapper(processorRegistry.getProcessor(session));
    }
}