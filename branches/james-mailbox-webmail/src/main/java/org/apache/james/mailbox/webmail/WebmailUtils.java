/**
 * 
 */
package org.apache.james.mailbox.webmail;

import org.apache.james.mailbox.MailboxSession;

/**
 * @author Pierre-Marie Dhaussy
 * 
 */
public final class WebmailUtils {

    /**
     * Constructor
     */
    private WebmailUtils() {
        // util class
    }

    public static String getLogin(final String userid) {
        return userid.substring(0, userid.indexOf(Character.getNumericValue(WebmailConstants.LOGIN_PROFILE_SEPARATOR)) - 1);
    }

    /**
     * @param userid
     * @return
     */
    public static String getProfileName(final String userid) {
        return userid.substring(userid.indexOf(Character.getNumericValue(WebmailConstants.LOGIN_PROFILE_SEPARATOR)) + 1);
    }

    /**
     * @param session webmail session
     * @return
     */
    public static String getProfileName(final MailboxSession session) {
        return getProfileName(session.getUser().getUserName());
    }
}
