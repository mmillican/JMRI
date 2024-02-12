package jmri.jmrit.railops.config;

import jmri.Disposable;
import jmri.InstanceManager;
import jmri.InstanceManagerAutoDefault;
import jmri.beans.PropertyChangeSupport;
import jmri.jmrit.operations.setup.AutoSave;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApiSettings extends PropertyChangeSupport implements InstanceManagerAutoDefault, Disposable {
    private static final String NONE = "";

    private static final String element_auth = "auth";
    private static final String attr_apiUrl = "apiUrl";
    private static final String attr_apiKey = "apiKey";

    private String _apiUrl = DEFAULT_API_URL;

    private String _apiKey = NONE;

    public static final String DEFAULT_API_URL = "https://prod-api.railops.app";

    public static final String API_URL_PROPERTY_CHANGE = "apiUrlChange"; // NOI18N
    public static final String API_KEY_PROPERTY_CHANGE = "apiKeyChange"; // NOI18N

    public static String getApiUrl() {
        String url = getDefault()._apiUrl;
        if (!url.endsWith("/")) {
            url = String.format("%s/", url);
        }
        return url;
    }

    public static void setApiUrl(String url) {
        String old = getDefault()._apiUrl;
        getDefault()._apiUrl = url;

        if (!old.equals(url)) {
            getDefault().firePropertyChange(API_URL_PROPERTY_CHANGE, old, url);
        }
    }

    public static Boolean isNonDefaultApiUrl() {
        return !getDefault()._apiUrl.equals(DEFAULT_API_URL);
    }

    public static Boolean canSetApiUrl() {
        return true; // TODO: Ideally this would only be allowed with an env variable or something set
    }

    public static String getApiKey() {
        return getDefault()._apiKey;
    }

    public static void setApiKey(String key) {
        String old = getDefault()._apiKey;
        getDefault()._apiKey = key;

        if (!old.equals(key)) {
            getDefault().firePropertyChange(API_KEY_PROPERTY_CHANGE, old, key);
        }
    }

    public static Element store() {
        Element e = new Element(element_auth);

        e.setAttribute(attr_apiUrl, getApiUrl());
        e.setAttribute(attr_apiKey, getApiKey());

        return e;
    }

    public static void load(Element e) {
        if (e.getChild(element_auth) ==  null) {
            log.debug("auth values missing");
            return;
        }

        Element auth = e.getChild(element_auth);

        String savedApiUrl = auth.getAttributeValue(attr_apiUrl);
        if (savedApiUrl != null && !savedApiUrl.isEmpty()) {
            getDefault()._apiUrl = auth.getAttributeValue(attr_apiUrl);
        }
        if (auth.getAttribute(attr_apiKey) != null) {
            getDefault()._apiKey = auth.getAttribute(attr_apiKey).getValue();
        }
    }

    public static ApiSettings getDefault() {
        return InstanceManager.getDefault(ApiSettings.class);
    }

    private static final Logger log = LoggerFactory.getLogger(ApiSettings.class);

    @Override
    public void dispose() {
        AutoSave.stop();
    }
}
