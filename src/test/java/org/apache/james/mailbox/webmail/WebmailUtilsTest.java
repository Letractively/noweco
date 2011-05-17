package org.apache.james.mailbox.webmail;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.james.mailbox.MailboxSession;
import org.junit.Test;
import org.slf4j.Logger;

public class WebmailUtilsTest {

    /**
     * 
     */
    private static final String USERID = "login@profile";

    @Test
    public void testGetLogin() {
        assertEquals("login", WebmailUtils.getLogin(USERID));
    }

    @Test
    public void testGetProfileNameString() {
        assertEquals("profile", WebmailUtils.getProfileName(USERID));
    }

    @Test
    public void testGetProfileNameMailboxSession() {
        MailboxSession mailboxSession = new MailboxSession() {

            public boolean isOpen() {
                // TODO Auto-generated method stub
                return false;
            }

            public User getUser() {
                return new User() {

                    public String getUserName() {
                        return USERID;
                    }

                    public String getPassword() {
                        // TODO Auto-generated method stub
                        return null;
                    }

                    public List<Locale> getLocalePreferences() {
                        // TODO Auto-generated method stub
                        return null;
                    }
                };
            }

            public SessionType getType() {
                // TODO Auto-generated method stub
                return null;
            }

            public Collection<String> getSharedSpaces() {
                // TODO Auto-generated method stub
                return null;
            }

            public long getSessionId() {
                // TODO Auto-generated method stub
                return 0;
            }

            public String getPersonalSpace() {
                // TODO Auto-generated method stub
                return null;
            }

            public char getPathDelimiter() {
                // TODO Auto-generated method stub
                return 0;
            }

            public String getOtherUsersSpace() {
                // TODO Auto-generated method stub
                return null;
            }

            public Logger getLog() {
                // TODO Auto-generated method stub
                return null;
            }

            public Map<Object, Object> getAttributes() {
                // TODO Auto-generated method stub
                return null;
            }

            public void close() {
                // TODO Auto-generated method stub

            }
        };
        assertEquals("profile", WebmailUtils.getProfileName(mailboxSession));
    }
}