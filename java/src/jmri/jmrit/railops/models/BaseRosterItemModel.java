package jmri.jmrit.railops.models;

//@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class BaseRosterItemModel {
    private static final String NONE = "";

    protected int _id = 0;
    protected int _collectionId = 0;
    protected String _scale = NONE;
    protected String _roadName = NONE;
    protected String _roadNumber = NONE;
    protected String _acquiredOn; // Eventually change this to a date?
    protected double _purchasePrice;
    protected double _value;
    protected int _length;
    protected String _modelManufacturer;
    protected double _weight;
    protected String _owner;
    protected String _notes;

    public int getId() { return _id; }
    public void setId(int val) { _id = val; }

    public int getCollectionId() { return _collectionId; }
    public void setCollectionId(int val) { _collectionId = val; }

    public String getRoadName() { return _roadName; }
    public void setRoadName(String val) { _roadName = val; }

    public String getRoadNumber() { return _roadNumber; }
    public void setRoadNumber(String val) { _roadNumber = val; }

    public String getScale() { return _scale ; }
    public void setScale(String val) { _scale = val; }

    public String getAcquiredOn() { return _acquiredOn; }
    public void setAcquiredOn(String val) { _acquiredOn = val; }

    public double getPurchasePrice() { return _purchasePrice; }
    public void setPurchasePrice(double val) { _purchasePrice = val; }

    public double getValue() { return _value; }
    public void setValue(double val) { _value = val; }

    public int getLength() { return _length; }
    public void setLength(int val) { _length = val; }
    public String getModelManufacturer() { return _modelManufacturer ; }
    public void setModelManufacturer(String val) { _modelManufacturer = val; }

    protected BaseRosterItemModel() {

    }
    protected BaseRosterItemModel(
        int id,
        int collectionId,
        String scale,
        String roadName,
        String roadNumber,
        String acquiredOn,
        double purchasePrice,
        double value,
        int length,
        String modelManufacturer,
        double weight,
        String owner,
        String notes
    ) {
        _id = id;
        _collectionId = collectionId;
        _scale = scale;
        _roadName = roadName;
        _roadNumber = roadNumber;
        _acquiredOn = acquiredOn;
        _purchasePrice = purchasePrice;
        _value = value;
        _length = length;
        _modelManufacturer = modelManufacturer;
        _weight = weight;
        _owner = owner;
        _notes = notes;
    }

    @Override
    public String toString() {
        return String.format("%s %s", _roadName, _roadNumber);
    }
}
