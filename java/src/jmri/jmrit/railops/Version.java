package jmri.jmrit.railops;

import java.util.ResourceBundle;

public class Version {

    static final private ResourceBundle VERSION_BUNDLE = ResourceBundle.getBundle("jmri.jmrit.railops.RailOps"); // NOT18N;

    static final public int major = Integer.parseInt(VERSION_BUNDLE.getString("version.major"));
    static final public int minor = Integer.parseInt(VERSION_BUNDLE.getString("version.minor"));
    static final public int build = Integer.parseInt(VERSION_BUNDLE.getString("version.build"));

    public static String getVersion() {
        return String.format("%s.%s.%s", major, minor, build);
    }
}
