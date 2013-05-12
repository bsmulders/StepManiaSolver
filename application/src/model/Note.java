/*
This file is part of StepManiaSolver

Copyright (c) 2013, Bobbie Smulders

Contact:  mail@bsmulders.com

GNU General Public License Usage
This file may be used under the terms of the GNU General Public License version 3.0 as published by the Free Software Foundation and appearing in the file LICENSE included in the packaging of this file.  Please review the following information to ensure the GNU General Public License version 3.0 requirements will be met: http://www.gnu.org/copyleft/gpl.html.
 */

package model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Note {

	private String description;
	private boolean error = false;
	private Date creationDate;

	private Note() {
		setCreationDate(new Date());
	}

	protected Note(String description) {
		this();
		setDescription(description);
	}

	protected Note(String description, boolean error) {
		this(description);
		setError(error);
	}

	// Getters and setters
	public String getDescription() {
		return description;
	}

	private void setDescription(String description) {
		this.description = description;
	}

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	private void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	// Helper method
	public String toHTML() {
		SimpleDateFormat shortFormat = new SimpleDateFormat("HH:mm:ss");
		SimpleDateFormat msFormat = new SimpleDateFormat("SSS");
		String locale = shortFormat.format(getCreationDate());
		String milliseconds = msFormat.format(getCreationDate());

		if (isError()) {
			return String.format("%s<font color=\"#BBBBBB\">.%s</font>: <font color=\"red\">%s</font><br/>",
					locale, milliseconds, getDescription());
		} else {
			return String.format("%s<font color=\"#BBBBBB\">.%s</font>: %s<br/>", locale, milliseconds,
					getDescription());
		}
	}

	public String toString() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
		String timeLocale = dateFormat.format(getCreationDate());

		return String.format("%s: %s\n", timeLocale, getDescription());
	}
}
