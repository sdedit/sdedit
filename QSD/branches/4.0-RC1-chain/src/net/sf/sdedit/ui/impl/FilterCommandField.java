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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import net.sf.sdedit.ui.components.TextArea;

public class FilterCommandField extends JTextField implements ActionListener,
        KeyListener, Runnable
{
    private ArrayList<String> history;

    private int historyPointer;

    private String currentText;

    private String homeDir;

    private boolean first;

    private Process process;

    private String text;

    private boolean selection;
    
    private TextArea textArea;
    
    private final DiagramTextTab tab;

    public FilterCommandField(DiagramTextTab tab) {
        this.tab = tab;
        addActionListener(this);
        addKeyListener(this);
        history = new ArrayList<String>();
        historyPointer = 0;
        currentText = null;
        homeDir = System.getProperty("user.home");
        first = true;
    }

    public void reset() {
        currentText = null;
        if (first) {
            setText("<enter filter command>");
            selectAll();
            first = false;
        } else {
            setText("");
        }
        historyPointer = history.size();
    }

    private void execute(String command) throws IOException {
        history.add(command);
        if (homeDir != null && !homeDir.equals("")) {
            command = command.replaceAll("~", homeDir);
        }
        selection = true;
        if (tab == null) {
            return;
        }
        textArea = tab.getTextArea();
        text = tab.getTextArea().getSelectedText();
        if (text == null) {
            selection = false;
            text = tab.getTextArea().getText();
        }
        synchronized (this) {
            process = Runtime.getRuntime().exec(command);
            changeBackground(Color.RED);
        }
        new Thread(this).start();
    }

    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));
            BufferedReader errorReader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream()));
            OutputStreamWriter osw = new OutputStreamWriter(
                    new BufferedOutputStream(process.getOutputStream()));
            PrintWriter writer = new PrintWriter(osw);
            writer.print(text);
            writer.flush();
            writer.close();
            StringBuffer output = new StringBuffer();
            String line = reader.readLine();
            while (line != null) {
                output.append(line + "\n");
                line = reader.readLine();
            }
            StringBuffer errors = new StringBuffer();
            line = errorReader.readLine();
            while (line != null) {
                errors.append(line + "\n");
                line = errorReader.readLine();
            }
            reader.close();
            errorReader.close();
            final String result = output.toString().trim();
            String error = errors.toString().trim();
            if (!error.equals("")) {
                tab.get_UI().errorMessage(null, null, "The filter command returned an error:\n"
                        + error);
            } else {
                SwingUtilities.invokeLater(new Runnable()
                {
                    public void run() {
                        if (selection) {
                            textArea.replaceSelection(result);
                        } else {
                            textArea.setText(result);
                        }
                    }
                });
            }
        } catch (IOException e) {
            synchronized (this) {
                if (process != null) {
                    tab.get_UI().errorMessage(e, null, null);
                }
                // Ignore the exception that is thrown when the process is
                // destroyed and set to null.
            }
        } finally {
            synchronized (this) {
                process = null;
                changeBackground (Color.WHITE);
            }
            tab.leaveFilterMode();
        }
    }
    
    private void changeBackground (final Color color) {
        SwingUtilities.invokeLater(new Runnable() {
           public void run () {
               setBackground(color);
           }
        });
    }

    public void actionPerformed(ActionEvent e) {
        if (process != null) {
            return;
        }
        String command = getText().trim();
        if (command.equals("")) {
            return;
        }
        try {
            execute(command);
        } catch (IOException ex) {
            tab.get_UI().errorMessage(ex, null, null);
        }
    }

    private void useHistory(int direction) {
        if (historyPointer == history.size() && direction > 0) {
            return;
        }
        if (historyPointer == 0 && direction < 0) {
            return;
        }
        historyPointer += direction;
        if (historyPointer == history.size()) {
            setText(currentText);
            currentText = null;
        } else {
            if (currentText == null) {
                currentText = getText();
            }
            setText(history.get(historyPointer));
        }
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            useHistory(-1);
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            useHistory(1);
        } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            synchronized (this) {
                if (process != null) {
                    process.destroy();
                    process = null;
                } else {
                    tab.leaveFilterMode();
                }
            }
        }
    }

    public void keyReleased(KeyEvent e) {}

    public void keyTyped(KeyEvent e) {}

}
