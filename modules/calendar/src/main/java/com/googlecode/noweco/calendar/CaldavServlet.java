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

package com.googlecode.noweco.calendar;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.noweco.calendar.caldav.Calendar;
import com.googlecode.noweco.calendar.caldav.CalendarHomeSet;
import com.googlecode.noweco.calendar.caldav.CalendarMultiget;
import com.googlecode.noweco.calendar.caldav.Collection;
import com.googlecode.noweco.calendar.caldav.Comp;
import com.googlecode.noweco.calendar.caldav.CurrentUserPrincipal;
import com.googlecode.noweco.calendar.caldav.Displayname;
import com.googlecode.noweco.calendar.caldav.Getcontenttype;
import com.googlecode.noweco.calendar.caldav.Getetag;
import com.googlecode.noweco.calendar.caldav.Multistatus;
import com.googlecode.noweco.calendar.caldav.ObjectFactory;
import com.googlecode.noweco.calendar.caldav.Owner;
import com.googlecode.noweco.calendar.caldav.PrincipalCollectionSet;
import com.googlecode.noweco.calendar.caldav.Prop;
import com.googlecode.noweco.calendar.caldav.Propfind;
import com.googlecode.noweco.calendar.caldav.Propstat;
import com.googlecode.noweco.calendar.caldav.Report;
import com.googlecode.noweco.calendar.caldav.Resourcetype;
import com.googlecode.noweco.calendar.caldav.Response;
import com.googlecode.noweco.calendar.caldav.SupportedCalendarComponentSet;
import com.googlecode.noweco.calendar.caldav.SupportedReport;
import com.googlecode.noweco.calendar.caldav.SupportedReportSet;
import com.googlecode.noweco.calendar.caldav.SyncCollection;
import com.googlecode.noweco.calendar.caldav.SyncToken;

/**
 * @author Gael Lalire
 */
public class CaldavServlet extends HttpServlet {

    private static final long serialVersionUID = -4166352339673292359L;

    private static final Logger LOGGER = LoggerFactory.getLogger(CaldavServlet.class);

    /**
     * <tt>207 Multi-Status</tt>.
     * (WebDAV - RFC 2518) or <tt>207 Partial Update
     * OK</tt> (HTTP/1.1 - draft-ietf-http-v11-spec-rev-01?)
     */
    public static final int SC_MULTI_STATUS = 207;

    private static final String METHOD_PROPFIND = "PROPFIND";

    private static final String METHOD_REPORT = "REPORT";

    private static final JAXBContext JAXB_CONTEXT;

    private static final MemoryFile ROOT;

