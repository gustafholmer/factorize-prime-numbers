// Gustaf Holmer guho0000
import java.math.BigInteger;
import java.lang.annotation.Documented;

@Documented
@interface ThreadSafe {}

@ThreadSafe
public class Factorizer implements Runnable {
    private static boolean isDone = false;
    private BigInteger min;
    private BigInteger max;
    private BigInteger product;
    private BigInteger factor1;
    private BigInteger factor2;
    private BigInteger step;
    private boolean foundFactors;
    private boolean productIsPrime;

    Factorizer(BigInteger min, BigInteger product, int step) {
        this.min = min;
        this.max = product;
        this.product = product;
        this.step = BigInteger.valueOf(step);
    }

    private static synchronized void makeFound() {
        isDone = true;
    }

    public void run() {
        BigInteger number = min;

        if (product.isProbablePrime(15)) {
            productIsPrime = true;
            return;
        }

        while (number.compareTo(max) <= 0) {
            if (isDone) {
                return;
            } else if (product.remainder(number).compareTo(BigInteger.ZERO) == 0) {
                makeFound();
                factor1 = number;
                factor2 = product.divide(factor1);
                foundFactors = true;
                return;
            }
            number = number.add(step);
        }
    }

    public static void main(String[] args) {
        try {
            BigInteger productToCalculate = new BigInteger(args[0]);
            int numThreads = Integer.parseInt(args[1]);
            String outputResult = "";
            long start = System.nanoTime();

            if (productToCalculate.compareTo(BigInteger.ONE) == 0 || productToCalculate.compareTo(BigInteger.ZERO) == 0) {
                outputResult = "No factorization possible";
            } else {

                Thread[] threads = new Thread[numThreads];
                Factorizer[] factorFinders = new Factorizer[numThreads];
                for (int i = 0; i < numThreads; i++) {
                    factorFinders[i] = new Factorizer(new BigInteger(Integer.toString(2 + i)), productToCalculate, numThreads);
                    threads[i] = new Thread(factorFinders[i]);
                }

                for (int i = 0; i < numThreads; i++) {
                    threads[i].start();
                }

                for (int i = 0; i < numThreads; i++) {
                    threads[i].join();

                    if (factorFinders[i].foundFactors) {
                        outputResult = "The two factors of the inputed integer: " + factorFinders[i].factor1 +
                                " * " + factorFinders[i].factor2;
                    } else if (factorFinders[i].productIsPrime) {
                        outputResult = "No factorization possible";
                    }
                }
            }

            long stop = System.nanoTime();

            System.out.println(outputResult);
            System.out.println("Total time to execute: " + (stop-start)/1.0E9);

        } catch (Exception exception) {
            System.out.println(exception);
        }
    }
}
