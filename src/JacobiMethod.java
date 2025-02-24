import java.util.Arrays;

public class JacobiMethod {

    public static double[] jacobi(double[][] A, double[] b, double[] x0, double tol, int maxIter) {
        int n = A.length;
        double[] x = new double[n];
        double[] xOld = Arrays.copyOf(x0, n);

        for (int k = 0; k < maxIter; k++) {
            for (int i = 0; i < n; i++) {
                double sum = 0.0;
                for (int j = 0; j < n; j++) {
                    if (i != j) {
                        sum += A[i][j] * xOld[j];
                    }
                }
                x[i] = (b[i] - sum) / A[i][i];
            }

            // Check for convergence
            if (converged(x, xOld, tol)) {
                return x;
            }

            // Update old values
            xOld = Arrays.copyOf(x, n);
        }
        return null; // Did not converge
    }

    private static boolean converged(double[] x, double[] xOld, double tol) {
        double norm = 0.0;
        for (int i = 0; i < x.length; i++) {
            norm += Math.pow(x[i] - xOld[i], 2);
        }
        return Math.sqrt(norm) < tol;
    }
}
