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
// THE POSSIBILITY OF SUCH DAMAGE.// Copyright (c) 2006 - 2011, Markus Strauch.

package net.sf.sdedit.util;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.WeakHashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import net.sf.sdedit.icons.Icons;
import net.sf.sdedit.ui.components.OptionDialog;

public class UIUtilities {

    private static WeakHashMap<String, Ref<Method>> editableMethods = new WeakHashMap<String, Ref<Method>>();

    private static Method getEditableMethod(Object object) {
        Ref<Method> ref = editableMethods.get(object.getClass().getName());
        if (ref == null) {
            ref = new Ref<Method>();
            for (Method method : object.getClass().getMethods()) {
                if (method.getName().equals("setEditable")
                        && method.getParameterTypes().length == 1
                        && method.getParameterTypes()[0].equals(Boolean.TYPE)) {
                    ref.t = method;
                    break;
                }
            }
            editableMethods.put(object.getClass().getName(), ref);
        }
        return ref.t;
    }
    
    public static JPanel borderedPanel (JComponent component, int top, int left, int bottom, int right, boolean expand) {
    	JPanel panel = new JPanel();
    	if (expand) {
        	panel.setLayout(new FlowLayout());
        	panel.setLayout(new BorderLayout());
    		panel.add(component, BorderLayout.CENTER);
    	} else {
    		panel.add(component); 
    	}
    	panel.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
    	return panel;
    }

    public static void setColumnWidths(JTable table, int... widths) {
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (int i = 0; i < widths.length; i++) {
            TableColumn col = table.getColumnModel().getColumn(i);
            col.setPreferredWidth(widths[i]);
        }

    }

    public static void setTableCellRenderer(JTable table,
            TableCellRenderer renderer) {
        Enumeration<TableColumn> enumeration = table.getColumnModel()
                .getColumns();
        while (enumeration.hasMoreElements()) {
            enumeration.nextElement().setCellRenderer(renderer);
        }
    }

    public static String getOption(JFrame appFrame, String text,
            String... options) {
        OptionDialog optionDialog = new OptionDialog(appFrame,
                "Please choose an option", Icons.getIcon("question"), text);
        for (String option : options) {
            optionDialog.addOption(option);
        }
        return optionDialog.getOption();
    }

