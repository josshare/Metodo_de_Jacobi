public class MainParallel {
    public static void main(String[] args) {
        // Given system: Ax = b
        double[][] A = {
                {10, 2, -3},
                {4, 7, -1},
                {-2, 1, 4}
        };
        double[] b = {1, -1, 5};
        double[] x0 = {0, 0, 0}; // Initial guess
        double tol = 1e-6;
        int maxIter = 100;

        System.out.println("Solving using sequential Jacobi method:");
        long startTimeSeq = System.nanoTime();
        double[] solutionSeq = JacobiMethod.jacobi(A, b, x0, tol, maxIter);
        long endTimeSeq = System.nanoTime();
        
        if (solutionSeq != null) {
            System.out.println("Sequential Solution: " + java.util.Arrays.toString(solutionSeq));
            System.out.println("Sequential Time: " + (endTimeSeq - startTimeSeq) / 1e6 + " ms");
        } else {
            System.out.println("Sequential method did not converge.");
        }

        System.out.println("\nSolving using parallel Jacobi method:");
        long startTimePar = System.nanoTime();
        double[] solutionPar = JacobiMethodParallel.jacobi(A, b, x0, tol, maxIter);
        long endTimePar = System.nanoTime();
        
        if (solutionPar != null) {
            System.out.println("Parallel Solution: " + java.util.Arrays.toString(solutionPar));
            System.out.println("Parallel Time: " + (endTimePar - startTimePar) / 1e6 + " ms");
        } else {
            System.out.println("Parallel method did not converge.");
        }
    }
}