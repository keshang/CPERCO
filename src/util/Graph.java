package util;// This program will read a solution to the CVRP problem from a file called best-solution.txt
// It requires the CVRPData.java file which is available on the unit web page.
// It is also necessary to add the following getLocation() method to CVRPData.java:

/*
  // Return the absolute co-ordinates of any node
  public static Point getLocation(int node) {
    if (!nodeIsValid(node)) {
      System.err.println("Error: Request for location of non-existent node " + node + ".");
      System.exit(-1);
    }

    return new Point(coords[node][X_COORDINATE], coords[node][Y_COORDINATE]);
  }
*/

// To run, ensure that the util.Graph.java file, the CVRPData.java file and the best-solution.txt file are all in the current directory.
// Compile using the command:
// javac util.Graph.java CVRPData.java
// Run using the command:
// java util.Graph

// This program assumes that there is only one depot, which is index 1 in CVRPData's data structure.
// It also assumes that best-solution.txt is formatted correctly (with login, cost etc)

// @author Thomas Pickering

import java.awt.*;
import java.awt.geom.Point2D;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Random;
import java.io.*;


// Class Description:
// Draws a graph of a given solution.

@SuppressWarnings("serial")
public class Graph extends JPanel {

  // ******************* Constants ************************

  public double[] X;
  public double[] Y;
  public boolean[][] arc;

  // ******************** Fields **************************
  
  // Window
  JFrame w;

  // ***************** Constructors ***********************


  public Graph(double[] xPos, double[] yPos) {
    //trucks = new ArrayList<ArrayList<Integer>>();
	X = new double[xPos.length];
	Y = new double[yPos.length];
	arc = new boolean[xPos.length][xPos.length];
	for(int i=0; i<xPos.length; i++)
	{
		X[i] = xPos[i]*30;
		Y[i] = yPos[i]*30;
	}
	for(int i=0; i<xPos.length; i++)
	{
		for(int j=0; j<xPos.length; j++)
		{
			arc[i][j] = false;
		}
	}
	
    setupWindow();
  }

  // ******************* Methods **************************



  // Sets up the graph window
  private void setupWindow() {
    w = new JFrame("Best Solution");
    w.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    //w.setLocationByPlatform(true);
//   w.setBounds(50, 50, (int)WIDTH  , (int)HEIGHT );
    w.add(this);
    
    w.setPreferredSize(new Dimension(530, 530));
    w.pack();
    w.setVisible(true);
  }


  
  public void paintComponent(Graphics g) {
	  super.paintComponent(g);
    //super.paint(g);
    Graphics2D g2d = (Graphics2D) g;
    //drawAxes(g2d);
    //g2d.setBackground(Color.WHITE);

    
    // Draw trucks
    for(int i=0; i<X.length; i++)
    {
		for(int j=0; j<X.length; j++)	
		{
				if(arc[i][j]==true)
				{
					g2d.drawLine((int)X[i], (int)Y[i], (int)X[j], (int)Y[j]);
				}
		}
					
    }
    
    //g2d.drawLine((int)X[0], (int)Y[0], (int)X[1], (int)Y[1]);
    /*
	g2d.setColor(truckColours.get(i));
        g2d.drawLine(x1, y1, x2, y2);
   */
    
    
    g2d.fillRect((int)X[0]-5, (int)Y[0]-5, 10, 10);
    g2d.drawString("Base", (int)X[0], (int)Y[0]);
    //g2d.fillRect((int)X[X.length-1]-5, (int)Y[X.length-1]-5, 10, 10);
    //g2d.drawString("End", (int)X[X.length-1], (int)Y[X.length-1]);
    for (int i = 1; i < X.length; i++) {
      g2d.drawRect((int)X[i]-2, (int)Y[i]-2, 4, 4);
      g2d.drawString(String.format("%d", i), (int)X[i], (int)Y[i]);
    }
  }

}