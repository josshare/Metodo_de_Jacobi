import java.util.Arrays;

public class Main {
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

        double[] solution = JacobiMethod.jacobi(A, b, x0, tol, maxIter);

        if (solution != null) {
            System.out.println("Solution: " + Arrays.toString(solution));
        } else {
            System.out.println("Method did not converge within the maximum iterations.");
        }
    }
}