package net.royqh.genetic.nsga2;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * 问题类
 *
 * 记录要优化的问题，包括变量个数、各变量取值范围，以及目标函数
 * 为了方便使用（和一般数学表述一致)，变量的下标从1开始
 */
public class Problem {
    private int varNum;
    private double ranges[][];
    private List<Function<double[],Double>> objectives;

    /***
     * 构造函数
     * @param variable_numbers 变量个数
     */
    public Problem(int variable_numbers){
        this.varNum = variable_numbers;
        this.ranges = new double[variable_numbers][2];
    }

    /**
     * 设置每个变量的起止范围
     *
     * 负无穷用Double.MIN_VALUE
     * 正无穷用Double.MAX_VALUE
     * @param i         第i个变量，注意下标从1开始
     * @param start     该变量可取的最小值
     * @param end       该变量可取的最大值
     */
    public void setRange(int i,double start,double end) {
        ranges[i-1][0]=start;
        ranges[i-1][1]=end;
    }

    /**
     * 增加目标函数
     *
     * @param obj 要增加的目标函数
     */
    public void addObjective(Function<double[],Double> obj){
        objectives.add(obj);
    }

    /**
     * 取目标函数列表
     * 
     * @return  目标函数列表
     */
    public List<Function<double[], Double>> getObjectives() {
        return Collections.unmodifiableList(objectives);
    }

    /**
     * 取变量i取值下限
     *
     * @param i 变量i的下标（注意下标从1开始)
     * @return  该变量取值下限
     */
    public double getRangeStart(int i){
        return ranges[i-1][0];
    }

    /**
     * 取变量i取值上限
     *
     * @param i 变量i的下标（注意下标从1开始）
     * @return 该变量取值上限
     */
    public double getRangeEnd(int i) {
        return ranges[i-1][1];
    }

    /**
     * 获取问题中的变量个数
     *
     * @return 问题中的变量个数
     */
    public int getVarNum() {
        return varNum;
    }
}
