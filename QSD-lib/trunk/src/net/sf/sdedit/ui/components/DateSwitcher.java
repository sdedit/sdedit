package net.sf.sdedit.ui.components;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.ParseException;
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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.sf.sdedit.icons.Icons;
import net.sf.sdedit.util.UIUtilities;

@SuppressWarnings("serial")
public class DateSwitcher extends JPanel implements ActionListener {

    public static interface DateSwitcherListener {

        public void dateSwitched(DateSwitcher source, Date newDate);

    }

    private JLabel dateLabel;

    private JButton previousButton;

    private JButton nextButton;

    private List<DateSwitcherListener> listeners;

    private final DateFormat dateFormat;

    private GregorianCalendar calendar;

    public DateSwitcher(String dateFormat) {
        this(new SimpleDateFormat(dateFormat));
    }

    private void init() {
        setLayout(new BorderLayout());
        dateLabel = new JLabel();
        dateLabel.setHorizontalAlignment(JLabel.CENTER);
        previousButton = new JButton(Icons.getIcon("previous"));
        previousButton.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 5));
        nextButton = new JButton(Icons.getIcon("next"));
        nextButton.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 2));
        add(previousButton, BorderLayout.WEST);
        add(dateLabel, BorderLayout.CENTER);
        add(nextButton, BorderLayout.EAST);
        listeners = new LinkedList<DateSwitcherListener>();
        previousButton.addActionListener(this);
        nextButton.addActionListener(this);
        calendar = new GregorianCalendar(TimeZone.getDefault());

    }

    public DateSwitcher(DateFormat dateFormat) {
        this.dateFormat = dateFormat;
        init();
    }

    public DateSwitcher() {
        this.dateFormat = DateFormat.getDateInstance();
        init();
    }

    public void addDateSwitcherListener(DateSwitcherListener listener) {
        listeners.add(listener);
    }

    public void removeDateSwitcherListener(DateSwitcherListener listener) {
        listeners.remove(listener);
    }

    public void setDate(Date date) {
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        update();
    }

    private void update() {
        dateLabel.setText(dateFormat.format(calendar.getTime()));
        for (DateSwitcherListener listener : Collections.checkedCollection(
                listeners, DateSwitcherListener.class)) {
            listener.dateSwitched(this, calendar.getTime());
        }
    }

    public Date getDate() {
        return calendar.getTime();
    }

    private void nextDay() {
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        update();
    }

    private void previousDay() {
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        update();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == previousButton) {
            previousDay();
        } else {
            nextDay();
        }

    }

    public static void main(String[] argv) {
        final JFrame frame = new JFrame();
        DateSwitcher dateSwitcher = new DateSwitcher();
        dateSwitcher.setDate(new Date());
        dateSwitcher.addDateSwitcherListener(new DateSwitcherListener() {

            public void dateSwitched(DateSwitcher source, Date newDate) {
                System.out.println(newDate);

            }

        });
        frame.getContentPane().add(dateSwitcher);
        frame.setVisible(true);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                frame.pack();
                UIUtilities.centerWindow(frame);
            }
        });
    }

}
