package HJSMT.PSO_SA_MT;

import java.util.HashSet;
import java.util.Set;

/**
 * 计算适应度的类
 */
public class FitnessCalculator{
    //Basic information.
    private Set<Integer>[][] machinetable;
    private int[][] timetable;
    private int maxmachine;
    private int m; //number of jobs.
    private int n; //number of operation per job.

    /**
     *  把机器的占用设成0-1向量：例如(1,0,1,0,0)，表示只占用1和3机器
     * @param worktable
     */
    private void parseWorktable(String[][] worktable) {
        machinetable=new Set[worktable.length][worktable[0].length];

        for (int i=0;i<worktable.length;i++) {
            for (int j=0;j<worktable[0].length;j++) {
                machinetable[i][j]=new HashSet<>();
                String[] p_splited;
                String demand=worktable[i][j];
                //把机器的占用设成0-1向量：例如(1,0,1,0,0)，表示只占用1和3机器
                //解析demand,看看需要哪几台机器
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
     * 计算适应值
     * @param jobs
     * @return
     */
    public int fitness(int[] jobs) {
        // TODO 自动生成的方法存根
        Set<Integer> demand;
        int time; //processing time
        /**
         * 每台machine的的已占用时间
         */
        int[] currentMachineTime = new int[maxmachine];

        /*
         * Current[job]: Next operation of the job can start here or after.
         */
        int[] currentJobTime = new int[m];

        //找出每个元素对应的已知量：需要 哪几种处理机 和 相应的时间。
        //operation[j]=i: the (i-1)th operation of job (j-1).
        int[] operations = new int[m];

        for (int i = 0; i < m*n; i++) {
            int job_id = jobs[i] - 1;   //元素所在矩阵的行，也就是工作的序号。
            operations[job_id]++;
            int op_in_job = operations[job_id] - 1;
            demand = machinetable[job_id][op_in_job];
            time = timetable[job_id][op_in_job];
            updatefunction_2(job_id, demand, time, currentJobTime, currentMachineTime);
        }
        //取出耗时最长的工作的工作时间即为适应值函数的返回值。
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
        //更新currentJobTime
        currentJobTime[job_id] = endTime;

        //下面的代码用于更新currentMachineTime;
        for (int machine_id : demand) {
            currentMachineTime[machine_id] = endTime;
        }

    }

}
