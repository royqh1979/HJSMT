package net.royqh.HJSMT.PSO_LS_MT;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class LS implements Callable<List<int[]>> {

	private List<int[]> list;
	private List<int[]> resultList;
	
	private int max_neighbors;
	
	private String[][] worktable;
	private int[][] timetable;
	private int maxmachine;
	private int m;
	private int n;
	
	private int[][] ResourceMap;
	private int[] Current;
	
	//basic information
	public LS(String[][] worktable, int[][] timetable, int maxmachine, List<int[]> list, int max_neighbors){
		this.worktable = worktable;
		this.timetable = timetable;
		this.maxmachine = maxmachine;
		m = worktable.length;
		n = worktable[0].length;
		this.list = list;
		this.max_neighbors = max_neighbors;
		resultList = new ArrayList<int[]>();
	}

	
	@Override
	public List<int[]> call() {
		
		int[] temp;
		
		for(int i=0; i<list.size(); i++){
			
			temp = Improvement_Method(list.get(i));
			
			resultList.add(temp);
			
		}
		
		return resultList;
	}
	
	private int[] Improvement_Method(int[] s) {
		// TODO Auto-generated method stub
		int[] s_neighbor;//with fitness
		int[] s_local_best = new int[m*n+1];
		int num=0; //number of iterations.
		
		System.arraycopy(s, 0, s_local_best, 0, s.length);;
		
		while(num < max_neighbors){
			s_neighbor = getNeighbor(s_local_best);//Visit one neighbor.
			
			if(s_neighbor[m*n] <= s_local_best[m*n]){
				System.arraycopy(s_neighbor, 0, s_local_best, 0, m*n+1);
			}
			num++;
		}
		return s_local_best;
	}
	
	private int[] getNeighbor(int[] s) {
		// TODO Auto-generated method stub
		int ran1 = (int) (Math.random()*(m*n)); //ran1~[0,m*n-1]
		int ran2 = (int) (Math.random()*(m*n));
		int temp;
		int[] neighbor = new int[m*n+1];//with fitness
		int[] neighbor1 = new int[m*n]; //without fitness
		
		System.arraycopy(s, 0, neighbor1, 0, m*n);
		
		//different positions selected.
		while(neighbor1[ran1]==neighbor1[ran2]){
			ran2 = (int) (Math.random()*(m*n));
		}
		
		temp = neighbor1[ran1];
		neighbor1[ran1] = neighbor1[ran2];
		neighbor1[ran2] = temp;
		//put fitness into neighbor
		System.arraycopy(neighbor1, 0, neighbor, 0, m*n);
		neighbor[m*n] = evaluate_2(neighbor1);
		
		return neighbor;
	}
	
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

}
