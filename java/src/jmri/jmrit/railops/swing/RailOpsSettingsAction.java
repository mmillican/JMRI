package jmri.jmrit.railops.swing;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class RailOpsSettingsAction extends AbstractAction {
    public RailOpsSettingsAction() {
        super("Settings");
    }

    static RailOpsSettingsFrame railOpsSettingsFrame = null;

    @Override
    @SuppressFBWarnings(value = "ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD", justification = "Only one OperationsSetupFrame")
    public void actionPerformed(ActionEvent e) {
        log.info("open settings...");

        if (railOpsSettingsFrame == null || !railOpsSettingsFrame.isVisible()) {

            log.info("init settings panel...");

            railOpsSettingsFrame = new RailOpsSettingsFrame();
            railOpsSettingsFrame.initComponents();
        }

        railOpsSettingsFrame.setExtendedState(Frame.NORMAL);
        railOpsSettingsFrame.setVisible(true);

        log.info("settings visible: {}", railOpsSettingsFrame.isVisible());
    }

    private final static org.slf4j.Logger log = LoggerFactory.getLogger(RailOpsSettingsAction.class);
}
