package jmri.jmrit.ussctc;

import jmri.util.JUnitUtil;
import jmri.*;
import jmri.jmrit.Sound;

import org.junit.Assert;
import org.junit.jupiter.api.*;

/**
 * Tests for PhysicalBell class in the jmri.jmrit.ussctc package
 *
 * @author Bob Jacobsen Copyright 2007, 2021
 */
public class ComputerBellTest {

    @Test
    public void testConstruction() {
        new ComputerBell(new Sound("program:resources/sounds/Bell.wav"));
    }

    @Test
    public void testBellStroke() {
        Bell bell = new ComputerBell(new Sound("program:resources/sounds/Bell.wav"));
        bell.ring();
    }

    @BeforeEach
    public void setUp() {
        JUnitUtil.setUp();
        JUnitUtil.resetProfileManager();
        JUnitUtil.initConfigureManager();
        JUnitUtil.initInternalTurnoutManager();
    }

    @AfterEach
    public void tearDown() {
        JUnitUtil.tearDown();
    }

}
