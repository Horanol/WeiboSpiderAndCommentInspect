package com.Prefuse;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.Prefuse.LabelRenderer;
import com.Prefuse.LabelRenderer.LabelShape;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.DataColorAction;
import prefuse.action.layout.graph.*;
import prefuse.activity.Activity;
import prefuse.controls.ControlAdapter;
import prefuse.controls.DragControl;
import prefuse.controls.FocusControl;
import prefuse.controls.PanControl;
import prefuse.controls.WheelZoomControl;
import prefuse.controls.ZoomControl;
import prefuse.controls.ZoomToFitControl;
import prefuse.data.Graph;
import prefuse.data.io.DataIOException;
import prefuse.data.io.GraphMLReader;
import prefuse.render.DefaultRendererFactory;
//import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.util.FontLib;
import prefuse.util.ui.JFastLabel;
import prefuse.visual.VisualItem;

public class ShowRepostNet {
	public static String positivePerString = "";
	public static String negativePerString = "";
	public static String neutralPerString= "";

	public static void countEmotions() {
		int positive = 0;
		int negative = 0;
		int neutral = 0;
		File predictDir = new File("./Comments/PredictResult");
		File[] predictFiles = predictDir.listFiles();
		for (int i = 0; i < predictFiles.length; i++) {
			File file = predictFiles[i];
			try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
				String string = null;
				while ((string = bufferedReader.readLine()) != null) {
					if (string.equals("1.0")) {
						positive++;
					} else if (string.equals("-1.0")) {
						negative++;
					} else {
						neutral++;
					}
				}
			} catch (IOException e) {
			}
		}

		int sum = positive + negative + neutral;
		float positivePre = (positive + 0.0f) / sum;
		float negativePer = (negative + 0.0f) / sum;
		float neutralPer = (neutral + 0.0f) / sum;
		
		NumberFormat format = NumberFormat.getPercentInstance();
		format.setMinimumFractionDigits(2);
		positivePerString = format.format(positivePre);
		negativePerString = format.format(negativePer);
		neutralPerString = format.format(neutralPer);
	}

	public static void main(String[] args) {
		countEmotions();
		Graph graph = null;
		// import data through class GraphMLReader
		try {
			graph = new GraphMLReader().readGraph("./Repost/repostNet.xml");
		} catch (DataIOException e) {
			e.printStackTrace();
			System.err.println("Error loading graph.Exiting...");
			System.exit(1);
		}

		// abstract the data through class Visualization & name it "graph"
		Visualization vis = new Visualization();
		vis.add("graph", graph);// two subgroups nodes and edges will be auto
								// added, accessible as "*.nodes" and "*.edges".

		// Render the graph
		// the next two line map the number of direct repost to the size of the
		// node,
		// to achieve this I changed the source code of the toolkit, see
		// LabelRenderer.java function getRawShape() for the change
		LabelRenderer r = new LabelRenderer("directRepostCount", LabelShape.Arc2D);
		vis.setRendererFactory(new DefaultRendererFactory(r));

		// COLOR
		// in this vision we haven't get view, so every node is gray, in later
		// vision the node color show the view of reposter
		int[] palette = new int[] { Color.red.getRGB(), Color.green.getRGB() };
		// map data values to colors of "fill", "gender" is the name of data
		// field
		// DataColorAction(data tuple,attribution area,colors to choice from)
		DataColorAction fill = new DataColorAction("graph.nodes", "view", Constants.NOMINAL, VisualItem.FILLCOLOR,
				palette);
		// use black for node text
		ColorAction text = new ColorAction("graph.nodes", VisualItem.TEXTCOLOR, ColorLib.gray(0));
		// use light grey for edges
		ColorAction edges = new ColorAction("graph.edges", VisualItem.STROKECOLOR, ColorLib.gray(100));
		ActionList color = new ActionList();
		color.add(fill);
		color.add(edges);
		color.add(text);

		// // SHAPE
		// int[] shapes = new int[]
		// {Constants.SHAPE_RECTANGLE,Constants.SHAPE_DIAMOND};
		// // the use of DataShapeAction is almost the same as DataColorAction
		// DataShapeAction nodeshape = new
		// DataShapeAction("graph.nodes","gender",shapes);
		// ActionList shape = new ActionList();
		// shape.add(nodeshape);

		// LAYOUT
		ActionList layout = new ActionList(Activity.INFINITY, 40L);// how many
																	// times
																	// should
																	// the
																	// ActionList
																	// repeat!!!
		// ActionList layout = new ActionList(10000);
		layout.add(new ForceDirectedLayout("graph"));// may we should choose a
														// layout!
		layout.add(new RepaintAction());

		vis.putAction("color", color);
		vis.putAction("layout", layout);
		// vis.putAction("shape", shape);

		// create a new Display that pull from our Visualization
		Display display = new Display(vis);
		display.setSize(720, 500);
		display.pan(250, 250);
		display.setBackground(Color.gray);
		display.addControlListener(new DragControl());
		display.addControlListener(new PanControl());
		display.addControlListener(new ZoomControl());
		display.addControlListener(new WheelZoomControl());
		display.addControlListener(new FocusControl());
		display.addControlListener(new ZoomToFitControl());

		final JFastLabel title = new JFastLabel("                  ");
		title.setPreferredSize(new Dimension(350, 25));
		title.setVerticalAlignment(SwingConstants.BOTTOM);
		title.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));
		title.setFont(FontLib.getFont("SimSun", Font.CENTER_BASELINE, 16));
		title.setBackground(Color.white);
		title.setForeground(Color.black);
		String label = "name";
		display.addControlListener(new ControlAdapter() {
			public void itemEntered(VisualItem item, MouseEvent e) {
				if (item.canGetString(label))
					title.setText(item.getString(label));
			}

			public void itemExited(VisualItem item, MouseEvent e) {
				title.setText(null);
			}
		});

		final JFastLabel emotionLabel = new JFastLabel("");
		emotionLabel.setPreferredSize(new Dimension(350, 25));
		emotionLabel.setVerticalAlignment(SwingConstants.BOTTOM);
		emotionLabel.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));
		emotionLabel.setFont(FontLib.getFont("SimSun", Font.CENTER_BASELINE, 16));
		emotionLabel.setBackground(Color.white);
		emotionLabel.setForeground(Color.black);
		emotionLabel.setText("Positive: "+positivePerString+
											"   Negative: "+negativePerString+
											"   Neutral: "+neutralPerString);
		
		Box box = new Box(BoxLayout.X_AXIS);
		box.add(Box.createHorizontalStrut(5));
		box.add(title);
		box.add(Box.createHorizontalStrut(150));
		box.add(emotionLabel);
		box.setBackground(Color.white);

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(display, BorderLayout.CENTER);
		panel.add(box, BorderLayout.SOUTH);

		// JFrame
		JFrame frame = new JFrame("转发关系图");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(panel);
		frame.setSize(1300, 1000);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		vis.run("color");
		vis.run("layout");
		// vis.run("shape");
	}
}
