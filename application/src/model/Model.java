/*
This file is part of StepManiaSolver

Copyright (c) 2013, Bobbie Smulders

Contact:  mail@bsmulders.com

GNU General Public License Usage
This file may be used under the terms of the GNU General Public License version 3.0 as published by the Free Software Foundation and appearing in the file LICENSE included in the packaging of this file.  Please review the following information to ensure the GNU General Public License version 3.0 requirements will be met: http://www.gnu.org/copyleft/gpl.html.
 */

package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import org.encog.neural.networks.BasicNetwork;

import task.CollectionComparator;
import task.Communicator;
import task.NeuralNetworkTrainer;
import task.basher.NeuralNetworkBasher;
import task.basher.RandomBasher;
import task.basher.SmartBasher;
import view.View;

public class Model {

	private Communicator communicator;
	private CollectionComparator collectionComparator;
	private NeuralNetworkTrainer neuralNetworkTrainer;
	private RandomBasher randomBasher;
	private SmartBasher smartBasher;
	private NeuralNetworkBasher neuralNetworkBasher;
	private View view;

	private BasicNetwork neuralNetwork;

	private ArrayList<Note> notes = new ArrayList<Note>();

	private ArrayList<DataPoint> oldDataPoints = new ArrayList<DataPoint>();
	private ArrayList<DataPoint> currentDataPoints = new ArrayList<DataPoint>();
	private ArrayList<DataPoint> dataHistory = new ArrayList<DataPoint>();
	private ArrayList<DataPoint> neuralNetworkEvaluation = new ArrayList<DataPoint>();
	private GameBorder gameBorder = new GameBorder();

	private ControlMethod controlMethod = ControlMethod.OFF;
	private ViewMode viewMode = ViewMode.SIMPLE;
	private boolean clientConnected = false;

	public Model() {
	}

	// Getter and setters
	private Communicator getCommunicator() {
		return communicator;
	}

	public void setCommunicator(Communicator communicator) {
		this.communicator = communicator;
	}

	private CollectionComparator getCollectionComparator() {
		return collectionComparator;
	}

	public void setCollectionComparator(CollectionComparator collectionComperator) {
		this.collectionComparator = collectionComperator;
	}

	public NeuralNetworkTrainer getNeuralNetworkTrainer() {
		return neuralNetworkTrainer;
	}

	public void setNeuralNetworkTrainer(NeuralNetworkTrainer neuralNetworkTrainer) {
		this.neuralNetworkTrainer = neuralNetworkTrainer;
	}

	private RandomBasher getRandomBasher() {
		return randomBasher;
	}

	public void setRandomBasher(RandomBasher randomBasher) {
		this.randomBasher = randomBasher;
	}

	private SmartBasher getSmartBasher() {
		return smartBasher;
	}

	public void setSmartBasher(SmartBasher smartBasher) {
		this.smartBasher = smartBasher;
	}

	private NeuralNetworkBasher getNeuralNetworkBasher() {
		return neuralNetworkBasher;
	}

	public void setNeuralNetworkBasher(NeuralNetworkBasher neuralNetworkBasher) {
		this.neuralNetworkBasher = neuralNetworkBasher;
	}

	private View getView() {
		return view;
	}

	public void setView(View view) {
		this.view = view;
	}

	public BasicNetwork getNeuralNetwork() {
		return neuralNetwork;
	}

	public void setNeuralNetwork(BasicNetwork neuralNetwork) {
		this.neuralNetwork = neuralNetwork;

		addNote("Neural network training complete");

		evaluateNeuralNetwork();
	}

	public void clearNeuralNetwork() {
		this.neuralNetwork = null;
	}

	public ArrayList<Note> getNotes() {
		return notes;
	}

	public void addNote(String note) {
		notes.add(new Note(note));
		updateView();
	}

	public void addNote(String note, boolean error) {
		notes.add(new Note(note, error));
		updateView();
	}

	private ArrayList<DataPoint> getCurrentDataPoints() {
		return currentDataPoints;
	}

