package HJSMT.PSO_SA_MT;

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
	ע�⣬���������б��У�ÿ������Ϊһ��int[]���飬����һ������ִ��˳����룬���ڰ����й���m*n���������
	Ԫ��[0]��[m*n-1]Ϊִ��˳����롣Ԫ��[m*n]Ϊ��ִ��˳���µ���С���ʱ��

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
	 * ��ʼ������Ⱥ
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
		
		//�Թ���i���г�ʼ����i��ȡֵ��1��m-1����m-1��,�������еĹ��������һ�������ĸ�ֵ������
		for(int i=0; i<m; i++){
			k=0;
			zerocount=0;
			
			/*
			 * ����ÿ����������Ҫ����n����ͬ������,��������е�����x��ʾ����Jobs�����еĵ�x��0ʵ��ת�䡣
			 * Ȼ����Ҫ�Ը������е�Ԫ�ؽ��б������Ա��Jobs�е�Ԫ�ؽ��и�ֵ��
			 * ���ɵ�����������ֵӦ����m*n,m*n-1,m*n-2,...,n��
			 */
			randomnums = getRandomnums(1,m*n-(i-1)*n,n);
			
			for(int j=0; j<p.length; j++){
				if(p[j]==0){
					zerocount++;   //����ڼ�����
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
		
		//�������һ��������
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
			int num = (int) (Math.random()*(max-min))+min;//�������һ��[min,max]��Χ�ڵ�����
			for(int j=0;j<i;j++){            
				if(num==result[j]){//�����ɵ�����������֮ǰ���ڵ�Ԫ��һһ�Ƚ�,������ظ�
					state=false;   //��������ظ���Ԫ�ؼ��뵽�����С�
					break;         //Ȼ��������������һ��ѭ�����ٴ������µ�����
				}
			}
			if(state){  //���û���ظ�
				result[i]=num;
				i++;     //ֱ������һ����ͬ��Ԫ�أ����Ѹ�Ԫ�ؼ��뵽�����в��������һ��ѭ����
			}
		}
		
		return result = arrange(result);
	}
	
	//algorithm for ranking from small to big.
	private int[] arrange(int[] array) {
		// TODO �Զ����ɵķ������
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
