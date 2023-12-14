package jmri.jmrit.railops;

import jmri.*;
import org.slf4j.LoggerFactory;

public class RailOpsSync {
    public RailOpsSync(String val) {
        test = val;

        log.info("Created RailOpsSync");

        InstanceManager.getDefault(ConfigureManager.class).registerUser(this);
    }

    String test;

    @Override
    public String toString() { return test; }

    private final static org.slf4j.Logger log = LoggerFactory.getLogger(RailOpsSync.class);
}