    public static void setEditable(Component comp, boolean editable) {
        Method method = getEditableMethod(comp);
        if (method != null) {
            try {
                method.invoke(comp, editable);
            } catch (RuntimeException re) {
                throw re;
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }

    }
    
    public static JFrame createExitingJFrame () {
    	WindowListener wl = new WindowAdapter () {

			@Override
			public void windowClosing(WindowEvent arg0) {
				System.exit(0);
			}
    		
    	};
    	JFrame frame = new JFrame ();
    	frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    	frame.addWindowListener(wl);
    	return frame;
    }

    public static void centerWindow(Window window) {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int width = window.getWidth();
        int height = window.getHeight();
        int left = Math.max(0, screen.width / 2 - width / 2);
        left = Math.min(left, screen.width - width);
        int top = Math.max(0, screen.height / 2 - height / 2);
        top = Math.min(top, screen.height - height);
        window.setLocation(left, top);
    }

    public static void centerWindow(Window window, Window parent) {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int width = window.getWidth();
        int height = window.getHeight();
        int left = Math.max(0, parent.getLocationOnScreen().x
                + parent.getSize().width / 2 - width / 2);
        left = Math.min(left, screen.width - width);
        int top = Math.max(0, parent.getLocationOnScreen().y
                + parent.getSize().height / 2 - height / 2);
        top = Math.min(top, screen.height - height);
        window.setLocation(left, top);
    }

    public static void showText(JFrame parent, String caption, String text) {
        JDialog textDialog = new JDialog(parent);
        textDialog.setTitle(caption);
        textDialog.setModal(true);
        textDialog.getContentPane().setLayout(new BorderLayout());
        JTextArea textArea = new JTextArea();
        textArea.setText(text);
        textArea.setFont(Font.decode("Monospace"));
        textDialog.getContentPane().add(new JScrollPane(textArea),
                BorderLayout.CENTER);
        textDialog.setSize(640, 480);
        centerWindow(textDialog, parent);
        textDialog.setVisible(true);
        textDialog.dispose();
    }

    public static void changeIconButton(JButton button) {
        button.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        button.setOpaque(false);
        button.setMargin(new Insets(1, 1, 1, 1));
    }

    public static void setGlobalFont(Font font) {

        UIManager.put("Button.font", font);
        UIManager.put("ToggleButton.font", font);
        UIManager.put("RadioButton.font", font);
        UIManager.put("CheckBox.font", font);
        UIManager.put("ColorChooser.font", font);
        UIManager.put("ComboBox.font", font);
        UIManager.put("Label.font", font);
        UIManager.put("List.font", font);
        UIManager.put("MenuBar.font", font);
        UIManager.put("MenuItem.font", font);
        UIManager.put("RadioButtonMenuItem.font", font);
        UIManager.put("CheckBoxMenuItem.font", font);
        UIManager.put("Menu.font", font);
        UIManager.put("PopupMenu.font", font);
        UIManager.put("OptionPane.font", font);
        UIManager.put("Panel.font", font);
        UIManager.put("ProgressBar.font", font);
        UIManager.put("ScrollPane.font", font);
        UIManager.put("Viewport.font", font);
        UIManager.put("TabbedPane.font", font);
        UIManager.put("Table.font", font);
        UIManager.put("TableHeader.font", font);
        UIManager.put("TextField.font", font);
        UIManager.put("PasswordField.font", font);
        UIManager.put("TextArea.font", font);
        UIManager.put("TextPane.font", font);
        UIManager.put("EditorPane.font", font);
        UIManager.put("TitledBorder.font", font);
        UIManager.put("ToolBar.font", font);
        UIManager.put("ToolTip.font", font);
        UIManager.put("Tree.font", font);

    }

    public static List<Component> getDescendants(Container container) {
        List<Component> descs = new LinkedList<Component>();
        collectDescendants(container, descs);
        return descs;
    }

    private static void collectDescendants(Container cont, List<Component> descs) {
        for (Component comp : cont.getComponents()) {
            descs.add(comp);
            if (comp instanceof Container) {
                collectDescendants((Container) comp, descs);
            }
        }
    }

    public static Image joinImages(Image img1, Image img2, int gap,
            int imageType) {
        int width = img1.getWidth(null) + img2.getWidth(null) + gap;
        int height = Math.max(img1.getHeight(null), img2.getHeight(null));
        Image join = new BufferedImage(width, height, imageType);
        join.getGraphics().drawImage(img1, 0, 0, null);
        join.getGraphics().drawImage(img2, img1.getWidth(null) + gap, 0, null);
        return join;

    }

    public static File affixType(File file, String type) {
        String fileName = file.getAbsolutePath();
        int dot = fileName.lastIndexOf('.');
        if (dot == -1) {
            return new File(fileName + "." + type);
        }
        String baseName = fileName.substring(0, dot);
        return new File(baseName + "." + type);
    }

    public static void addExtension(JFileChooser fileChooser, String extension,
            String description) {
        FileFilter existing = null;
        for (FileFilter ff : fileChooser.getChoosableFileFilters()) {
            if (ff instanceof FF) {
                if (((FF) ff).getExtension().equals(extension.toUpperCase())) {
                    existing = ff;
                    break;
                }
            }
        }
        if (existing != null) {
            fileChooser.removeChoosableFileFilter(existing);
        }
        fileChooser.addChoosableFileFilter(new FF(extension, description));
    }

    public static void setExtension(JFileChooser fileChooser, String extension) {
        for (FileFilter ff : fileChooser.getChoosableFileFilters()) {
            if (ff instanceof FF) {
                if (((FF) ff).getExtension().equals(extension.toUpperCase())) {
                    fileChooser.setFileFilter(ff);
                    return;
                }
            }
        }
    }
    
    public static void suggestName (JFileChooser fileChooser, String basename) {
        String name = basename;
        FileFilter ff = fileChooser.getFileFilter();
        if (ff instanceof FF) {
            name = name + "." + ((FF) ff).getExtension().toLowerCase();
        }
        File file = new File(fileChooser.getCurrentDirectory(), name);
        fileChooser.setSelectedFile(file);
    }
    
    public static JFrame show (JComponent comp) {
        final JFrame frame = new JFrame();
        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
            
        });
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(800,600);
        frame.setName(comp.getName());
        frame.getContentPane().add(new JScrollPane(comp));
        frame.setVisible(true);
        
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run () {
                UIUtilities.centerWindow(frame);
            }
        });
        
        return frame;
    }

    protected static class FF extends FileFilter {

        private String ext;

        private String description;

        protected FF(String ext, String description) {
            this.ext = ext.toUpperCase();
            this.description = description;
        }

        protected String getExtension() {
            return ext;
        }

        @Override
        public boolean accept(File pathname) {
            return pathname.isDirectory() || pathname.getAbsolutePath().toUpperCase().endsWith(ext);
        }

        @Override
        public String getDescription() {
            return description;
        }

    }
    
    public static class Bordered extends JPanel  {
        
        private static final long serialVersionUID = -6567329111561123261L;

        public Bordered (Component center, Component north, Component east, Component south, Component west) {
            setLayout(new BorderLayout());
            if (center != null) {
                add(center, BorderLayout.CENTER);
            }
            if (north != null) {
                add(north, BorderLayout.NORTH);
            }
            if (east != null) {
                add(east, BorderLayout.EAST);
            }
            if (south != null) {
                add(south, BorderLayout.SOUTH);
            }
            if (west != null) {
                add(west, BorderLayout.WEST);
            }
        }
    }
    
    public static class Grid extends JPanel {
        
        private static final long serialVersionUID = 1301195180733352164L;

        public Grid (int rows, int columns, Component... components) {
            setLayout(new GridLayout(rows, columns));
            for (int i = 0; i < components.length; i++) {
                add(components[i]);
            }
        }
        
    }
    
    public static void drawPolyline (Graphics g, Point [] points) {
    	int n = points.length;
    	int [] xp = new int [n];
    	int [] yp = new int [n];
    	for (int i = 0; i < n; i++) {
    		xp[i] = points[i].x;
    		yp[i] = points[i].y;
      	}
    	g.drawPolyline(xp, yp, n);
    }
    
}
