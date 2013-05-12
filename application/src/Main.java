/*
This file is part of StepManiaSolver

Copyright (c) 2013, Bobbie Smulders

Contact:  mail@bsmulders.com

GNU General Public License Usage
This file may be used under the terms of the GNU General Public License version 3.0 as published by the Free Software Foundation and appearing in the file LICENSE included in the packaging of this file.  Please review the following information to ensure the GNU General Public License version 3.0 requirements will be met: http://www.gnu.org/copyleft/gpl.html.
 */

import model.Model;
import task.CollectionComparator;
import task.Communicator;
import task.NeuralNetworkTrainer;
import task.basher.NeuralNetworkBasher;
import task.basher.RandomBasher;
import task.basher.SmartBasher;
import view.View;
import controller.Controller;

public class Main {

	public static void main(String[] args) {
		// Model
		Model model = new Model();

		// Controller
		Controller controller = new Controller(model);

		// View
		View view = new View(model, controller);
		model.setView(view);
		controller.setView(view);

		// RandomBasher
		RandomBasher randomBasher = new RandomBasher();
		randomBasher.addListener(controller);
		model.setRandomBasher(randomBasher);

		// CollectionComparator
		CollectionComparator collectionComparator = new CollectionComparator();
		collectionComparator.addListener(controller);
		model.setCollectionComparator(collectionComparator);

		// NeuralNetworkTrainer
		NeuralNetworkTrainer neuralNetworkTrainer = new NeuralNetworkTrainer();
		neuralNetworkTrainer.addListener(controller);
		model.setNeuralNetworkTrainer(neuralNetworkTrainer);

		// Communicator
		Communicator communicator = new Communicator();
		communicator.addListener(controller);
		model.setCommunicator(communicator);
		communicator.start();

		// SmartBasher
		SmartBasher smartBasher = new SmartBasher();
		smartBasher.addListener(controller);
		model.setSmartBasher(smartBasher);

		// NeuralNetworkBasher
		NeuralNetworkBasher neuralNetworkBasher = new NeuralNetworkBasher();
		neuralNetworkBasher.addListener(controller);
		model.setNeuralNetworkBasher(neuralNetworkBasher);

		model.addNote("Application started");
	}
}
