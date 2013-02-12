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

package net.sf.sdedit.ui.impl;

import static javax.swing.SwingUtilities.invokeLater;
import static javax.swing.SwingUtilities.isEventDispatchThread;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.sf.sdedit.Constants;
import net.sf.sdedit.config.Configuration;
import net.sf.sdedit.config.ConfigurationManager;
import net.sf.sdedit.config.GlobalConfiguration;
import net.sf.sdedit.diagram.Diagram;
import net.sf.sdedit.diagram.DiagramDataProvider;
import net.sf.sdedit.diagram.Lifeline;
import net.sf.sdedit.drawable.Arrow;
import net.sf.sdedit.drawable.Drawable;
import net.sf.sdedit.drawable.Figure;
import net.sf.sdedit.drawable.LabeledBox;
import net.sf.sdedit.editor.Editor;
import net.sf.sdedit.editor.EditorHint;
import net.sf.sdedit.editor.EditorHintFactory;
import net.sf.sdedit.editor.plugin.FileActionProvider;
import net.sf.sdedit.editor.plugin.FileHandler;
import net.sf.sdedit.error.DiagramError;
import net.sf.sdedit.error.FatalError;
import net.sf.sdedit.error.SemanticError;
import net.sf.sdedit.icons.Icons;
import net.sf.sdedit.message.ForwardMessage;
import net.sf.sdedit.text.TextHandler;
import net.sf.sdedit.ui.components.AutoCompletion;
import net.sf.sdedit.ui.components.AutoCompletion.SuggestionProvider;
import net.sf.sdedit.ui.components.TextArea;
import net.sf.sdedit.ui.components.buttons.ActionManager;
import net.sf.sdedit.ui.components.buttons.Activator;
import net.sf.sdedit.ui.components.configuration.Bean;
import net.sf.sdedit.ui.components.configuration.configurators.KeyStrokeConfigurator;
import net.sf.sdedit.util.Pair;
import net.sf.sdedit.util.PopupActions;
import net.sf.sdedit.util.Ref;
import net.sf.sdedit.util.collection.MultiMap;

/**
 * A single tab in the user interface, consisting of a diagram view, a text pane
 * and a status bar that can be exchanged by a text field for entering a filter
 * command applied to the text in the pane. All methods that depend on or change
 * the state of GUI components on the screen use the event dispatch thread
 * internally.
 * 
 * @author Markus Strauch
 * 
 */
