/**
 * 
 */
package org.apache.james.mailbox.webmail.mail.model;

import org.apache.james.mailbox.store.mail.model.AbstractComparableProperty;
import org.apache.james.mailbox.store.mail.model.Property;

/**
 * @author Pierre-Marie Dhaussy
 * 
 */
public class WebmailProperty extends AbstractComparableProperty<WebmailProperty> {

    private int order;

    private String namespace;

    private String localName;

    private String value;

    public WebmailProperty(final String namespace, final String localName, final String value, final int order) {
        this.namespace = namespace;
        this.localName = localName;
        this.value = value;
        this.order = order;
    }

    public WebmailProperty(final Property property, final int order) {
        this(property.getNamespace(), property.getLocalName(), property.getValue(), order);
    }

    /**
     * @return the order
     */
    @Override
    public int getOrder() {
        return order;
    }

    /**
     * @param order the order to set
     */
    public void setOrder(final int order) {
        this.order = order;
    }

    /**
     * @return the namespace
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * @param namespace the namespace to set
     */
    public void setNamespace(final String namespace) {
        this.namespace = namespace;
    }

    /**
     * @return the localName
     */
    public String getLocalName() {
        return localName;
    }

    /**
     * @param localName the localName to set
     */
    public void setLocalName(final String localName) {
        this.localName = localName;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(final String value) {
        this.value = value;
    }
}