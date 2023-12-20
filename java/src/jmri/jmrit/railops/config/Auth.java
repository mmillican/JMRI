package jmri.jmrit.railops.config;

import jmri.Disposable;
import jmri.InstanceManager;
import jmri.InstanceManagerAutoDefault;
import jmri.beans.PropertyChangeSupport;
import jmri.jmrit.operations.setup.AutoSave;
import org.apiguardian.api.API;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Auth extends PropertyChangeSupport implements InstanceManagerAutoDefault, Disposable {
    private static final String NONE = "";

    private static final String element_auth = "auth";
    private static final String attr_apiKey = "apiKey";

    private String apiKey = NONE;

    public static final String API_KEY_PROPERTY_CHANGE = "apiKeyChange"; // NOI18N

    public static String getApiKey() {
        return getDefault().apiKey;
    }

    public static void setApiKey(String key) {
        String old = getDefault().apiKey;
        getDefault().apiKey = key;

        if (!old.equals(key)) {
            getDefault().firePropertyChange(API_KEY_PROPERTY_CHANGE, old, key);
        }
    }

    public static Element store() {
        Element e = new Element(element_auth);

        e.setAttribute(attr_apiKey, getApiKey());

        return e;
    }

    public static void load(Element e) {
        if (e.getChild(element_auth) ==  null) {
            log.debug("auth values missing");
            return;
        }

        Element auth = e.getChild(element_auth);
        org.jdom2.Attribute a;

        if (auth.getAttribute(attr_apiKey) != null) {
            getDefault().apiKey = auth.getAttribute(attr_apiKey).getValue();
        }
    }

    public static Auth getDefault() {
        return InstanceManager.getDefault(Auth.class);
    }

    private static final Logger log = LoggerFactory.getLogger(Auth.class);

    @Override
    public void dispose() {
        AutoSave.stop();
    }
}
