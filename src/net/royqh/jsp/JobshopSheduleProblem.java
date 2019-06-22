package net.royqh.jsp;

/**
 * 调度车间问题类
 */
public class JobshopSheduleProblem {


    String name;
    int operationNums[];
    int machineTable[][];
    int durationTable[][];
    int numMachines;
    int numJobs;

    public JobshopSheduleProblem(String name,int operationNums[], int machineTable[][], int durationTable[][]) {
        this.name = name;
        this.operationNums = operationNums;
        this.machineTable = machineTable;
        this.durationTable = durationTable;
        this.numJobs = operationNums.length;
        this.numMachines =0;
        for (int i = 0; i< numJobs; i++ ) {
            for (int j = 0; j< operationNums[i]; j++) {
                if (numMachines <= machineTable[i][j]) {
                    numMachines = machineTable[i][j]+1;
                }
            }
        }
    }

    /**
     * 问题的名称
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * 各工件(Job)的工序(task/operation)个数
     * @return
     */
    public int[] getOperationNums() {
        return operationNums;
    }

    /**
     * 各工序加工所在的机器编号
     * @return
     */
    public int[][] getMachineTable() {
        return machineTable;
    }

    /**
     * 各工序的加工时间
     * @return
     */
    public int[][] getDurationTable() {
        return durationTable;
    }

    /**
     * 机器总数
     * @return
     */
    public int getNumMachines() {
        return numMachines;
    }

    /**
     * 工件总数
     * @return
     */
    public int getNumJobs() {
        return numJobs;
    }

    /**
     * 读取算例文件，返回对应的算例对象
     * @param filename
     * @return
     */
    public static JobshopSheduleProblem fromFile(String filename) {
        //TODO: 从文件中读取，待实现
        return null;
    }
}
