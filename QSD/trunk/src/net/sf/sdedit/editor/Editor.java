// Copyright (c) 2006 - 2011, Markus Strauch.
// All rights reserved.
// 
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
// 
// * Redistributions of source code must retain the above copyright notice, 
// this list of conditions and the following disclaimer.
// * Redistributions in binary form must reproduce the above copyright notice, 
// this list of conditions and the following disclaimer in the documentation 
// and/or other materials provided with the distribution.
// 
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
// THE POSSIBILITY OF SUCH DAMAGE.

package net.sf.sdedit.editor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.Action;

import net.sf.sdedit.Constants;
import net.sf.sdedit.config.Configuration;
import net.sf.sdedit.config.ConfigurationManager;
import net.sf.sdedit.config.GlobalConfiguration;
import net.sf.sdedit.eclipse.Eclipse;
import net.sf.sdedit.editor.apple.AppInstaller;
import net.sf.sdedit.editor.plugin.FileActionProvider;
import net.sf.sdedit.editor.plugin.FileHandler;
import net.sf.sdedit.editor.plugin.Plugin;
import net.sf.sdedit.server.RealtimeServer;
import net.sf.sdedit.ui.Tab;
import net.sf.sdedit.ui.UserInterface;
import net.sf.sdedit.ui.UserInterfaceListener;
import net.sf.sdedit.ui.components.buttons.ActionManager;
import net.sf.sdedit.ui.components.configuration.Bean;
import net.sf.sdedit.ui.components.configuration.ConfigurationAction;
import net.sf.sdedit.ui.impl.LookAndFeelManager;
import net.sf.sdedit.ui.impl.UserInterfaceImpl;
import net.sf.sdedit.util.OS;
import net.sf.sdedit.util.Ref;
import net.sf.sdedit.util.UIUtilities;
import net.sf.sdedit.util.Utilities;

/**
 * The control class of the Quick Sequence Diagram Editor.
 * 
 * @author Markus Strauch
 */
public final class Editor implements Constants, UserInterfaceListener

{
    private GlobalConfiguration globalConfiguration;

    private UserInterface ui;

    private Actions actions;

    // Reference to the real-time-server, if one is running, otherwise null
    private RealtimeServer server;

    private LinkedList<String> recentFiles;

    private LinkedList<Action> recentFileActions;

    // Flag denoting if the application has already been set up.
    private boolean setup = false;

    private Map<String, Object> globals;

    private ActionManager actionManager;

    private FileActionProvider fileActionProvider;

    private List<FileHandler> fileHandlers;

    private DiagramFileHandler defaultFileHandler;

    private static Editor instance;

    private List<Plugin> plugins;

    public static Editor getEditor() {
        if (instance == null) {
            instance = new Editor();
        }
        return instance;
    }

    private Editor() {
        UIUtilities.setGlobalFont(ConfigurationManager.getGlobalConfiguration()
                .getGuiFont());
        String laf = ConfigurationManager.getGlobalConfiguration()
                .getLookAndFeel();
        LookAndFeelManager.changeTo(laf);
        globals = new HashMap<String, Object>();

        fileHandlers = new LinkedList<FileHandler>();
        plugins = new LinkedList<Plugin>();

        defaultFileHandler = new DiagramFileHandler();

        // we do not use addFileHandler
        fileHandlers.add(defaultFileHandler);

        actionManager = new ActionManager();

        ui = newUI();

        if (OS.TYPE == OS.Type.MAC) {
            AppInstaller.installApplication(this);
        }
        recentFiles = new LinkedList<String>();
        recentFileActions = new LinkedList<Action>();
        globalConfiguration = ConfigurationManager.getGlobalConfiguration();

        fileActionProvider = new FileActionProvider();

        ui.addListener(this);

    }

