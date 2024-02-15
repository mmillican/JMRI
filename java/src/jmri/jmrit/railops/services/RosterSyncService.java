package jmri.jmrit.railops.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import jmri.InstanceManager;
import jmri.InstanceManagerAutoDefault;
import jmri.InstanceManagerAutoInitialize;
import jmri.jmrit.railops.config.RailOpsXml;
import jmri.jmrit.railops.models.CarModel;
import jmri.jmrit.railops.models.LocomotiveModel;
import jmri.jmrit.railops.models.ModelCollection;
import jmri.jmrit.railops.models.roster.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.http.HttpResponse;
import java.util.List;

public class RosterSyncService implements InstanceManagerAutoDefault, InstanceManagerAutoInitialize {
    private final RailOpsApiService _apiService;
    private final ObjectMapper _mapper;

    public RosterSyncService() {
        _apiService = InstanceManager.getDefault(RailOpsApiService.class);
        _mapper = new ObjectMapper();
    }

    @Override
    public void initialize() {
        InstanceManager.getDefault(RailOpsXml.class);
    }

    public static RosterSyncService getDefault() {
        return InstanceManager.getDefault(RosterSyncService.class);
    }

    public List<ModelCollection> getCollections() throws Exception {
        List<ModelCollection> collections = _apiService.getList("collections", ModelCollection.class);

        log.debug("Retrieved {} collections", collections.size());
        return collections;
    }

    public List<LocomotiveModel> getLocomotives(int collectionId) throws Exception {
        String url = String.format("locomotives?collectionId=%s", collectionId);
        List<LocomotiveModel> locomotives = _apiService.getList(url, LocomotiveModel.class);

        log.info("Retrieved {} locomotives from remote roster", locomotives.size());
        return locomotives;
    }

    public jmri.jmrit.railops.models.roster.BulkUpsertRosterResponse upsertLocomotives(int collectionId, List<UpsertLocomotiveModel> locomotives) throws Exception {
        BulkUpsertRosterRequest<UpsertLocomotiveModel> requestModel = new BulkUpsertRosterRequest<>(
                collectionId,
                locomotives,
                UpsertCompareMode.ReportingMarksAndNumber
        );

        HttpResponse<String> httpResponse = _apiService.post("locomotives/bulk", requestModel);

        log.debug("Bulk upsert locomotive HTTP response: {}", httpResponse);
        return _mapper.readValue(httpResponse.body(), BulkUpsertRosterResponse.class);
    }

    public List<CarModel> getCars(int collectionId) throws Exception {
        String url = String.format("cars?collectionId=%s", collectionId);
        List<CarModel> cars = _apiService.getList(url, CarModel.class);

        log.info("Retrieved {} cars from remote roster", cars.size());
        return cars;
    }

    public BulkUpsertRosterResponse upsertCars(int collectionId, List<UpsertCarModel> cars) throws Exception {
        BulkUpsertRosterRequest<UpsertCarModel> requestModel = new BulkUpsertRosterRequest<>(
                collectionId,
                cars,
                UpsertCompareMode.ReportingMarksAndNumber
        );

        HttpResponse<String> httpResponse = _apiService.post("cars/bulk", requestModel);

        log.debug("Bulk upsert car HTTP response: {}", httpResponse);
        return _mapper.readValue(httpResponse.body(), BulkUpsertRosterResponse.class);
    }

    private static final Logger log = LoggerFactory.getLogger(RosterSyncService.class);
}
