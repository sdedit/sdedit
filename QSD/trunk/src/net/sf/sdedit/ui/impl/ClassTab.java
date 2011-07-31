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

import java.awt.Component;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import net.sf.sdedit.diagram.Diagram;
import net.sf.sdedit.diagram.Lifeline;
import net.sf.sdedit.editor.plugin.FileHandler;
import net.sf.sdedit.icons.Icons;
import net.sf.sdedit.message.ForwardMessage;
import net.sf.sdedit.text.TextBasedMessageData;
import net.sf.sdedit.ui.Tab;
import net.sf.sdedit.ui.components.Zoomable;
import net.sf.sdedit.ui.components.buttons.Activator;
import net.sf.sdedit.util.EF;
import net.sf.sdedit.util.Grep.Region;
import net.sf.sdedit.util.Pair;
import net.sf.sdedit.util.TableModelAdapter;
import net.sf.sdedit.util.TableModelAdapter.RowEditor;
import net.sf.sdedit.util.TableModelAdapter.RowExpansion;
import net.sf.sdedit.util.Utilities;
import net.sf.sdedit.util.collection.MultiMap;

import com.zookitec.layout.ContainerEF;
import com.zookitec.layout.ExplicitConstraints;
import com.zookitec.layout.ExplicitLayout;
import com.zookitec.layout.Expression;
import com.zookitec.layout.MathEF;

public class ClassTab extends Tab implements RowExpansion, RowEditor {

    private static final long serialVersionUID = 7210793282951827425L;

    private String className;

    private MultiMap<String, ForwardMessage, LinkedList<?>, TreeMap<?, ?>> messages;

    private static final String[] LIFELINE_TABLE_COLUMN_NAMES = { "Name",
            "Anonymous", "External", "Thread" };

    private static final Class<?>[] LIFELINE_TABLE_COLUMN_TYPES = {
            String.class, Boolean.class, Boolean.class, Boolean.class };

    private static final String[] METHOD_TABLE_COLUMN_NAMES = { "Method",
            "#Occurrences" };

    private static final Class<?>[] METHOD_TABLE_COLUMN_TYPES = { String.class,
            String.class };

    private boolean canClose;

    private DiagramTextTab diagramTextTab;
    
    private JPanel content;
    
    private JLabel nameLabel;
    
    private static final int NAME_LABEL_MARGIN = 3;

    public ClassTab(DiagramTextTab diagramTextTab, String className) {
        super(diagramTextTab.get_UI());
        this.diagramTextTab = diagramTextTab;
        this.className = className;
        canClose = false;
        this.messages = new MultiMap<String, ForwardMessage, LinkedList<?>, TreeMap<?, ?>>(
                LinkedList.class, TreeMap.class);
    }

    private void addMessage(ForwardMessage message) {
        String method = message.getText();
        if (method != null && method.length() > 0) {
            int br = method.indexOf('(');
            if (br > 0) {
                method = method.substring(0, br);
            }
        }
        messages.add(method, message);
    }
    
    private void addNameLabel () {
        nameLabel = new JLabel();
        nameLabel.setText("Class " + className);
        nameLabel.setBorder(BorderFactory.createEmptyBorder(NAME_LABEL_MARGIN, NAME_LABEL_MARGIN, NAME_LABEL_MARGIN, NAME_LABEL_MARGIN));
        ExplicitConstraints c = new ExplicitConstraints(nameLabel);
        c.setX(EF.centeredX(content, nameLabel));
        content.add(nameLabel, c);
    }
    
