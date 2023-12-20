package jmri.jmrit.railops.swing;

import jmri.InstanceManager;
import jmri.jmrit.operations.setup.Control;
import jmri.jmrit.railops.config.Auth;
import jmri.jmrit.railops.config.RailOpsXml;
import jmri.jmrit.railops.config.Roster;
import jmri.jmrit.railops.models.ModelCollection;
import jmri.jmrit.railops.services.RosterSyncService;
import jmri.util.swing.JmriJOptionPane;
import jmri.util.swing.JmriPanel;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.List;

public class RailOpsSettingsPanel extends JmriPanel {
    final RosterSyncService _rosterSyncService;
    JButton saveApiKeyButton = new JButton("Save");
    JButton saveCollectionButton = new JButton("Save");
    JButton openWebsiteButton = new JButton("Signup");

    JTextField apiKeyTextField = new JTextField(25);

    JComboBox<jmri.jmrit.railops.models.ModelCollection> collectionComboBox = new JComboBox<>();

    @Override
    public String getTitle() {
        return "RailOps Settings";
    }

    public RailOpsSettingsPanel() {
        super();

        InstanceManager.getDefault(RailOpsXml.class);
        _rosterSyncService = InstanceManager.getDefault(RosterSyncService.class);
        apiKeyTextField.setText(Auth.getApiKey());

        loadCollections();

        setSize(new Dimension(Control.panelWidth500, Control.panelHeight500));
        setMinimumSize(new Dimension(Control.panelWidth500, Control.panelHeight500));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        var signupMsg = "<html><b>A RailOps account is required to use this feature.</b><br />"
                + "<ol>"
                + "<li>Click the button below, or visit https://railops.app and register for an account.</li>"
                + "<li>After your account is created, follow the instructions to create an API key.</li>"
                + "<li>Enter the API key generated into the text box below and click 'Save'.</li>"
                + "</ol></html>";

        var accountMessagePanel = new JPanel();
        accountMessagePanel.setLayout(new BoxLayout(accountMessagePanel, BoxLayout.Y_AXIS));
        var signupMessageLabel = new JLabel(signupMsg);

        accountMessagePanel.add(signupMessageLabel);
        accountMessagePanel.add(openWebsiteButton);
        accountMessagePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        accountMessagePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        add(accountMessagePanel);


        var apiKeyPanel = new JPanel();
        apiKeyPanel.setLayout(new GridBagLayout());
        apiKeyPanel.setBorder(BorderFactory.createTitledBorder("API Key"));
        apiKeyPanel.add(apiKeyTextField);
        apiKeyPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        apiKeyPanel.add(saveApiKeyButton);

        add(apiKeyPanel);

        var collectionPanel = new JPanel();
        collectionPanel.setLayout(new GridBagLayout());
        collectionPanel.setBorder(BorderFactory.createTitledBorder("Collection"));
        collectionPanel.add(collectionComboBox);
        collectionPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        collectionPanel.add(saveCollectionButton);

        add(collectionPanel);

        addButtonAction(saveApiKeyButton);
        addButtonAction(saveCollectionButton);
        addButtonAction(openWebsiteButton);
    }

    protected void buttonActionPerformed(java.awt.event.ActionEvent ae) {
        if (ae.getSource() == saveApiKeyButton) {
            saveApiKey();
        } else if (ae.getSource() == saveCollectionButton) {
            saveCollection();
        } else if (ae.getSource() == openWebsiteButton) {
            var desktop = Desktop.getDesktop();
            try {
                desktop.browse(new URI("https://railops.app"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected void addButtonAction(JButton btn) {
        btn.addActionListener(this::buttonActionPerformed);
    }

    private void writeSettings()
    {
        try {
            InstanceManager.getDefault(jmri.jmrit.railops.config.RailOpsXml.class).save();

            JmriJOptionPane.showMessageDialogNonModal(
                    this,
                    "The RailOps settings have been saved.",
                    "Saved",
                    JmriJOptionPane.INFORMATION_MESSAGE,
                    null
            );
        } catch(IOException e) {
            log.error("Error writing rrops settings", e);

            JmriJOptionPane.showMessageDialogNonModal(
                    this,
                    "Error saving the settings. Please try again.",
                    "Error",
                    JmriJOptionPane.ERROR_MESSAGE,
                    null
            );
        }
    }

    private void saveApiKey() {
        log.debug("saving api key");

        Auth.setApiKey(apiKeyTextField.getText());

        writeSettings();
        loadCollections(); // reload the collections after API key has been saved
    }

    private void loadCollections() {
        if (Auth.getApiKey().isEmpty()) {
            collectionComboBox.setEnabled(false);
            return;
        } else {
            collectionComboBox.setEnabled(true);
        }

        ModelCollection selectedCollection = null;

        collectionComboBox.removeAllItems();

        try {
            List<ModelCollection> collections = _rosterSyncService.getCollections();
            for (ModelCollection collection : collections) {
                collectionComboBox.addItem(collection);
                if (collection.getCollectionId() == Roster.getCollectionId()) {
                    selectedCollection = collection;
                }
            }

            if (selectedCollection != null) {
                collectionComboBox.setSelectedItem(selectedCollection);
            }
        } catch (Exception ex) {
            log.error("Could not get collections from API", ex);
        }
    }

    private void saveCollection() {

        var selectedCollection = collectionComboBox.getSelectedItem() != null
                ? (jmri.jmrit.railops.models.ModelCollection) collectionComboBox.getSelectedItem()
                : null;

        Roster.setCollectionId(selectedCollection != null
                ? Integer.parseInt(selectedCollection.getId())
                : -1
        );

        writeSettings();
    }

    // Copied from OperationsPanel
    protected void addItemToGrid(JPanel p, JComponent c, int x, int y) {
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = x;
        gc.gridy = y;
        gc.weightx = 100.0;
        gc.weighty = 100.0;
        p.add(c, gc);
    }

    private final static org.slf4j.Logger log = LoggerFactory.getLogger(RailOpsSettingsPanel.class);
}
