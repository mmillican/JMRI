package jmri.jmrit.railops.swing;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class RailOpsSettingsAction extends AbstractAction {
    public RailOpsSettingsAction() {
        super("Settings");
    }

    static jmri.jmrit.railops.swing.RailOpsSettingsPanel railOpsSettingsPanel = null;

    @Override
    @SuppressFBWarnings(value = "ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD", justification = "Only one OperationsSetupFrame")
    public void actionPerformed(ActionEvent e) {
        log.info("open settings...");

        if (railOpsSettingsPanel == null || !railOpsSettingsPanel.isVisible()) {

            log.info("init settings panel...");

            railOpsSettingsPanel = new RailOpsSettingsPanel();
            railOpsSettingsPanel.initComponents();
        }

        railOpsSettingsPanel.requestFocusInWindow();
        railOpsSettingsPanel.setVisible(true);

        log.info("settings visible: {}", railOpsSettingsPanel.isVisible());
    }

    private final static org.slf4j.Logger log = LoggerFactory.getLogger(RailOpsSettingsAction.class);
}
