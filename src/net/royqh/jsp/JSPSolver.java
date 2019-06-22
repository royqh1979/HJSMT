package net.royqh.jsp;

/**
 * 求解器接口
 */
public interface JSPSolver {
    /**
     * 求解JSP问题
     * @param problem
     * @return 最短完成时间
     */
    int solve(JobshopSheduleProblem problem);

}
