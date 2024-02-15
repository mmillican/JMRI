package jmri.jmrit.railops.swing;

import jmri.InstanceManager;
import jmri.jmrit.operations.rollingstock.cars.Car;
import jmri.jmrit.operations.rollingstock.cars.CarManager;
import jmri.jmrit.operations.rollingstock.engines.Engine;
import jmri.jmrit.operations.rollingstock.engines.EngineManager;
import jmri.jmrit.operations.setup.Control;
import jmri.jmrit.railops.config.ApiSettings;
import jmri.jmrit.railops.config.RailOpsXml;
import jmri.jmrit.railops.config.Roster;
import jmri.jmrit.railops.models.roster.BulkUpsertRosterResponse;
import jmri.jmrit.railops.models.roster.UpsertCarModel;
import jmri.jmrit.railops.models.roster.UpsertLocomotiveModel;
import jmri.jmrit.railops.services.RosterSyncService;
import jmri.util.swing.JmriJOptionPane;
import jmri.util.swing.JmriPanel;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

public class RailOpsSyncPanel extends JmriPanel implements PropertyChangeListener {
    private final ApiUrlWarning _apiUrlWarning;

    JButton syncToRemoteButton = new JButton("Sync to RailOps");
    JButton refreshRemoteButton = new JButton("Refresh RailOps Counts");
    JButton openSettingsButton = new JButton("Settings");

    private static final JLabel locomotivesHeadingLabel = new JLabel("Locomotives");
    private static final JLabel carsHeadingLabel = new JLabel("Cars");
    private static final JLabel localHeadingLabel = new JLabel("Local JMRI Roster");
    private static final JLabel remoteHeadingLabel = new JLabel("RailOps Roster");

    private static final JLabel localLocomotiveCountLabel = new JLabel("0");
    private static final JLabel localCarCountLabel = new JLabel("0");
    private static final JLabel remoteLocomotiveCountLabel = new JLabel("0");
    private static final JLabel remoteCarCountLabel = new JLabel("0");

    private final jmri.jmrit.railops.services.RosterSyncService _rosterSyncService;

    @Override
    public String getTitle() {
        return "RailOps Sync";
    }

    @Nonnull
    @Override
    public List<JMenu> getMenus() {
        var list = new ArrayList<JMenu>();

        var menu = new JMenu();
        menu.setText("Options");

        menu.add(new RailOpsSettingsAction());

        list.add(menu);

        return list;
    }

    public RailOpsSyncPanel() {
        super();

        InstanceManager.getDefault(RailOpsXml.class);
        _rosterSyncService = InstanceManager.getDefault(RosterSyncService.class);

        _apiUrlWarning = new ApiUrlWarning();
        add(_apiUrlWarning);
//        apiKeyTextField.setText(Auth.getApiKey());

        int localLocomotiveCount = InstanceManager.getDefault(EngineManager.class).getNumEntries();
        int localCarCount = InstanceManager.getDefault(CarManager.class).getNumEntries();

        localLocomotiveCountLabel.setText(Integer.toString(localLocomotiveCount));
        localCarCountLabel.setText(Integer.toString(localCarCount));
//        localCarCountLabel.set

        if (!ApiSettings.getApiKey().isEmpty()) {
            if (jmri.jmrit.railops.config.Roster.getCollectionId() != 0) {
                refreshRemoteRoster(jmri.jmrit.railops.config.Roster.getCollectionId());
            }
        }

        setSize(new Dimension(Control.panelWidth700, Control.panelHeight300));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        if (ApiSettings.getApiKey().isEmpty()) {
            var configWarningPanel = new JPanel();
            configWarningPanel.setLayout(new BoxLayout(configWarningPanel, BoxLayout.X_AXIS));

            var noApiKeyLabel = new JLabel("API Key must be configured prior to syncing");
            noApiKeyLabel.setForeground(Color.RED);
            configWarningPanel.add(noApiKeyLabel);

            configWarningPanel.add(Box.createRigidArea(new Dimension(10, 0)));

            configWarningPanel.add(openSettingsButton);
            addButtonAction(openSettingsButton);

            add(configWarningPanel);
        }

        // Row 2 - Roster Summary
        JPanel panelCountSummary = new JPanel();
        panelCountSummary.setLayout(new GridBagLayout());
        panelCountSummary.setBorder(BorderFactory.createTitledBorder("Roster Summary"));

        addItemToGrid(panelCountSummary, locomotivesHeadingLabel, 1, 0);
        addItemToGrid(panelCountSummary, carsHeadingLabel, 2, 0);

        addItemToGrid(panelCountSummary, localHeadingLabel, 0, 1);
        addItemToGrid(panelCountSummary, localLocomotiveCountLabel, 1, 1);
        addItemToGrid(panelCountSummary, localCarCountLabel, 2, 1);

        addItemToGrid(panelCountSummary, remoteHeadingLabel, 0, 2);
        addItemToGrid(panelCountSummary, remoteLocomotiveCountLabel, 1, 2);
        addItemToGrid(panelCountSummary, remoteCarCountLabel, 2, 2);

        add(panelCountSummary);

        // Row 3 - Buttons/Actions
        JPanel panelActions = new JPanel();
        panelActions.setLayout(new GridBagLayout());
        panelActions.setBorder(BorderFactory.createTitledBorder("Actions"));

        addItemToGrid(panelActions, refreshRemoteButton, 0, 0);
        addItemToGrid(panelActions, syncToRemoteButton, 1, 0);

        add(panelActions);

        addButtonAction(refreshRemoteButton);
        addButtonAction(syncToRemoteButton);

        toggleButtonState();

        ApiSettings.getDefault().addPropertyChangeListener(this);
        Roster.getDefault().addPropertyChangeListener(this);
    }

