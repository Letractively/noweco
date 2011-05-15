/**
 * 
 */
package org.apache.james.mailbox.webmail.processor;

import java.util.Properties;

import org.apache.james.mailbox.webmail.WebmailConstants;

/**
 * This POJO contain the webmail configuration like :
 * <ul>
 * <li>base URL for access plain mime messages</li>
 * <li>mailboxes (folders) char delimiter</li>
 * <li>specific parameters for particular webmail</li>
 * </ul>
 * 
 * @author Pierre-Marie Dhaussy
 */
public class WebmailProcessorConfiguration {

	/**
	 * 
	 */
	private String processorClassName = null;

	/**
	 * 
	 */
	private char folderDelimiter = WebmailConstants.ARBITRARY_FOLDER_SEPARATOR;

	/**
	 * 
	 */
	private Properties specifics = null;

	/**
	 * @return
	 */
	public String getProcessorClassName() {
		return processorClassName;
	}

	/**
	 * 
	 * @return
	 */
	public char getFolderDelimiter() {
		return folderDelimiter;
	}

	/**
	 * @return
	 */
	public Properties getSpecifics() {
		return specifics;
	}
}
