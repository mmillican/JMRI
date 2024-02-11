package jmri.jmrit.railops.swing;

import jmri.jmrit.railops.config.ApiSettings;

import javax.swing.*;
import java.awt.*;

public class ApiUrlWarning extends JPanel {
    private final JLabel _apiUrlWarningLabel;

    public ApiUrlWarning() {
        super();

        _apiUrlWarningLabel = new JLabel();
        _apiUrlWarningLabel.setForeground(Color.red);

        add(_apiUrlWarningLabel);
        setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        refresh();
    }

    public void refresh() {
        if (ApiSettings.isNonDefaultApiUrl()) {
            setVisible(true);
            _apiUrlWarningLabel.setText(String.format("WARNING: API URL is set to %s", ApiSettings.getApiUrl()));
        } else {
            setVisible(false);
        }
    }
}
