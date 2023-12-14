package jmri.jmrit.railops.models.roster;

public enum UpsertCompareMode {
    Id(1), ReportingMarksAndNumber(2);

    private final int value;

    private UpsertCompareMode(int value) {
        this.value = value;
    }
}
