package HJSMT.PSO_LS_MT;

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
		// TODO �Զ����ɵķ������
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

		//�ҳ�ÿ��Ԫ�ض�Ӧ����֪������Ҫ �ļ��ִ���� �� ��Ӧ��ʱ�䡣
		//operation[j]=i: the (i-1)th operation of job (j-1).
		int[] operations = new int[m];
		for(int i=0; i<jobs2.length; i++){
			row = jobs2[i];   //Ԫ�����ھ�����У�Ҳ���ǹ�������š�
			operations[row-1]++;
			demand = worktable[row-1][operations[row-1]-1];
			time = timetable[row-1][operations[row-1]-1];
			updatefunction_2(row, demand, time, Current, ResourceMap);
		}
		//ȡ����ʱ��Ĺ����Ĺ���ʱ�伴Ϊ��Ӧֵ�����ķ���ֵ��
		int fitness_result = Current[0];
		for(int i=0; i<Current.length;i++){
			if(fitness_result<Current[i]){
				fitness_result = Current[i];
			}
		}
		return fitness_result;	
	}
	
	private void updatefunction_2(int row, String demand, int time, int[] current, int[][] resourcemap) {
		// TODO �Զ����ɵķ������
		int[] demandvector = new int[maxmachine];//һ��0-1����,��ʾһ������Ի�����ʹ�����
		int[] resourcevector = new int[maxmachine];//һ��0-1����,��ʾ������Դ��ʹ�����
		int further=0;//������������������ƳٵĴ���
		int count=0;//���ڼ�¼�����Ĵ���
		String[] p_splited;
		//�ѻ�����ռ�����0-1����������(1,0,1,0,0)����ʾֻռ��1��3����
		//����demand,������Ҫ�ļ�̨����
		p_splited = demand.split(";");
		for(int j=0 ;j<p_splited.length; j++){
			int index = Integer.parseInt(p_splited[j]);
			demandvector[index-1]=1;
		}
		
		for(int i=0; count < time; i++){
			//��ȡ��ʱ�̵Ļ�������
			for(int y=0;y<maxmachine;y++){
				resourcevector[y] = resourcemap[y][current[row-1]+i];
			}
			if(vectorsMinus_2(resourcevector,demandvector)){//������ʱ��������㹻��
				count++;
			}else{      //���ĳһʱ���������������ӳ�һ����λ��������Χ��
				if(count!=0){   //���˶�ʧ��Ϣ
					further = count + further;
				}
				count=0;//����������¿�ʼ������
				further++;
			}
		}

		//����Ĵ������ڸ���resourcemap
		for(int j = current[row-1]+further; j<current[row-1]+further+time; j++){
			for(int k=0; k<maxmachine; k++){
				if(demandvector[k]!=0){
					resourcemap[k][j] = 1;
				}
			}
		}
		ResourceMap = resourcemap;
		
		//����Ĵ������ڸ���current[]
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