    public void start() {
        setupUI();
        readRecentFiles();
        if (globalConfiguration.isAutostartServer()) {
            try {
                startRealtimeServer(globalConfiguration.getRealtimeServerPort());
                ui.message("Started real-time diagram server @localhost:"
                        + server.getPort());
            } catch (Exception e) {
                ui.errorMessage(e, null,
                        "The real-time diagram server could not be started.");
            }
        }
        setup = true;
        if (OS.TYPE == OS.Type.MAC) {
            File fileToLoad = AppInstaller.getFileToLoad();
            if (fileToLoad != null) {
                try {

                    load(fileToLoad.toURI().toURL());
                } catch (RuntimeException e) {
                    throw e;
                } catch (Exception e) {
                    ui.errorMessage(e, null,
                            "Cannot load " + fileToLoad.getAbsolutePath());
                }
            }
        }
    }

    public void addFileHandler(FileHandler fileHandler) {

        fileHandlers.add(fileHandler);
        ui.addAction("&File.Open",
                fileActionProvider.getOpenAction(fileHandler, ui),
                fileActionProvider.getOpenActivator);

    }

    public ActionManager getActionManager() {
        return actionManager;
    }

    public void registerGlobalObject(String name, Object object) {
        globals.put(name, object);
    }

    public Object getGlobalObject(String name) {
        return globals.get(name);
    }

    private FileHandler findFileHandler(String type) {
        for (FileHandler fileHandler : fileHandlers) {
            for (String ext : fileHandler.getFileTypes()) {
                if (ext.equalsIgnoreCase(type)) {
                    return fileHandler;
                }
            }
        }
        return null;
    }

    public Tab load(URL url) throws IOException, URISyntaxException {
        String file = url.getFile();
        FileHandler handler = null;
        int d = file.lastIndexOf('.');
        if (d >= 0) {
            String ext = file.substring(d + 1);
            handler = findFileHandler(ext);
        }
        if (handler == null) {
            ui.errorMessage(null, null, "Cannot handle " + file);
            return null;
        }
        return handler.loadFile(url, ui);

    }

    /**
     * @see net.sf.sdedit.ui.UserInterfaceListener#hyperlinkClicked(java.lang.String)
     */
    public void hyperlinkClicked(String hyperlink) {
        if (hyperlink.startsWith("example:")) {
            String file = hyperlink.substring(hyperlink.indexOf(':') + 1);
            actions.getExampleAction(file, file).actionPerformed(null);
        } else if (hyperlink.startsWith("help:")) {
            int first = hyperlink.indexOf(':');
            int last = hyperlink.lastIndexOf(':');
            String title = hyperlink.substring(first + 1, last);
            String file = hyperlink.substring(last + 1);
            ui.help(title, file.replaceAll(".html", ""), false);
        }
    }

    public <T extends Plugin> T getPlugin(Class<T> pluginClass) {
        for (Plugin plugin : plugins) {
            if (pluginClass.isInstance(plugin)) {
                return pluginClass.cast(plugin);
            }
        }
        return null;
    }

    private void readRecentFiles() {
        String sep = System.getProperty("path.separator");
        String recent = globalConfiguration.getRecentFiles();
        if (recent != null && !recent.equals("")) {
            int i = 0;
            for (String file : recent.split(sep)) {
                if (new File(file).exists()) {
                    i++;
                    recentFiles.add(file);
                    Action act = actions.getRecentFileAction(file);
                    recentFileActions.add(act);
                    ui.addAction("&File.Open &recent file", act, null);
                    if (i == globalConfiguration.getMaxNumOfRecentFiles()) {
                        return;
                    }
                }
            }
        }
    }

    public List<String> getRecentFiles() {
        return Collections.checkedList(recentFiles, String.class);
    }

