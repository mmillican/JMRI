package jmri.jmrit.railops.swing;

import jmri.jmrit.swing.ToolsMenuAction;
import jmri.util.swing.JmriAbstractAction;
import jmri.util.swing.JmriPanel;
import jmri.util.swing.WindowInterface;
import org.openide.util.lookup.ServiceProvider;

import javax.swing.*;

@ServiceProvider(service = jmri.jmrit.swing.ToolsMenuAction.class)
public class RailOpsSyncAction extends JmriAbstractAction implements ToolsMenuAction {
    public RailOpsSyncAction(String s, WindowInterface wi) {
        super(s, wi);
    }

    public RailOpsSyncAction(String s, Icon i, WindowInterface wi) {
        super(s, i, wi);
    }

    public RailOpsSyncAction(String s) {
        super (s);
    }

    public RailOpsSyncAction() {
        this ("RailOps Sync");
    }

    public JmriPanel makePanel() {
        var panel = new RailOpsSyncPanel();
        panel.initComponents();
        return panel;
    }
}
