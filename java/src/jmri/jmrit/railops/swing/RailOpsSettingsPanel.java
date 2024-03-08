package jmri.jmrit.railops.swing;

import jmri.InstanceManager;
import jmri.jmrit.operations.setup.Control;
import jmri.jmrit.railops.Version;
import jmri.jmrit.railops.config.ApiSettings;
import jmri.jmrit.railops.config.RailOpsXml;
import jmri.jmrit.railops.config.Roster;
import jmri.jmrit.railops.models.ModelCollection;
import jmri.jmrit.railops.services.RosterSyncService;
import jmri.util.swing.JmriJOptionPane;
import jmri.util.swing.JmriPanel;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;
import java.util.List;

public class RailOpsSettingsPanel extends JmriPanel {
    final RailOpsXml _railOpsConfig;
    final RosterSyncService _rosterSyncService;

    private final ApiUrlWarning _apiUrlWarning;

    JButton saveApiSettingsButton = new JButton("Save");
    JButton resetApiUrlButton = new JButton("Reset URL");
    JButton saveCollectionButton = new JButton("Save");
    JButton openWebsiteButton = new JButton("Signup");

    JTextField apiKeyTextField = new JTextField(25);

    JTextField apiUrlTextField = new JTextField(25);

    JComboBox<jmri.jmrit.railops.models.ModelCollection> collectionComboBox = new JComboBox<>();

    @Override
    public String getTitle() {
        return "RailOps Settings";
    }

    public RailOpsSettingsPanel() {
        super();

        _railOpsConfig = InstanceManager.getDefault(RailOpsXml.class);
        _rosterSyncService = InstanceManager.getDefault(RosterSyncService.class);

        apiUrlTextField.setText(ApiSettings.getApiUrl());
        apiKeyTextField.setText(ApiSettings.getApiKey());

        loadCollections();

        setSize(new Dimension(Control.panelWidth500, Control.panelHeight600));
        setMinimumSize(new Dimension(Control.panelWidth500, Control.panelHeight600));
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

        _apiUrlWarning = new ApiUrlWarning();
        add(_apiUrlWarning);

        accountMessagePanel.add(signupMessageLabel);
        accountMessagePanel.add(openWebsiteButton);
        accountMessagePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        accountMessagePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        add(accountMessagePanel);

        var apiSettingsPanel = new JPanel();
        apiSettingsPanel.setLayout(new GridBagLayout());
        apiSettingsPanel.setBorder(BorderFactory.createTitledBorder("API Settings"));

        var apiUrlLabel = new JLabel("API URL:");
        apiUrlLabel.setLabelFor(apiUrlTextField);
        UiHelper.addItemToGrid(apiSettingsPanel, apiUrlLabel, 0, 0);
        UiHelper.addItemToGrid(apiSettingsPanel, apiUrlTextField, 1, 0);
        UiHelper.addItemToGrid(apiSettingsPanel, resetApiUrlButton, 2, 0);

        var apiKeyLabel = new JLabel("API Key:");
        apiUrlLabel.setLabelFor(apiKeyTextField);
        UiHelper.addItemToGrid(apiSettingsPanel, apiKeyLabel, 0, 1);
        UiHelper.addItemToGrid(apiSettingsPanel, apiKeyTextField, 1, 1);

        UiHelper.addItemToGrid(apiSettingsPanel, saveApiSettingsButton, 1, 2);

        add(apiSettingsPanel);

        var collectionPanel = new JPanel();
        collectionPanel.setLayout(new GridBagLayout());
        collectionPanel.setBorder(BorderFactory.createTitledBorder("Collection"));
        collectionPanel.add(collectionComboBox);
        collectionPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        collectionPanel.add(saveCollectionButton);

        add(collectionPanel);

        var appInfoPanel = new JPanel();
        appInfoPanel.setLayout(new GridBagLayout());

        var pluginVersion = Version.getVersion();
        var syncVersionPluginLabel = new JLabel(String.format("RailOps Plugin Version: %s", pluginVersion));

        appInfoPanel.add(syncVersionPluginLabel);

        add(appInfoPanel);

        openWebsiteButton.addActionListener((ActionEvent ae) -> {
            var desktop = Desktop.getDesktop();
            try {
                desktop.browse(new URI("https://railops.app"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        saveApiSettingsButton.addActionListener((ActionEvent ae) -> saveApiSettings());
        resetApiUrlButton.addActionListener((ActionEvent ae) -> resetApiUrl());
        saveCollectionButton.addActionListener((ActionEvent ae) -> saveCollection());
    }

    private void writeSettings()
    {
        try {
            _railOpsConfig.save();

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

    private void saveApiSettings() {
        log.debug("saving api settings");

        ApiSettings.setApiUrl(apiUrlTextField.getText());
        ApiSettings.setApiKey(apiKeyTextField.getText());

        writeSettings();
        _apiUrlWarning.refresh();
        loadCollections(); // reload the collections after API key has been saved
    }

    private void resetApiUrl() {
        log.debug("Resetting API URL...");

        apiUrlTextField.setText(ApiSettings.DEFAULT_API_URL);
    }

    private void loadCollections() {
        if (ApiSettings.getApiKey().isEmpty()) {
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

            if (Roster.getCollectionId() == 0 && collections.size() == 1) {
                log.info("No collection was set and only 1 was retrieved from API. Setting current collection to that.");
                selectedCollection = collections.getFirst();
                if (selectedCollection != null) {
                    Roster.setCollectionId(selectedCollection.getCollectionId());
                }
            }

            if (selectedCollection != null) {
                collectionComboBox.setSelectedItem(selectedCollection);
            }
        } catch (Exception ex) {
            log.error("Could not get collections from API", ex);

            JmriJOptionPane.showMessageDialogNonModal(
                    this,
                    "There was an error loading your RailOps Collections. Try again, or check the logs for more information.",
                    "Error",
                    JmriJOptionPane.ERROR_MESSAGE,
                    null
            );
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

    private final static org.slf4j.Logger log = LoggerFactory.getLogger(RailOpsSettingsPanel.class);
}