    public void addToRecentFiles(String file) {
        int max = globalConfiguration.getMaxNumOfRecentFiles();
        if (max == 0) {
            return;
        }
        int i = recentFiles.indexOf(file);
        Action act;
        if (i >= 0) {
            recentFiles.remove(i);
            act = recentFileActions.get(i);
            recentFileActions.remove(i);

        } else {
            act = actions.getRecentFileAction(file);
            ui.addAction("&File.Open &recent file", act, null);
            if (recentFiles.size() == max) {
                Action last = recentFileActions.removeLast();
                ui.removeAction("&File.Open &recent file", last);
                recentFiles.removeLast();
            }
        }
        recentFiles.addFirst(file);
        recentFileActions.addFirst(act);
    }

    private void writeRecentFiles() {
        String sep = System.getProperty("path.separator");
        StringBuffer buffer = new StringBuffer();
        for (String file : recentFiles) {
            if (buffer.length() > 0) {
                buffer.append(sep);
            }
            buffer.append(file);
        }
        globalConfiguration.setRecentFiles(buffer.toString());
    }

    public int startRealtimeServer(int port) throws IOException {
        if (isServerRunning()) {
            return 0;
        }
        server = new RealtimeServer(port, this);
        server.setDaemon(true);
        server.start();
        return server.getPort();
    }

    public boolean isServerRunning() {
        return server != null;
    }

    public void shutDownServer() {
        if (isServerRunning()) {
            server.shutDown();
            server = null;
        }
    }

