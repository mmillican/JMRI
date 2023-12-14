package jmri.jmrit.railops.models.roster;

public class UpsertLocomotiveModel extends UpsertRosterItemModel {
    private String _model;
    private String _engineType;

    public String getModel() { return _model; }
    public void setModel(String val) { _model = val; }

    public String getEngineType() { return _engineType; }
    public void setEngineType(String val) { _engineType = val; }

    public UpsertLocomotiveModel(int id,
                                 String roadName,
                                 String roadNumber,
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
        super(id, roadName, roadNumber, acquiredOn, purchasePrice, value, length, modelManufacturer, weight, owner, notes);

        _model = model;
        _engineType = engineType;
    }
}
