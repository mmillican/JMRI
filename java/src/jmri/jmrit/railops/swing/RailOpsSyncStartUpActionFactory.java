package jmri.jmrit.railops.swing;

import jmri.util.startup.AbstractStartupActionFactory;
import jmri.util.startup.StartupActionFactory;
import org.openide.util.lookup.ServiceProvider;

import javax.annotation.Nonnull;
import java.util.Locale;

@ServiceProvider(service = StartupActionFactory.class)
public class RailOpsSyncStartUpActionFactory extends AbstractStartupActionFactory {
    @Nonnull
    @Override
    public String getTitle(@Nonnull Class<?> clazz, @Nonnull Locale locale) throws IllegalArgumentException {
        if (clazz.equals(jmri.jmrit.railops.swing.RailOpsSyncAction.class)) {
            return "Open RailOps Sync";
        }
        throw new IllegalArgumentException(clazz.getName() + " is not supported by " + this.getClass().getName());
    }

    @Nonnull
    @Override
    public Class<?>[] getActionClasses() {
        return new Class[] { RailOpsSyncAction.class };
    }

}

