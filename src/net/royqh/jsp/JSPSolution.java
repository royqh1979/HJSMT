package net.royqh.jsp;

public class JSPSolution {
    int jobOrderInMachines[][];
    int taskOrderInMachines[][];
    int finishTime;

    public JSPSolution(int[][] jobOrderInMachines, int[][] taskOrderInMachines, int finishTime) {
        this.jobOrderInMachines = jobOrderInMachines;
        this.taskOrderInMachines = taskOrderInMachines;
        this.finishTime = finishTime;
    }

    /**
     * 任务在机器中的执行顺序，和taskOrderInMachines配合使用
     * 例如，1号机器中执行的第一个任务是 工件（编号为jobOrderInMachines[1][0]）的第i项任务（i的值为taskOrderInMachines[1][0])
     * @return
     */
    public int[][] getJobOrderInMachines() {
        return jobOrderInMachines;
    }

    /**
     * 任务在机器中的执行顺序，和jobOrderInMachines配合使用
     * 例如，1号机器中执行的第一个任务是 工件（编号为jobOrderInMachines[1][0]）的第i项任务（i的值为taskOrderInMachines[1][0])
     * @return
     */
    public int[][] getTaskOrderInMachines() {
        return taskOrderInMachines;
    }

    /**
     * 获取最终完成时间
     * @return
     */
    public int getFinishTime() {
        return finishTime;
    }
}
