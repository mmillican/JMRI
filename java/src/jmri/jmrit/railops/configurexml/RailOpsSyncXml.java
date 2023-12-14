package jmri.jmrit.railops.configurexml;

import jmri.*;
import jmri.configurexml.JmriConfigureXmlException;
import jmri.jmrit.railops.RailOpsSync;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

public class RailOpsSyncXml extends jmri.configurexml.AbstractXmlAdapter {

    public RailOpsSyncXml() {

    }

    @Override
    public Element store(Object o) {

        jmri.jmrit.railops.RailOpsSync rr = (jmri.jmrit.railops.RailOpsSync) o;

        Element elem = new Element("railopssync");
        elem.setAttribute("class", this.getClass().getName());

        elem.addContent(
            new Element("testval")
                .addContent(rr.toString())
        );

        return elem;
    }

    @Override
    public boolean load(@Nonnull Element shared, Element perNode) throws JmriConfigureXmlException {

        boolean result = true;

        String content = shared.getChild("testval").getValue();

        new RailOpsSync(content);

        return result;
    }

    @Override
    public void load(Element e, Object o) throws JmriConfigureXmlException {
        log.error("load(Element, Object) called unexpectedly");
    }

    @Override
    public int loadOrder() {
        return Manager.TIMEBASE;
    }

    private final static Logger log = LoggerFactory.getLogger(RailOpsSyncXml.class);
}
