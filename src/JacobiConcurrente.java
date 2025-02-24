import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ThreadLocalRandom;

public class JacobiConcurrente {

    private double[][] A; // Matriz de coeficientes
    private double[] b; // Vector de términos independientes
    private double[] x; // Solución actual
    private double[] xNuevo; // Solución en la siguiente iteración
    private int iteraciones; // Número actual de iteraciones realizadas
    private int n; // Dimensión del sistema
    private double tolerancia; // Tolerancia para la convergencia
    private int maxIteraciones; // Número máximo de iteraciones
    private int numHilos; // Número de hilos a utilizar
    private CyclicBarrier barrera; // Barrera para sincronizar los hilos
    private volatile boolean converge; // Variable para indicar si el algoritmo ha convergido

    public JacobiConcurrente(double[][] A, double[] b, double[] xInicial, double tolerancia, int maxIteraciones, int numHilos) {
        this.A = A;
        this.b = b;
        this.x = xInicial;
        this.n = A.length;
        this.tolerancia = tolerancia;
        this.maxIteraciones = maxIteraciones;
        this.numHilos = numHilos;
        this.xNuevo = new double[n];
        this.barrera = new CyclicBarrier(numHilos + 1); // +1 para el hilo principal
        this.converge = false;
    }

    public double[] resolver() {
        Thread[] hilos = new Thread[numHilos];
        int tamanoBloque = n / numHilos;
        int residuo = n % numHilos;

        // Crear y lanzar los hilos
        for (int i = 0; i < numHilos; i++) {
            int inicio = i * tamanoBloque + Math.min(i, residuo);
            int fin = inicio + tamanoBloque + (i < residuo ? 1 : 0);
            hilos[i] = new Thread(new CalculadorJacobi(inicio, fin));
            hilos[i].start();
        }

        // Iterar hasta la convergencia o alcanzar el número máximo de iteraciones
        iteraciones = 0;
        for (int iteracion = 0; iteracion < maxIteraciones && !converge; iteracion++) {
            iteraciones = iteracion + 1;
            try {
                // Esperar a que todos los hilos terminen de calcular sus nuevas estimaciones
                barrera.await();

                // Verificar la convergencia
                converge = verificarConvergencia();

                // Intercambiar las soluciones actual y nueva
                double[] temp = x;
                x = xNuevo;
                xNuevo = temp;

                // Esperar a que todos los hilos terminen de verificar la convergencia e intercambiar soluciones
                barrera.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
                return null; // Error
            }
        }

        // Esperar a que todos los hilos terminen
        for (int i = 0; i < numHilos; i++) {
            try {
                hilos[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (converge) {
            System.out.println("El algoritmo de Jacobi convergió en " + iteraciones + " iteraciones.");
        } else {
            System.out.println("El algoritmo de Jacobi no convergió después de " + maxIteraciones + " iteraciones.");
        }

        return x;
    }

    private boolean verificarConvergencia() {
        double maxCambio = 0.0;
        for (int i = 0; i < n; i++) {
            double cambio = Math.abs(xNuevo[i] - x[i]);
            if (cambio > maxCambio) {
                maxCambio = cambio;
            }
        }
        return maxCambio < tolerancia;
    }

    private class CalculadorJacobi implements Runnable {
        private int inicio;
        private int fin;

        public CalculadorJacobi(int inicio, int fin) {
            this.inicio = inicio;
            this.fin = fin;
        }

        @Override
        public void run() {
            for (int iteracion = 0; iteracion < maxIteraciones && !converge; iteracion++) {
                // Calcular nuevas estimaciones para las variables asignadas a este hilo
                for (int i = inicio; i < fin; i++) {
                    double suma = 0.0;
                    for (int j = 0; j < n; j++) {
                        if (j != i) {
                            suma += A[i][j] * x[j];
                        }
                    }
                    xNuevo[i] = (b[i] - suma) / A[i][i];
                }

                try {
                    // Esperar a que todos los hilos terminen de calcular sus nuevas estimaciones
                    barrera.await();

                    // Verificar la convergencia (solo un hilo necesita hacerlo)
                    /*
                    if (inicio == 0) {
                        converge = verificarConvergencia();
                    }
                     */

                    // Intercambiar las soluciones actual y nueva
                    /*
                    double[] temp = x;
                    x = xNuevo;
                    xNuevo = temp;
                     */

                    // Esperar a que todos los hilos terminen de verificar la convergencia e intercambiar soluciones
                    barrera.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        // Ejemplo de uso
        double[][] A = {
                {4, 1, 1},
                {1, 3, 1},
                {1, 1, 4}
        };
        double[] b = {5, 5, 6};
        double[] xInicial = {0, 0, 0};
        double tolerancia = 1e-6;
        int maxIteraciones = 100;
        int numHilos = 4; // Puedes ajustar el número de hilos

        JacobiConcurrente jacobi = new JacobiConcurrente(A, b, xInicial, tolerancia, maxIteraciones, numHilos);
        double[] solucion = jacobi.resolver();

        // Imprimir la solución
        if (solucion != null) {
            System.out.println("Solución:");
            for (int i = 0; i < solucion.length; i++) {
                System.out.println("x[" + i + "] = " + solucion[i]);
            }
        } else {
            System.out.println("Error al resolver el sistema.");
        }
    }
}
