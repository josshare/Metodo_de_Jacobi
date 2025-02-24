import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.CountDownLatch;

public class JacobiMethodParallel {
    private static final int NUM_THREADS = Runtime.getRuntime().availableProcessors();

    private static class JacobiWorker implements Runnable {
        private final double[][] A;
        private final double[] b;
        private final double[] xOld;
        private final double[] x;
        private final int startRow;
        private final int endRow;
        private final CountDownLatch latch;

        public JacobiWorker(double[][] A, double[] b, double[] xOld, double[] x,
                           int startRow, int endRow, CountDownLatch latch) {
            this.A = A;
            this.b = b;
            this.xOld = xOld;
            this.x = x;
            this.startRow = startRow;
            this.endRow = endRow;
            this.latch = latch;
        }

        @Override
        public void run() {
            for (int i = startRow; i < endRow; i++) {
                double sum = 0.0;
                for (int j = 0; j < A.length; j++) {
                    if (i != j) {
                        sum += A[i][j] * xOld[j];
                    }
                }
                x[i] = (b[i] - sum) / A[i][i];
            }
            latch.countDown();
        }
    }

    public static double[] jacobi(double[][] A, double[] b, double[] x0, double tol, int maxIter) {
        int n = A.length;
        double[] x = new double[n];
        double[] xOld = Arrays.copyOf(x0, n);
        
        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
        
        try {
            for (int k = 0; k < maxIter; k++) {
                CountDownLatch latch = new CountDownLatch(NUM_THREADS);
                
                // Split the work among threads
                int rowsPerThread = n / NUM_THREADS;
                int remainingRows = n % NUM_THREADS;
                int startRow = 0;
                
                // Create and submit tasks for each thread
                for (int t = 0; t < NUM_THREADS; t++) {
                    int threadRows = rowsPerThread + (t < remainingRows ? 1 : 0);
                    int endRow = startRow + threadRows;
                    
                    executor.execute(new JacobiWorker(A, b, xOld, x, startRow, endRow, latch));
                    startRow = endRow;
                }
                
                // Wait for all threads to complete
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return null;
                }

                // Check for convergence
                if (converged(x, xOld, tol)) {
                    return x;
                }

                // Update old values
                xOld = Arrays.copyOf(x, n);
            }
            return null; // Did not converge
            
        } finally {
            executor.shutdown();
        }
    }

    private static boolean converged(double[] x, double[] xOld, double tol) {
        double normDiff = 0.0;
        double normX = 0.0;
        
        for (int i = 0; i < x.length; i++) {
            normDiff += Math.pow(x[i] - xOld[i], 2);
            normX += Math.pow(x[i], 2);
        }
        
        return Math.sqrt(normDiff) / Math.sqrt(normX) < tol;
    }
}