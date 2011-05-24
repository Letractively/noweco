/**
 * 
 */
package org.apache.james.mailbox.webmail;

/**
 * @author Pierre-Marie Dhaussy
 * 
 */
/**
 * @author Pierre-Marie Dhaussy
 * 
 */
public final class WebmailConstants {

    /**
     * Constructor
     */
    private WebmailConstants() {
        // constants class
    }

    /**
     * This folder separator is converted by processor if necessary
     */
    public static final char ARBITRARY_FOLDER_SEPARATOR = '/';

    /**
     * userid : login@profil
     */
    public static final char LOGIN_PROFILE_SEPARATOR = '@';

    /**
     * New line characters
     */
    public static final char[] NEW_LINE = { 0x0D, 0x0A };
}