	public void addDataPoint(DataPoint dataPoint) {
		checkGameBorder(dataPoint);
		getCurrentDataPoints().add(dataPoint);
	}

	public ArrayList<DataPoint> getOldDataPoints() {
		return oldDataPoints;
	}

	private void setOldDataPoints(ArrayList<DataPoint> oldDataPoints) {
		this.oldDataPoints = oldDataPoints;
	}

	public ArrayList<DataPoint> getDataHistory() {
		return dataHistory;
	}

	public void addDataHistory(DataPoint dataPoint) {
		dataHistory.add(dataPoint);

		if (dataPoint.getButton() > 0) {
			updateView();
			addNote("Found datapoint (" + dataPoint + ")");
		}
	}

	public ArrayList<DataPoint> getNeuralNetworkEvaluation() {
		return neuralNetworkEvaluation;
	}

	public void addNeuralNetworkEvaluation(DataPoint dataPoint) {
		neuralNetworkEvaluation.add(dataPoint);
	}

	public GameBorder getGameBorder() {
		return gameBorder;
	}

	public ControlMethod getControlMethod() {
		return controlMethod;
	}

	public void setControlMethod(ControlMethod controlMethod) {
		this.controlMethod = controlMethod;

		if (controlMethod == ControlMethod.NETWORK && getNeuralNetwork() == null) {
			addNote("Warning: The neural network is not yet trained", true);
		} else if (controlMethod == ControlMethod.SMART && getDataHistory().size() == 0) {
			addNote("Warning: There is no data history to work off", true);
		}
	}

	public ViewMode getViewMode() {
		return viewMode;
	}

	public void setViewMode(ViewMode viewMode) {
		this.viewMode = viewMode;
		updateView();
	}

	public boolean isClientConnected() {
		return clientConnected;
	}

	public void setClientConnected(boolean clientConnected) {
		this.clientConnected = clientConnected;

		if (isClientConnected()) {
			addNote("StepMania client connected");
		} else {
			addNote("StepMania client disconnected");
		}
	}

	// Called from Controller
	public void resetDataPoints() {
		// Copy the lists to not overwrite them
		ArrayList<DataPoint> oldList = new ArrayList<DataPoint>(getOldDataPoints());
		ArrayList<DataPoint> newList = new ArrayList<DataPoint>(getCurrentDataPoints());

		// Use one of the control method to bash buttons
		switch (controlMethod) {
		case OFF:
			break;
		case RANDOM:
			getCollectionComparator().compare(oldList, newList);
			getRandomBasher().bash();
			break;
		case SMART:
			getSmartBasher().scanAndBash(getDataHistory(), newList);
			break;
		case NETWORK:
			getNeuralNetworkBasher().scanAndBash(getNeuralNetwork(), newList);
			break;
		}

		// Transfer the current data points to oldDataPoints and clear the current data points
		setOldDataPoints(new ArrayList<DataPoint>(currentDataPoints));
		getCurrentDataPoints().clear();

		// Update the view
		updateView();
	}

	public void pressButton(int button) {
		getCollectionComparator().setLastPressedButton(button);

		getCommunicator().sendOutgoingButtonPress(button);

		addNote("Button " + button + " was sent to StepMania");
	}

	public void trainNeuralNetwork() {
		if (getDataHistory().size() > 0) {

			try {
				getNeuralNetworkTrainer().train(getDataHistory());
				addNote("Started neural network training");
			} catch (IllegalStateException ise) {
				addNote("Error: There already is an active training session ongoing", true);
			}
		} else {
			addNote("Error: There is no data history to work off", true);
		}
	}

	public void stopTrainingNeuralNetwork() {
		if (getNeuralNetworkTrainer().isTraining()) {
			getNeuralNetworkTrainer().stopTraining();
			addNote("Stopped neural network training");
		} else {
			addNote("Error: There is no active training session", true);
		}
	}

	public void clearData() {
		// Remove all available data
		getOldDataPoints().clear();
		getCurrentDataPoints().clear();
		getDataHistory().clear();
		getNeuralNetworkEvaluation().clear();
		clearNeuralNetwork();
		addNote("Cleared data");
	}

