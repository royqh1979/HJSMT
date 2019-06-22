package HJSMT.PSO_SA_MT;

import java.util.HashSet;
import java.util.Set;

/**
 * ������Ӧ�ȵ���
 */
public class FitnessCalculator{
    //Basic information.
    private Set<Integer>[][] machinetable;
    private int[][] timetable;
    private int maxmachine;
    private int m; //number of jobs.
    private int n; //number of operation per job.

    /**
     *  �ѻ�����ռ�����0-1����������(1,0,1,0,0)����ʾֻռ��1��3����
     * @param worktable
     */
    private void parseWorktable(String[][] worktable) {
        machinetable=new Set[worktable.length][worktable[0].length];

        for (int i=0;i<worktable.length;i++) {
            for (int j=0;j<worktable[0].length;j++) {
                machinetable[i][j]=new HashSet<>();
                String[] p_splited;
                String demand=worktable[i][j];
                //�ѻ�����ռ�����0-1����������(1,0,1,0,0)����ʾֻռ��1��3����
                //����demand,������Ҫ�ļ�̨����
                p_splited = demand.split(";");
                for(String s:p_splited){
                    int machine_id = Integer.parseInt(s)-1;
                    machinetable[i][j].add(machine_id);
                }
            }
        }

    }
    public FitnessCalculator(String[][] worktable, int[][] timetable, int maxmachine){

        parseWorktable(worktable);
        this.timetable = timetable;
        this.maxmachine = maxmachine;
        m = worktable.length;
        n = worktable[0].length;
    }
    /**
     * ������Ӧֵ
     * @param jobs
     * @return
     */
    public int fitness(int[] jobs) {
        // TODO �Զ����ɵķ������
        Set<Integer> demand;
        int time; //processing time
        /**
         * ÿ̨machine�ĵ���ռ��ʱ��
         */
        int[] currentMachineTime = new int[maxmachine];

        /*
         * Current[job]: Next operation of the job can start here or after.
         */
        int[] currentJobTime = new int[m];

        //�ҳ�ÿ��Ԫ�ض�Ӧ����֪������Ҫ �ļ��ִ���� �� ��Ӧ��ʱ�䡣
        //operation[j]=i: the (i-1)th operation of job (j-1).
        int[] operations = new int[m];

        for (int i = 0; i < m*n; i++) {
            int job_id = jobs[i] - 1;   //Ԫ�����ھ�����У�Ҳ���ǹ�������š�
            operations[job_id]++;
            int op_in_job = operations[job_id] - 1;
            demand = machinetable[job_id][op_in_job];
            time = timetable[job_id][op_in_job];
            updatefunction_2(job_id, demand, time, currentJobTime, currentMachineTime);
        }
        //ȡ����ʱ��Ĺ����Ĺ���ʱ�伴Ϊ��Ӧֵ�����ķ���ֵ��
        int fitness_result = currentJobTime[0];
        for (int i = 0; i < currentJobTime.length; i++) {
            if (fitness_result < currentJobTime[i]) {
                fitness_result = currentJobTime[i];
            }
        }
        return fitness_result;
    }

    private void updatefunction_2(int job_id, Set<Integer> demand, int duration, int[] currentJobTime, int[] currentMachineTime) {
        int startTime = currentJobTime[job_id];
        for (int machine_id : demand) {
            if (startTime < currentMachineTime[machine_id]) {
                startTime = currentMachineTime[machine_id];
            }
        }
        int endTime = startTime + duration;
        //����currentJobTime
        currentJobTime[job_id] = endTime;

        //����Ĵ������ڸ���currentMachineTime;
        for (int machine_id : demand) {
            currentMachineTime[machine_id] = endTime;
        }

    }

}
