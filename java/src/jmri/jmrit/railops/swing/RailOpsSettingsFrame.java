package jmri.jmrit.railops.swing;

import jmri.jmrit.operations.setup.Control;
import jmri.util.JmriJFrame;

import java.awt.*;

public class RailOpsSettingsFrame extends JmriJFrame {
    public RailOpsSettingsFrame() {
        super("RailOps Settings");

        setEscapeKeyClosesWindow(true);

        var settingsPanel = new RailOpsSettingsPanel();
        setContentPane(settingsPanel);
        setSize(Control.panelWidth600, Control.panelHeight500);
        setResizable(false);
    }

    @Override
    public void initComponents() {
        super.initComponents();

        setMinimumSize(new Dimension(Control.panelWidth500, Control.panelHeight500));
    }
}
