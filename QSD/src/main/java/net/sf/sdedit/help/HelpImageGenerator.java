package net.sf.sdedit.help;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.sf.sdedit.config.ConfigurationManager;
import net.sf.sdedit.config.SequenceConfiguration;
import net.sf.sdedit.diagram.DiagramFactory;
import net.sf.sdedit.diagram.PaintDevice;
import net.sf.sdedit.diagram.SequenceDiagramFactory;
import net.sf.sdedit.error.DiagramError;
import net.sf.sdedit.server.Exporter;
import net.sf.sdedit.ui.components.configuration.Bean;
import net.sf.sdedit.util.DocUtil;
import net.sf.sdedit.util.DocUtil.XMLException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class HelpImageGenerator {

	private File helpDirectory;

	public HelpImageGenerator(File helpDirectory) {
		this.helpDirectory = helpDirectory;
	}

	public void prepare(String helpFile) throws IOException, XMLException,
			DiagramError {
		File file = new File(helpDirectory, helpFile);
		InputStream in = new FileInputStream(file);
		Document doc = null;
		try {
			doc = DocUtil.readDocument(in, "utf-8");
			NodeList list = DocUtil.evalXPathAsNodeList(doc,
					"//div[@class='sd']");
			for (Node node : DocUtil.iterate(list)) {
				Element div = (Element) node;
				Element img = null;
				Element preText = null;
				for (Node child : DocUtil.iterate(div.getChildNodes())) {
					if ("img".equals(child.getNodeName())) {
						img = (Element) child;
					}
					if ("pre".equals(child.getNodeName())) {
						preText = (Element) child;
					}
				}
				if (img != null && preText != null) {
					String seq = preText.getTextContent();
					Bean<SequenceConfiguration> conf = ConfigurationManager
							.createNewDefaultConfiguration(SequenceConfiguration.class);
					String name = img.getAttribute("name");
					File dir = new File(helpDirectory, "sd");
					dir.mkdir();
					File pngFile = new File(dir, name + ".png");
					FileOutputStream fos = new FileOutputStream(pngFile);
					try {
						Exporter exporter = Exporter.getExporter("png",
								"Landscape", "A4", fos);
						PaintDevice paintDevice = new PaintDevice(exporter);
						DiagramFactory factory = new SequenceDiagramFactory(
								seq, paintDevice);
						factory.generateDiagram(conf.getDataObject());
						exporter.export();
						System.out
								.println("saved " + pngFile.getAbsolutePath());
					} finally {
						fos.close();
					}
					img.setAttribute("src", "sd/" + name + ".png");
				}
			}

		} finally {
			in.close();
		}

		OutputStream out = new FileOutputStream(file);
		try {
			DocUtil.writeDocument(doc, "utf-8", out, null, true);
			System.out.println("saved " + file.getAbsolutePath());
		} finally {
			out.close();
		}

	}
	
	public static void main (String [] argv) throws Exception {
		HelpImageGenerator hig = new HelpImageGenerator(new File("/home/strauch/git/sdedit/QSD/src/main/java/resource"));
		hig.prepare("help.html");
	}

}
