package jmri.jmrit.railops.swing;

import jmri.jmrit.operations.setup.Control;
import jmri.util.JmriJFrame;

import java.awt.*;

public class RailOpsSettingsFrame extends JmriJFrame {
    public RailOpsSettingsFrame() {
        super("RailOps Settings");

        var settingsPanel = new RailOpsSettingsPanel();
        setContentPane(settingsPanel);
    }

    @Override
    public void initComponents() {
        super.initComponents();

        setMinimumSize(new Dimension(Control.panelWidth500, Control.panelHeight500));
    }
}
