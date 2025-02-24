public class Main {
    public static void main(String[] args) {
        // Define the system parameters
        double[][] A = {
                {4, 1, 1},
                {1, 3, 1},
                {1, 1, 4}
        };
        double[] b = {5, 5, 6};
        double[] xInicial = {0, 0, 0};
        double tolerancia = 1e-6;
        int maxIteraciones = 100;
        int numHilos = 4;

        // Create and solve using JacobiConcurrente
        JacobiConcurrente jacobi = new JacobiConcurrente(A, b, xInicial, tolerancia, maxIteraciones, numHilos);
        double[] solucion = jacobi.resolver();

        // Print the solution
        if (solucion != null) {
            System.out.println("Solución encontrada:");
            for (int i = 0; i < solucion.length; i++) {
                System.out.println("x[" + i + "] = " + solucion[i]);
            }
        } else {
            System.out.println("No se pudo encontrar una solución.");
        }
    }
}