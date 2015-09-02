package org.xpen.chess.puzzle8;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import org.xpen.chess.puzzle8.ai.Solver;
import org.xpen.chess.puzzle8.ai.SolverAStar;
import org.xpen.chess.puzzle8.ai.SolverIdaStar;
import org.xpen.chess.puzzle8.ai.SolverIdaStar2;
import org.xpen.chess.puzzle8.ai.SolverWidthFirst;

public class GameMain {
    
    private static Solver solver;

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.print("Enter dimension(3 or 4):");
        int dimension = input.nextInt();
        System.out.print("Numbers(1 for random, 2 for pre-defined):");
        int mode = input.nextInt();
        
        //Initialize board
        byte[] initial3 = { 8,2,4,3,5,6,7,1,0 };
        byte[] initial4 = { 8,2,4,3,5,6,7,1,0,9,10,11,12,13,14,15 };
        ///byte[] initial4 = { 1,6,13,4,12,11,5,3,2,9,8,14,10,7,15,0 };
        ////byte[] initial4 = { 13,14,4,10,9,6,15,12,11,8,5,1,7,3,2,0};
        //byte[] initial4 = { 8,2,6,4,5,7,10,11,9,12,0,13,14,3,1,15};
        byte[] initial = null;
        Board init = null;
        
        if (mode == 2) {
            if (dimension == 3) {
                initial = initial3;
            } else if (dimension == 4) {
                initial = initial4;
            }
            init = new Board(dimension, initial);
            
        } else if (mode == 1) {
            
            initial = new byte[dimension * dimension];
            boolean solvable = false;
            while (!solvable) {
                List<Byte> initialList = new ArrayList<Byte>();
                for (byte i = 0; i < initial.length; i++) {
                    initialList.add(i);
                }
                
                Collections.shuffle(initialList);
                
                for (int i = 0; i <  initialList.size(); i++) {
                    initial[i] = initialList.get(i);
                }
                
                //swap 0 to last position
                for (int i = 0; i < initial.length; i++) {
                    if (initial[i] == 0) {
                        initial[i] = initial[dimension * dimension - 1];
                        initial[dimension * dimension - 1] = 0;
                    }
                }
                
                init = new Board(dimension, initial);
                solvable = init.isSolvable();
            }
        }
        
        

        
        //Choose algorithm
        System.out.println("Algorithms");
        System.out.println("1--Breadth first");
        System.out.println("2--A*");
        System.out.println("3--IDA*");
        System.out.println("4--IDA*(save memory)");
        System.out.print("Choose algorithm:");
        int algorithm = input.nextInt();
        
        input.close();
        
        if (algorithm == 1) {
            solver = new SolverWidthFirst(init);
        } else if (algorithm == 2) {
            solver = new SolverAStar(init);
        } else if (algorithm == 3) {
            solver = new SolverIdaStar(init);
        } else if (algorithm == 4) {
            solver = new SolverIdaStar2(init);
        } else {
            System.out.println("Bad Algorithms");
            return;
        }
        
        
        //Solve start
        System.out.println(init);
        
        long start = System.currentTimeMillis();
        Board solved = solver.solve();
        long elapsed = System.currentTimeMillis() - start;
        
        if (solved == null) {
            System.out.println("No solution possible");
        } else {
            solved.printAll();
            System.out.println("totalStep = " + solved.step() +", elapsed (ms) = " + elapsed + ", lookupTimes=" + solver.getLookuptimes());
            System.out.println("memory used:" + Runtime.getRuntime().totalMemory()/1024/1024 + "Mb");
        }

    }

}
