package gui;

import java.awt.FlowLayout;
import java.awt.Label;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

public class GUI {

	/**
	 * @wbp.parser.entryPoint
	 */
	public void startGUI() {

		JFrame frame = new JFrame("Algorithm");
		frame.setSize(800, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JButton shapeFileBtn = new JButton("Select Shapefile");
		frame.getContentPane().add(shapeFileBtn);

		Label shapeFileLabel = new Label("select shape file");
		frame.getContentPane().add(shapeFileLabel);
		JFileChooser fileChooser = new JFileChooser();
		frame.setVisible(true);

		System.out.println();

	}

}