    public void buttonActionPerformed(java.awt.event.ActionEvent ae) {
        if (ae.getSource() == openSettingsButton) {
            log.info("show settings");
            var settingsFrame = new RailOpsSettingsFrame();
            settingsFrame.setVisible(true);
        } else if (ae.getSource() == refreshRemoteButton) {
            try {
                refreshRemoteRoster(jmri.jmrit.railops.config.Roster.getCollectionId());
            } catch (Exception ex) {
                log.error("Error triggering refreshRemoteButton action", ex);
            }
        } else if (ae.getSource() == syncToRemoteButton) {
            syncRosterToRemote();
        }
    }

    protected void addButtonAction(JButton btn) {
        btn.addActionListener(this::buttonActionPerformed);
    }

    private void toggleButtonState() {
        boolean apiSettingsValid = !ApiSettings.getApiKey().isEmpty()
                && !ApiSettings.getApiUrl().isEmpty()
                && Roster.getCollectionId() !=  0;

        refreshRemoteButton.setEnabled(apiSettingsValid);
        syncToRemoteButton.setEnabled(apiSettingsValid);
    }

    private void refreshRemoteRoster(int collectionId) {
        log.info("refreshing remote roster for collection ID {}", collectionId);

        try {
            List<jmri.jmrit.railops.models.LocomotiveModel> locomotives = _rosterSyncService.getLocomotives(collectionId);
            remoteLocomotiveCountLabel.setText(Integer.toString(locomotives.size()));

            List<jmri.jmrit.railops.models.CarModel> cars = _rosterSyncService.getCars(collectionId);
            remoteCarCountLabel.setText(Integer.toString(cars.size()));
        } catch (Exception ex) {
            log.error("Error refreshing remote roster", ex);

            JmriJOptionPane.showMessageDialogNonModal(
                    this,
                    "There was an error loading the remote roster from RailOps. Try again, or check the logs for more information.",
                    "Error loading roster",
                    JmriJOptionPane.ERROR_MESSAGE,
                    null
            );
        }
    }

    private void syncRosterToRemote() {
        syncToRemoteButton.setEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        try {
            try {
                syncLocomotivesToRemote(jmri.jmrit.railops.config.Roster.getCollectionId());
            } catch (Exception ex) {
                log.error("Error syncing locomotives", ex);
                throw ex;
            }

            try {
                syncCarsToRemote(jmri.jmrit.railops.config.Roster.getCollectionId());
            } catch (Exception ex) {
                log.error("Error syncing cars", ex);
                throw ex;
            }

            JmriJOptionPane.showMessageDialogNonModal(
                    this,
                    "Your locomotive and car roster has been synced to RailOps",
                    "Sync Complete",
                    JmriJOptionPane.INFORMATION_MESSAGE,
                    null
            );
        } catch (Exception ex) {
            JmriJOptionPane.showMessageDialogNonModal(
                    this,
                    "There was an error syncing your roster. Try again, or check the logs for more information.",
                    "Error syncing roster",
                    JmriJOptionPane.ERROR_MESSAGE,
                    null
            );
        }

        setCursor(Cursor.getDefaultCursor());
        syncToRemoteButton.setEnabled(true);
    }

