/*
This file is part of StepManiaSolver

Copyright (c) 2013, Bobbie Smulders

Contact:  mail@bsmulders.com

GNU General Public License Usage
This file may be used under the terms of the GNU General Public License version 3.0 as published by the Free Software Foundation and appearing in the file LICENSE included in the packaging of this file.  Please review the following information to ensure the GNU General Public License version 3.0 requirements will be met: http://www.gnu.org/copyleft/gpl.html.
 */

package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import model.ControlMethod;
import model.DataPoint;
import model.Model;
import model.ViewMode;

import org.encog.neural.networks.BasicNetwork;

import task.CollectionComparatorListener;
import task.CommunicatorListener;
import task.NeuralNetworkTrainerListener;
import task.basher.ButtonBasherListener;
import view.View;

public class Controller implements ButtonBasherListener, CollectionComparatorListener,
		NeuralNetworkTrainerListener, CommunicatorListener, ActionListener, WindowListener {

	private Model model;
	private View view;

	public Controller(Model model) {
		setModel(model);
	}

	// Getters and setters
	private Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	private View getView() {
		return view;
	}

	public void setView(View view) {
		this.view = view;
	}

	// NeuralNetworkTrainerListener handlers
	@Override
	public void processNeuralNetworkTrainerResult(BasicNetwork neuralNetwork) {
		getModel().setNeuralNetwork(neuralNetwork);
	}

	@Override
	public void processNeuralNetworkIteration(int iteration, double error, int timeleft) {
		getModel().neuralNetworkIteration(iteration, error, timeleft);
	}

	// CollectionComperator handler
	@Override
	public void processCollectionComperatorResult(DataPoint dataPoint) {
		getModel().addDataHistory(dataPoint);
	}

	// CommunicatorListener handlers
	@Override
	public void processIncomingNote(double x, double y) {
		getModel().addDataPoint(new DataPoint(x, y));
	}

	@Override
	public void processIncomingVsync() {
		getModel().resetDataPoints();
	}

	@Override
	public void processClientConnection(boolean clientConnected) {
		getModel().setClientConnected(clientConnected);
	}

	// ButtonBasherListener handler
	@Override
	public void processButtonBash(int button) {
		getModel().pressButton(button);
	}

	// ActionListener handler
	@Override
	public void actionPerformed(ActionEvent e) {
		if (getView() != null) {
			if (e.getSource() == getView().getImportCSVItem()) {
				String filename = view.displayDialog("Import location",
						"Please enter the location for the CSV import", "/Users/bsmulders/export.csv");

				if (filename != null) {
					getModel().importCSV(filename);
				}
			} else if (e.getSource() == getView().getExportCSVItem()) {
				String filename = view.displayDialog("Export location",
						"Please enter the location for the CSV export", "/Users/bsmulders/export.csv");

				if (filename != null) {
					getModel().exportCSV(filename);
				}
			} else if (e.getSource() == getView().getQuitItem()) {
				getModel().quit();
			} else if (e.getSource() == getView().getTrainNetworkItem()) {
				getModel().trainNeuralNetwork();
			} else if (e.getSource() == getView().getStopTrainingItem()) {
				getModel().stopTrainingNeuralNetwork();
			} else if (e.getSource() == getView().getClearDataItem()) {
				getModel().clearData();
			} else if (e.getSource() == getView().getOffControlItem()) {
				getModel().setControlMethod(ControlMethod.OFF);
			} else if (e.getSource() == getView().getRandomControlItem()) {
				getModel().setControlMethod(ControlMethod.RANDOM);
			} else if (e.getSource() == getView().getSmartControlItem()) {
				getModel().setControlMethod(ControlMethod.SMART);
			} else if (e.getSource() == getView().getNetworkControlItem()) {
				getModel().setControlMethod(ControlMethod.NETWORK);
			} else if (e.getSource() == getView().getSimpleViewItem()) {
				getModel().setViewMode(ViewMode.SIMPLE);
			} else if (e.getSource() == getView().getFullViewItem()) {
				getModel().setViewMode(ViewMode.FULL);
			} else {
				System.err.println("Unimplemented actionPerformed listener");
			}
		}
	}

	// WindowListener handlers
	@Override
	public void windowActivated(WindowEvent arg0) {
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		getModel().quit();
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
	}
}