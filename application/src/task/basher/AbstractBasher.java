/*
This file is part of StepManiaSolver

Copyright (c) 2013, Bobbie Smulders

Contact:  mail@bsmulders.com

GNU General Public License Usage
This file may be used under the terms of the GNU General Public License version 3.0 as published by the Free Software Foundation and appearing in the file LICENSE included in the packaging of this file.  Please review the following information to ensure the GNU General Public License version 3.0 requirements will be met: http://www.gnu.org/copyleft/gpl.html.
 */

package task.basher;

import java.util.ArrayList;

public abstract class AbstractBasher {

	ArrayList<ButtonBasherListener> listeners = new ArrayList<ButtonBasherListener>();
	private long nextScan;

	// Settings
	private int scanInterval;

	// Getters and setters
	protected long getNextScan() {
		return nextScan;
	}

	protected void setNextScan(long nextScan) {
		this.nextScan = nextScan;
	}

	public int getScanInterval() {
		return scanInterval;
	}

	public void setScanInterval(int scanInterval) {
		this.scanInterval = scanInterval;
	}

	// Listener pattern methods
	public void addListener(ButtonBasherListener bbl) {
		getListeners().add(bbl);
	}

	public void removeListener(ButtonBasherListener bbl) {
		getListeners().remove(bbl);
	}

	private ArrayList<ButtonBasherListener> getListeners() {
		return listeners;
	}

	protected void sendButtonBashUpdate(int key) {
		for (ButtonBasherListener bbl : getListeners()) {
			bbl.processButtonBash(key);
		}
	}
}
