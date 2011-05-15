/**
 * 
 */
package org.apache.james.mailbox.webmail.model;

import org.apache.james.mailbox.MailboxPath;
import org.apache.james.mailbox.MailboxSession;
import org.apache.james.mailbox.store.mail.model.Mailbox;

/**
 * @author Pierre-Marie Dhaussy
 * 
 */
public class WebmailMailbox implements Mailbox<Integer> {

    /**
     * id
     */
    private Integer id = null;

    /**
     * 
     */
    private String name = null;

    /**
     * 
     */
    private String user = null;

    /**
     * 
     */
    private String namespace = null;

    /**
     * 
     */
    private int uidValidity = 0;

    /**
     * @param mailboxPath
     * @param session
     * @param randomUidValidity
     */
    public WebmailMailbox(final MailboxPath mailboxPath, final MailboxSession session, final int uidValidity) {
        this.uidValidity = uidValidity;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.mail.model.Mailbox#getMailboxId()
     */
    public Integer getMailboxId() {
        return id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.mail.model.Mailbox#getNamespace()
     */
    public String getNamespace() {
        return namespace;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.mail.model.Mailbox#setNamespace(java.lang.String)
     */
    public void setNamespace(final String namespace) {
        this.namespace = namespace;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.mail.model.Mailbox#getUser()
     */
    public String getUser() {
        return user;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.mail.model.Mailbox#setUser(java.lang.String)
     */
    public void setUser(final String user) {
        this.user = user;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.mail.model.Mailbox#getName()
     */
    public String getName() {
        return name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.mail.model.Mailbox#setName(java.lang.String)
     */
    public void setName(final String name) {
        this.name = name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.mail.model.Mailbox#getUidValidity()
     */
    public long getUidValidity() {
        return uidValidity;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof WebmailMailbox) {
            if (id != null) {
                if (id.equals(((WebmailMailbox) obj).getMailboxId())) {
                    return true;
                }
            } else {
                if (((WebmailMailbox) obj).getMailboxId() == null) {
                    return true;
                }
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + namespace.hashCode();
        result = PRIME * result + user.hashCode();
        result = PRIME * result + name.hashCode();
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return namespace + ":" + user + ":" + name;
    }
}