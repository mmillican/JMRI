package jmri.jmrit.railops.config;

import jmri.Disposable;
import jmri.InstanceManager;
import jmri.InstanceManagerAutoDefault;
import jmri.beans.PropertyChangeSupport;
import jmri.jmrit.operations.setup.AutoSave;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

public class Roster extends PropertyChangeSupport implements InstanceManagerAutoDefault, Disposable {
    private static final String NONE = "";

    private static final String element_roster = "roster";
    private static final String attr_collectionId = "collectionId";
    private static final String attr_lastSyncTime = "lastSyncTime";

    private int collectionId = 0;
    private LocalDateTime lastSyncTime;

    public static int getCollectionId() {
        return getDefault().collectionId;
    }

    public static void setCollectionId(int id) {
        getDefault().collectionId = id;
    }

    public static LocalDateTime getLastSyncTime() {
        return getDefault().lastSyncTime;
    }

    public static void setLastSyncTime(LocalDateTime dt) {
        getDefault().lastSyncTime = dt;
    }

    public static Element store() {
        Element e = new Element(element_roster);

        e.setAttribute(attr_collectionId, Integer.toString(getCollectionId()));
        if (getLastSyncTime() != null) {
            e.setAttribute(attr_lastSyncTime, getLastSyncTime().toString());
        }

        return e;
    }

    public static void load(Element e) {
        if (e.getChild(element_roster) == null) {
            log.debug("roster values missing");
            return;
        }

        Element roster = e.getChild(element_roster);

        if (roster.getAttribute(attr_collectionId) != null) {
            getDefault().collectionId = Integer.parseInt(roster.getAttributeValue(attr_collectionId));
        }

        if (roster.getAttribute(attr_lastSyncTime) != null) {
            getDefault().lastSyncTime = LocalDateTime.parse(roster.getAttributeValue(attr_lastSyncTime));
        }
    }

    public static Roster getDefault() {
        return InstanceManager.getDefault(Roster.class);
    }

    @Override
    public void dispose() {
        AutoSave.stop();
    }

    private static final Logger log = LoggerFactory.getLogger(Roster.class);
}
