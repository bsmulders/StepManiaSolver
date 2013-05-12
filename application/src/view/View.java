/*
This file is part of StepManiaSolver

Copyright (c) 2013, Bobbie Smulders

Contact:  mail@bsmulders.com

GNU General Public License Usage
This file may be used under the terms of the GNU General Public License version 3.0 as published by the Free Software Foundation and appearing in the file LICENSE included in the packaging of this file.  Please review the following information to ensure the GNU General Public License version 3.0 requirements will be met: http://www.gnu.org/copyleft/gpl.html.
 */

package view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;

import view.component.DataHistoryPanel;
import view.component.NeuralNetworkEvaluationPanel;
import view.component.NoteTextArea;
import view.component.ReceivedDataPanel;

import model.Model;
import controller.Controller;

public class View extends JFrame {

	private static final long serialVersionUID = -528391186189888616L;

	private Model model;
	private Controller controller;
	private JMenuItem importCSVItem;
	private JMenuItem exportCSVItem;
	private JMenuItem quitItem;
	private JMenuItem trainNetworkItem;
	private JMenuItem stopTrainingItem;
	private JMenuItem clearDataItem;
	private JRadioButtonMenuItem offControlItem;
	private JRadioButtonMenuItem randomControlItem;
	private JRadioButtonMenuItem smartControlItem;
	private JRadioButtonMenuItem networkControlItem;
	private JRadioButtonMenuItem fullViewItem;
	private JRadioButtonMenuItem simpleViewItem;

	public View(Model model, Controller controller) {
		setModel(model);
		setController(controller);

		addWindowListener(controller);
		addComponents(getContentPane());

		setTitle("StepMania Solver");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocation(0, 100);
		setVisible(true);
		setIgnoreRepaint(true);
		pack();
		setMinimumSize(getSize());
	}

	// Getters and setters
	private Model getModel() {
		return model;
	}

	private void setModel(Model model) {
		this.model = model;
	}

	private Controller getController() {
		return controller;
	}

	private void setController(Controller controller) {
		this.controller = controller;
	}

	public JMenuItem getImportCSVItem() {
		return importCSVItem;
	}

	private void setImportCSVItem(JMenuItem importCSVItem) {
		this.importCSVItem = importCSVItem;
	}

	public JMenuItem getExportCSVItem() {
		return exportCSVItem;
	}

	private void setExportCSVItem(JMenuItem exportCSVItem) {
		this.exportCSVItem = exportCSVItem;
	}

	public JMenuItem getQuitItem() {
		return quitItem;
	}

	private void setQuitItem(JMenuItem quitItem) {
		this.quitItem = quitItem;
	}

	public JMenuItem getTrainNetworkItem() {
		return trainNetworkItem;
	}

	private void setTrainNetworkItem(JMenuItem trainNetworkItem) {
		this.trainNetworkItem = trainNetworkItem;
	}

	public JMenuItem getStopTrainingItem() {
		return stopTrainingItem;
	}

	private void setStopTrainingItem(JMenuItem stopTrainingItem) {
		this.stopTrainingItem = stopTrainingItem;
	}

	public JMenuItem getClearDataItem() {
		return clearDataItem;
	}

	private void setClearDataItem(JMenuItem clearDataItem) {
		this.clearDataItem = clearDataItem;
	}

	public JRadioButtonMenuItem getOffControlItem() {
		return offControlItem;
	}

	private void setOffControlItem(JRadioButtonMenuItem offControlItem) {
		this.offControlItem = offControlItem;
	}

	public JRadioButtonMenuItem getRandomControlItem() {
		return randomControlItem;
	}

	private void setRandomControlItem(JRadioButtonMenuItem randomControlItem) {
		this.randomControlItem = randomControlItem;
	}

	public JRadioButtonMenuItem getSmartControlItem() {
		return smartControlItem;
	}

	private void setSmartControlItem(JRadioButtonMenuItem smartControlItem) {
		this.smartControlItem = smartControlItem;
	}

	public JRadioButtonMenuItem getNetworkControlItem() {
		return networkControlItem;
	}

	private void setNetworkControlItem(JRadioButtonMenuItem networkControlItem) {
		this.networkControlItem = networkControlItem;
	}

	public JRadioButtonMenuItem getFullViewItem() {
		return fullViewItem;
	}

	private void setFullViewItem(JRadioButtonMenuItem fullViewItem) {
		this.fullViewItem = fullViewItem;
	}

	public JRadioButtonMenuItem getSimpleViewItem() {
		return simpleViewItem;
	}

	private void setSimpleViewItem(JRadioButtonMenuItem simpleViewItem) {
		this.simpleViewItem = simpleViewItem;
	}

