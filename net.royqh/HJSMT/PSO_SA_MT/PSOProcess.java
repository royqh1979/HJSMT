package HJSMT.PSO_SA_MT;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class PSOProcess implements Callable<List<int[]>>{

	private int[] gbest;
	
	private FitnessCalculator fitnessCalculator;
	private int m;
	private int n;
	private List<int[]> particles;
	private List<int[]> resultList;
	
	private int max_neighbors = 100;
	
	private int[][] ResourceMap;
	private int[] Current;
	
	public PSOProcess(FitnessCalculator fitnessCalculator,int m,int n, int[] gbest, List<int[]> particles){
		
		this.fitnessCalculator=fitnessCalculator;
		this.m = m;
		this.n = n;
		this.gbest = gbest;
		this.particles = particles;

		resultList = new ArrayList<int[]>();
		
		
	}
	
	
	@Override
	public List<int[]> call() throws Exception {
		// TODO Auto-generated method stub
		for(int i=0; i < particles.size(); i++){
			int[] s_combined = new int[m*n+1];
			s_combined = Improvement_Method(SolutionCombinationMethod(gbest, particles.get(i)));
			resultList.add(s_combined);	
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
		neighbor[m*n] = fitnessCalculator.fitness(neighbor1);
		
		return neighbor;
	}
	
	

	private int[] SolutionCombinationMethod(int[] a, int[] b) {
		// TODO Auto-generated method stub
		int[] a1 = new int[m*n];
		int[] b1 = new int[m*n];
		int[] p_combined_a = new int[m*n+1];//with fitness
		int[] p_combined_a1 = new int[m*n];//without fitness
		int[] p_combined_b = new int[m*n+1];//with fitness
		int[] p_combined_b1 = new int[m*n];//without fitness
		int index=0;
		
		System.arraycopy(a, 0, a1, 0, m*n);
		System.arraycopy(b, 0, b1, 0, m*n);
		
		/*
		 * a.First of all, S1 and S2 will be defined which are used in following section.
		 */
		//some selected job number would be put in S1 and the others would be put in S2.
		int[] S1 ;
		//Because the length can not be confirmed, Class-List is used here.
		List<Integer> S2 = new ArrayList<Integer>() ;
		//A random number [1,m-1] is generated.
		//Later r random number are generated and added to S1
		int r = (int) (Math.random()*(m-1))+1; 
		
		int counter = 0;
	
		//Pick r random numbers for S1
		S1 = getRandomnums2(1,m,r);
		
		//Initialize S2 and add the rest number to it.
		for(int i=1; i<=m; i++){
			
			for(int j=0; j<S1.length;){
				if(i==S1[j]){
					counter=0;
					break;
				}else{
					counter++;
					j++;
				}
			}
			if(counter==S1.length){
				S2.add(i);
				counter=0;
			}
		}
		
		/*
		 * b.When S1 and S2 are ready, the combined solution is generated.
		 */
		//for solution a:
		for(int i=0; i<m*n; i++){
			if(ifExist1(a1[i],S1)){
				p_combined_a1[index++] = a1[i];
			}
			if(ifExist2(b1[i],S2)){
				p_combined_a1[index++] = b1[i];
			}
		}
		System.arraycopy(p_combined_a1, 0, p_combined_a, 0, m*n);
		p_combined_a[m*n] = fitnessCalculator.fitness(p_combined_a1);
		
		//for solution b
		index=0;
		for(int i=0; i<m*n; i++){
			if(ifExist2(a1[i],S2)){
				p_combined_b1[index++] = a1[i];
			}
			
			if(ifExist1(b1[i],S1)){
				p_combined_b1[index++] = b1[i];
			}
		}
		System.arraycopy(p_combined_b1, 0, p_combined_b, 0, m*n);
		p_combined_b[m*n] = fitnessCalculator.fitness(p_combined_b1);

		if(p_combined_a[m*n]>p_combined_b[m*n]){
			return p_combined_b;
		}else{
			return p_combined_a;
		}
	}
	
	private int[] getRandomnums2(int min, int max, int n) {
		// TODO Auto-generated method stub
		int result[] = new int[n];  
		boolean state=true;
		
		if (n > (max - min + 1) || max < min) {  
	           return null;  
	    }
		
		for(int i=0; i<n;){  
			state = true;  
			int num = (int) (Math.random()*(max-min+1))+min;//随机生成一个[min,max]范围内的数字
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
	
	
	private boolean ifExist1(int r, int[] p) {
		// TODO Auto-generated method stub
		
		boolean result = false;
		for(int i=0; i<p.length; i++){
			
			if(p[i]==r){
				result = true;
				break;
			}
		}
		return result;
	}
	
	private boolean ifExist2(int r, List<Integer> p) {
		// TODO Auto-generated method stub
		
		boolean result = false;
		for(int i=0; i<p.size(); i++){
			
			if(p.get(i)==r){
				result = true;
				break;
			}
		}
		
		return result;
	}
	


}
