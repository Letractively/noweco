/**
 * 
 */
package org.apache.james.mailbox.webmail.processor;

import org.apache.james.mailbox.store.Authenticator;
import org.apache.james.mailbox.store.mail.MailboxMapper;
import org.apache.james.mailbox.store.mail.MessageMapper;
import org.apache.james.mailbox.store.user.SubscriptionMapper;

/**
 * @author Pierre-Marie Dhaussy
 * 
 */
public interface WebmailProcessor extends Authenticator, MailboxMapper<Integer>, MessageMapper<Integer>, SubscriptionMapper {
    /**
     * Set the processor profile
     * 
     * @param profile the profile
     */
    void setProfile(WebmailProfile profile);
}
