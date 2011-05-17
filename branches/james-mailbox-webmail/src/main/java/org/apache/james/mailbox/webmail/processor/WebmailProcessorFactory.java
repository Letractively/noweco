/**
 * 
 */
package org.apache.james.mailbox.webmail.processor;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.james.mailbox.webmail.WebmailConstants;

import com.thoughtworks.xstream.XStream;

/**
 * @author Pierre-Marie Dhaussy
 * 
 */
public class WebmailProcessorFactory {
    /**
     * The unique instance of the singleton
     */
    private static final WebmailProcessorFactory INSTANCE = new WebmailProcessorFactory();

    /**
     * Profile profile => Processor
     */
    private static final Map<String, WebmailProcessor> PROCESSORS = new HashMap<String, WebmailProcessor>();

    /**
     * 
     */
    private static final XStream XSTREAM = new XStream();

    /**
     * Return the unique instance of the singleton
     * 
     * @return the instance of {@link WebmailProcessorFactory}
     */
    public static WebmailProcessorFactory getInstance() {
        return INSTANCE;
    }

    /**
     * Singleton infer private constructor
     */
    private WebmailProcessorFactory() {
        //
    }

    /**
     * Return webmail profile
     * 
     * @param p_Profil webmail profile name
     * @return webmail profile
     */
    private WebmailProfile loadProfile(final String profileName) {
        String profilePath = "./" + WebmailConstants.PROFILE_DIRECTORY + profileName + ".xml";
        File profileFile = new File(profilePath);

        if (!profileFile.exists()) {
            // TODO throw Some exception
            System.out.println("Profile file [" + profileFile.getAbsolutePath() + "] not exists");
        }
        /*
         * Load configuration bean with XStream;
         */
        XSTREAM.processAnnotations(WebmailProfile.class);
        return (WebmailProfile) XSTREAM.fromXML(profileFile.getAbsolutePath());
    }

    /**
     * Create appropriate processor from profile name
     * 
     * @param session webmail session
     * @return a processor
     */
    private WebmailProcessor createProcessor(final String profileName) {
        /*
         * TODO load configuration with WebmailProcessorConfigurationFactory
         */
        WebmailProfile profile = loadProfile(profileName);

        /*
         * TODO load appropriate processor
         */
        WebmailProcessor processor = null;
        try {
            processor = (WebmailProcessor) Class.forName(profile.getProcessor()).newInstance();
            processor.setProfile(profile);
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return processor;
    }

    /**
     * @param profileName
     * @return
     */
    public WebmailProcessor getProcessor(final String profileName) {
        if (!PROCESSORS.containsKey(profileName)) {
            PROCESSORS.put(profileName, createProcessor(profileName));
        }
        return PROCESSORS.get(profileName);
    }
}