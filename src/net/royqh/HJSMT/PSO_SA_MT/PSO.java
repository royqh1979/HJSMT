package net.royqh.HJSMT.PSO_SA_MT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PSO {
	
	private FitnessCalculator fitnessCalculator;
	private int m; //number of jobs.
	private int n; //number of operation per job.
	private String problem_name; //for writer.
	
	//PSO
	private int size; //number of particles to generate.
	private int Popsize; //number of particles for iteration.
	private int num_iterations;
	/*
	注意，下列粒子列表中，每个粒子为一个int[]数组，代表一个机器执行顺序编码，由于案例中共有m*n个工序，因此
	元素[0]到[m*n-1]为执行顺序编码。元素[m*n]为该执行顺序下的最小完成时间

	 */
	private List<int[]> ini_particles; //full of paricles.
	private List<int[]> particles; //full of paricles.
	private List<int[]> p_pbests; //full of best positions found by particles.
	private List<int[]> gbest; //global best position.

	private int length_of_gbest = 20;
	private int[] p_seed; //seed particle.
	
	//SA
	private int D;
	private double T;
	private double Pr = 0.1;
	private double cool = 0.95;
	
	//for evaluating.
	private int[][] ResourceMap;
	private int[] Current;
	
	//for recording
	private long startTime;


	public PSO(String[][] worktable, int[][] timetable, int maxmachine, int Popsize, String problem, int num_iterations){
		fitnessCalculator=new FitnessCalculator(worktable,timetable,maxmachine);
		m = worktable.length;
		n = worktable[0].length;
		this.Popsize = Popsize;
		size = Popsize*100;
		
		this.problem_name = problem;
		
		this.num_iterations = num_iterations;
		
		
		ini_particles = new ArrayList<int[]>();
		particles = new ArrayList<int[]>();
		p_pbests = new ArrayList<int[]>();
		gbest = new ArrayList<int[]>();
		
		startTime = System.currentTimeMillis();
	}

	/**
	 * 初始化粒子群
	 *
	 */
	public void Initialization(){
		System.out.println("PSO procedure is initialzing....");
		
		//1. Generate "size" particles as initial particles.
		while(ini_particles.size() < size-1){
			
			p_seed = Seed_Generator(n, m);
			Diversity_Generator(p_seed);
		}
		
		//2. Pick the top "Popsize" best particles from ini_particles.
		Collections.sort(ini_particles, new Comparator<int[]>(){
			@Override
			public int compare(int[] o1, int[] o2) {
				// TODO Auto-generated method stub
				return o1[m*n]-o2[m*n];
			}
		});

		for(int i=0; i<Popsize; i++){
			particles.add(ini_particles.get(i));
		}
		
		//3. Initialize p_pbests.
		for(int i=0; i<particles.size(); i++){
			int[] p = particles.get(i);
			p_pbests.add(p);
		}
		
		//4. Initialize gbest.
		for(int i=0; i<length_of_gbest; i++){
			int[] temp = new int[m*n+1];
			System.arraycopy(p_pbests.get(i), 0, temp, 0, m*n+1);
			gbest.add(temp);
		}
		
		
		//for SA
		D = ini_particles.get(ini_particles.size()-1)[m*n] - ini_particles.get(0)[m*n] ;
		T = -D/Math.log(Pr);//T = -(Cw-Cb)/(ln Pr)
		
	}
	


	public int[] run() throws InterruptedException, ExecutionException{
		
		System.out.println("PSO procedure is running...\r\n");
		
		int iteration = 0;
		
		int particles_per_Thread = Popsize/5; //number of Threads to be generated.
		
		while((iteration++)<num_iterations){
			
//			System.out.println(iteration+"-th iteration:");
//			System.out.println("Solution 1 fitness: "+ gbest.get(0)[m*n]);
//			System.out.println("Solution 2 fitness: "+ gbest.get(1)[m*n]);
//			System.out.println("Solution 3 fitness: "+ gbest.get(2)[m*n]);
//			System.out.println("Solution 4 fitness: "+ gbest.get(3)[m*n]);
//			System.out.println("Solution 5 fitness: "+ gbest.get(4)[m*n]);
//			System.out.println("T = "+new DecimalFormat("0.00").format(T)+"\u2103. Elapse time: "+ (System.currentTimeMillis()-startTime)/1000+"s");
//			System.out.println();
			
			//1. PSO process
			ExecutorService esor1 = Executors.newFixedThreadPool(5);
			List<Future<List<int[]>>> FutureList = new ArrayList<Future<List<int[]>>>();
			for(int i=0; i<5; i++){

				List<int[]> list = new ArrayList<int[]>();
				for(int j=particles_per_Thread*i; j<particles_per_Thread*i+particles_per_Thread; j++){

					list.add(particles.get(j));
				}
				
				FutureList.add(esor1.submit(new PSOProcess(fitnessCalculator,m,n, gbest.get(0), list)));
			}
			//update particles
			particles.clear();
			for(int i=0; i<5; i++){
				
				List<int[]> list = new ArrayList<int[]>();
				list = FutureList.get(i).get();
				
				for(int j=0; j<list.size(); j++){
					particles.add(list.get(i));
				}	
			}
			esor1.shutdown();
			
			//update pbest
			for(int i=0; i<Popsize; i++){
				
				if(particles.get(i)[m*n] < p_pbests.get(i)[m*n]){
					System.arraycopy(particles.get(i), 0, p_pbests.get(i), 0, m*n+1);
				}
			}
			
			//update gbest
			for(int i=0; i<Popsize; i++){
				if(p_pbests.get(i)[m*n] < gbest.get(length_of_gbest-1)[m*n]){
					
					boolean ifDup = false;
					//if there is a duplication particle
					for(int j=0; j<length_of_gbest; j++){
						if(Arrays.equals(p_pbests.get(i), gbest.get(j))){
							ifDup = true;
							break;
						}
					}
					
					if(ifDup == false){
						gbest.add(p_pbests.get(i));
						Collections.sort(gbest, new Comparator<int[]>(){
							@Override
							public int compare(int[] o1, int[] o2) {
								// TODO Auto-generated method stub
								return o1[m*n]-o2[m*n];
							}
						});
						gbest.remove(length_of_gbest);
					}
				}
			}

			
			//2. SA
			ExecutorService esor_SA = Executors.newFixedThreadPool(5);
			List<Future<int[]>> FutureList_SA = new ArrayList<Future<int[]>>();
			for(int i=0; i<length_of_gbest; i++){
				FutureList_SA.add(esor_SA.submit(new SA(fitnessCalculator,m,n, T, gbest.get(i))));
			}
			//update particles
			gbest.clear();
			for(int i=0; i<length_of_gbest; i++){
				
				gbest.add(FutureList_SA.get(i).get());
			}
			esor_SA.shutdown();
			
			Collections.sort(gbest, new Comparator<int[]>(){
				@Override
				public int compare(int[] o1, int[] o2) {
					// TODO Auto-generated method stub
					return o1[m*n]-o2[m*n];
				}
			});
			T = cool * T;

			
		}
		
		
		
		return gbest.get(0);
		
	}
	
	
	
	/*
	 * The Diversity Generation Method.
	 * input:seed solution, s_seed.
	 * output:trial solutions, s_trial[][].
	 * This method performs on the global metrix, s_trial[][].
	 * If the s_trial[][] is fulfilled, the procedure stops.
	 */
	private void Diversity_Generator(int[] seedsolution) {
		// TODO Auto-generated method stub
		int pos = 0;//position of p[].
		int h = m*n/2; //Refer to Glover(1998)
		
		//generate trial solutions:P(1),P(2),...,P(h)
		for(int i=1; i<=h; i++){	
			int[] p1 = new int[m*n]; //without fitness
			int[] p = new int[m*n+1]; //with fitness
			
			//generate:P(h:h),P(h:h-1),...,P(h:1)
			for(int s=i; s>0; s--){
				//generate:s, s+h, s+2h, ..., s+rh. (s+rh<m*n)
				for(int k=s; k<=m*n; k=k+i){
					p1[pos++] = seedsolution[k-1];
				}	
			}
			//If the s_trial is fulfilled, PopSize, then stop.
			if(ini_particles.size() == size-1){
				break;
			}
			
			System.arraycopy(p1, 0, p, 0, m*n);
			p[m*n] = fitnessCalculator.fitness(p1);
			
			ini_particles.add(p);
			pos=0;
		}
	}

	

	/*
	 * SeedGenerator Method is used to generate a seed solution.
	 * There are m jobs and each job has n operations.
	 * When n and m are given, a seed solution, s_seed, is generated!
	 * SubMethod: "getRandomnums" and "arrange"
	 */
	private int[] Seed_Generator(int n2, int m2) {
		// TODO Auto-generated method stub
		int[] p = new int[n*m]; 
		int k ;
		int zerocount ;
		int[] randomnums;
		
		//对工作i进行初始化，i的取值从1到m-1，共m-1个,代表所有的工作。最后一个工作的赋值在下面
		for(int i=0; i<m; i++){
			k=0;
			zerocount=0;
			
			/*
			 * 对于每个工作而言要产生n个相同的数字,这个数组中的数字x表示，在Jobs数组中的第x个0实现转变。
			 * 然后需要对该数组中的元素进行遍历，以便对Jobs中的元素进行赋值。
			 * 生成的随机数的最大值应该是m*n,m*n-1,m*n-2,...,n。
			 */
			randomnums = getRandomnums(1,m*n-(i-1)*n,n);
			
			for(int j=0; j<p.length; j++){
				if(p[j]==0){
					zerocount++;   //代表第几个零
					if(zerocount==randomnums[k]){
						p[j]=i;
						k++;
						if(k>=n){
							break;
						}
					}
				}
			}
		}
		
		//对于最后一个工作：
		for(int t=0; t<m*n; t++){
			if(p[t]==0){
				p[t]=m;
			}
		}
		
		return p;
	}
	
	/*
	 * Get n random numbers from [min,max).
	 */
	private int[] getRandomnums(int min, int max, int n) {
		// TODO Auto-generated method stub
		int result[] = new int[n];  
		boolean state=true;
		
		if (n > (max - min + 1) || max < min) {  
	           return null;  
	    }
		
		for(int i=0; i<n;){  
			state = true;  
			int num = (int) (Math.random()*(max-min))+min;//随机生成一个[min,max]范围内的数字
			for(int j=0;j<i;j++){            
				if(num==result[j]){//把生成的数字与数组之前存在的元素一一比较,如果有重复
					state=false;   //不把这个重复的元素加入到数组中。
					break;         //然后跳出，进入下一次循环，再次生成新的数字
				}
			}
			if(state){  //如果没有重复
				result[i]=num;
				i++;     //直到生成一个不同的元素，并把该元素加入到数组中才算进入下一个循环。
			}
		}
		
		return result = arrange(result);
	}
	
	//algorithm for ranking from small to big.
	private int[] arrange(int[] array) {
		// TODO 自动生成的方法存根
		int mid;
		for(int i=0; i<array.length-1; i++){
			for(int j=i+1;j<array.length;j++){
				if(array[i]>array[j]){
					mid = array[i];
					array[i] = array[j];
					array[j] = mid;
				}
			}
		}
		return array;
	}
	
	
}
