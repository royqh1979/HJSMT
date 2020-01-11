package net.royqh.genetic.nsga2;

/***
 * 问题的一个解
 *
 * 包含各变量的值，以及该解是否可行。注意变量的下标i从1开始
 */
public class Solution {
    double vars[];
    boolean feasible =true;

    /**
     * 构造方法
     *
     * @param n 变量个数
     */
    public Solution(int n){
        this.vars = new double[n];
    }

    /**
     * 设置第i个变量的值。注意下标i从1开始
     *
     * @param i     变量i的下标
     * @param val   变量的值
     */
    public void setVar(int i,double val){
        this.vars[i-1]=val;
    }

    /**
     * 获取第i个变量的值。注意下标i从1开始
     *
     * @param i 变量的下标
     * @return  变量的值
     */
    public double getVar(int i){
        return vars[i-1];
    }

    /**
     * 该解是否为可行解
     *
     * @return
     */
    public boolean isFeasible() {
        return feasible;
    }

    /**
     * 设置该解是否为可行解
     *
     * @param feasible 
     */
    public void setFeasible(boolean feasible) {
        this.feasible = feasible;
    }
}