    private void setupUI() {

        addActions();

        ui.showUI();
        ui.addToolbarSeparator();
        ui.addToToolbar(actions.helpAction, null);
        try {
            installPlugins();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void installPlugins() throws IOException {
        URL url = Utilities.getResource("plugins.txt");
        InputStream stream = url.openStream();
        try {
            for (String line : Utilities.readLines(stream, Charset.defaultCharset())) {
                line = line.trim();
                if (line.length() > 0 && line.charAt(0) != '#') {
                    installPlugin(line);
                }
            }
        } finally {
            stream.close();
        }
    }

    private void installPlugin(String pluginClass) {
        Plugin plugin = Utilities.newInstance(pluginClass, Plugin.class);
        ((Plugin) plugin).install();
        plugins.add((Plugin) plugin);
    }

    @SuppressWarnings("serial")
    private void addActions() {

        actions = new Actions(this);

        ui.addAction("&File", actions.newDiagramAction, null);

        ui.addCategory("&File.Open", "open");

        ui.addAction("&File.Open",
                fileActionProvider.getOpenAction(defaultFileHandler, ui),
                fileActionProvider.getOpenActivator);

        ui.addCategory("&File.Open &recent file", "open");

        ui.addAction("&File",
                fileActionProvider.getSaveAction(defaultFileHandler, ui),
                fileActionProvider.getSaveActivator);

        ui.addAction("&File",
                fileActionProvider.getSaveAsAction(defaultFileHandler, ui),
                fileActionProvider.getSaveActivator);

        Action exportAction = actions.getExportAction();

        Action saveImageAction = new SaveImageAction(ui);

        if (exportAction != null) {
            ui.addAction("&File", exportAction,
                    actions.nonEmptyDiagramActivator);
        } else {
            ui.addAction("&File", saveImageAction,
                    actions.nonEmptyDiagramActivator);
        }

        ui.addAction("&File", actions.closeTabAction, actions.canCloseActivator);
        ui.addAction("&File", actions.closeAllAction, null);

        Action printPDFAction = actions.getPrintAction("pdf");
        if (printPDFAction != null) {
            ui.addAction("&File", printPDFAction,
                    actions.noDiagramErrorActivator);
        }
        ui.addAction("&File", actions.quitAction, null);

        ConfigurationAction<Configuration> wrapAction = new TabConfigurationAction(
                "lineWrap", "[control shift W]&Wrap lines",
                "Wrap lines whose length exceed the width of the text area",
                "wrap", ui);

        ConfigurationAction<Configuration> threadedAction = new TabConfigurationAction(
                "threaded",
                Shortcuts.getShortcut(Shortcuts.ENABLE_THREADS)
                        + "Enable &multithreading",
                "Create diagrams with arbitrarily many sequences running concurrently",
                "threads", ui);
        
        ConfigurationAction<Configuration> slackAction = new TabConfigurationAction(
                "slackMode",
                "Slack mode",
                "Switch on or off slack mode",
                "slack", ui);

        ConfigurationAction<GlobalConfiguration> autoUpdateAction = new ConfigurationAction<GlobalConfiguration>(
                "autoUpdate", "Auto-redraw", "Update diagram as you type",
                "reload") {
            @Override
            public Bean<GlobalConfiguration> getBean() {
                return ConfigurationManager.getGlobalConfigurationBean();
            }
        };

        ConfigurationAction<GlobalConfiguration> autoScrollAction = new ConfigurationAction<GlobalConfiguration>(
                "autoScroll",
                "Auto-scrolling",
                "Scroll automatically to where the message currently being specified is visible",
                "autoscroll") {
            @Override
            public Bean<GlobalConfiguration> getBean() {
                return ConfigurationManager.getGlobalConfigurationBean();
            }
        };

        ui.addAction("&Edit", actions.undoAction, actions.textTabActivator);
        ui.addAction("&Edit", actions.redoAction, actions.textTabActivator);
        ui.addAction("&Edit", actions.clearAction, actions.textTabActivator);

        ui.addConfigurationAction("&Edit", threadedAction,
                actions.textTabActivator);
        
        ui.addConfigurationAction("&Edit", slackAction,
                actions.textTabActivator);

        ui.addAction("&Edit", actions.configureGloballyAction, null);
        ui.addAction("&Edit", actions.configureDiagramAction,
                actions.diagramTabActivator);
        ui.addAction("&Edit", actions.copyBitmapToClipBoardAction,
                actions.nonEmptyDiagramActivator);
        ui.addAction("&Edit", actions.copyVectorGraphicsToClipBoardAction,
                actions.nonEmptyDiagramActivator);
        /*
        ui.addAction("&Edit", actions.prettyPrintAction,
                actions.nonEmptyDiagramActivator);
        */

        ui.addCategory("&View", null);

        ui.addConfigurationAction("&View", autoUpdateAction, null);
        ui.addConfigurationAction("&View", autoScrollAction, null);

        ui.addAction("&View", actions.redrawAction, actions.diagramTabActivator);

        ui.addAction("&View", actions.widenAction,
                actions.canConfigureActivator);
        ui.addAction("&View", actions.narrowAction, actions.canNarrowActivator);
        ui.addConfigurationAction("&View", wrapAction, actions.textTabActivator);
        ui.addAction("&View", actions.fullScreenAction,
                actions.supportsFullScreenActivator);

        ui.addAction("&View", actions.splitLeftRightAction,
                actions.horizontalSplitPossibleActivator);
        ui.addAction("&View", actions.splitTopBottomAction,
                actions.verticalSplitPossibleActivator);

        if (OS.TYPE != OS.Type.MAC) {
            ui.setQuitAction(actions.quitAction);
        }

        ui.addToToolbar(actions.newDiagramAction, null);

        ui.addToToolbar(
                fileActionProvider.getOpenAction(defaultFileHandler, ui),
                fileActionProvider.getOpenActivator);
        ui.addToToolbar(
                fileActionProvider.getSaveAction(defaultFileHandler, ui),
                fileActionProvider.getSaveActivator);
        ui.addToToolbar(
                fileActionProvider.getSaveAsAction(defaultFileHandler, ui),
                fileActionProvider.getSaveActivator);

        if (exportAction != null) {
            ui.addToToolbar(exportAction, actions.nonEmptyDiagramActivator);
        } else {
            ui.addToToolbar(saveImageAction, actions.nonEmptyDiagramActivator);
        }

        if (printPDFAction != null) {
            ui.addToToolbar(printPDFAction, actions.noDiagramErrorActivator);
        }

        ui.addToolbarSeparator();

        ui.addToToolbar(actions.configureGloballyAction, null);
        ui.addToToolbar(actions.configureDiagramAction,
                actions.diagramTabActivator);
        ui.addToToolbar(actions.redrawAction, actions.diagramTabActivator);

        ui.addToolbarSeparator();

        ui.addToToolbar(actions.fullScreenAction,
                actions.supportsFullScreenActivator);
        ui.addToToolbar(actions.splitLeftRightAction,
                actions.horizontalSplitPossibleActivator);
        ui.addToToolbar(actions.splitTopBottomAction,
                actions.verticalSplitPossibleActivator);

        ui.addToolbarSeparator();

        ui.addToToolbar(actions.homeAction, actions.homeActivator);
        ui.addToToolbar(actions.previousAction, actions.previousActivator);
        ui.addToToolbar(actions.nextAction, actions.nextActivator);

        ui.addAction("E&xtras", actions.serverAction, null);
        ui.addAction("E&xtras", actions.filterAction, actions.textTabActivator);
        ui.addAction("E&xtras", new ExportMapAction(this),
                actions.nonEmptyDiagramActivator);

        ui.addAction("&Help", actions.helpAction, null);
        ui.addAction("&Help", actions.helpOnMultithreadingAction, null);
        ui.addAction("&Help", actions.asyncNotesAction, null);
        if (OS.TYPE != OS.Type.MAC) {
            ui.addAction("&Help", actions.showAboutDialogAction, null);
        }

        ui.addAction("&Help.&Examples",
                actions.getExampleAction("Ticket order", "order.sdx"), null);
        ui.addAction("&Help.&Examples",
                actions.getExampleAction("Breadth first search", "bfs.sdx"),
                null);
        ui.addAction("&Help.&Examples",
                actions.getExampleAction("Levels and mnemonics", "levels.sdx"),
                null);
        ui.addAction("&Help.&Examples", actions.getExampleAction(
                "SSH 2 (by courtesy of Carlos Duarte)", "ssh.sdx"), null);
        ui.addAction("&Help.&Examples",
                actions.getExampleAction("Webserver", "webserver.sdx"), null);

    }

    public DiagramFileHandler getDefaultFileHandler() {
        return defaultFileHandler;
    }

    public boolean isSetup() {
        return setup;
    }

    public void quit() {
        if (closeAll()) {
            writeRecentFiles();
            try {
                ConfigurationManager.storeConfigurations();
            } catch (IOException e) {
                e.printStackTrace();
                ui.errorMessage(e, null,
                        "Could not save the global preferences file.");
            }
            if (server != null) {
                server.shutDown();
            }
            ui.exit();
            if (Eclipse.getEclipse() == null) {
                System.exit(0);
            }
        }
    }

    /**
     * Returns true if ALL tabs could be closed.
     */
    boolean closeAll() {
        boolean confirmed = true;
        Ref<Boolean> noToAll = new Ref<Boolean>(false);
        for (Tab tab : ui.getTabContainer().getTabs()) {
            ui.selectTab(tab);
            if (!tab.isReadyToBeClosed(noToAll)) {
                confirmed = false;
                break;
            }
            if (noToAll.t) {
                confirmed = true;
                break;
            }
        }
        if (confirmed) {
            for (Tab tab : ui.getTabContainer().getTabs()) {
                tab.close(false);
            }
            return true;
        }
        return false;
    }

    /**
     * Returns the user interface.
     * 
     * @return the user interface
     */
    public UserInterface getUI() {
        return ui;
    }

    private UserInterface newUI() {
        return new UserInterfaceImpl();
    }

    public void tabChanged(Tab previousTab, Tab currentTab) {

        if (previousTab != null) {
            previousTab.deactivate(actionManager, fileActionProvider);
        }

        if (currentTab != null) {
            currentTab.activate(actionManager, fileActionProvider);
        }
    }
}
