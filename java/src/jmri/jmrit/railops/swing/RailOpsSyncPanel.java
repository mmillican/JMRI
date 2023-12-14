package jmri.jmrit.railops.swing;

import jmri.InstanceManager;
import jmri.jmrit.operations.rollingstock.cars.Car;
import jmri.jmrit.operations.rollingstock.cars.CarManager;
import jmri.jmrit.operations.rollingstock.engines.Engine;
import jmri.jmrit.operations.rollingstock.engines.EngineManager;
import jmri.jmrit.operations.setup.Control;
import jmri.jmrit.railops.config.RailOpsXml;
import jmri.jmrit.railops.config.Roster;
import jmri.jmrit.railops.models.CarModel;
import jmri.jmrit.railops.models.LocomotiveModel;
import jmri.jmrit.railops.models.ModelCollection;
import jmri.jmrit.railops.models.roster.BulkUpsertRosterResponse;
import jmri.jmrit.railops.models.roster.UpsertLocomotiveModel;
import jmri.jmrit.railops.services.RosterSyncService;
import jmri.jmrit.railops.config.Auth;
import jmri.util.swing.JmriPanel;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RailOpsSyncPanel extends JmriPanel {
    JButton syncToRemoteButton = new JButton("Sync to Remote");
    JButton refreshRemoteButton = new JButton("Refresh Remote");
    JButton saveSettingsButton = new JButton("Save");

    JTextField apiKeyTextField = new JTextField(25);

    protected JComboBox<jmri.jmrit.railops.models.ModelCollection> collectionComboBox = new JComboBox<>();

    private static final JLabel locomotivesHeadingLabel = new JLabel("Locomotives");
    private static final JLabel carsHeadingLabel = new JLabel("Cars");
    private static final JLabel localHeadingLabel = new JLabel("Local Roster");
    private static final JLabel remoteHeadingLabel = new JLabel("Remote Roster");

    private static final JLabel localLocomotiveCountLabel = new JLabel("0");
    private static final JLabel localCarCountLabel = new JLabel("0");
    private static final JLabel remoteLocomotiveCountLabel = new JLabel("0");
    private static final JLabel remoteCarCountLabel = new JLabel("0");

    private final jmri.jmrit.railops.services.RosterSyncService _rosterSyncService;

    @Override
    public String getTitle() {
        return "RailOps Sync";
    }

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

        apiKeyTextField.setText(Auth.getApiKey());

        int localLocomotiveCount = InstanceManager.getDefault(EngineManager.class).getNumEntries();
        int localCarCount = InstanceManager.getDefault(CarManager.class).getNumEntries();

        localLocomotiveCountLabel.setText(Integer.toString(localLocomotiveCount));
        localCarCountLabel.setText(Integer.toString(localCarCount));

        if (!Auth.getApiKey().isEmpty()) {
            // this will set the value of the collection combobox
            loadCollections();

            if (jmri.jmrit.railops.config.Roster.getCollectionId() != 0) {
                refreshRemoteRoster(jmri.jmrit.railops.config.Roster.getCollectionId());
            }
        }

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

        if (Auth.getApiKey().isEmpty()) {
            refreshRemoteButton.setEnabled(false);
            syncToRemoteButton.setEnabled(false);
        }

        add(panelActions);

        addButtonAction(saveSettingsButton);
        addButtonAction(refreshRemoteButton);
        addButtonAction(syncToRemoteButton);
    }

    public void buttonActionPerformed(java.awt.event.ActionEvent ae) {
        if (ae.getSource() == saveSettingsButton) {
            save();
        } else if (ae.getSource() == refreshRemoteButton) {
            try {
                refreshRemoteRoster(jmri.jmrit.railops.config.Roster.getCollectionId());
            } catch (Exception ex) {
                log.error("Error triggering refreshRemoteButton action", ex);
            }
        } else if (ae.getSource() == syncToRemoteButton) {
            try {
                syncLocomotivesToRemote(jmri.jmrit.railops.config.Roster.getCollectionId());
            } catch (Exception ex) {
                log.error("Error syncing locomotives", ex);
            }
            try {
                syncCarsToRemote(jmri.jmrit.railops.config.Roster.getCollectionId());
            } catch (Exception ex) {
                log.error("Error syncing cars", ex);
            }
        }
    }

    protected void addButtonAction(JButton btn) {
        btn.addActionListener(this::buttonActionPerformed);
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
        }
    }

    private void syncLocomotivesToRemote(int collectionId) throws Exception {
        List<Engine> localEngines = InstanceManager.getDefault(EngineManager.class).getList();
        List<LocomotiveModel> remoteLocomotives = _rosterSyncService.getLocomotives(jmri.jmrit.railops.config.Roster.getCollectionId());

        int createdEngineCount = 0;

        List<jmri.jmrit.railops.models.roster.UpsertLocomotiveModel> upsertLocomotives = new ArrayList<>();

        for(Engine engine : localEngines) {
//            Optional<LocomotiveModel> matchingLoco = remoteLocomotives.stream()
//                    .filter(x -> x.getRoadName().equals(engine.getRoadName()) && x.getRoadNumber().equals(engine.getNumber()))
//                    .findFirst();
//
//            if (matchingLoco.isPresent()) {
//                log.info("Locomotive {} {} already exists in remote roster; skipping", engine.getRoadName(), engine.getNumber());
//                continue;
//            }

            var locoModel = new UpsertLocomotiveModel(
                    0,
                    engine.getRoadName().trim(),
                    engine.getNumber().trim(),
                    null, // TODO: Set acquired date
                    0,
                    0,
                    engine.getLengthInteger(),
                    "",
                    engine.getAdjustedWeightTons(),
                    engine.getOwnerName(),
                    engine.getComment(),
                    engine.getModel(),
                    engine.getTypeName()
            );
            upsertLocomotives.add(locoModel);

//            _rosterSyncService.createLocomotive(collectionId, locoModel);
//            createdEngineCount++;
        }

        log.info("submitting {} locomotives...", upsertLocomotives.size());

        BulkUpsertRosterResponse upsertResponse = _rosterSyncService.upsertLocomotives(collectionId, upsertLocomotives);

        log.info("Created {} / Updated {} locomotives in remote roster",
                upsertResponse.getCreatedCount(), upsertResponse.getUpdatedCount());

        refreshRemoteRoster(collectionId); // TODO: maybe we should just set/add the created count?
    }

    private void syncCarsToRemote(int collectionId) throws Exception {
        List<Car> localCars = InstanceManager.getDefault(CarManager.class).getList();
        List<jmri.jmrit.railops.models.CarModel> remoteCars = _rosterSyncService.getCars(jmri.jmrit.railops.config.Roster.getCollectionId());

        int createdCarCount = 0;

        for(Car car : localCars) {
            Optional<jmri.jmrit.railops.models.CarModel> matchingCar = remoteCars.stream()
                    .filter(x -> x.getRoadName().equals(car.getRoadName()) && x.getRoadNumber().equals(car.getNumber()))
                    .findFirst();

            if (matchingCar.isPresent()) {
                log.info("Car {} {} already exists in remote roster; skipping", car.getRoadName(), car.getNumber());
                continue;
            }

            jmri.jmrit.railops.models.CarModel carModel = new CarModel(
                    0,
                    collectionId,
                    car.getRoadName(),
                    car.getNumber(),
                    "", // TODO: Set scale from operations settings?
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

            _rosterSyncService.createCar(collectionId, carModel);
            createdCarCount++;
        }

        log.info("Created {} cars in remote roster", createdCarCount);
        refreshRemoteRoster(jmri.jmrit.railops.config.Roster.getCollectionId()); // TODO: maybe we should just set/add the created count?
    }

    private void save() {
        log.debug("saving settings");

        Auth.setApiKey(apiKeyTextField.getText());

        jmri.jmrit.railops.models.ModelCollection selectedCollection = collectionComboBox.getSelectedItem() != null
            ? (jmri.jmrit.railops.models.ModelCollection) collectionComboBox.getSelectedItem()
            : null;

        jmri.jmrit.railops.config.Roster.setCollectionId(selectedCollection != null ? Integer.parseInt(selectedCollection.getId()) : -1);

        try {
            InstanceManager.getDefault(jmri.jmrit.railops.config.RailOpsXml.class).save();

            // re-load collections if API key was changed
            // TODO: _only_ reload collections when apiKey was saved
            loadCollections();
        } catch(IOException e) {
            log.error("Error writing rrops settings", e);
        }
    }

    private void loadCollections() {
        if (Auth.getApiKey().isEmpty()) {
            collectionComboBox.setEnabled(false);
            return;
        }

        jmri.jmrit.railops.models.ModelCollection selectedCollection = null;

        collectionComboBox.removeAllItems();

        try {
            List<jmri.jmrit.railops.models.ModelCollection> collections = _rosterSyncService.getCollections();
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

    // Copied from OperationsPanel
    protected void addItemToGrid(JPanel p, JComponent c, int x, int y) {
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = x;
        gc.gridy = y;
        gc.weightx = 100.0;
        gc.weighty = 100.0;
        p.add(c, gc);
    }

    private final static org.slf4j.Logger log = LoggerFactory.getLogger(RailOpsSyncPanel.class);
}
