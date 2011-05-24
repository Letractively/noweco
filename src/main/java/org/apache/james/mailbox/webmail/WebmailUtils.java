/**
 * 
 */
package org.apache.james.mailbox.webmail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.james.mailbox.MailboxSession;
import org.apache.james.mailbox.store.mail.model.Header;

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

    public static ByteArrayOutputStream headersToOutputStream(final List<? extends Header> headers) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            final Writer writer = new OutputStreamWriter(baos, "us-ascii");
            for (Header header : headers) {
                writer.write(header.getFieldName());
                writer.write(": ");
                writer.write(header.getValue());
                writer.write(WebmailConstants.NEW_LINE);
            }
            writer.write(WebmailConstants.NEW_LINE);
            writer.flush();
            baos.flush();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return baos;
    }

    public static InputStream headersToInputStream(final List<? extends Header> headers) {
        return new ByteArrayInputStream(headersToOutputStream(headers).toByteArray());
    }

    public static byte[] headersToByteArray(final List<? extends Header> headers) {
        return headersToOutputStream(headers).toByteArray();
    }

    public static ByteArrayOutputStream bodyToOutputStream(final String body) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            final Writer writer = new OutputStreamWriter(baos, "us-ascii");
            writer.write(body);
            writer.write(WebmailConstants.NEW_LINE);
            writer.flush();
            baos.flush();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return baos;
    }

    public static InputStream bodyToInputStream(final String body) {
        return new ByteArrayInputStream(bodyToOutputStream(body).toByteArray());
    }

    public static byte[] bodyToByteArray(final String body) {
        return bodyToOutputStream(body).toByteArray();
    }
}