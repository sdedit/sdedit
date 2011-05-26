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
		Configurator<Date, C> implements FocusListener, KeyListener {

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
	// private static DateFormat dateFormat = DateFormat.getInstance();
	private DateFormat dateFormat;

	private GregorianCalendar calendar;

	private JTextField textField;

	private int stepMinutes;

	public DateConfigurator(Bean<C> bean, PropertyDescriptor property) {
		super(bean, property);
		setLayout(new BorderLayout());
		String datePattern = property.getWriteMethod().getAnnotation(
				Adjustable.class).datePattern();
		if (datePattern != null && datePattern.length() > 0) {
			dateFormat = new SimpleDateFormat();
			((SimpleDateFormat) dateFormat).applyPattern(datePattern);
		} else {
			dateFormat = DateFormat.getInstance();
		}
		dateFormat.setLenient(true);
		JLabel label = new JLabel(getProperty().getWriteMethod().getAnnotation(
				Adjustable.class).info()
				+ ":");
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		add(label, BorderLayout.WEST);

		calendar = new GregorianCalendar(TimeZone.getDefault());
		textField = new JTextField();
		textField.addFocusListener(this);
		add(textField, BorderLayout.CENTER);
		stepMinutes = property.getWriteMethod().getAnnotation(Adjustable.class)
				.step();
		textField.addKeyListener(this);

	}

	@Override
	protected void _actionPerformed(ActionEvent evt) {
		Date date;
		if ("".equals(textField.getText().trim())) {
			date = getNullValue();
		} else {
			try {
				date = dateFormat.parse(textField.getText().trim());
			} catch (ParseException e) {
				refresh();
				return;
			}
		}
		getBean().setValue(getProperty(), date);

	}

	@Override
	protected void _setEnabled(boolean enabled) {
		textField.setEnabled(enabled);

	}

	public void changeHour(int diff) {
		calendar.setTime(getValue());
		calendar.add(Calendar.HOUR_OF_DAY, diff);
		refresh(calendar.getTime());
		actionPerformed(null);
	}

	public void changeMinutes(int diff) {
		calendar.setTime(getValue());
		calendar.add(Calendar.MINUTE, diff);
		refresh(calendar.getTime());
		actionPerformed(null);
	}

	protected void setToCurrentTime() {
		calendar.setTime(new Date());
		calendar.set(Calendar.SECOND, 0);
		refresh(calendar.getTime());
		actionPerformed(null);
	}

	@Override
	protected void refresh(Date value) {
		if (value.equals(getNullValue())) {
			textField.setText("");
		} else {
			calendar.setTime(value);
			int minutes = calendar.get(Calendar.MINUTE);
			int mod = minutes % stepMinutes;
			if (mod > 0) {
				minutes -= mod;
				calendar.set(Calendar.MINUTE, minutes);
				getBean().setValue(getProperty(), calendar.getTime());

			}

			textField.setText(dateFormat.format(calendar.getTime()));
		}
	}

	public void focusGained(FocusEvent e) {
	}

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
		} else if (e.isControlDown() && e.isShiftDown()) {
			if (e.getKeyCode() == KeyEvent.VK_SPACE) {
				setToCurrentTime();
			}
		} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			refresh();
		}

	}

	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void focus() {
		textField.selectAll();
		textField.requestFocusInWindow();

	}

}
