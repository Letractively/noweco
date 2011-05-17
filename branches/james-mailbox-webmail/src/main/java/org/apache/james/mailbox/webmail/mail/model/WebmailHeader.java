/**
 * 
 */
package org.apache.james.mailbox.webmail.mail.model;

import org.apache.james.mailbox.store.mail.model.AbstractComparableHeader;

/**
 * @author Pierre-Marie Dhaussy
 * 
 */
public class WebmailHeader extends AbstractComparableHeader {
    /**
     * 
     */
    private int lineNumber = -1;

    /**
     * 
     */
    private String fieldName = null;

    /**
     * 
     */
    private String value = null;

    /**
     * @param lineNumber the lineNumber to set
     */
    public void setLineNumber(final int lineNumber) {
        this.lineNumber = lineNumber;
    }

    /**
     * @param fieldName the fieldName to set
     */
    public void setFieldName(final String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * @param value the value to set
     */
    public void setValue(final String value) {
        this.value = value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.mail.model.Header#getFieldName()
     */
    public String getFieldName() {
        return fieldName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.mail.model.Header#getLineNumber()
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.mailbox.store.mail.model.Header#getValue()
     */
    public String getValue() {
        return value;
    }
}