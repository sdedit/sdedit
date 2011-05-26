package net.sf.sdedit.ui.components;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sf.sdedit.icons.Icons;
import net.sf.sdedit.util.Bijection;
import net.sf.sdedit.util.Utilities;

@SuppressWarnings("serial")
public class DateSwitcher extends JPanel implements ActionListener {
    
    private static Bijection<Integer,Integer> dateComponents;
    
    static {
        dateComponents = new Bijection<Integer,Integer>();
        dateComponents.add(0, Calendar.YEAR);
        dateComponents.add(1, Calendar.MONTH);
        dateComponents.add(2, Calendar.WEEK_OF_YEAR);
        dateComponents.add(3, Calendar.DAY_OF_MONTH);
        dateComponents.add(4, Calendar.HOUR_OF_DAY);
        dateComponents.add(5, Calendar.MINUTE);
        dateComponents.add(6, Calendar.SECOND);
        dateComponents.add(7, Calendar.MILLISECOND);
    }

    public static interface DateSwitcherListener {

        public void dateSwitched(DateSwitcher source, Date newDate);

    }

    private JLabel dateLabel;

    private JButton previousButton;

    private JButton nextButton;
    
    private JButton reloadButton;

    private List<DateSwitcherListener> listeners;

    private final DateFormat dateFormat;

    private GregorianCalendar calendar;
    
    private int granularity;

    public DateSwitcher(String dateFormat, int granularity) {
        this(new SimpleDateFormat(dateFormat), granularity);
    }

    private void init() {
        setLayout(new BorderLayout());
        JPanel datePanel = new JPanel();
        datePanel.setLayout(new BorderLayout());
        dateLabel = new JLabel();
        dateLabel.setHorizontalAlignment(JLabel.CENTER);
        datePanel.add(dateLabel, BorderLayout.CENTER);
        reloadButton = new JButton(Icons.getIcon("reload"));
        reloadButton.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
        datePanel.add(reloadButton, BorderLayout.EAST);
        
        previousButton = new JButton(Icons.getIcon("previous"));
        previousButton.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 5));
        nextButton = new JButton(Icons.getIcon("next"));
        nextButton.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 2));
        add(previousButton, BorderLayout.WEST);
        add(datePanel, BorderLayout.CENTER);
        add(nextButton, BorderLayout.EAST);
        listeners = new LinkedList<DateSwitcherListener>();
        previousButton.addActionListener(this);
        nextButton.addActionListener(this);
        reloadButton.addActionListener(this);
        calendar = new GregorianCalendar(TimeZone.getDefault());

    }

    public DateSwitcher(DateFormat dateFormat, int granularity) {
        this.dateFormat = dateFormat;
        this.granularity = dateComponents.getPreImage(granularity);
        init();
    }

    public DateSwitcher() {
        this(DateFormat.getDateInstance(), Calendar.DAY_OF_MONTH);
    }

    public void addDateSwitcherListener(DateSwitcherListener listener) {
        listeners.add(listener);
    }

    public void removeDateSwitcherListener(DateSwitcherListener listener) {
        listeners.remove(listener);
    }

    public void setDate(Date date) {
        calendar.setTime(date);
        if (granularity == 2) {
            return;
        }
        for (int i = granularity+1; i < dateComponents.size(); i++) {
            int r = 0;
            if (granularity == 1 || granularity == 0) {
                r = 1;
            }
            if (i != 2) {
                calendar.set(dateComponents.getImage(i), r);
            }
        }
        update();
    }

    public void update() {
        dateLabel.setText(formatDate());
        for (DateSwitcherListener listener : Collections.checkedCollection(
                listeners, DateSwitcherListener.class)) {
            listener.dateSwitched(this, calendar.getTime());
        }
        dateLabel.requestFocusInWindow();
    }
    
    public String formatDate () {
        return dateFormat.format(calendar.getTime());
    }

    public Date getDate() {
        return calendar.getTime();
    }

    private void nextDate() {
        calendar.add(dateComponents.getImage(granularity), 1);
        update();
    }

    private void previousDate() {
        calendar.add(dateComponents.getImage(granularity), -1);
        update();
    }

    public void actionPerformed(ActionEvent e) {
        switch (Utilities.iIn(e.getSource(), previousButton, nextButton, reloadButton)) {
        case 0:
            previousDate();
            break;
        case 1: 
            nextDate();
            break;
        case 2:
            update();
        }
    }
}
