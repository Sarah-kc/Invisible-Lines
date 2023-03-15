import java.util.*;
import java.io.PrintStream;
import java.io.*;
public class ComputeScores{
    public static double passed = 0.0;
    public static void doTest(double[][] lineInfo, boolean verbose, int[] solution, long expectedTime, double score) {
        if (verbose) {
            System.out.println("Input lines:");
            if (lineInfo[0].length<1) {
                System.out.println("[Empty]");
            }
            else {
                for (int i = 0; i < lineInfo[0].length; i++) {
                    System.out.printf("%d: slope=%.1f, intercept=%.1f%n", i, lineInfo[0][i], lineInfo[1][i]);
                }
            }
            System.out.printf("%nSolution:%n%s%n%n", Arrays.toString(solution));
        }

        System.gc();

        RuntimeException storedException = null;
        VirtualMachineError vmError = null;
        PrintStream out = System.out;
        PrintStream err = System.err;
        long totalTime = 0L;

        int[] student = null;
        try {
            long startTime = System.nanoTime();
            double[] slopes = Arrays.copyOf(lineInfo[0], lineInfo[0].length);
            double[] intercepts = Arrays.copyOf(lineInfo[1], lineInfo[1].length);
            student = Uppermost.visibleLines(slopes, intercepts);
            long endTime   = System.nanoTime();
            totalTime = endTime - startTime;
            slopes = null;
            intercepts = null;
        } catch (RuntimeException e) {
            storedException = e;
        } catch (VirtualMachineError e) {
            vmError = e;
        }

        System.setOut(out);
        System.setErr(err);

        if (storedException != null) {
            solution = null;
            student = null;
            System.gc();
            System.out.println("Your code generated an exception on this test.");
            //storedException.printStackTrace(System.out);
            //System.out.println(storedException.getMessage());
            throw storedException;
        } else if (vmError != null) {
            solution = null;
            student = null;
            System.gc();
            System.out.println("Your code generated a runtime error on this test.");
            throw vmError;
        }

        if (verbose) {
            System.out.printf("%nYour result:%n%s%n%n", Arrays.toString(student));
        }

        if(solution.length!=student.length)
	{
	    System.out.printf("Number of visible lines returned is incorrect.\n\t");
	    return;
	}
        for (int i = 0; i < solution.length; i++) {
	    if(solution[i]!=student[i])
	    {
               	System.out.printf("Mismatch at index " + i + "\n");
		return;
	    }
        }
        if(totalTime<expectedTime){
                System.out.printf("Correctly returned %d visible out of %d total lines%n", solution.length, lineInfo[0].length);
                passed += score;
        }
        else if(totalTime>=expectedTime)System.out.println("Time limit exceeded");
    }
    public static void baseline() {
        System.out.println("Test case name: Compilation baseline");
        System.out.println("Test case passed");
        passed += 1.0;
    }
    public static Object ReadObjectFromFile(String filepath) {

        try {

            FileInputStream fileIn = new FileInputStream(filepath);
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);

            Object obj = objectIn.readObject();

            objectIn.close();
            return obj;

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    public static void empty() {
        System.out.println("Test case name: Empty Input");
        try {
            int[] expected = (int []) ReadObjectFromFile("empty_input.dat");
            doTest(new double[][] {
                { },
                { }
            }, false, expected, 1000000000, 1.0);
        }
        finally {
            System.gc();
        }
    }
    public static void single() {
        System.out.println("Test case name: Single Line");
        try {
            int[] expected = (int []) ReadObjectFromFile("single_line1.dat");
            doTest(new double[][] {
                { -1 },
                { 0 }
            }, false, expected, 1000000000, 1.0/3.0);

            expected = (int []) ReadObjectFromFile("single_line2.dat");
            doTest(new double[][] {
                { 0 },
                { 1 }
            }, false, expected, 1000000000, 1.0/3.0);

            expected = (int []) ReadObjectFromFile("single_line3.dat");
            doTest(new double[][] {
                { 1 },
                { 0 }
            }, false, expected, 1000000000, 1.0/3.0);

        }
        finally {
            System.gc();
        }
    }
    public static void parallel() {
        System.out.println("Test case name: Two Parallel Lines");
        try {
            int[] expected = (int []) ReadObjectFromFile("two_parallel_lines1.dat");
            doTest(new double[][] {
                { 1, 1 },
                { 1, 2 }
            }, false, expected, 1000000000, 1.0/6.0);

            expected = (int []) ReadObjectFromFile("two_parallel_lines2.dat");
            doTest(new double[][] {
                { -1, -1 },
                { 1, 2 }
            }, false, expected, 1000000000, 1.0/6.0);

            expected = (int []) ReadObjectFromFile("two_parallel_lines3.dat");
            doTest(new double[][] {
                { 1, 1 },
                { -1, -2 }
            }, false, expected, 1000000000, 1.0/6.0);

            expected = (int []) ReadObjectFromFile("two_parallel_lines4.dat");
            doTest(new double[][] {
                { -1, -1 },
                { -1, -2 }
            }, false, expected, 1000000000, 1.0/6.0);

            expected = (int []) ReadObjectFromFile("two_parallel_lines5.dat");
            doTest(new double[][] {
                { 0, 0 },
                { 1, 2 }
            }, false, expected, 1000000000, 1.0/6.0);

            expected = (int []) ReadObjectFromFile("two_parallel_lines6.dat");
            doTest(new double[][] {
                { 0, 0 },
                { -1, -2 }
            }, false, expected, 1000000000, 1.0/6.0);
        }
        finally {
            System.gc();
        }
    }
    public static void two_intersecting() {
        System.out.println("Test case name: Two Intersecting Lines");
        try {
            int[] expected = (int []) ReadObjectFromFile("two_intersecting_lines1.dat");
            doTest(new double[][] {
                { 1, -1 },
                { -1, 2 }
            }, false, expected, 1000000000, 1.0/6.0);

            expected = (int []) ReadObjectFromFile("two_intersecting_lines2.dat");
            doTest(new double[][] {
                { -1, 1 },
                { -10, 0 }
            }, false, expected, 1000000000, 1.0/6.0);

            expected = (int []) ReadObjectFromFile("two_intersecting_lines3.dat");
            doTest(new double[][] {
                { -10, -3 },
                { -100, -500 }
            }, false, expected, 1000000000, 1.0/6.0);

            expected = (int []) ReadObjectFromFile("two_intersecting_lines4.dat");
            doTest(new double[][] {
                { -3, -10 },
                { 5, -100 }
            }, false, expected, 1000000000, 1.0/6.0);

            expected = (int []) ReadObjectFromFile("two_intersecting_lines5.dat");
            doTest(new double[][] {
                { 3, 10 },
                { -100, -110 }
            }, false, expected, 1000000000, 1.0/6.0);

            expected = (int []) ReadObjectFromFile("two_intersecting_lines6.dat");
            doTest(new double[][] {
                { 10, 3 },
                { 4, 8 }
            }, false, expected, 1000000000, 1.0/6.0);
        }
        finally {
            System.gc();
        }
    }
    public static void three_visible() {
        System.out.println("Test case name: Three Visible Lines");
        try {
            int [] expected = (int []) ReadObjectFromFile("three_visible_lines1.dat");
            doTest(new double[][] {
                { 0, -1, 1 },
                { 5, -5, -5}
            }, false, expected, 1000000000, 1.0/3.0);

            expected = (int []) ReadObjectFromFile("three_visible_lines2.dat");
            doTest(new double[][] {
                { 3, 2, 1 },
                { -27, -5, 0}
            }, false, expected, 1000000000, 1.0/3.0);

            expected = (int []) ReadObjectFromFile("three_visible_lines3.dat");
            doTest(new double[][] {
                { -3, -2, -1 },
                { -30, -10, -9}
            }, false, expected, 1000000000, 1.0/3.0);

        }
        finally {
            System.gc();
        }
    }
    public static void three_hiding() {
        System.out.println("Test case name: Three Lines One Hidden");
        try {
            int [] expected = (int []) ReadObjectFromFile("three_lines_one_hidden1.dat");
            doTest(new double[][] {
                { 0, -1, 1 },
                { -5, 5, 5}
            }, false, expected, 1000000000, 1.0/3.0);

            expected = (int []) ReadObjectFromFile("three_lines_one_hidden2.dat");
            doTest(new double[][] {
                { 3, 2, 1 },
                { -3, -5, 0}
            }, false, expected, 1000000000, 1.0/3.0);

            expected = (int []) ReadObjectFromFile("three_lines_one_hidden3.dat");
            doTest(new double[][] {
                { -3, -2, -1 },
                { -10, -30, -9}
            }, false, expected, 1000000000, 1.0/3.0);

        }
        finally {
            System.gc();
        }
    }
    public static void fifteen() {
        System.out.println("Test case name: 15 Lines");
        double[][] lineInfo = null;
        try {
            lineInfo = (double[][]) ReadObjectFromFile("15_lines_input.dat");

        }
        finally {
            System.gc();
        }
        try {
            int [] expected = (int []) ReadObjectFromFile("15_lines.dat");
	    System.out.println(expected.length);
            doTest(lineInfo, false, expected, 1000000000, 1.0);

        }
        finally {
            System.gc();
        }
    }
    public static void many() {
        System.out.println("Test case name: Many Lines 1");
        double[][] lineInfo = null;
        try {
            lineInfo = (double[][]) ReadObjectFromFile("many_lines_1_input.dat");

        }
        finally {
            System.gc();
        }
        try {
            int [] expected = (int []) ReadObjectFromFile("many_lines_1.dat");
            doTest(lineInfo, false, expected, 20000000000L, 2.0);

        }
        finally {
            System.gc();
        }
    }


    public static void main(String[] args) {
        System.out.println("Computing scores ...");
        ComputeScores.baseline();
        ComputeScores.empty();
        ComputeScores.single();
        ComputeScores.parallel();
        ComputeScores.two_intersecting();
        ComputeScores.three_visible();
        ComputeScores.three_hiding();
        ComputeScores.fifteen();
        ComputeScores.many();
        System.out.println("Your score: " + (((int)(passed*1000.0))/100.0) + " out of 100.0");
    }
}
