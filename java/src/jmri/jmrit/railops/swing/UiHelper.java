package jmri.jmrit.railops.swing;

import javax.swing.*;
import java.awt.*;

public class UiHelper {
    public static void addItemToGrid(JPanel p, JComponent c, int x, int y) {
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = x;
        gc.gridy = y;
        gc.weightx = 100.0;
        gc.weighty = 100.0;
        p.add(c, gc);
    }
}
