package jmri.jmrit.railops.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jmri.beans.Identifiable;
import org.slf4j.*;

import javax.annotation.Nonnull;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelCollection implements Identifiable {
    public static final String NONE = "";

    protected int _id = 0;
    protected String _name = NONE;

    public ModelCollection(
        @JsonProperty("id") int id,
        @JsonProperty("name") String name
    ) {
        _id = id;
        _name = name;
    }

    public int getCollectionId() { return _id; }
    @Override
    @Nonnull
    public String getId() { return String.valueOf(_id); }

    // for combo boxes

    @Override
    public String toString() { return _name; }

    public String getName() { return _name; }

    private final static Logger log = LoggerFactory.getLogger(ModelCollection.class);
}
