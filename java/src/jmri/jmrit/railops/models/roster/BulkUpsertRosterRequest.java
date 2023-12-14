package jmri.jmrit.railops.models.roster;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BulkUpsertRosterRequest<TType> {
    private int _collectionId = 0;

    private List<TType> _items = new ArrayList<>();

    private int _compareMode; // See UpsertCompareMode

    public int getCollectionId() { return _collectionId; }
    public void setCollectionId(int value) { _collectionId = value; }

    public List<TType> getItems() { return _items; }
    public void setItems(List<TType> value) { _items = value; }

    public int getCompareMode() { return _compareMode; }
    public void setCompareMode(int value) { _compareMode = value; }

    public BulkUpsertRosterRequest(int collectionId,
                                   List<TType> items,
                                   jmri.jmrit.railops.models.roster.UpsertCompareMode compareMode)
    {
        _collectionId = collectionId;
        _items = items;

        // TODO: Figure out how to use enum values...
        if (compareMode == UpsertCompareMode.Id) {
            _compareMode = 1;
        } else {
            _compareMode = 2;
        }
    }

}

