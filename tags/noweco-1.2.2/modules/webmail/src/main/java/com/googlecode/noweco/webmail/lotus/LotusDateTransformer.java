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

package com.googlecode.noweco.webmail.lotus;

import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author Gael Lalire
 */
public final class LotusDateTransformer {

    private LotusDateTransformer() {
    }

    private static final SimpleDateFormat RFC822_DATE_FORMAT = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss ", Locale.US) {
        private static final long serialVersionUID = 1L;

        @Override
        public StringBuffer format(final Date date, final StringBuffer toAppendTo, final FieldPosition pos) {
            StringBuffer sb = super.format(date, toAppendTo, pos);

            int zoneMillis = calendar.get(GregorianCalendar.ZONE_OFFSET);
            int dstMillis = calendar.get(GregorianCalendar.DST_OFFSET);
            int minutes = (zoneMillis + dstMillis) / 1000 / 60;

            if (minutes < 0) {
                sb.append('-');
                minutes = -minutes;
            } else {
                sb.append('+');
            }

            sb.append(String.format("%02d%02d", minutes / 60, minutes % 60));

            return sb;
        }

        // TODO write parse method
    };

    private static final TimeZone GMT = TimeZone.getTimeZone("GMT");

    private static final TimeZone CET = TimeZone.getTimeZone("GMT+1:00");

    private static final TimeZone CEDT = TimeZone.getTimeZone("GMT+2:00");

    private static final SimpleDateFormat NOTES_DATE_FORMAT = new SimpleDateFormat("d-MMM-yyyy HH:mm:ss ", Locale.US) {
        private static final long serialVersionUID = 1L;

        @Override
        public StringBuffer format(final Date date, final StringBuffer toAppendTo, final FieldPosition pos) {
            int offset = TimeZone.getDefault().getOffset(date.getTime());

            StringBuffer sb = super.format(new Date(date.getTime() + offset), toAppendTo, pos);

            int minutes = offset / 1000 / 60;
            if (minutes == 60) {
                sb.append("CET");
            } else if (minutes == 2 * 60) {
                sb.append("CEDT");
            } else {

                if (minutes < 0) {
                    sb.append('-');
                    minutes = -minutes;
                } else {
                    sb.append('+');
                }

                sb.append(String.format("%02d%02d", minutes / 60, minutes % 60));
            }
            return sb;
        }


        @Override
        public Date parse(final String text, final ParsePosition pos) {
            Date date = super.parse(text, pos);

            String remain = text.substring(pos.getIndex());
            if (remain.startsWith("CEDT")) {
                date.setTime(date.getTime() - CEDT.getRawOffset());
            } else if (remain.startsWith("CET")) {
                date.setTime(date.getTime() - CET.getRawOffset());
            } else {
                // TODO else : parse +0200
                pos.setErrorIndex(pos.getIndex());
                pos.setIndex(0);
            }
            return date;
        }

    };

    static {
        NOTES_DATE_FORMAT.setTimeZone(GMT);
    }

    public static String formatToRfc822(final Date date) {
        synchronized (RFC822_DATE_FORMAT) {
            return RFC822_DATE_FORMAT.format(date);
        }
    }

    public static Date parseFromNotes(final String date) throws ParseException {
        synchronized (NOTES_DATE_FORMAT) {
            return NOTES_DATE_FORMAT.parse(date);
        }
    }

    public static String convertNotesToRfc822(final String dateHeader) throws ParseException {
        return "Date: " + formatToRfc822(parseFromNotes(dateHeader.substring("Date:".length())));
    }

}
