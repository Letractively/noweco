/**
 * 
 */
package org.apache.james.mailbox.webmail.processor;

import java.util.HashMap;
import java.util.Map;

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
        // nothing to do
    }

    /**
     * Return webmail configuration name
     * 
     * @param p_Profil webmail profile
     * @return configuration
     */
    private WebmailProcessorConfiguration createConfiguration(final String profile) {
        /*
         * Load configuration bean with XStream;
         */
        return null;
    }

    /**
     * Create appropriate processor from profile name
     * 
     * @param session webmail session
     * @return a processor
     */
    private WebmailProcessor createProcessor(final String profile) {
        /*
         * TODO load configuration with WebmailProcessorConfigurationFactory
         */
        WebmailProcessorConfiguration configuration = createConfiguration(profile);

        /*
         * TODO load appropriate processor
         */
        WebmailProcessor processor = null;
        try {
            processor = (WebmailProcessor) Class.forName(configuration.getProcessorClassName()).newInstance();
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
     * @param profile
     * @return
     */
    public WebmailProcessor getProcessor(final String profile) {
        if (!PROCESSORS.containsKey(profile)) {
            PROCESSORS.put(profile, createProcessor(profile));
        }
        return PROCESSORS.get(profile);
    }
}