    static {
        try {
            JAXB_CONTEXT = JAXBContext.newInstance(ObjectFactory.class.getPackage().getName());
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
        String userName = "admin";
        Prop prop;
        PrincipalCollectionSet principalCollectionSet;
        CurrentUserPrincipal currentUserPrincipal;
        CalendarHomeSet calendarHomeSet;
        Resourcetype resourcetype;
        Owner owner;
        Displayname displayname;

        ROOT = new MemoryFile(null, "", true);
        MemoryFile principals = new MemoryFile(ROOT, "principals", true);
        MemoryFile principalsUsers = new MemoryFile(principals, "users", true);
        MemoryFile principalsUsersCurrent = new MemoryFile(principalsUsers, userName, true);

        MemoryFile calendar = new MemoryFile(ROOT, "calendars", true);
        MemoryFile calendarUsers = new MemoryFile(calendar, "users", true);
        MemoryFile calendarUsersCurrent = new MemoryFile(calendarUsers, userName, true);
        MemoryFile calendarUsersCurrentCalendar = new MemoryFile(calendarUsersCurrent, "calendar", true);

        prop = ROOT.getProp();
        principalCollectionSet = new PrincipalCollectionSet();
        principalCollectionSet.setHref(principals.getURI());
        prop.setPrincipalCollectionSet(principalCollectionSet);

        prop = principals.getProp();
        currentUserPrincipal = new CurrentUserPrincipal();
        currentUserPrincipal.setHref(principalsUsersCurrent.getURI());
        prop.setCurrentUserPrincipal(currentUserPrincipal);

        prop = principalsUsersCurrent.getProp();
        principalCollectionSet = new PrincipalCollectionSet();
        principalCollectionSet.setHref(principals.getURI());
        prop.setPrincipalCollectionSet(principalCollectionSet);
        calendarHomeSet = new CalendarHomeSet();
        calendarHomeSet.setHref(calendarUsersCurrent.getURI());
        prop.setCalendarHomeSet(calendarHomeSet);
        displayname = new Displayname();
        displayname.getContent().add(userName);
        prop.setDisplayname(displayname);

        prop = calendarUsersCurrentCalendar.getProp();
        displayname = new Displayname();
        displayname.getContent().add("nowecoCalendar");
        prop.setDisplayname(displayname);
        resourcetype = new Resourcetype();
        resourcetype.setCollection(new Collection());
        resourcetype.setCalendar(new Calendar());
        prop.setResourcetype(resourcetype);
        owner = new Owner();
        owner.setHref(principalsUsersCurrent.getURI());
        prop.setOwner(owner);
    }

    public Marshaller createMarshaller() throws IOException {
        try {
            Marshaller marshaller = JAXB_CONTEXT.createMarshaller();
            marshaller.setProperty("jaxb.formatted.output", true);
            return marshaller;
        } catch (JAXBException e) {
            throw new CalendarException("Unable to create marshaller", e);
        }
    }

    public Unmarshaller createUnmarshaller() throws IOException {
        try {
            return JAXB_CONTEXT.createUnmarshaller();
        } catch (JAXBException e) {
            throw new CalendarException("Unable to create marshaller", e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void service(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getMethod();
        if (LOGGER.isDebugEnabled()) {
            List<String> headers = Collections.list((Enumeration<String>) req.getHeaderNames());
            LOGGER.debug("Command : {}, Appel : {}, headers {}", new Object[] { method, req.getRequestURI(), headers });
        }

        if (!authent(req)) {
            resp.addHeader("WWW-Authenticate", "BASIC realm=\"Noweco CalDAV\"");
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        String requestURI = req.getRequestURI();
        if (requestURI.length() != 0 && requestURI.charAt(0) != '/') {
            // unknown relative URI
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        if (METHOD_PROPFIND.equals(method)) {
            doPropfind(req, resp);
        } else if (METHOD_REPORT.equals(method)) {
            doReport(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    private static final String DEPTH_INFINITY = "infinity";

    public void doReport(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        Unmarshaller unMarshaller = createUnmarshaller();
        Marshaller marshaller = createMarshaller();
        Object xmlRequest = null;
        try {
            xmlRequest = unMarshaller.unmarshal(req.getReader());
        } catch (JAXBException e) {
            throw new CalendarException("Unable to parse request", e);
        }
        if (LOGGER.isTraceEnabled()) {
            try {
                StringWriter writer = new StringWriter();
                marshaller.marshal(xmlRequest, writer);
                LOGGER.trace("receive :\n{}", writer.toString());
            } catch (JAXBException e) {
                // ignore
            }
        }

        Multistatus multistatus = new Multistatus();

        if (xmlRequest instanceof CalendarMultiget) {
            CalendarMultiget calendarMultiget = (CalendarMultiget) xmlRequest;
            Prop reqProp = calendarMultiget.getProp();

            int status = propFind(multistatus, reqProp, req.getHeader("Depth"), calendarMultiget.getHref());
            if (status != HttpServletResponse.SC_OK) {
                resp.sendError(status);
                return;
            }
        } else if (xmlRequest instanceof SyncCollection) {
            SyncCollection syncCollection = (SyncCollection) xmlRequest;
            Prop reqProp = syncCollection.getProp();
            String requestURI = req.getRequestURI();
            MemoryFile locate = MemoryFileUtils.locate(ROOT, requestURI);
            for (MemoryFile memoryFile : locate.getChildren()) {
                int status = propFind(multistatus, reqProp, "0", memoryFile.getURI());
                if (status != HttpServletResponse.SC_OK) {
                    resp.sendError(status);
                    return;
                }
            }
            SyncToken syncToken = new SyncToken();
            syncToken.getContent().add("<string mal formee>");
            multistatus.setSyncToken(syncToken);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
            return;
        }

        resp.setStatus(SC_MULTI_STATUS);
        resp.setContentType("text/xml;charset=\"UTF-8\"");
        PrintWriter httpWriter = resp.getWriter();
        try {
            Writer writer;
            if (LOGGER.isTraceEnabled()) {
                writer = new StringWriter();
            } else {
                writer = httpWriter;
            }
            marshaller.marshal(multistatus, writer);
            if (LOGGER.isTraceEnabled()) {
                String string = writer.toString();
                LOGGER.trace("send :\n{}", string);
                httpWriter.write(string);
            }
        } catch (JAXBException e) {
            throw new CalendarException("Unable to format response", e);
        }
        httpWriter.close();
    }

    public boolean authent(final HttpServletRequest req) throws ServletException, IOException {
        String authorization = req.getHeader("authorization");
        if (authorization == null) {
            return false;
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(Base64.decodeBase64(authorization.substring("Basic "
                .length()))), "US-ASCII"));
        String[] credentials = bufferedReader.readLine().split(":");
        bufferedReader.close();
        if (credentials.length == 2) {
            String user = credentials[0];
            String password = credentials[1];
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("connect with user '{}' and password '{}'", user, password);
            }
            // TODO call spi to connect
            return true;
        }
        return false;
    }

    public int propFind(final Multistatus multistatus, final Prop reqProp, final String depth, final String requestURI) {
        MemoryFile requestMemoryFile = MemoryFileUtils.locate(ROOT, requestURI);
        if (requestMemoryFile == null) {
            return HttpServletResponse.SC_NOT_FOUND;
        }

        List<MemoryFile> memoryFiles;
        if ("0".equals(depth)) {
            memoryFiles = Collections.singletonList(requestMemoryFile);
        } else if ("1".equals(depth)) {
            memoryFiles = new ArrayList<MemoryFile>();
            memoryFiles.add(requestMemoryFile);
            if (requestMemoryFile.isDir()) {
                memoryFiles.addAll(requestMemoryFile.getChildren());
            }
        } else if (depth == null || DEPTH_INFINITY.equals(depth)) {
            memoryFiles = MemoryFileUtils.recursiveList(requestMemoryFile);
        } else {
            return HttpServletResponse.SC_BAD_REQUEST;
        }

        for (MemoryFile memoryFile : memoryFiles) {
            Response response = new Response();
            response.getHref().add(memoryFile.getURI());
            Propstat propstatOK = new Propstat();
            Propstat propstatNotFound = new Propstat();
            Prop propOK = new Prop();
            Prop propNotFound = new Prop();
            propstatOK.setProp(propOK);
            propstatNotFound.setProp(propNotFound);

            simplePropFind(memoryFile, reqProp, propOK, propNotFound);
            if (reqProp.getCalendarData() != null) {
                propOK.setCalendarData(memoryFile.getContent());
            }
            if (reqProp.getSupportedReportSet() != null) {
                SupportedReportSet supportedReportSet = new SupportedReportSet();
                SupportedReport supportedReport;
                Report report;

                supportedReport = new SupportedReport();
                report = new Report();
                report.setCalendarMultiget(new CalendarMultiget());
                supportedReport.setReport(report);
                supportedReportSet.getSupportedReport().add(supportedReport);

                supportedReport = new SupportedReport();
                report = new Report();
                report.setSyncCollection(new SyncCollection());
                supportedReport.setReport(report);
                supportedReportSet.getSupportedReport().add(supportedReport);

                propOK.setSupportedReportSet(supportedReportSet);
            }
            if (reqProp.getSupportedCalendarComponentSet() != null) {
                SupportedCalendarComponentSet supportedCalendarComponentSet = new SupportedCalendarComponentSet();
                Comp comp = new Comp();
                comp.setName("VEVENT");
                supportedCalendarComponentSet.getComp().add(comp);
                propOK.setSupportedCalendarComponentSet(supportedCalendarComponentSet);
            }
            propstatOK.setStatus("HTTP/1.1 200 OK");
            response.getPropstat().add(propstatOK);

            propstatNotFound.setStatus("HTTP/1.1 404 Not Found");
            response.getPropstat().add(propstatNotFound);

            multistatus.getResponse().add(response);
        }
        return HttpServletResponse.SC_OK;
    }

    public void simplePropFind(final MemoryFile memoryFile, final Prop reqProp, final Prop propOK, final Prop propNotFound) {
        if (reqProp.getPrincipalCollectionSet() != null && memoryFile.getProp().getPrincipalCollectionSet() != null) {
            propOK.setPrincipalCollectionSet(memoryFile.getProp().getPrincipalCollectionSet());
        } else {
            propNotFound.setPrincipalCollectionSet(reqProp.getPrincipalCollectionSet());
        }
        if (reqProp.getCurrentUserPrincipal() != null && memoryFile.getProp().getCurrentUserPrincipal() != null) {
            propOK.setCurrentUserPrincipal(memoryFile.getProp().getCurrentUserPrincipal());
        } else {
            propNotFound.setCurrentUserPrincipal(reqProp.getCurrentUserPrincipal());
        }
        if (reqProp.getDisplayname() != null && memoryFile.getProp().getDisplayname() != null) {
            propOK.setDisplayname(memoryFile.getProp().getDisplayname());
        } else {
            propNotFound.setDisplayname(reqProp.getDisplayname());
        }
        if (reqProp.getResourcetype() != null && memoryFile.getProp().getResourcetype() != null) {
            propOK.setResourcetype(memoryFile.getProp().getResourcetype());
        } else {
            propNotFound.setResourcetype(reqProp.getResourcetype());
        }
        if (reqProp.getCalendarHomeSet() != null && memoryFile.getProp().getCalendarHomeSet() != null) {
            propOK.setCalendarHomeSet(memoryFile.getProp().getCalendarHomeSet());
        } else {
            propNotFound.setCalendarHomeSet(reqProp.getCalendarHomeSet());
        }
        if (reqProp.getGetetag() != null && memoryFile.getProp().getGetetag() != null) {
            propOK.setGetetag(memoryFile.getProp().getGetetag());
        } else {
            propNotFound.setGetetag(reqProp.getGetetag());
        }
        if (reqProp.getOwner() != null && memoryFile.getProp().getOwner() != null) {
            propOK.setOwner(memoryFile.getProp().getOwner());
        } else {
            propNotFound.setOwner(reqProp.getOwner());
        }
        if (reqProp.getGetcontenttype() != null && memoryFile.getProp().getGetcontenttype() != null) {
            propOK.setGetcontenttype(memoryFile.getProp().getGetcontenttype());
        } else {
            propNotFound.setGetcontenttype(reqProp.getGetcontenttype());
        }
        propNotFound.getAny().addAll(reqProp.getAny());
    }

    public void doPropfind(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        Unmarshaller unMarshaller = createUnmarshaller();
        Marshaller marshaller = createMarshaller();

        Propfind propfind = null;
        try {
            propfind = (Propfind) unMarshaller.unmarshal(req.getReader());
        } catch (JAXBException e) {
            throw new CalendarException("Unable to parse request", e);
        }
        if (LOGGER.isTraceEnabled()) {
            try {
                StringWriter writer = new StringWriter();
                marshaller.marshal(propfind, writer);
                LOGGER.trace("receive :\n{}", writer.toString());
            } catch (JAXBException e) {
                // ignore
            }
        }

        Prop reqProp = propfind.getProp();

        Multistatus multistatus = new Multistatus();
        int status = propFind(multistatus, reqProp, req.getHeader("Depth"), req.getRequestURI());
        if (status != HttpServletResponse.SC_OK) {
            resp.sendError(status);
            return;
        }

        resp.setStatus(SC_MULTI_STATUS);
        resp.setContentType("text/xml;charset=\"UTF-8\"");

        PrintWriter httpWriter = resp.getWriter();

        try {
            Writer writer;
            if (LOGGER.isTraceEnabled()) {
                writer = new StringWriter();
            } else {
                writer = httpWriter;
            }
            marshaller.marshal(multistatus, writer);
            if (LOGGER.isTraceEnabled()) {
                String string = writer.toString();
                LOGGER.trace("send :\n{}", string);
                httpWriter.write(string);
            }
        } catch (JAXBException e) {
            throw new CalendarException("Unable to format response", e);
        }
        httpWriter.close();
    }

    @Override
    public void doDelete(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        MemoryFile locate = MemoryFileUtils.locate(ROOT, req.getRequestURI());
        if (locate == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        locate.getParent().getChildren().remove(locate);
    }

    @Override
    public void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        MemoryFile locate = MemoryFileUtils.locate(ROOT, req.getRequestURI());
        if (locate == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        Getcontenttype getcontenttype = locate.getProp().getGetcontenttype();
        if (getcontenttype != null) {
            resp.setContentType(getcontenttype.getContent().get(0));
        }
        PrintWriter writer = resp.getWriter();
        writer.write(locate.getContent());
        writer.close();
    }

    @Override
    public void doPut(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        String requestURI = req.getRequestURI();
        String[] split = requestURI.split("/");
        MemoryFile memoryFile = ROOT;
        for (int i = 0; i < split.length - 1; i++) {
            String name = split[i];
            MemoryFile locate = MemoryFileUtils.locate(memoryFile, name);
            if (locate == null) {
                memoryFile = new MemoryFile(memoryFile, name, true);
            } else {
                memoryFile = locate;
            }
        }
        String name = split[split.length - 1];
        MemoryFile locate = MemoryFileUtils.locate(memoryFile, name);
        if (locate == null) {
            memoryFile = new MemoryFile(memoryFile, name, false);
        } else {
            memoryFile = locate;
        }
        BufferedReader reader = req.getReader();
        String line = reader.readLine();
        StringBuilder stringBuilder = new StringBuilder();
        while (line != null) {
            stringBuilder.append(line);
            stringBuilder.append('\n');
            line = reader.readLine();
        }
        memoryFile.setContent(stringBuilder.toString());
        Getcontenttype getcontenttype = new Getcontenttype();
        getcontenttype.getContent().add(req.getHeader("Content-Type"));
        memoryFile.getProp().setGetcontenttype(getcontenttype);
        Prop prop = memoryFile.getProp();
        prop.setResourcetype(new Resourcetype());
        Getetag getetag = new Getetag();
        getetag.getContent().add("\"" + System.currentTimeMillis() + "\"");
        prop.setGetetag(getetag);
        resp.setHeader("ETag", prop.getGetetag().getContent().get(0));
        resp.setStatus(HttpServletResponse.SC_CREATED);
    }

    @Override
    public void doOptions(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("DAV", "1");
        resp.setHeader("Allow", "DELETE, GET, OPTIONS, POST, PROPFIND, PUT, REPORT");
    }
}
