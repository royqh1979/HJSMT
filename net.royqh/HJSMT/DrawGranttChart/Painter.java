package HJSMT.DrawGranttChart;

import java.awt.Graphics;
import java.awt.Point;

public class Painter {
	//basic information
	private String[][] worktable;
	private int[][] timetable;
	private int maxmachine;
	private int[] solution;
	private int m;
	private int n;
	//for evaluating
	private int[][] ResourceMap;
	private int[] Current;
	
	private int height;
	private int X_ori;
	private int Y_ori;
	
	
	public Painter(String[][] worktable,int[][] timetable,int maxmachine,int[] solution, Point point, int height){
		
		this.worktable = worktable;
		this.timetable = timetable;
		this.maxmachine = maxmachine;
		this.solution = solution;
		this.height = height;
		m = worktable.length;
		n = worktable[0].length;
		
		X_ori = point.x;
		Y_ori = point.y;
	};
	
	public int paintGranttChart(Graphics g){
		
		return evaluate_2(solution, g);
	}

	//This method has the ability to output a granttchart.
	public int evaluate_2(int[] jobs2, Graphics g){
		// TODO �Զ����ɵķ������
		if(jobs2.length == m*n+1){
			int[] temp = new int[m*n];
			System.arraycopy(jobs2, 0, temp, 0, m*n);
			jobs2 = temp;
		}
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
			updatefunction_2(row, operations[row-1], demand, time, Current, ResourceMap, g);
		}
		//ȡ����ʱ��Ĺ����Ĺ���ʱ�伴Ϊ��Ӧֵ�����ķ���ֵ��
		int fitness_result = Current[0];
		for(int i=0; i<Current.length;i++){
			if(fitness_result<Current[i]){
				fitness_result = Current[i];
			}
		}
		System.out.println("Fitness: "+fitness_result);
		return fitness_result;	
	}
	
	private void updatefunction_2(int row, int col, String demand, int time, int[] current, int[][] resourcemap, Graphics g) {
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
		/*
		 * Paint the granttchart.
		 */
		for(int i=0; i<p_splited.length; i++){
			
			int machine = Integer.parseInt(p_splited[i]);
			int X = X_ori+40+current[row-1] + further;
			int Y = Y_ori+25*(machine-1);
			g.drawRect(X, Y, time, height);
			g.drawString("O" + row +","+col, X + time/2-10, Y+15);
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
