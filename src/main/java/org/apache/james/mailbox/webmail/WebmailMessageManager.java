/**
 * 
 */
package org.apache.james.mailbox.webmail;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

import javax.mail.Flags;

import org.apache.james.mailbox.MailboxException;
import org.apache.james.mailbox.store.StoreMessageManager;
import org.apache.james.mailbox.store.mail.MessageMapperFactory;
import org.apache.james.mailbox.store.mail.UidProvider;
import org.apache.james.mailbox.store.mail.model.Header;
import org.apache.james.mailbox.store.mail.model.Mailbox;
import org.apache.james.mailbox.store.mail.model.Message;
import org.apache.james.mailbox.store.mail.model.PropertyBuilder;
import org.apache.james.mailbox.util.MailboxEventDispatcher;
import org.apache.james.mailbox.webmail.mail.model.WebmailHeader;
import org.apache.james.mailbox.webmail.mail.model.WebmailMessage;

/**
 * @author Pierre-Marie Dhaussy
 * 
 */
public class WebmailMessageManager extends StoreMessageManager<Integer> {

    public WebmailMessageManager(final MessageMapperFactory<Integer> mapperFactory, final UidProvider<Integer> uidProvider, final MailboxEventDispatcher dispatcher, final Mailbox<Integer> mailbox)
            throws MailboxException {
        super(mapperFactory, uidProvider, dispatcher, mailbox);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.StoreMessageManager#createMessage(long, java.util.Date, int, int, java.io.InputStream, javax.mail.Flags, java.util.List,
     * org.apache.james.mailbox.store.mail.model.PropertyBuilder)
     */
    @Override
    protected Message<Integer> createMessage(final long uid, final Date internalDate, final int size, final int bodyStartOctet, final InputStream documentIn, final Flags flags,
            final List<Header> headers, final PropertyBuilder propertyBuilder) throws MailboxException {
        WebmailMessage message = new WebmailMessage();
        message.setUid(uid);
        message.setInternalDate(internalDate);
        message.setSize(size);
        message.setBodyStartOctet(bodyStartOctet);
        message.setBodyContent(documentIn);
        message.setFlags(flags);
        message.setHeaders(headers);
        message.setProperties(propertyBuilder.toProperties());
        return message;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.StoreMessageManager#createHeader(int, java.lang.String, java.lang.String)
     */
    @Override
    protected Header createHeader(final int lineNumber, final String fieldName, final String value) {
        WebmailHeader header = new WebmailHeader();
        header.setLineNumber(lineNumber);
        header.setFieldName(fieldName);
        header.setValue(value);
        return header;
    }
}