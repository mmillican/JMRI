package jmri.jmrit.railops.models.roster;

public class UpsertLocomotiveModel extends UpsertRosterItemModel {
    private String _model;
    private String _engineType;

    private String _decoderFamily;
    private String _decoderModel;
    private String _decoderComments;

    private String _longAddress;
    private String _shortAddress;

    // TODO: ADd function maps as dictionary
    // public Dictionary<string, string> FunctionMaps { get; set; } = new();

    public String getModel() { return _model; }
    public void setModel(String val) { _model = val; }

    public String getEngineType() { return _engineType; }
    public void setEngineType(String val) { _engineType = val; }

    public String getDecoderFamily() { return _decoderFamily; }
    public void setDecoderFamily(String val) { _decoderFamily = val; }

    public String getDecoderModel() { return _decoderModel; }
    public void setDecoderModel(String val) { _decoderModel = val; }

    public String getDecoderComments() { return _decoderComments; }
    public void setDecoderComments(String val) { _decoderComments = val; }

    public String getLongAddress() { return _longAddress; }
    public void setLongAddress(String val) { _longAddress = val; }

    public String getShortAddress() { return _shortAddress; }
    public void setShortAddress(String val) { _shortAddress = val; }

    public UpsertLocomotiveModel(int id,
                                 String roadName,
                                 String roadNumber,
                                 String model,
                                 String engineType) {
        super(id, roadName, roadNumber);

        _model = model;
        _engineType = engineType;
    }
}
