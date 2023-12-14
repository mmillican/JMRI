package jmri.jmrit.railops.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LocomotiveModel extends BaseRosterItemModel {
    private String _model;
    private String _engineType;

    public String getModel() { return _model; }
    public void setModel(String val) { _model = val; }

    public String getEngineType() { return _engineType; }
    public void setEngineType(String val) { _engineType = val; }

    public LocomotiveModel() {

    }

    public LocomotiveModel(int id,
        int collectionId,
        String roadName,
        String roadNumber,
        String scale,
        String acquiredOn,
        double purchasePrice,
        double value,
        int length,
        String modelManufacturer,
        double weight,
        String owner,
        String notes,
        String model,
        String engineType) {
        super(id, collectionId, scale, roadName, roadNumber, acquiredOn, purchasePrice, value, length, modelManufacturer, weight, owner, notes);

        _model = model;
        _engineType = engineType;
    }

}
