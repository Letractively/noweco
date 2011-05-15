/**
 * 
 */
package org.apache.james.mailbox.webmail.model;

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

    private long uid;
    private Mailbox<Integer> mailbox;
    protected boolean answered;
    protected boolean deleted;
    protected boolean draft;
    protected boolean flagged;
    protected boolean recent;
    protected boolean seen;

    // Document
    private InputStream bodyContent;
    private int bodyStartOctet;
    private InputStream fullContent;
    private long fullContentOctets;
    private List<Header> headers;
    private String mediaType;
    private List<Property> properties;
    private String subType;
    private Long textualLineCount;

    // MailboxMembership
    private Date internalDate;
    private long size;

    private boolean modified = false;

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.mail.model.MailboxMembership#setFlags( javax.mail.Flags)
     */
    public void setFlags(Flags flags) {
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
     * @return the uid
     */
    public long getUid() {
        return uid;
    }

    /**
     * @param uid the uid to set
     */
    public void setUid(long uid) {
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
    public void setMailbox(Mailbox<Integer> mailbox) {
        this.mailbox = mailbox;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.mail.model.Message#getMailboxId()
     */
    public Integer getMailboxId() {
        return mailbox.getMailboxId();
    }

    /**
     * @return the answered
     */
    public boolean isAnswered() {
        return answered;
    }

    /**
     * @param answered the answered to set
     */
    public void setAnswered(boolean answered) {
        this.answered = answered;
    }

    /**
     * @return the deleted
     */
    public boolean isDeleted() {
        return deleted;
    }

    /**
     * @param deleted the deleted to set
     */
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    /**
     * @return the draft
     */
    public boolean isDraft() {
        return draft;
    }

    /**
     * @param draft the draft to set
     */
    public void setDraft(boolean draft) {
        this.draft = draft;
    }

    /**
     * @return the flagged
     */
    public boolean isFlagged() {
        return flagged;
    }

    /**
     * @param flagged the flagged to set
     */
    public void setFlagged(boolean flagged) {
        this.flagged = flagged;
    }

    /**
     * @return the recent
     */
    public boolean isRecent() {
        return recent;
    }

    /**
     * @param recent the recent to set
     */
    public void setRecent(boolean recent) {
        this.recent = recent;
    }

    /**
     * @return the seen
     */
    public boolean isSeen() {
        return seen;
    }

    /**
     * @param seen the seen to set
     */
    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    /**
     * @return the bodyContent
     */
    public InputStream getBodyContent() {
        return bodyContent;
    }

    /**
     * @param bodyContent the bodyContent to set
     */
    public void setBodyContent(InputStream bodyContent) {
        this.bodyContent = bodyContent;
    }

    /**
     * @return the bodyStartOctet
     */
    public int getBodyStartOctet() {
        return bodyStartOctet;
    }

    /**
     * @param bodyStartOctet the bodyStartOctet to set
     */
    public void setBodyStartOctet(int bodyStartOctet) {
        this.bodyStartOctet = bodyStartOctet;
    }

    /**
     * @return the fullContent
     */
    public InputStream getFullContent() {
        return fullContent;
    }

    /**
     * @param fullContent the fullContent to set
     */
    public void setFullContent(InputStream fullContent) {
        this.fullContent = fullContent;
    }

    /**
     * @return the fullContentOctets
     */
    public long getFullContentOctets() {
        return fullContentOctets;
    }

    /**
     * @param fullContentOctets the fullContentOctets to set
     */
    public void setFullContentOctets(long fullContentOctets) {
        this.fullContentOctets = fullContentOctets;
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
    public void setHeaders(List<Header> headers) {
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
    public void setMediaType(String mediaType) {
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
    public void setProperties(List<Property> properties) {
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
    public void setSubType(String subType) {
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
    public void setTextualLineCount(Long textualLineCount) {
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
    public void setInternalDate(Date internalDate) {
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
    public void setSize(long size) {
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
    public void setModified(boolean modified) {
        this.modified = modified;
    }
}