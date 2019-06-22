package net.royqh.HJSMT.PSO_SA_MT;

import java.util.concurrent.Callable;

public class SA implements Callable<int[]> {

    private double T;
    private int[] p;

    private FitnessCalculator fitnessCalculator;
    private int m;
    private int n;

    private int[][] ResourceMap;
    private int[] Current;

    //basic information
    public SA(FitnessCalculator fitnessCalculator, int m, int n, double T, int[] p) {
        this.fitnessCalculator = fitnessCalculator;
        this.m = m;
        this.n = n;
        this.p = p;
        this.T = T;
    }


    @Override
    public int[] call() {

        int D;
        int[] best = new int[m * n + 1];
        System.arraycopy(p, 0, best, 0, m * n + 1);

        for (int i = 0; i < m * n; i++) {
            int[] neighbor = getNeighbor(p);
            D = neighbor[m * n] - p[m * n];
            if (D < 0) {
                System.arraycopy(neighbor, 0, p, 0, m * n + 1);
            } else {
                if (Math.random() < Math.pow(Math.E, -D / T)) {
                    //System.out.println("D="+D);
                    //System.out.println("exp(...)="+Math.pow(Math.E, -D/T));
                    //System.out.println("old="+p[m*n]+", new="+neighbor[m*n]);

                    System.arraycopy(neighbor, 0, p, 0, m * n + 1);
                    //System.out.println("p="+neighbor[m*n]);
                }
            }

            if (p[m * n] < best[m * n]) {
                System.arraycopy(p, 0, best, 0, m * n + 1);
            }
        }
        return best;
    }


    private int[] getNeighbor(int[] s) {
        // TODO Auto-generated method stub
        int ran1 = (int) (Math.random() * (m * n)); //ran1~[0,m*n-1]
        int ran2 = (int) (Math.random() * (m * n));
        int temp;
        int[] neighbor = new int[m * n + 1];//with fitness

        System.arraycopy(s, 0, neighbor, 0, m * n);

        //different positions selected.
        while (neighbor[ran1] == neighbor[ran2]) {
            ran2 = (int) (Math.random() * (m * n));
        }

        temp = neighbor[ran1];
        neighbor[ran1] = neighbor[ran2];
        neighbor[ran2] = temp;
        //put fitness into neighbor
        neighbor[m * n] = fitnessCalculator.fitness(neighbor);

        return neighbor;
    }

}