    private JScrollPane addScrollPane (Component comp, Expression y, Expression height, String title, int borderWidth) {
        JScrollPane scrollPane = new JScrollPane(comp);
        ExplicitConstraints c = EF.inheritBounds(scrollPane, content);
        c.setY(y);
        c.setHeight(height);
        c.setX(ContainerEF.left(content));
        Border outer = BorderFactory.createEmptyBorder(borderWidth, borderWidth, borderWidth, borderWidth);
        Border inner = BorderFactory.createTitledBorder(title);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(outer, inner));
        content.add(scrollPane, c);
        return scrollPane;
    }
    
    public void updateData(Collection<Lifeline> lifelines,
            Collection<ForwardMessage> collection) {
        getContentPanel().removeAll();
        getContentPanel().setLayout(new ExplicitLayout());
        messages.clear();
        
        content = new JPanel();
        content.setLayout(new ExplicitLayout());
        ExplicitConstraints c = EF.inheritBounds(content, getContentPanel());
        c.setWidth(MathEF.min(MathEF.constant(640), ContainerEF.width(getContentPanel())));
        getContentPanel().add(content, c);

        addNameLabel();
        
        Expression height = EF.underHeight(nameLabel, 0).divide(2);
        Expression objPaneY = EF.underY(nameLabel, 0);
        Expression metPaneY = objPaneY.add(height);
        
        TableModelAdapter tma = new TableModelAdapter(
                LIFELINE_TABLE_COLUMN_NAMES, LIFELINE_TABLE_COLUMN_TYPES, this,
                this);
        tma.setData(lifelines);
        JTable lifelineTable = new JTable(tma);
        addScrollPane(lifelineTable, objPaneY, height, "Instances", 5);

        for (ForwardMessage message : collection) {
            addMessage(message);
        }

        TableModelAdapter methodTableModelAdapter = new TableModelAdapter(
                METHOD_TABLE_COLUMN_NAMES, METHOD_TABLE_COLUMN_TYPES, this,
                this);
        methodTableModelAdapter.setData(this.messages.entries());
        JTable methodTable = new JTable(methodTableModelAdapter);
        addScrollPane(methodTable, metPaneY, height, "Methods", 5);
        
        getContentPanel().invalidate();
        getContentPanel().revalidate();
    }

    @Override
    public Icon getIcon() {
        return Icons.getIcon("class");
    }

    @Override
    protected Zoomable<? extends JComponent> getZoomable() {
        return null;
    }

    @Override
    public boolean canClose() {
        return canClose;
    }

    @Override
    public boolean canGoHome() {
        return false;
    }

    @Override
    protected void _getContextActions(List<Action> actionList) {

    }

    @Override
    protected List<Pair<Action, Activator>> getOverloadedActions() {
        return null;
    }

    @Override
    public FileHandler getFileHandler() {
        return null;
    }

    public Object[] expand(Object row) {
        Object[] expanded = null;
        if (row instanceof Lifeline) {
            Lifeline line = (Lifeline) row;
            expanded = new Object[] { line.getName(), line.isAnonymous(), line.isExternal(), line.hasThread() };
        } else if (row instanceof Entry) {
            @SuppressWarnings("unchecked")
            Entry<String, Collection<ForwardMessage>> entry = (Entry<String, Collection<ForwardMessage>>) row;
            expanded = new Object[] { entry.getKey(), String.valueOf(entry.getValue().size()) };
        }
        return expanded;

    }

    public void forceClose() {
        canClose = true;
        close(false);
    }

    public boolean isEditable(Object row, int index) {
        if (row instanceof Lifeline) {
            return index == 0;
        }
        return false;
    }

    public void setValue(Object row, int index, Object value) {
        if (row instanceof Lifeline) {
            Lifeline lifeline = (Lifeline) row;
            if (index == 0) {
                String newName = (String) value;
                if (newName != null && newName.length() > 0) {
                    for (Lifeline existing : diagramTextTab.getDiagram()
                            .getAllLifelines()) {
                        if (existing.getName().equals(newName)) {
                            return;
                        }
                    }
                    try {
                        renameLifeline(lifeline, newName);
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                }

            }
        }

    }

    private int addWhitespace(Document document, int lineBegin)
            throws BadLocationException {
        int w = 0;
        while (Character.isWhitespace(document.getText(lineBegin + w - 1, 1)
                .charAt(0))) {
            w++;
        }
        return lineBegin + w;
    }

    private int replace(Document document, int lineBegin, Region region,
            String oldName, String newName) throws BadLocationException {
        int rs = region.getStart();
        String text = region.getText();
        int len = text.length();
        lineBegin = addWhitespace(document, lineBegin);
        String newText = Utilities.replaceFirst(text, oldName, newName);
        document.remove(lineBegin + rs - 1, len);
        document.insertString(lineBegin + rs - 1, newText, null);
        return newText.length() - text.length();
    }

    private void renameLifeline(Lifeline lifeline, String newName)
            throws BadLocationException {
        diagramTextTab.setIgnoreChanges(true);
        Diagram diagram = diagramTextTab.getDiagram();
        Document document = diagramTextTab.getTextArea().getDocument();

        int lineBegin = (Integer) diagram.getStateForDrawable(lifeline
                .getHead());
        int diff = replace(document, lineBegin, lifeline.getNameRegion(),
                lifeline.getName(), newName);
        diff = newName.length() - lifeline.getName().length();
        for (ForwardMessage msg : diagram.getMessages()) {
            lineBegin = diff
                    + (Integer) diagram.getStateForDrawable(msg.getArrow());
            TextBasedMessageData md = (TextBasedMessageData) msg.getData();
            if (lifeline.getName().equals(md.getCaller())) {
                diff += replace(document, lineBegin, md.getRegion("caller"),
                        lifeline.getName(), newName);
            }
            lineBegin = diff
                    + (Integer) diagram.getStateForDrawable(msg.getArrow());
            if (lifeline.getName().equals(md.getCallee())) {
                diff += replace(document, lineBegin, md.getRegion("callee"),
                        lifeline.getName(), newName);
            }
        }
        diagramTextTab.setIgnoreChanges(false);
        diagramTextTab.refresh(true);
    }

}
