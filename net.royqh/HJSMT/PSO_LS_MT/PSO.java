package HJSMT.PSO_LS_MT;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PSO {
	
	//Basic information.
	private String[][] worktable;
	private int[][] timetable;
	private int maxmachine;
	private int m; //number of jobs.
	private int n; //number of operation per job.
	private String problem_name; //for writer.
	
	//PSO
	private int size; //number of particles to generate.
	private int Popsize; //number of particles for iteration.
	private int num_iterations;
	private List<int[]> ini_particles; //full of paricles.
	private List<int[]> particles; //full of paricles.
	private List<int[]> p_pbests; //full of best positions found by particles.
	private int[] gbest; //global best position.
	private int length_of_gbest = 5;
	private int[] p_seed; //seed particle.

	
	//LS
	private int LSIter = 30;
	
	//for evaluating.
	private int[][] ResourceMap;
	private int[] Current;
	
	//for recording
	private long startTime;
	
	
	public PSO(String[][] worktable, int[][] timetable, int maxmachine, int Popsize, String problem, int num_iterations){
		this.worktable = worktable;
		this.timetable = timetable;
		this.maxmachine = maxmachine;
		m = worktable.length;
		n = worktable[0].length;
		this.Popsize = Popsize;
		size = 100;
		
		this.problem_name = problem;
		
		this.num_iterations = num_iterations;
		
		
		ini_particles = new ArrayList<int[]>();
		particles = new ArrayList<int[]>();
		p_pbests = new ArrayList<int[]>();;
		
		startTime = System.currentTimeMillis();
	}
	
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

		gbest = p_pbests.get(0);
		
	}
	


	public int[] run() throws InterruptedException, ExecutionException{
		
		System.out.println("PSO procedure is running...\r\n");
		
		int iteration = 0;
		
		int particles_per_Thread = Popsize/5; //number of Threads to be generated.
		
		while((iteration++)<num_iterations){
			
			System.out.println(iteration+"-th iteration:");
			System.out.println("Best solution's fitness: "+ gbest[m*n]);
			System.out.println("Elapse time: "+ (System.currentTimeMillis()-startTime)/1000+"s");
			System.out.println();
			
			//1. PSO process
			ExecutorService esor1 = Executors.newFixedThreadPool(5);
			List<Future<List<int[]>>> FutureList = new ArrayList<Future<List<int[]>>>();
			for(int i=0; i<5; i++){

				List<int[]> list = new ArrayList<int[]>();
				for(int j=particles_per_Thread*i; j<particles_per_Thread*i+particles_per_Thread; j++){

					list.add(particles.get(j));
				}
				
				FutureList.add(esor1.submit(new PSOProcess(worktable, timetable, maxmachine, gbest, list)));
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
			
			
			//2.LS
			ExecutorService esor_LS = Executors.newFixedThreadPool(5);
			List<Future<List<int[]>>> FutureList_LS = new ArrayList<Future<List<int[]>>>();
			for(int i=0; i<5; i++){

				List<int[]> list = new ArrayList<int[]>();
				for(int j=particles_per_Thread*i; j<particles_per_Thread*i+particles_per_Thread; j++){

					list.add(particles.get(j));
				}
				
				FutureList_LS.add(esor_LS.submit(new LS(worktable, timetable, maxmachine, list, LSIter)));
			}
			//update particles
			particles.clear();
			for(int i=0; i<5; i++){
				
				List<int[]> list = new ArrayList<int[]>();
				list = FutureList_LS.get(i).get();
				
				for(int j=0; j<list.size(); j++){
					particles.add(list.get(i));
				}	
			}
			esor_LS.shutdown();
			
			
			
			//update pbest
			for(int i=0; i<Popsize; i++){
				
				if(particles.get(i)[m*n] < p_pbests.get(i)[m*n]){
					System.arraycopy(particles.get(i), 0, p_pbests.get(i), 0, m*n+1);
				}
			}
			
			//update gbest
			for(int i=0; i<Popsize; i++){
				if(p_pbests.get(i)[m*n]<=gbest[m*n]){
					gbest = p_pbests.get(i);
				}
			}

			/*
			//2. SA
			ExecutorService esor_SA = Executors.newFixedThreadPool(5);
			List<Future<int[]>> FutureList_SA = new ArrayList<Future<int[]>>();
			for(int i=0; i<length_of_gbest; i++){
		
				FutureList_SA.add(esor_SA.submit(new SA(worktable, timetable, maxmachine, T, gbest.get(i))));
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
			*/
			
			
		}
		
		
		
		return gbest;
		
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
			p[m*n] = evaluate_2(p1);
			
			ini_particles.add(p);
			pos=0;
		}
	}

	
	/*
	 * This method is used to get the fitness from the input permutation(solution)
	 * input:a permutation(solution) to be evaluated.
	 * output:fitness.
	 * This method calls: updatefunction, vectorsMinus.
	 */
	public int evaluate_2(int[] jobs2){
		// TODO 自动生成的方法存根
		int row;
		String demand;
		int time; //processing time
		/*
		 * ResourceMap[machine][time].
		 * element = 0; available machine;
		 * element = 1; occupied machine;
		 */
		ResourceMap = new int[maxmachine][9000]; 
		/*
		 * Current[job]: Next operation of the job can start here or after.
		 */
		Current = new int[m];

		//找出每个元素对应的已知量：需要 哪几种处理机 和 相应的时间。
		//operation[j]=i: the (i-1)th operation of job (j-1).
		int[] operations = new int[m];
		for(int i=0; i<jobs2.length; i++){
			row = jobs2[i];   //元素所在矩阵的行，也就是工作的序号。
			operations[row-1]++;
			demand = worktable[row-1][operations[row-1]-1];
			time = timetable[row-1][operations[row-1]-1];
			updatefunction_2(row, demand, time, Current, ResourceMap);
		}
		//取出耗时最长的工作的工作时间即为适应值函数的返回值。
		int fitness_result = Current[0];
		for(int i=0; i<Current.length;i++){
			if(fitness_result<Current[i]){
				fitness_result = Current[i];
			}
		}
		return fitness_result;	
	}
	
	private void updatefunction_2(int row, String demand, int time, int[] current, int[][] resourcemap) {
		// TODO 自动生成的方法存根
		int[] demandvector = new int[maxmachine];//一个0-1变量,表示一个工序对机器的使用情况
		int[] resourcevector = new int[maxmachine];//一个0-1变量,表示机器资源的使用情况
		int further=0;//若不满足条件，向后推迟的次数
		int count=0;//用于记录连续的次数
		String[] p_splited;
		//把机器的占用设成0-1向量：例如(1,0,1,0,0)，表示只占用1和3机器
		//解析demand,看看需要哪几台机器
		p_splited = demand.split(";");
		for(int j=0 ;j<p_splited.length; j++){
			int index = Integer.parseInt(p_splited[j]);
			demandvector[index-1]=1;
		}
		
		for(int i=0; count < time; i++){
			//提取该时刻的机器余量
			for(int y=0;y<maxmachine;y++){
				resourcevector[y] = resourcemap[y][current[row-1]+i];
			}
			if(vectorsMinus_2(resourcevector,demandvector)){//如果这个时候的余量足够。
				count++;
			}else{      //如果某一时刻余量不够，则延长一个单位的搜索范围。
				if(count!=0){   //不了丢失信息
					further = count + further;
				}
				count=0;//打断连续从新开始计数。
				further++;
			}
		}

		//下面的代码用于更新resourcemap
		for(int j = current[row-1]+further; j<current[row-1]+further+time; j++){
			for(int k=0; k<maxmachine; k++){
				if(demandvector[k]!=0){
					resourcemap[k][j] = 1;
				}
			}
		}
		ResourceMap = resourcemap;
		
		//下面的代码用于更新current[]
		current[row-1]= current[row-1]+time+further;
		Current = current;
	}
	
	private boolean vectorsMinus_2(int[] resourcevector, int[] demandvector) {
		// TODO Auto-generated method stub
		boolean result = true;
		for(int i=0;i<maxmachine;i++){		
			if(resourcevector[i]==1 && demandvector[i]==1){
				result = false;
				break;
			}
		}
		return result;
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
