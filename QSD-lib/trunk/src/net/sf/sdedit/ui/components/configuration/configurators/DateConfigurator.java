package net.sf.sdedit.ui.components.configuration.configurators;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyDescriptor;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import net.sf.sdedit.ui.components.configuration.Adjustable;
import net.sf.sdedit.ui.components.configuration.Bean;
import net.sf.sdedit.ui.components.configuration.Configurator;
import net.sf.sdedit.ui.components.configuration.DataObject;

public class DateConfigurator<C extends DataObject> extends
		Configurator<Date, C> implements FocusListener, KeyListener{

	/*
	 * public class DateParser { public static void main( String args[] ) {
	 * DateFormat formater = DateFormat.getDateTimeInstance( ); try { Date date
	 * = formater.parse( "23.7.2002 12:54:56" ); Calendar cal = new
	 * GregorianCalendar( TimeZone.getTimeZone("ECT") ); cal.setTime( date );
	 * 
	 * DateFormat formater2 = DateFormat.getDateTimeInstance( DateFormat.LONG,
	 * DateFormat.LONG ); System.out.println( formater2.format( cal.getTime() )
	 * );
	 * 
	 * } catch ( ParseException e ) { System.err.println( e ); } } }
	 */
	//private static DateFormat dateFormat = DateFormat.getInstance();
	

	private DateFormat dateFormat;

	private GregorianCalendar calendar;

	private JTextField textField;
	
	private int stepMinutes;

	public DateConfigurator(Bean<C> bean, PropertyDescriptor property) {
		super(bean, property);
		setLayout(new BorderLayout());
		String datePattern = property.getWriteMethod().getAnnotation(Adjustable.class).datePattern();
		if (datePattern != null && datePattern.length() > 0) {
			dateFormat = new SimpleDateFormat();
			((SimpleDateFormat) dateFormat).applyPattern(datePattern);
		} else {
			dateFormat = DateFormat.getInstance();
		}
		dateFormat.setLenient(true);
		JLabel label = new JLabel(getProperty().getWriteMethod().getAnnotation(Adjustable.class).info() + ":"); 
	    label.setHorizontalAlignment(SwingConstants.RIGHT);
		add(label, BorderLayout.WEST);

		calendar = new GregorianCalendar(TimeZone.getDefault());
		textField = new JTextField();
		textField.addFocusListener(this);
	    add(textField, BorderLayout.CENTER);
	    stepMinutes = property.getWriteMethod().getAnnotation(Adjustable.class).step();
	    textField.addKeyListener(this);

	}

	@Override
	protected void _actionPerformed(ActionEvent evt) {
		Date date;
		try {
			date = dateFormat.parse(textField.getText().trim());
		} catch (ParseException e) {
			refresh();
			return;
		}
		getBean().setValue(getProperty(), date);
		

	}

	@Override
	protected void _setEnabled(boolean enabled) {
		textField.setEnabled(enabled);

	}
	
	public void changeHour (int diff) {
		calendar.setTime(getValue());
		calendar.add(Calendar.HOUR_OF_DAY, diff);
		refresh(calendar.getTime());
		actionPerformed(null);
	}
	
	public void changeMinutes (int diff) {
		calendar.setTime(getValue());
		calendar.add(Calendar.MINUTE, diff);
		refresh(calendar.getTime());
		actionPerformed(null);
	}

	@Override
	protected Date getNullValue() {
		return new Date(0);
	}

	@Override
	protected void refresh(Date value) {
		calendar.setTime(value);
		int minutes = calendar.get(Calendar.MINUTE);
		int mod = minutes % stepMinutes;
		if (mod > 0) {
			minutes -= mod;
			calendar.set(Calendar.MINUTE, minutes);
			
		}
		textField.setText(dateFormat.format(calendar.getTime()));
	}

	public void focusGained(FocusEvent e) {}

	public void focusLost(FocusEvent e) {
		actionPerformed(null);
	
		
	}

	public void keyPressed(KeyEvent e) {

		
	}

	public void keyReleased(KeyEvent e) {
		if (e.isAltDown()) {
			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				changeMinutes(-stepMinutes);
			} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				changeMinutes(stepMinutes);
			} else if (e.getKeyCode() == KeyEvent.VK_UP) {
				changeHour(1);
			} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
				changeHour(-1);
			}
		}
		
	}

	public void keyTyped(KeyEvent e) {

		
	}

}