public class DiagramTextTab extends DiagramTab implements DocumentListener,
        SuggestionProvider, PropertyChangeListener, ActionListener,
        PopupActions.ContextHandler {

    private static final long serialVersionUID = -4105088603920744983L;

    // final private UserInterfaceImpl ui;

    final private JLabel errorLabel;

    final private JLabel hintLabel;

    final private JLabel statusLabel;

    final private JPanel bottomPanel;

    final private TextArea textArea;

    final private FilterCommandField filterField;

    final private JPanel statusPanel;

    /**
     * This string is set to the contents of the text area when it is to be
     * declared to be consistent via <tt>setClean(true)</tt> or when a file is
     * loaded.
     */
    private String code;

    /**
     * The index of the character in the text-area where an erroreous line
     * starts, or -1. See {@linkplain #setError(boolean, String, int, int)}.
     */
    private int errorCharIndex;

    private EditorHint hint;

    private JSplitPane splitter;

    private JScrollPane textScroller;

    private boolean filterMode;

    private Bean<Configuration> oldConfiguration;

    private GlobalConfiguration globalConf;

    private boolean changed;

    private Timer changeTimer;

    private boolean mustScroll;

    private Timer scrollTimer;

    private boolean ignoreChanges;

    private Map<String, ClassTab> classTabs;

    public DiagramTextTab(UserInterfaceImpl ui, Font codeFont,
            Bean<Configuration> configuration

    ) {
        super(ui);
        textArea = new TextArea();
        globalConf = ConfigurationManager.getGlobalConfigurationBean()
                .getDataObject();
        ConfigurationManager.getGlobalConfigurationBean()
                .addPropertyChangeListener(this);

        textArea.setFont(codeFont);
        textArea.getDocument().addDocumentListener(this);
        textArea.setMinimumSize(new Dimension(100, 100));
        textArea.addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent ev) {

                if (ev != null) {

                    KeyStroke keyStroke = KeyStroke.getKeyStroke(
                            ev.getKeyCode(), ev.getModifiers());

                    if (keyStroke == KeyStrokeConfigurator.NULL_KEYSTROKE) {
                        return;
                    }

                    if (hint != null
                            && keyStroke.equals(globalConf
                                    .getAcceptHintKeyStroke())) {
                        hint.execute();
                    } else if (keyStroke.equals(globalConf.getCutKeyStroke())) {
                        textArea.cut();
                    } else if (keyStroke.equals(globalConf.getCopyKeyStroke())) {
                        textArea.copy();
                    } else if (keyStroke.equals(globalConf.getPasteKeyStroke())) {
                        textArea.paste();
                    }

                    /*
                     * else if (keyStroke.equals(globalConf
                     * .getNextTabKeyStroke())) { ui.nextTab(); } else if
                     * (keyStroke.equals(globalConf .getPreviousTabKeyStroke()))
                     * { ui.previousTab(); }
                     */
                }
            }
        });
        textArea.addCaretListener(new CaretListener() {

            public void caretUpdate(CaretEvent e) {
                if (globalConf.isAutoScroll()) {
                    mustScroll = true;
                    scrollTimer.restart();
                }
            }
        });
        new AutoCompletion(textArea, this, '=', ':', '>');

        filterField = new FilterCommandField(this);
        filterMode = false;
        textScroller = new JScrollPane();
        textScroller.getVerticalScrollBar().setUnitIncrement(30);
        textScroller.getHorizontalScrollBar().setUnitIncrement(30);

        setLineWrap(configuration.getDataObject().isLineWrap());
        splitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT, getZoomPane(),
                textScroller);
        splitter.setOneTouchExpandable(true);
        splitter.setResizeWeight(0.8);
        getContentPanel().setLayout(new BorderLayout());
        getContentPanel().add(splitter, BorderLayout.CENTER);
        errorCharIndex = -1;
        code = "";
        errorLabel = new JLabel("");
        statusLabel = new JLabel("");
        errorLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (errorCharIndex > -1) {
                    errorLabel.setCursor(Constants.HAND_CURSOR);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                errorLabel.setCursor(Cursor.getDefaultCursor());
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (errorCharIndex > -1) {
                    moveCursorToPosition(errorCharIndex);
                }
            }
        });
        hintLabel = new JLabel("");
        hintLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 4));
        hintLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (hint != null) {
                    hintLabel.setCursor(Constants.HAND_CURSOR);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hintLabel.setCursor(Cursor.getDefaultCursor());
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (hint != null) {
                    hint.execute();
                }

            }
        });

        changeTimer = new Timer(20 * ConfigurationManager
                .getGlobalConfiguration().getAutodrawLatency(), this);
        changeTimer.start();
        changed = false;

        scrollTimer = new Timer(20 * ConfigurationManager
                .getGlobalConfiguration().getAutodrawLatency(), this);
        scrollTimer.start();
        mustScroll = false;

        statusPanel = getStatusPanel();
        statusPanel.setLayout(new BorderLayout());
        bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());

        bottomPanel.add(errorLabel, BorderLayout.CENTER);
        bottomPanel.add(statusLabel, BorderLayout.EAST);
        bottomPanel.add(hintLabel, BorderLayout.WEST);
        bottomPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 20));

        statusPanel.add(bottomPanel, BorderLayout.CENTER);

        setConfiguration(configuration);
        setInteraction(new DiagramTextInteraction(this));
        textArea.getPopupActions(this).addAction(scrollToAction);
        classTabs = new TreeMap<String, ClassTab>();
    }

    public Icon getIcon() {
        return Icons.getIcon("text");
    }

    private void setLineWrap(boolean on) {
        if (on) {
            textScroller.setViewportView(textArea);
        } else {
            JPanel noWrapPanel = new JPanel(new BorderLayout());
            noWrapPanel.add(textArea);
            textScroller.setViewportView(noWrapPanel);
        }
    }

    private void somethingChanged() {
        if (!ignoreChanges) {
            changed = true;
            changeTimer.restart();
            invokeLater(new Runnable() {
                public void run() {
                    boolean isStained = textArea.getText().length() != code
                            .length()
                            || !textArea.getText().equals(code)
                            || !oldConfiguration.equals(getConfiguration());
                    setClean(!isStained);
                }
            });
        }

    }

    /**
     * 
     * @param layout
     *            0 for a split along the x-axis, 1 for the y-axis
     */
    public void layout(int layout) {
        remove(splitter);
        switch (layout) {

        case 0:
            splitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                    textScroller, getZoomPane());
            splitter.setResizeWeight(0.2);
            break;
        case 1:
            splitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT, getZoomPane(),
                    textScroller);
            splitter.setOneTouchExpandable(true);
            splitter.setResizeWeight(0.8);
            break;
        default:
            throw new IllegalArgumentException("layout " + layout
                    + " not supported");
        }
        splitter.setOneTouchExpandable(true);
        add(splitter, BorderLayout.CENTER);
        revalidate();
        getConfiguration().getDataObject().setVerticallySplit(layout == 1);
    }

    public TextArea getTextArea() {
        return textArea;
    }
    
    @Override
    public boolean isReadyToBeClosed (Ref<Boolean> noToAll) {
        boolean ready = super.isReadyToBeClosed(noToAll);
        if (ready) {
            removeClassTabs();
        }
        return ready;
    }

    @Override
    public boolean close(boolean check) {
        boolean close = super.close(check);
        if (close) {
            ConfigurationManager.getGlobalConfigurationBean()
                    .removePropertyChangeListener(this);
            changeTimer.stop();
        }
        return close;
    }

    public void setClean(boolean clean) {
        super.setClean(clean);
        if (clean) {
            code = textArea.getText();
            oldConfiguration = getConfiguration().copy();
        }
    }

    /**
     * Returns the code that is currently begin displayed by the text-area.
     * 
     * @return the code that is currently being displayed by the text-area.
     */
    public String getCode() {
        return textArea.getText();
    }

    /**
     * Changes the code displayed by the text-area. After that,
     * {@linkplain #isClean()} will return true.
     * 
     * @param code
     *            the code to be displayed by the text-area
     */
    public void setCode(final String code) {
        if (isEventDispatchThread()) {
            textArea.setText(code);
            setClean(true);
            goHome();
            return;
        }
        invokeLater(new Runnable() {
            public void run() {
                textArea.setText(code);
                setClean(true);
                goHome();
            }
        });
    }

    public void goHome() {
        super.goHome();
        textArea.setCaretPosition(0);
        textArea.requestFocusInWindow();
    }

    public int getCursorPosition() {
        return textArea.getCaretPosition();
    }

    public void moveCursorToPosition(int position) {
        textArea.requestFocusInWindow();
        textArea.setCaretPosition(0);
        textArea.setCaretPosition(position);

        Point p = textArea.getCaret().getMagicCaretPosition();
        if (p != null) {
            textArea.scrollRectToVisible(new Rectangle(p));
        }
    }

    public void undo() {
        textArea.undo();
    }

    public void redo() {
        textArea.redo();
    }

    void enterFilterMode() {
        if (filterMode) {
            return;
        }
        filterMode = true;
        invokeLater(new Runnable() {
            public void run() {
                filterField.reset();
                statusPanel.remove(bottomPanel);
                statusPanel.add(filterField, BorderLayout.CENTER);
                statusPanel.revalidate();
                filterField.requestFocus();
            }
        });
    }

    public void toggleFilterMode() {
        if (filterMode) {
            leaveFilterMode();
        } else {
            enterFilterMode();
        }
    }

    void leaveFilterMode() {
        if (!filterMode) {
            return;
        }
        filterMode = false;
        invokeLater(new Runnable() {
            public void run() {
                filterField.reset();
                statusPanel.remove(filterField);
                statusPanel.add(bottomPanel, BorderLayout.CENTER);
                statusPanel.revalidate();
            }
        });
    }

    protected void scrollToCurrentDrawable() {
        scrollToCurrentDrawable(true);
    }

    protected void scrollToCurrentDrawable(boolean highlight) {
        int begin = textArea.getCurrentLineBegin();
        Diagram diagram = getDiagram();
        if (diagram != null) {
            Drawable drawable = diagram.getDrawableForState(begin);
            if (drawable != null) {
                scrollToDrawable(drawable,
                        highlight && globalConf.isHighlightCurrent());
            } else {
                int caret = textArea.getCaretPosition();
                if (textArea.getText().substring(caret).trim().length() == 0) {
                    getZoomPane().scrollToBottom();
                }
            }
        }
    }

    public void append(final String text) {
        if (isEventDispatchThread()) {
            textArea.setText(textArea.getText() + text);
            // happens automatically via DocumentListener
            // redrawThread.indicateChange();
        } else {
            invokeLater(new Runnable() {
                public void run() {
                    textArea.setText(textArea.getText() + text);
                    // redrawThread.indicateChange();
                }
            });
        }
    }

    void setStatus(final String status) {
        invokeLater(new Runnable() {
            public void run() {
                statusLabel.setText(status + "    ");
            }
        });
    }

    void setError(final boolean warning, final String error, final int begin,
            final int end, final EditorHint hint) {
        invokeLater(new Runnable() {
            public void run() {
                if (warning) {
                    errorLabel.setForeground(Color.ORANGE);
                } else {
                    errorLabel.setForeground(Color.RED);
                }
                errorLabel.setText(error);
                errorCharIndex = begin;
                textArea.markError(begin, end);
                if (hint != null) {
                    hintLabel.setText("<html><u>[" + hint.getCaption() + "]");

                } else {
                    hintLabel.setText("");
                }
                DiagramTextTab.this.hint = hint;
            }
        });
    }

    /**
     * @see net.sf.sdedit.ui.components.AutoCompletion.SuggestionProvider#getSuggestions(java.lang.String)
     */
    public List<String> getSuggestions(String prefix) {
        String regexp = "^" + prefix.replaceAll("\\*", ".*") + ".*$";
        Pattern pattern = Pattern.compile(regexp);
        List<String> suggestions = new LinkedList<String>();
        Diagram diag = getDiagram();
        if (diag != null) {
            for (Lifeline lifeline : diag.getAllLifelines()) {
                String name = lifeline.getName();
                if (pattern.matcher(name).matches()) {
                    // if (name.startsWith(prefix)) {
                    suggestions.add(name);
                }
            }
        }
        return suggestions;
    }

    public void setConfiguration(Bean<Configuration> configuration) {
        super.setConfiguration(configuration);
        oldConfiguration = configuration.copy();
        layout(configuration.getDataObject().isVerticallySplit() ? 1 : 0);

    }

    /**
     * Called on configuration changes.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        super.propertyChange(evt);
        if (evt.getPropertyName().toLowerCase().equals("linewrap")) {
            boolean wrap = (Boolean) evt.getNewValue();
            setLineWrap(wrap);
        } else if (evt.getPropertyName().toLowerCase()
                .equals("autodrawlatency")) {
            changeTimer.stop();
            changeTimer.setDelay((Integer) evt.getNewValue());
            changeTimer.start();
        }
        somethingChanged();
    }

    /**
     * Called on source text changes.
     */
    public void changedUpdate(DocumentEvent e) {
        somethingChanged();
    }

    /**
     * Called on source text changes.
     */
    public void insertUpdate(DocumentEvent e) {
        somethingChanged();
    }

    /**
     * Called on source text changes.
     */
    public void removeUpdate(DocumentEvent e) {
        somethingChanged();
    }

    @Override
    protected List<Pair<Action, Activator>> getOverloadedActions() {
        return null;
    }

    @Override
    public FileHandler getFileHandler() {
        return Editor.getEditor().getDefaultFileHandler();
    }

    @Override
    public DiagramDataProvider getProvider() {
        return new TextHandler(getCode());
    }

    @Override
    public void activate(ActionManager actionManager,
            FileActionProvider faProvider) {
        super.activate(actionManager, faProvider);
        changed = false;
        changeTimer.restart();
        leaveFilterMode();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                getTextArea().requestFocusInWindow();
            }
        });
    }

    @Override
    public void deactivate(ActionManager actionManager,
            FileActionProvider faProvider) {
        super.deactivate(actionManager, faProvider);
        changeTimer.stop();
    }

    public void displayDiagram(Diagram diagram, DiagramError error) {
        super.displayDiagram(diagram, error);
        //addClassTabs();
    }

    @Override
    protected void handleDiagramError(DiagramError error) {
        // TODO Auto-generated method stub
        if (error == null) {
            setError(false, "", -1, -1, null);
            if (getDiagram().getFragmentManager().openFragmentsExist()) {
                setError(
                        true,
                        "Warning: There are open comments. Use [c:<type> <text>]...[/c]",
                        -1, -1, null);
            }

            int noteNumber = getDiagram().getNextFreeNoteNumber();
            if (noteNumber == 0) {
                setStatus("");
            } else {
                setStatus("Next note number: "
                        + getDiagram().getNextFreeNoteNumber());
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
                    handleBug(getDiagram(), fatal.getCause());
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            } else {
                TextHandler handler = (TextHandler) error.getProvider();
                String prefix = "";

                if (error instanceof SemanticError) {
                    prefix = getDiagram().isThreaded()
                            && getDiagram().getCallerThread() != -1 ? "Thread "
                            + getDiagram().getCallerThread() + ": " : "";
                }
                setError(false, prefix + error.getMessage(),
                        handler.getLineBegin() - 1, handler.getLineEnd(),
                        EditorHintFactory.createHint(this, error));
            }
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == changeTimer) {
            if (changed) {
                refresh(false);
            }
            changed = false;
        } else if (e.getSource() == scrollTimer) {
            if (mustScroll) {
                scrollToCurrentDrawable(true);
            }
            mustScroll = false;
        }
    }

    @Override
    protected void _getContextActions(List<Action> actionList) {
        /* empty */
    }

    @Override
    public boolean canClose() {
        return true;
    }

    public Object getObjectForCurrentContext(JComponent comp) {
        int begin = textArea.getCurrentLineBegin();
        Diagram diagram = getDiagram();
        if (diagram != null) {
            Drawable drawable = diagram.getDrawableForState(begin);
            return drawable;
        }
        return null;
    }

    protected PopupActions.Action scrollToAction = new PopupActions.Action() {

        private static final long serialVersionUID = 1L;

        private Drawable drawable;

        {
            putValue(Action.NAME, "Scroll to");
        }

        @Override
        protected boolean beforePopup(Object context) {
            if (context == null) {
                return false;
            }
            drawable = (Drawable) context;
            if (context instanceof LabeledBox) {
                putValue(Action.NAME, "Scroll to lifeline");
            } else if (context instanceof Arrow) {
                putValue(Action.NAME, "Scroll to message");
            } else if (context instanceof Figure) {
                putValue(Action.NAME, "Scroll to figure");
            } else {
                putValue(Action.NAME, "Scroll to");
            }
            return true;
        }

        public void actionPerformed(ActionEvent e) {
            scrollToDrawable(drawable, true);
        }

    };

    private void removeClassTabs() {
        for (ClassTab tab : classTabs.values()) {
            tab.forceClose();
        }
    }

    private void addClassTabs() {
        MultiMap<String, Lifeline, TreeSet<?>, TreeMap<?, ?>> classNames = new MultiMap<String, Lifeline, TreeSet<?>, TreeMap<?, ?>>(
                TreeSet.class, TreeMap.class);
        MultiMap<String, ForwardMessage, LinkedList<?>, TreeMap<?, ?>> messages = new MultiMap<String, ForwardMessage, LinkedList<?>, TreeMap<?, ?>>(
                LinkedList.class, TreeMap.class);

        for (Lifeline lifeline : getDiagram().getAllLifelines()) {
            String className;
            className = lifeline.getType();
            classNames.add(className, lifeline);
        }

        for (ForwardMessage msg : getDiagram().getMessages()) {
            Lifeline lifeline = msg.getCallee();
            if (lifeline == null) {
                lifeline = msg.getCaller();
            }
            String className = lifeline.getType();
            messages.add(className, msg);
        }

        if (!classNames.keySet().equals(classTabs.keySet())) {
            removeClassTabs();
            classTabs.clear();
            for (Entry<String, TreeSet<?>> entry : classNames.entrySet()) {
                ClassTab classTab = new ClassTab(this, entry.getKey());
                // entry.getValue(), messages.getValues(entry.getKey()));
                classTab.setTitle(entry.getKey());
                get_UI().getTabContainer().addChildTab(classTab, this, false,
                        null);
                classTabs.put(entry.getKey(), classTab);
            }
        }

        for (Entry<String, TreeSet<?>> entry : classNames.entrySet()) {
            @SuppressWarnings("unchecked")
            TreeSet<Lifeline> lifelinesOfClass = (TreeSet<Lifeline>) entry.getValue();
            classTabs.get(entry.getKey()).updateData(
                    lifelinesOfClass,
                    (LinkedList<ForwardMessage>) messages.getValues(entry
                            .getKey()));
        }

        // get_UI().selectTab(this);
    }

    public void setIgnoreChanges(boolean ignoreChanges) {
        this.ignoreChanges = ignoreChanges;
    }

    public boolean isIgnoreChanges() {
        return ignoreChanges;
    }

}
