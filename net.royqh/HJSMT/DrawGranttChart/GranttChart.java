package HJSMT.DrawGranttChart;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class GranttChart extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentpane;
	private int X = 20;
	private int Y = 30;
	private int height = 18;
	private int width = 30;
	private Point point = new Point(X,Y);
	
	//problem information
	String[][] worktable = {{"1","2","3","4","5"},
			{"1","2","4","3","5"},
			{"2","1","3","5","4"},
			{"2","1","5","3","4"},
			{"3","2","1","4","5"},
			{"3","2","5","1","4"},
			{"2","1","3","4","5"},
			{"3","2","1","4","5"},
			{"1","4","3","2","5"},
			{"2","3","1","4","5"},
			{"2","4","1","5","3"},
			{"3","1","2","4","5"},
			{"1","3","2","4","5"},
			{"3","1","2","4","5"},
			{"1","2","5","3","4"},
			{"2","1","4","5","3"},
			{"1","3","2","4","5"},
			{"1","2","5","3","4"},
			{"2","3","1","4","5"},
			{"1","2","3","4","5"}};
	int[][] timetable = {{29,9,49,62,44},
			{43,75,69,46,72},
			{91,39,90,12,45},
			{81,71,9,85,22},
			{14,22,26,21,72},
			{84,52,48,47,6},
			{46,61,32,32,30},
			{31,46,32,19,36},
			{76,76,85,40,26},
			{85,61,64,47,90},
			{78,36,11,56,21},
			{90,11,28,46,30},
			{85,74,10,89,33},
			{95,99,52,98,43},
			{6,61,69,49,53},
			{2,95,72,65,25},
			{37,13,21,89,55},
			{86,74,88,48,79},
			{69,51,11,89,74},
			{13,7,76,52,45}};
	int[] solution = {5,16,17,5,5,17,12,17,5,16,11,5,17,6,19,15,20,11,17,9,16,20,19,10,11,11,19,20,9,2,15,16,13,10,19,7,15,14,10,2,20,19,7,6,2,15,20,14,4,13,10,6,15,10,7,13,1,3,2,7,18,13,9,7,14,2,3,1,14,12,18,1,4,13,8,12,18,3,6,1,8,8,9,12,4,3,18,14,6,8,4,9,3,12,18,8,16,1,11,4};
	int maxmachine = 5;
	
	//Others
	private Painter painter;
	
	
	public GranttChart(){
		
		//Override the method of "paint()", in order to paint out the graphics, otherwise nothing will be painted.
		contentpane = new JPanel(){
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			@Override
			public void paint (Graphics g){
				super.paint (g);
				int makespan;
				
				// Draw the GranttChart.
				makespan = painter.paintGranttChart(g);
				// Draw the processors.
				for(int i=0; i<maxmachine; i++){
					g.drawRect(point.x, point.y+25*i, width, height);
					g.drawString("M"+(i+1), point.x+5, point.y+25*i+15);
					
				}
				
				// Draw the X-axie.
				int X_length = makespan + 100;
				int X_start = point.x+40;
				int Y_start = point.y-8;
				g.drawLine(X_start, Y_start, X_start+X_length, Y_start);
				for(int X_current=X_start; X_current<X_start+X_length; X_current=X_current+10){
					g.drawLine(X_current, Y_start, X_current, Y_start+3);
					if(X_current%50==0){
						g.drawLine(10+X_current, Y_start, 10+X_current, Y_start-3);
						g.drawString(10+X_current-X_start+"", 5+X_current, Y_start-5);
					}
				}
				
				// Draw the line of termination
				g.drawLine(X_start+makespan, Y_start, X_start+makespan, Y_start+25*maxmachine+100);
				g.dispose ();
			}
		};
		
		setContentPane(contentpane);
		contentpane.setBackground(Color.WHITE);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(5,5,1200,900);
		setVisible(true);
		painter = new Painter(worktable, timetable, maxmachine, solution, point, height);
	}
	
	public GranttChart(String[][] worktable, int[][] timetable, int[] solution, int maxmachine){
		
		this.worktable = worktable;
		this.timetable = timetable;
		this.solution = solution;
		this.maxmachine = maxmachine;
		
		//Override the method of "paint()", in order to paint out the graphics, otherwise nothing will be painted.
		contentpane = new JPanel(){
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			@Override
			public void paint (Graphics g){
				super.paint (g);
				int makespan;
				
				// Draw the GranttChart.
				makespan = painter.paintGranttChart(g);
				// Draw the processors.
				for(int i=0; i<maxmachine; i++){
					g.drawRect(point.x, point.y+25*i, width, height);
					g.drawString("M"+(i+1), point.x+5, point.y+25*i+15);
					
				}
				
				// Draw the X-axie.
				int X_length = makespan + 100;
				int X_start = point.x+40;
				int Y_start = point.y-8;
				g.drawLine(X_start, Y_start, X_start+X_length, Y_start);
				for(int X_current=X_start; X_current<X_start+X_length; X_current=X_current+10){
					g.drawLine(X_current, Y_start, X_current, Y_start+3);
					if(X_current%50==0){
						g.drawLine(10+X_current, Y_start, 10+X_current, Y_start-3);
						g.drawString(10+X_current-X_start+"", 5+X_current, Y_start-5);
					}
				}
				
				// Draw the line of termination
				g.drawLine(X_start+makespan, Y_start, X_start+makespan, Y_start+25*maxmachine+100);
				g.dispose ();
			}
		};
		
		setContentPane(contentpane);
		contentpane.setBackground(Color.WHITE);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(5,5,1200,900);
		setVisible(true);
		painter = new Painter(worktable, timetable, maxmachine, solution, point, height);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new GranttChart();
	}
}