    private void syncLocomotivesToRemote(int collectionId) throws Exception {
        List<Engine> localOpsEngines = InstanceManager.getDefault(EngineManager.class).getList();
        var dcProEngines = jmri.jmrit.roster.Roster.getDefault().getEntriesMatchingCriteria(null, null, null, null, null, null,
                null, null, null, null, null);

        log.info("Retrieved {} engines from DecoderPro", dcProEngines.size());

        List<jmri.jmrit.railops.models.roster.UpsertLocomotiveModel> upsertLocomotives = new ArrayList<>();
        for(Engine engine : localOpsEngines) {
            log.debug("Mapping engine '{}'", engine.getId());

            var decoderEngine = dcProEngines.stream()
                    .filter(x -> x.getId().replace(" ", "")
                            .equals(engine.getId().replace(" ", "")))
                    .findFirst()
                    .orElse(null);

            if (decoderEngine != null) {
                log.debug("matching decoder pro engine: {} (mfg: {})", decoderEngine.getId(), decoderEngine.getMfg());
            } else {
                log.debug("matching decoder pro engine NOT found {}", engine.getId());
            }

            var locoModel = new UpsertLocomotiveModel(
                    0,
                    engine.getRoadName().trim(),
                    engine.getNumber().trim(),
                    null, // TODO: Set acquired date
                    0,
                    0,
                    engine.getLengthInteger(),
                    decoderEngine != null ? decoderEngine.getMfg() : null,
                    engine.getAdjustedWeightTons(),
                    engine.getOwnerName(),
                    engine.getComment(),
                    engine.getModel(),
                    engine.getTypeName()
            );

            if (decoderEngine != null) {
                locoModel.setDecoderFamily(decoderEngine.getDecoderFamily());
                locoModel.setDecoderModel(decoderEngine.getDecoderModel());
                locoModel.setDecoderComments(decoderEngine.getDecoderComment());

                if (decoderEngine.isLongAddress()) {
                    locoModel.setLongAddress(decoderEngine.getDccAddress());
                } else {
                    locoModel.setShortAddress(decoderEngine.getDccAddress());
                }
            }

            upsertLocomotives.add(locoModel);
        }

        log.info("submitting {} locomotives...", upsertLocomotives.size());

        BulkUpsertRosterResponse upsertResponse = _rosterSyncService.upsertLocomotives(collectionId, upsertLocomotives);

        log.info("Created {} / Updated {} locomotives in remote roster",
                upsertResponse.getCreatedCount(), upsertResponse.getUpdatedCount());

        refreshRemoteRoster(collectionId); // TODO: maybe we should just set/add the created count?
    }

    private void syncCarsToRemote(int collectionId) throws Exception {
        List<Car> localCars = InstanceManager.getDefault(CarManager.class).getList();

        List<jmri.jmrit.railops.models.roster.UpsertCarModel> upsertCars = new ArrayList<>();
        for(Car car : localCars) {
            var carModel = new UpsertCarModel(
                    0,
                    car.getRoadName(),
                    car.getNumber(),
                    null, // TODO: Set acquired date
                    0,
                    0,
                    car.getLengthInteger(),
                    "",
                    car.getAdjustedWeightTons(),
                    car.getOwnerName(),
                    car.getComment(),
                    car.getTypeName(),
                    car.isPassenger(),
                    car.isCaboose(),
                    car.hasFred(),
                    car.isUtility(),
                    car.isHazardous(),
                    car.getColor()
            );
            upsertCars.add(carModel);
        }

        log.info("submitting {} cars...", upsertCars.size());

        BulkUpsertRosterResponse upsertResponse = _rosterSyncService.upsertCars(collectionId, upsertCars);

        log.info("Created {} / Updated {} cars in remote roster",
                upsertResponse.getCreatedCount(), upsertResponse.getUpdatedCount());

        refreshRemoteRoster(collectionId); // TODO: maybe we should just set/add the created count?
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(ApiSettings.API_KEY_PROPERTY_CHANGE)) {
            // TODO: show/hide auth key warning
            toggleButtonState();
        }

        if (evt.getPropertyName().equals(Roster.COLLECTION_ID_PROPERTY_CHANGE)
            || evt.getPropertyName().equals(ApiSettings.API_URL_PROPERTY_CHANGE)) {

            log.debug("{} property change event fired; updating collection info", evt.getPropertyName());
            _apiUrlWarning.refresh();
            refreshRemoteRoster(Roster.getCollectionId());
        }
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

    @Override
    public void dispose() {
        super.dispose();

        ApiSettings.getDefault().removePropertyChangeListener(this);
        Roster.getDefault().removePropertyChangeListener(this);
    }

    private final static org.slf4j.Logger log = LoggerFactory.getLogger(RailOpsSyncPanel.class);

}
