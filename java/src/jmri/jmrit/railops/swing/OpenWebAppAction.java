package jmri.jmrit.railops.swing;

import jmri.jmrit.railops.Bundle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URI;

public class OpenWebAppAction extends AbstractAction {

    public OpenWebAppAction() {
        super("Open Web App");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        var desktop = Desktop.getDesktop();
        try {
            desktop.browse(new URI(Bundle.getMessage("webApp.url")));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
