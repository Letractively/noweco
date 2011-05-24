/**
 * 
 */
package org.apache.james.mailbox.webmail.mail.model;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import javax.mail.Flags;

import org.apache.james.mailbox.store.mail.model.AbstractMessage;
import org.apache.james.mailbox.store.mail.model.Header;
import org.apache.james.mailbox.store.mail.model.Mailbox;
import org.apache.james.mailbox.store.mail.model.Property;

/**
 * @author Pierre-Marie Dhaussy
 * 
 */
public class WebmailMessage extends AbstractMessage<Integer> {

    /*
     * 
     */

    private long uid;

    private Mailbox<Integer> mailbox;

    /*
     * 
     */

    private Date internalDate;

    private String subType;

    private List<Header> headers;

    private String mediaType;

    private List<Property> properties;

    /*
     * 
     */

    private boolean modified = false;

    private boolean answered = false;

    private boolean deleted = false;

    private boolean draft = false;

    private boolean flagged = false;

    private boolean recent = false;

    private boolean seen = false;

    /*
     * 
     */

    private byte[] bodyContent;

    private byte[] fullContent;

    private Long textualLineCount;

    private long size;

    /*
     * (non-Javadoc)
     * @see
     * org.apache.james.mailbox.store.mail.model.MailboxMembership#setFlags(
     * javax.mail.Flags)
     */
    public void setFlags(final Flags flags) {
        if (flags != null) {
            answered = flags.contains(Flags.Flag.ANSWERED);
            deleted = flags.contains(Flags.Flag.DELETED);
            draft = flags.contains(Flags.Flag.DRAFT);
            flagged = flags.contains(Flags.Flag.FLAGGED);
            recent = flags.contains(Flags.Flag.RECENT);
            seen = flags.contains(Flags.Flag.SEEN);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.james.mailbox.store.mail.model.Message#getUid()
     */
    public long getUid() {
        return uid;
    }

    /**
     * @param uid the uid to set
     */
    public void setUid(final long uid) {
        this.uid = uid;
    }

    /**
     * @return the mailbox
     */
    public Mailbox<Integer> getMailbox() {
        return mailbox;
    }

    /**
     * @param mailbox the mailbox to set
     */
    public void setMailbox(final Mailbox<Integer> mailbox) {
        this.mailbox = mailbox;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.james.mailbox.store.mail.model.Message#getMailboxId()
     */
    public Integer getMailboxId() {
        return mailbox.getMailboxId();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.james.mailbox.store.mail.model.Message#isAnswered()
     */
    public boolean isAnswered() {
        return answered;
    }

    /**
     * @param answered the answered to set
     */
    public void setAnswered(final boolean answered) {
        this.answered = answered;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.james.mailbox.store.mail.model.Message#isDeleted()
     */
    public boolean isDeleted() {
        return deleted;
    }

    /**
     * @param deleted the deleted to set
     */
    public void setDeleted(final boolean deleted) {
        this.deleted = deleted;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.james.mailbox.store.mail.model.Message#isDraft()
     */
    public boolean isDraft() {
        return draft;
    }

    /**
     * @param draft the draft to set
     */
    public void setDraft(final boolean draft) {
        this.draft = draft;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.james.mailbox.store.mail.model.Message#isFlagged()
     */
    public boolean isFlagged() {
        return flagged;
    }

    /**
     * @param flagged the flagged to set
     */
    public void setFlagged(final boolean flagged) {
        this.flagged = flagged;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.james.mailbox.store.mail.model.Message#isRecent()
     */
    public boolean isRecent() {
        return recent;
    }

    /**
     * @param recent the recent to set
     */
    public void setRecent(final boolean recent) {
        this.recent = recent;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.james.mailbox.store.mail.model.Message#isSeen()
     */
    public boolean isSeen() {
        return seen;
    }

    /**
     * @param seen the seen to set
     */
    public void setSeen(final boolean seen) {
        this.seen = seen;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.james.mailbox.store.mail.model.Message#getBodyContent()
     */
    public InputStream getBodyContent() {
        return new ByteArrayInputStream(bodyContent);
    }

    /**
     * @param bodyContent the bodyContent to set
     */
    public void setBodyContent(final InputStream bodyContent) {
        // FIXME
        // this.bodyContent = bodyContent;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.james.mailbox.store.mail.model.AbstractMessage#getBodyStartOctet()
     */
    @Override
    public int getBodyStartOctet() {
        return fullContent.length - bodyContent.length;
    }

    /**
     * @param bodyStartOctet
     */
    public void setBodyStartOctet(final int bodyStartOctet) {
        // FIXME
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.james.mailbox.store.mail.model.Message#getFullContent()
     */
    public InputStream getFullContent() {
        return new ByteArrayInputStream(fullContent);
    }

    // /**
    // * @param fullContent the fullContent to set
    // */
    // public void setFullContent(final InputStream fullContent) {
    // this.fullContent = fullContent;
    // }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.james.mailbox.store.mail.model.Message#getFullContentOctets()
     */
    public long getFullContentOctets() {
        return fullContent.length;
    }

    /**
     * @return the headers
     */
    public List<Header> getHeaders() {
        return headers;
    }

    /**
     * @param headers the headers to set
     */
    public void setHeaders(final List<Header> headers) {
        this.headers = headers;
    }

    /**
     * @return the mediaType
     */
    public String getMediaType() {
        return mediaType;
    }

    /**
     * @param mediaType the mediaType to set
     */
    public void setMediaType(final String mediaType) {
        this.mediaType = mediaType;
    }

    /**
     * @return the properties
     */
    public List<Property> getProperties() {
        return properties;
    }

    /**
     * @param properties the properties to set
     */
    public void setProperties(final List<Property> properties) {
        this.properties = properties;
    }

    /**
     * @return the subType
     */
    public String getSubType() {
        return subType;
    }

    /**
     * @param subType the subType to set
     */
    public void setSubType(final String subType) {
        this.subType = subType;
    }

    /**
     * @return the textualLineCount
     */
    public Long getTextualLineCount() {
        return textualLineCount;
    }

    /**
     * @param textualLineCount the textualLineCount to set
     */
    public void setTextualLineCount(final Long textualLineCount) {
        this.textualLineCount = textualLineCount;
    }

    /**
     * @return the internalDate
     */
    public Date getInternalDate() {
        return internalDate;
    }

    /**
     * @param internalDate the internalDate to set
     */
    public void setInternalDate(final Date internalDate) {
        this.internalDate = internalDate;
    }

    /**
     * @return the size
     */
    public long getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(final long size) {
        this.size = size;
    }

    /**
     * @return the modified
     */
    public boolean isModified() {
        return modified;
    }

    /**
     * @param modified the modified to set
     */
    public void setModified(final boolean modified) {
        this.modified = modified;
    }
}