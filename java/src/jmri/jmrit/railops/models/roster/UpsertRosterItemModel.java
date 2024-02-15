package jmri.jmrit.railops.models.roster;

//@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class UpsertRosterItemModel {
    private static final String NONE = "";

    protected int _id = 0;
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

    public String getRoadName() { return _roadName; }
    public void setRoadName(String val) { _roadName = val; }

    public String getRoadNumber() { return _roadNumber; }
    public void setRoadNumber(String val) { _roadNumber = val; }

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

    public double getWeight() { return _weight; }
    public void setWeight(double val) { _weight = val; }

    public String getOwner() { return _owner; }
    public void setOwner(String val) { _owner = val; }

    public String getNotes() { return _notes; }
    public void setNotes(String val) { _notes = val; }

    protected UpsertRosterItemModel() {

    }
    protected UpsertRosterItemModel(
        int id,
        String roadName,
        String roadNumber
    ) {
        _id = id;
        _roadName = roadName;
        _roadNumber = roadNumber;
    }

    @Override
    public String toString() {
        return String.format("%s %s", _roadName, _roadNumber);
    }
}
