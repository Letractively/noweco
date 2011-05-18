/**
 * 
 */
package org.apache.james.mailbox.webmail.processor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.james.mailbox.webmail.WebmailException;

import com.thoughtworks.xstream.XStream;

/**
 * @author Pierre-Marie Dhaussy
 * 
 */
public class WebmailProcessorFactory {
    /**
     * profile name => Processor
     */
    private static final Map<String, WebmailProcessor> PROCESSORS = new HashMap<String, WebmailProcessor>();

    /**
     * 
     */
    private static final XStream XSTREAM = new XStream();

    /**
     * 
     */
    private String profileFilesPath = "./";

    /**
     * Constructor
     * 
     * @param profileFilesPath path to profile files directory
     */
    public WebmailProcessorFactory(final String profileFilesPath) {
        this.profileFilesPath = profileFilesPath;
        if (!this.profileFilesPath.endsWith("/")) {
            this.profileFilesPath += "/";
        }
    }

    /**
     * Return webmail profile
     * 
     * @param p_Profil webmail profile name
     * @return webmail profile
     * @throws WebmailException
     */
    private WebmailProfile loadProfile(final String profileName) throws WebmailException {
        WebmailProfile profile = null;

        String profilePath = profileFilesPath + profileName + ".xml";
        File profileFile = new File(profilePath);

        try {
            InputStream profileStream = new FileInputStream(profileFile);
            /*
             * Load configuration bean with XStream;
             */
            XSTREAM.processAnnotations(WebmailProfile.class);
            profile = (WebmailProfile) XSTREAM.fromXML(profileStream);
        } catch (FileNotFoundException e) {
            throw new WebmailException("Profile file [" + profileFile.getAbsolutePath() + "] not exists", e);
        }

        return profile;
    }

    /**
     * Create appropriate processor from profile name
     * 
     * @param session webmail session
     * @return a processor
     * @throws WebmailException
     */
    private WebmailProcessor createProcessor(final String profileName) throws WebmailException {
        WebmailProfile profile = loadProfile(profileName);

        Class<?> processorClass = null;
        try {
            processorClass = Class.forName(profile.getProcessor());
        } catch (ClassNotFoundException e) {
            throw new WebmailException("Class [" + profile.getProcessor() + "] not exists", e);
        }

        WebmailProcessor processor = null;
        try {
            processor = (WebmailProcessor) processorClass.newInstance();

        } catch (Exception e) {
            throw new WebmailException("Class [" + profile.getProcessor() + "] not exists", e);
        }

        processor.setProfile(profile);
        return processor;
    }

    /**
     * @param profileName
     * @return
     * @throws WebmailException
     */
    public WebmailProcessor getProcessor(final String profileName) throws WebmailException {
        if (!PROCESSORS.containsKey(profileName)) {
            PROCESSORS.put(profileName, createProcessor(profileName));
        }
        return PROCESSORS.get(profileName);
    }
}