	// Helper methods
	private void addComponents(Container pane) {
		// Menubar
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		// File menu
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		menuBar.add(fileMenu);

		setImportCSVItem(new JMenuItem("Import CSV"));
		getImportCSVItem().addActionListener(getController());
		fileMenu.add(getImportCSVItem());

		setExportCSVItem(new JMenuItem("Export CSV"));
		getExportCSVItem().addActionListener(getController());
		fileMenu.add(getExportCSVItem());

		fileMenu.add(new JSeparator());

		setQuitItem(new JMenuItem("Quit"));
		getQuitItem().addActionListener(getController());
		fileMenu.add(getQuitItem());

		// Data menu
		JMenu dataMenu = new JMenu("Data");
		dataMenu.setMnemonic(KeyEvent.VK_D);
		menuBar.add(dataMenu);

		setTrainNetworkItem(new JMenuItem("Train network"));
		getTrainNetworkItem().addActionListener(getController());
		dataMenu.add(getTrainNetworkItem());

		setStopTrainingItem(new JMenuItem("Stop training"));
		getStopTrainingItem().addActionListener(getController());
		dataMenu.add(getStopTrainingItem());

		dataMenu.add(new JSeparator());

		setClearDataItem(new JMenuItem("Clear all data"));
		getClearDataItem().addActionListener(getController());
		dataMenu.add(getClearDataItem());

		// Control menu
		JMenu controlMenu = new JMenu("Control");
		controlMenu.setMnemonic(KeyEvent.VK_C);
		menuBar.add(controlMenu);

		ButtonGroup controlGroup = new ButtonGroup();

		setOffControlItem(new JRadioButtonMenuItem("Off"));
		getOffControlItem().addActionListener(getController());
		getOffControlItem().setSelected(true);
		controlGroup.add(getOffControlItem());
		controlMenu.add(getOffControlItem());

		setRandomControlItem(new JRadioButtonMenuItem("Random buttons"));
		getRandomControlItem().addActionListener(getController());
		controlGroup.add(getRandomControlItem());
		controlMenu.add(getRandomControlItem());

		setSmartControlItem(new JRadioButtonMenuItem("Smart algorithm"));
		getSmartControlItem().addActionListener(getController());
		controlGroup.add(getSmartControlItem());
		controlMenu.add(getSmartControlItem());

		setNetworkControlItem(new JRadioButtonMenuItem("Neural network"));
		getNetworkControlItem().addActionListener(getController());
		controlGroup.add(getNetworkControlItem());
		controlMenu.add(getNetworkControlItem());

		// View menu
		JMenu viewMenu = new JMenu("View");
		viewMenu.setMnemonic(KeyEvent.VK_V);
		menuBar.add(viewMenu);

		ButtonGroup viewGroup = new ButtonGroup();

		setSimpleViewItem(new JRadioButtonMenuItem("Simplified view"));
		getSimpleViewItem().addActionListener(getController());
		getSimpleViewItem().setSelected(true);
		viewGroup.add(getSimpleViewItem());
		viewMenu.add(getSimpleViewItem());

		setFullViewItem(new JRadioButtonMenuItem("Full view"));
		getFullViewItem().addActionListener(getController());
		viewGroup.add(getFullViewItem());
		viewMenu.add(getFullViewItem());

		// Data panel
		GridLayout dataLayout = new GridLayout(1, 3);
		dataLayout.setHgap(5);

		JPanel dataPanel = new JPanel(dataLayout);
		dataPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		ReceivedDataPanel rdPanel = new ReceivedDataPanel(getModel(), 150, 400);
		dataPanel.add(rdPanel);
		DataHistoryPanel dhPanel = new DataHistoryPanel(getModel(), 150, 400);
		dataPanel.add(dhPanel);
		NeuralNetworkEvaluationPanel nnePanel = new NeuralNetworkEvaluationPanel(getModel(), 150, 400);
		dataPanel.add(nnePanel);

		// Status panel
		JPanel statusPanel = new JPanel(new BorderLayout());
		statusPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));

		NoteTextArea noteTextArea = new NoteTextArea(getModel());

		JScrollPane scrollPane = new JScrollPane(noteTextArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setPreferredSize(new Dimension(200, 150));
		statusPanel.add(scrollPane, BorderLayout.CENTER);

		// Vertical splitpane
		JSplitPane verticalSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, dataPanel, statusPanel);
		verticalSplitPane.setOneTouchExpandable(true);
		verticalSplitPane.setBorder(null);
		add(verticalSplitPane);
	}

	public String displayDialog(String title, String message, String initial) {
		return (String) JOptionPane.showInputDialog(this, message, title, JOptionPane.QUESTION_MESSAGE, null,
				null, initial);
	}
}
