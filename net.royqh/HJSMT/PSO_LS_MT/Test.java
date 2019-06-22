package HJSMT.PSO_LS_MT;

import java.util.concurrent.ExecutionException;

public class Test {

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		// TODO Auto-generated method stub
		
		String problem = "LA16";
		String[][] worktable =
			{{"2","7","10","9","8","3","1","5","4","6"},
					{"5","3","6","10","1","8","2","9","7","4"},
					{"4","3","9","2","5","10","8","7","1","6"},
					{"2","4","3","8","9","10","7","1","6","5"},
					{"3","1","6","7","8","2","5","10","4","9"},
					{"3","4","6","10","5","7","1","9","2","8"},
					{"4","3","1","2","10","9","7","6","5","8"},
					{"2","1","4","5","7","10","9","6","3","8"},
					{"5","3","9","6","4","8","2","7","10","1"},
					{"9","10","3","5","4","1","8","7","2","6"}};
			int[][] timetable = 
				{{21,71,16,52,26,34,53,21,55,95},
						{55,31,98,79,12,66,42,77,77,39},
						{34,64,62,19,92,79,43,54,83,37},
						{87,69,87,38,24,83,41,93,77,60},
						{98,44,25,75,43,49,96,77,17,79},
						{35,76,28,10,61,9,95,35,7,95},
						{16,59,46,91,43,50,52,59,28,27},
						{45,87,41,20,54,43,14,9,39,71},
						{33,37,66,33,26,8,28,89,42,78},
						{69,81,94,96,27,69,45,78,74,84}}
;
		int maxmachine =10;
		int PopSize = 40;
		int num_iteration = 100;
		
		
		int m = worktable.length;
		int n = worktable[0].length;
		PSO pso = new PSO(worktable, timetable, maxmachine, PopSize, problem, num_iteration);
		pso.Initialization();
		int[] result = pso.run();
		System.out.println(result[m*n]+"");

	}

}
