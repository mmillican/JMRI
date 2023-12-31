package jmri.jmrit.railops.models.roster;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UpsertCarModel extends UpsertRosterItemModel {
    private String _type;
    private Boolean _isPassenger;
    private Boolean _isCaboose;
    private Boolean _hasFRED;
    private Boolean _isUtility;
    private Boolean _isHazardous;
    private String _color;

    public String getType() { return _type; }
    public void setType(String val) { _type = val; }

    public Boolean getIsPassenger() { return _isPassenger; }
    public void setIsPassenger(Boolean val) { _isPassenger = val; }

    public Boolean getIsCaboose() { return _isCaboose; }
    public void setIsCaboose(Boolean val) { _isCaboose = val; }

    public Boolean getHasFRED() { return _hasFRED; }
    public void setHasFRED(Boolean val) { _hasFRED = val; }

    public Boolean getIsUtility() { return _isUtility; }
    public void setIsUtility(Boolean val) { _isUtility = val; }

    public Boolean getIsHazardous() { return _isHazardous; }
    public void setIsHazardous(Boolean val) { _isHazardous = val; }

    public String getColor() { return _color; }
    public void setColor(String val) { _color = val; }

    public UpsertCarModel(int id,
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
                    String type,
                    Boolean isPassenger,
                    Boolean isCaboose,
                    Boolean hasFRED,
                    Boolean isUtility,
                    Boolean isHazardous,
                    String color) {
        super(id, roadName, roadNumber, acquiredOn, purchasePrice, value, length, modelManufacturer, weight, owner, notes);

        _type = type;
        _isPassenger = isPassenger;
        _isCaboose = isCaboose;
        _hasFRED = hasFRED;
        _isUtility = isUtility;
        _isHazardous = isHazardous;
        _color = color;
    }

}
