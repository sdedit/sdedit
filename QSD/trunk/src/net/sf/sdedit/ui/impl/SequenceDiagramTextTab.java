package net.sf.sdedit.ui.impl;

import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;

import net.sf.sdedit.config.Configuration;
import net.sf.sdedit.config.ConfigurationManager;
import net.sf.sdedit.diagram.AbstractPaintDevice;
import net.sf.sdedit.diagram.Diagram;
import net.sf.sdedit.diagram.DiagramDataProvider;
import net.sf.sdedit.diagram.DiagramFactory;
import net.sf.sdedit.diagram.IPaintDevice;
import net.sf.sdedit.diagram.PaintDevice;
import net.sf.sdedit.diagram.SequenceDiagram;
import net.sf.sdedit.diagram.SequenceDiagramFactory;
import net.sf.sdedit.editor.EditorHintFactory;
import net.sf.sdedit.error.DiagramError;
import net.sf.sdedit.error.FatalError;
import net.sf.sdedit.error.SemanticError;
import net.sf.sdedit.error.SequenceDiagramError;
import net.sf.sdedit.text.TextHandler;
import net.sf.sdedit.ui.PanelGraphicDevice;
import net.sf.sdedit.ui.components.configuration.Bean;

public class SequenceDiagramTextTab extends DiagramTextTab {

    private static final long serialVersionUID = 5278509849011224397L;

    public SequenceDiagramTextTab(UserInterfaceImpl ui, Font codeFont,
            Bean<Configuration> configuration) {
        super(ui, codeFont, configuration);
    }

    public DiagramDataProvider createProvider() {
        return new TextHandler(getCode());
    }
    
    private SequenceDiagram diagram () {
        return (SequenceDiagram) getDiagram();
    }
    
    protected void handleBug(Diagram diagram, RuntimeException ex) {

        String name = "sdedit-errorlog-" + System.currentTimeMillis();

        File errorLogFile = new File(name);
        try {
            errorLogFile.createNewFile();
        } catch (IOException e0) {
            try {
                errorLogFile = new File(System.getProperty("user.home"), name);
                errorLogFile.createNewFile();
            } catch (IOException e1) {
                errorLogFile = new File(System.getProperty("java.io.tmpdir",
                        name));
            }
        }

        try {
            saveLog(errorLogFile, ex, (TextHandler) diagram.getDataProvider());
        } catch (IOException e) {
           get_UI().errorMessage(e, null,
                    "An error log file could not be saved.");
        }

    }

    @Override
    public void handleDiagramError(DiagramError error) {
        if (error == null) {
            setError(false, "", -1, -1, null);
            if (diagram().getFragmentManager().openFragmentsExist()) {
                setError(
                        true,
                        "Warning: There are open comments. Use [c:<type> <text>]...[/c]",
                        -1, -1, null);
            }

            int noteNumber = diagram().getNextFreeNoteNumber();
            if (noteNumber == 0) {
                setStatus("");
            } else {
                setStatus("Next note number: "
                        + diagram().getNextFreeNoteNumber());
            }
        } else {
            setStatus("");
            if (error instanceof FatalError) {
                FatalError fatal = (FatalError) error;
                System.err
                        .println("********************************************************");
                System.err
                        .println("*                                                      *");
                System.err
                        .println("*            A FATAL ERROR HAS OCCURED.                *");
                System.err
                        .println("*                                                      *");
                System.err
                        .println("********************************************************");
                error.getCause().printStackTrace();
                // cautiously embedding this call into a try/catch-block
                try {
                    handleBug(diagram(), fatal.getCause());
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            } else {
                TextHandler handler = (TextHandler) error.getProvider();
                String prefix = "";

                if (error instanceof SemanticError) {
                    prefix = diagram().isThreaded()
                            && diagram().getCallerThread() != -1 ? "Thread "
                            + diagram().getCallerThread() + ": " : "";
                }
                setError(false, prefix + error.getMessage(),
                        handler.getLineBegin() - 1, handler.getLineEnd(),
                        EditorHintFactory.createHint(this, (SequenceDiagramError) error));
            }
        }

    }
    
    private static final String getFatalErrorDescription(Throwable ex) {
        return "A FATAL ERROR has occurred: " + ex.getClass().getSimpleName();
    }
    
    private void saveLog(File logFile, Throwable exception,
            TextHandler textHandler) throws IOException {

        FileOutputStream stream = new FileOutputStream(logFile);
        try {
            PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(
                    stream, ConfigurationManager.getGlobalConfiguration()
                            .getFileEncoding()));
            BufferedReader bufferedReader = new BufferedReader(
                    new StringReader(textHandler.getText()));
            int error = textHandler.getLineNumber();
            printWriter.println(exception.getClass().getSimpleName()
                    + " has occurred in line " + error + "\n");
            int i = 0;
            for (;;) {
                String line = bufferedReader.readLine();
                if (line == null) {
                    bufferedReader.close();
                    break;
                }
                line = line.trim();
                if (i == error - 1) {
                    line = ">>>>>>>>>>>>>> " + line;
                }
                printWriter.println(line);
                i++;
            }
            printWriter.println("\n\n:::::::::::::::::::::::::::::\n\n");
            exception.printStackTrace(printWriter);
            printWriter.flush();
            printWriter.close();
            get_UI()
                    .errorMessage(
                            null,
                            "FATAL ERROR",
                            getFatalErrorDescription(exception)
                                    + "\n\nAn error log file has been saved under \n"
                                    + logFile.getAbsolutePath()
                                    + "\n\n"
                                    + "Please send an e-mail with this file as an attachment to:\n"
                                    + "sdedit@users.sourceforge.net");
        } finally {
            stream.close();
        }
    }

    @Override
    public DiagramFactory createFactory(IPaintDevice paintDevice) {
        return new SequenceDiagramFactory(this, paintDevice);
    }
    
    @Override
    public String getCategory() {
        return "Sequence diagrams";
    }

    @Override
    public AbstractPaintDevice createPaintDevice() {
        PanelGraphicDevice ppd = new PanelGraphicDevice(true);
        if (getInteraction() != null) {
            ppd.setPartner(getInteraction());
        }   
        return new PaintDevice(ppd);
    }

}
