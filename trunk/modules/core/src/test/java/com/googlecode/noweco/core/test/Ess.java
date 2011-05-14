/*
 * Copyright 2011 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.googlecode.noweco.core.test;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Gael Lalire
 */
public class Ess {

    @Test
    @Ignore
    public void test(final String[] args) throws Exception {
        ess("from@localhost", "to@localhost");
    }

    private void ess(final String from, final String to) throws Exception {
        Session session = Session.getDefaultInstance(new Properties());
        // TODO Auto-generated constructor stub
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(from));
        InternetAddress[] address = new InternetAddress[] { new InternetAddress(to) };
        msg.setRecipients(Message.RecipientType.TO, address);
        msg.setSubject("JavaMail APIs Multipart Test");
        msg.setSentDate(new Date());

        // create and fill the first message part
        MimeBodyPart mbp1 = new MimeBodyPart();
        mbp1.setText("Hello");

        // create and fill the second message part
        MimeBodyPart mbp2 = new MimeBodyPart();
        // Use setText(text, charset), to show it off !
        mbp2.setText("Salut", "us-ascii");

        // create the Multipart and its parts to it
        Multipart mp = new MimeMultipart();
        mp.addBodyPart(mbp1);
        mp.addBodyPart(mbp2);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        // add the Multipart to the message
        msg.setContent(mp);
        int size = msg.getSize();
        System.out.println(size);
        msg.writeTo(byteArrayOutputStream);

        System.out.println(byteArrayOutputStream.toString());


//        msg.writeTo(os);
    }

}