	public void neuralNetworkIteration(int iteration, double error, int timeleft) {
		String description = String.format("Training iteration: %d; Error rate: %3.3f %% (%d seconds left)",
				iteration, error * 100, (timeleft > 0) ? timeleft / 1000 : 0);

		addNote(description);
	}

	public void quit() {
		finalize();
		System.exit(0);
	}

	// Helper methods
	private void evaluateNeuralNetwork() {
		getNeuralNetworkEvaluation().clear();

		// Go through all possible coordinates and ask the neural network which button it would press
		for (int x = getGameBorder().getLeft(); x <= getGameBorder().getRight(); x++) {
			for (int y = getGameBorder().getTop(); y <= getGameBorder().getBottom(); y++) {
				DataPoint dataPoint = new DataPoint(x, y);
				int button = neuralNetworkBasher.enquireNeuralNetwork(getNeuralNetwork(), dataPoint);

				if (button >= 0) {
					dataPoint.setButton(button);
					addNeuralNetworkEvaluation(dataPoint);
				}
			}
		}

		updateView();
		addNote("Evaluated neural network");
	}

	private void checkGameBorder(DataPoint dataPoint) {
		// The game border is unknown at the start of the game. After processing each data point, we must see
		// if it still fits within the game border. Adjust the game border if necessary

		if (dataPoint.getX() < gameBorder.getLeft()) {
			getGameBorder().setLeft((int) Math.floor(dataPoint.getX()));

			addNote("Adjusted left border to " + gameBorder.getLeft());
		} else if (dataPoint.getX() > gameBorder.getRight()) {
			getGameBorder().setRight((int) Math.ceil(dataPoint.getX()));

			addNote("Adjusted right border to " + gameBorder.getRight());
		}

		if (dataPoint.getY() < gameBorder.getTop()) {
			getGameBorder().setTop((int) Math.floor(dataPoint.getY()));
			getCollectionComparator().setTopBorder(dataPoint.getY());

			addNote("Adjusted top border to " + gameBorder.getTop());
		} else if (dataPoint.getY() > gameBorder.getBottom()) {
			getGameBorder().setBottom((int) Math.ceil(dataPoint.getY()));

			addNote("Adjusted bottom border to " + gameBorder.getBottom());
		}
	}

	private void updateView() {
		if (getView() != null) {
			getView().repaint();
		}
	}

	// File IO
	public void importCSV(String filename) {
		getDataHistory().clear();

		try {
			FileReader fileReader = new FileReader(filename);
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			// Skip header
			bufferedReader.readLine();

			String line;
			while ((line = bufferedReader.readLine()) != null) {
				String[] csv = line.split(",");
				double x = Double.parseDouble(csv[0]);
				double y = Double.parseDouble(csv[1]);
				int button = (int) (Double.parseDouble(csv[2]) * 4);

				DataPoint dataPoint = new DataPoint(x, y, button);
				checkGameBorder(dataPoint);
				getDataHistory().add(dataPoint);
			}

			fileReader.close();

			updateView();
			addNote("Succesfully imported CSV from " + filename);
		} catch (IOException e) {
			addNote("Error: Could not import " + filename, true);
		}
	}

	public void exportCSV(String filename) {
		try {
			FileWriter fileWriter = new FileWriter(filename);

			fileWriter.append("x,y,value\n");

			for (DataPoint dataPoint : getDataHistory()) {
				String line = String.format("%f,%f,%f\n", dataPoint.getX(), dataPoint.getY(),
						dataPoint.getButton() * 0.25);

				fileWriter.append(line);
			}

			fileWriter.flush();
			fileWriter.close();

			addNote("Succesfully exported CSV to " + filename);
		} catch (IOException e) {
			addNote("Error: Could not export " + filename, true);
		}
	}

	// Finalize method
	@Override
	public void finalize() {
		getCommunicator().finalize();
		getCollectionComparator().finalize();
		getNeuralNetworkTrainer().finalize();
	}
}
