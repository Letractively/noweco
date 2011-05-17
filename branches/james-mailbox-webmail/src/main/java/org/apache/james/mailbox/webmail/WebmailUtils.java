/**
 * 
 */
package org.apache.james.mailbox.webmail;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.james.mailbox.MailboxSession;

/**
 * @author Pierre-Marie Dhaussy
 * 
 */
public final class WebmailUtils {

    /**
     * 
     */
    private static final String REGEXP = "([^@]+)@([^@]+)";

    /**
     * 
     */
    private static final Pattern PATTERN = Pattern.compile(REGEXP);

    /**
     * Constructor
     */
    private WebmailUtils() {
        // util class
    }

    public static String getLogin(final String userid) {
        String login = null;
        Matcher matcher = PATTERN.matcher(userid);
        if (matcher.matches()) {
            login = matcher.group(1);
        }
        return login;
    }

    /**
     * @param userid
     * @return
     */
    public static String getProfileName(final String userid) {
        String profileName = null;
        Matcher matcher = PATTERN.matcher(userid);
        if (matcher.matches()) {
            profileName = matcher.group(2);
        }
        return profileName;
    }

    /**
     * @param session webmail session
     * @return
     */
    public static String getProfileName(final MailboxSession session) {
        return getProfileName(session.getUser().getUserName());
    }
}
