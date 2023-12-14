package jmri.jmrit.railops.models.roster;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BulkUpsertRosterResponse {
    private int _providedCount;
    private int _createdCount;
    private int _updatedCount;

    public int getProvidedCount() { return _providedCount; }
    public int getCreatedCount() { return _createdCount; }
    public int getUpdatedCount() { return _updatedCount; }
}
