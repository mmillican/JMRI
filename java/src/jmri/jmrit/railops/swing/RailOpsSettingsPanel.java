package jmri.jmrit.railops.swing;

import jmri.jmrit.operations.setup.Control;
import jmri.util.swing.JmriPanel;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

public class RailOpsSettingsPanel extends JmriPanel {

    JButton saveSettingsButton = new JButton("Save");

    JTextField apiKeyTextField = new JTextField(25);

    JComboBox<jmri.jmrit.railops.models.ModelCollection> collectionComboBox = new JComboBox<>();

    @Override
    public String getTitle() {
        return "RailOps Settings";
    }

    public RailOpsSettingsPanel() {
        super();

        log.info("RailOpsSettingsPanel ctor...");

        setSize(new Dimension(Control.panelWidth700, Control.panelHeight300));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));


        // Row 1
        JPanel row1 = new JPanel();
        row1.setLayout(new BoxLayout(row1, BoxLayout.X_AXIS));

        JPanel apiKeyPanel = new JPanel();
        apiKeyPanel.setLayout(new GridBagLayout());
        apiKeyPanel.setBorder(BorderFactory.createTitledBorder("API Key"));
        apiKeyPanel.add(apiKeyTextField);
        row1.add(apiKeyPanel);

        JPanel collectionPanel = new JPanel();
        collectionPanel.setLayout(new GridBagLayout());
        collectionPanel.setBorder(BorderFactory.createTitledBorder("Collection"));
        collectionPanel.add(collectionComboBox);
        row1.add(collectionPanel);

        JPanel saveSettingsPanel = new JPanel();
        saveSettingsPanel.setLayout(new GridBagLayout());
        saveSettingsPanel.add(saveSettingsButton);

        row1.add(saveSettingsPanel);

        add(row1);
    }

    private final static org.slf4j.Logger log = LoggerFactory.getLogger(RailOpsSettingsPanel.class);
}
