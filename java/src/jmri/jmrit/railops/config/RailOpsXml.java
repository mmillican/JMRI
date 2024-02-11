package jmri.jmrit.railops.config;

import jmri.InstanceManagerAutoDefault;
import jmri.InstanceManagerAutoInitialize;
import jmri.jmrit.XmlFile;
import jmri.util.FileUtil;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.ProcessingInstruction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class RailOpsXml extends XmlFile implements InstanceManagerAutoDefault, InstanceManagerAutoInitialize {
    private static String fileLocation = FileUtil.getUserFilesPath();
    private static final String fileDirectory = "railops";
    private static final String fileName = "railops.xml";

    public RailOpsXml() {
    }

    public void save() throws IOException {
        String path = getFilePath();
        log.info("saving rrops settings to {}", path);
        writeFile(path);
    }

    public void writeFile(String name) throws java.io.IOException {
        log.debug("writeFile {}", name);
        File file = findFile(name);
        if (file == null) {
            file = new File(name);
        }

        Element root = new Element("rail-ops"); // NOI18N
        // TODO: do we need a dtd file for the definitions?
        Document doc = newDocument(root, dtdLocation + "railops.dtd");

        java.util.Map<String, String> m = new java.util.HashMap<>();
        m.put("type", "text/xsl");
        m.put("href", xsltLocation + "railops.xsl"); // NOI18N
        // TODO: Do we need an XSL file?

        ProcessingInstruction p = new ProcessingInstruction("xml-stylesheet", m);
        doc.addContent(0, p);

        root.addContent(ApiSettings.store());
        root.addContent(Roster.store());

        writeXML(file, doc);

        // setDirty(false); ??
    }

    public void readFile(String name) throws org.jdom2.JDOMException, java.io.IOException {
        if (findFile(name) == null) {
            log.debug("{} file not found", name);
            return;
        }

        Element root = rootFromName(name);
        if (root == null) {
            log.debug("{} file cannot be read", name);
            return;
        }

        ApiSettings.load(root);
        Roster.load(root);
    }

    public String getFilePath() {
        return getFileLocation() + fileDirectory + File.separator + fileName;
    }

    public static String getFileLocation() {
        return fileLocation;
    }

    public static void setFileLocation(String location) {
        fileLocation = location;
    }

    private final static Logger log = LoggerFactory.getLogger(RailOpsXml.class);

    @Override
    public void initialize() {
        try {
            String path = getFilePath();
            readFile(path);
        } catch(IOException | JDOMException e) {
            log.error("Error reading file", e);
        }
    }
}
