package org.apache.james.mailbox.webmail.processor;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.apache.james.mailbox.webmail.WebmailException;
import org.apache.james.mailbox.webmail.processor.third.SysoutProcessor;
import org.junit.Test;

public class WebmailProcessorFactoryTest {

    /**
     * 
     */
    @Test
    public void testGetProcessor() {
        WebmailProcessorFactory factory = new WebmailProcessorFactory("src/test/resources/WebmailProcessorFactoryTest/");
        try {
            Object object = factory.getProcessor("sysout");
            assertNotNull(object);
            System.out.println(object);
            assertTrue(object instanceof WebmailProcessor);
            assertTrue(object instanceof SysoutProcessor);
        } catch (WebmailException e) {
            fail(e.getMessage());
        }
    }
}