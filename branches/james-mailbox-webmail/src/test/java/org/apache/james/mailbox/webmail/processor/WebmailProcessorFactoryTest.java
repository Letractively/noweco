package org.apache.james.mailbox.webmail.processor;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
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
            assertTrue(object instanceof WebmailProcessor);
            assertTrue(object instanceof SysoutProcessor);
        } catch (WebmailException e) {
            fail(e.getMessage());
        }
    }

    /**
     * 
     */
    @Test
    public void testGetSameProcessor() {
        WebmailProcessorFactory factory = new WebmailProcessorFactory("src/test/resources/WebmailProcessorFactoryTest/");
        try {
            Object processor1 = factory.getProcessor("sysout");
            assertNotNull(processor1);
            assertTrue(processor1 instanceof WebmailProcessor);
            assertTrue(processor1 instanceof SysoutProcessor);
            
            Object processor2 = factory.getProcessor("sysout");
            assertNotNull(processor2);
            assertTrue(processor2 instanceof WebmailProcessor);
            assertTrue(processor2 instanceof SysoutProcessor);
            
            assertSame(processor1, processor2);
        } catch (WebmailException e) {
            fail(e.getMessage());
        }
    }
}