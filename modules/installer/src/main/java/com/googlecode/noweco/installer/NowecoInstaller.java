package com.googlecode.noweco.installer;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;

/**
 * @author gaellalire
 */
public final class NowecoInstaller {

    private NowecoInstaller() {
    }

    public static void install(final File home, final List<Integer> version) throws Exception {
        IOUtils.copy(NowecoInstaller.class.getResourceAsStream("settings.xml"), new FileOutputStream(new File(home, "settings.xml")));
    }

    public static void uninterruptedMigrate(final File fromHome, final List<Integer> fromVersion, final Runnable fromRunnable, final File toHome, final List<Integer> toVersion, final Runnable toRunnable) throws Exception {
        // fromRunnable is running, toRunnable is not
        // the goal is to give to toRunnable a way


        // this is the first version
        throw new Exception("NYI");
    }

    public static void migrate(final File fromHome, final List<Integer> fromVersion, final File toHome, final List<Integer> toVersion) throws Exception {
        // this is the first version
        throw new Exception("NYI");
    }

    public static void uninstall(final File home, final List<Integer> version) {

    }

}
