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
import java.util.List;

public class RailOpsSettingsPanel extends JmriPanel {
    final RosterSyncService _rosterSyncService;
    JButton saveApiKeyButton = new JButton("Save");
    JButton saveCollectionButton = new JButton("Save");
    JButton closeWindowButton = new JButton("Close");

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

        setSize(new Dimension(Control.panelWidth500, Control.panelHeight400));
        setMinimumSize(new Dimension(Control.panelWidth500, Control.panelHeight400));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        if (!Auth.getApiKey().isEmpty()) {
            var signupMsg = "A RailOps.app account is required to set use this feature.\n\n"
                    + "1) Visit https://railops.app in your browser and register for an account.\n"
                    + "2) After your account is created, follow the instructions to create an API key.\n"
                    + "3) Enter the API key generated into the text box below and click 'Save'.";

            var accountRequiredPanel = new JPanel();
            var signupMsgTextArea = new JTextArea(signupMsg, 6, 50);
            signupMsgTextArea.setEditable(false);
            signupMsgTextArea.setLineWrap(true);
            signupMsgTextArea.setWrapStyleWord(true);
            signupMsgTextArea.setOpaque(false);
            signupMsgTextArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));

            accountRequiredPanel.add(signupMsgTextArea);

            add(accountRequiredPanel);
        }

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
    }

//    @Override
//    public void initComponents() {
//        loadCollections();
//    }

    protected void buttonActionPerformed(java.awt.event.ActionEvent ae) {
        if (ae.getSource() == saveApiKeyButton) {
            saveApiKey();
        } else if (ae.getSource() == saveCollectionButton) {
            saveCollection();
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

//    private void closeSettingsWindow() {
//        if (getTopLevelAncestor() != null) {
//            getTopLevelAncestor().setVisible(false);
//        }
//        dispose();
//    }

    private final static org.slf4j.Logger log = LoggerFactory.getLogger(RailOpsSettingsPanel.class);
}
