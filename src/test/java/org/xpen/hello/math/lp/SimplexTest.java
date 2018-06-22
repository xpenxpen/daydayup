package org.xpen.hello.math.lp;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.linear.LinearConstraint;
import org.apache.commons.math3.optim.linear.LinearConstraintSet;
import org.apache.commons.math3.optim.linear.LinearObjectiveFunction;
import org.apache.commons.math3.optim.linear.NonNegativeConstraint;
import org.apache.commons.math3.optim.linear.PivotSelectionRule;
import org.apache.commons.math3.optim.linear.Relationship;
import org.apache.commons.math3.optim.linear.SimplexSolver;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.junit.Test;

/**
 * 演示线性编程
 * 酿酒师问题
 * 出处：http://algs4.cs.princeton.edu/lectures/99LinearProgramming.pdf
 *
 */
public class SimplexTest {

    @Test
    public void testBeer() throws Exception {
        /*
                    求13A+23B的最大值
        5A  + 15B <= 480
        4A  + 4B  <= 160
        35A + 20B <= 1190
        A >= 0
        B >= 0
        */
        LinearObjectiveFunction f = new LinearObjectiveFunction(new double[] {13, 23}, 0);

        Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
        constraints.add(new LinearConstraint(new double[] {5, 15}, Relationship.LEQ, 480));
        constraints.add(new LinearConstraint(new double[] {4, 4}, Relationship.LEQ, 160));
        constraints.add(new LinearConstraint(new double[] {35, 20}, Relationship.LEQ, 1190));

        // create and run solver
        PointValuePair solution = new SimplexSolver().optimize(
                f, new LinearConstraintSet(constraints),
                GoalType.MAXIMIZE, new NonNegativeConstraint(true),
                PivotSelectionRule.BLAND);

        if (solution != null) {
            // get solution
            double max = solution.getValue();
            System.out.println("Opt: " + max);

            // print decision variables
            for (int i = 0; i < 2; i++) {
                System.out.print(solution.getPoint()[i] + "\t");
            }
        }
    }

}
