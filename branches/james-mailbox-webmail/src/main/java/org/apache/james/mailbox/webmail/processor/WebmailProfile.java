/**
 * 
 */
package org.apache.james.mailbox.webmail.processor;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * This POJO contain the webmail profile like :
 * <ul>
 * <li>the webmail corresponding processor</li>
 * <li>base URL for access plain mime messages</li>
 * <li>mailboxes (folders) char delimiter</li>
 * <li>specific parameters for particular webmail</li>
 * </ul>
 * 
 * @author Pierre-Marie Dhaussy
 */
@XStreamAlias("profile")
public class WebmailProfile {

    /**
     * 
     */
    @XStreamAlias("name")
    private String name = null;

    /**
     * 
     */
    @XStreamAlias("description")
    private String description = null;

    /**
     * 
     */
    @XStreamAlias("processor")
    private String processor = null;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * @return the processor
     */
    public String getProcessor() {
        return processor;
    }

    /**
     * @param processor the processor to set
     */
    public void setProcessor(final String processor) {
        this.processor = processor;
    }
}