/**
 * 
 */
package org.apache.james.mailbox.webmail.processor;

import org.apache.james.mailbox.MailboxException;

/**
 * @author Pierre-Marie Dhaussy
 * 
 */
public abstract class WebmailAbstractProcessor implements WebmailProcessor {

	/**
     * 
     */
	protected WebmailProfile profile = null;

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.apache.james.mailbox.webmail.processor.WebmailProcessor#setProfile(org.apache.james.mailbox.webmail.processor.WebmailProfile)
	 */
	public void setProfile(final WebmailProfile profile) {
		this.profile = profile;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.apache.james.mailbox.store.transaction.Mapper#execute(org.apache.james.mailbox.store.transaction.Mapper.Transaction)
	 */
	public <T> T execute(final Transaction<T> transaction)
			throws MailboxException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.apache.james.mailbox.store.transaction.Mapper#endRequest()
	 */
	public void endRequest() {
		//
	}
}