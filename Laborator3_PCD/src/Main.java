import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.locks.ReentrantLock;

public class Main {

    // Datele studentilor
    private static final String NUME = "Alexandrina si Stefan";
    private static final String PRENUME = "Pulbere si Serghei";
    private static final String GRUPA = "CR-231FR";
    private static final String DISCIPLINA =
            "Programarea concurenta si distribuita";

    // Tabloul de numere
    private static final int[][] array = new int[10][10];

    // Sincronizare pentru Thread 1 si Thread 2
    private static final CyclicBarrier barrier12 =
            new CyclicBarrier(2);

    private static final CountDownLatch finished12 =
            new CountDownLatch(2);

    // Sincronizare pentru Thread 3 si Thread 4
    private static final Semaphore semaphore34 =
            new Semaphore(1);

    private static final ReentrantLock lock34 =
            new ReentrantLock();

    // Sincronizare pentru afisarea caracterelor
    private static final Object outputLock = new Object();

    // Contorizarea finalizarii celor patru fire
    private static final CountDownLatch allFinished =
            new CountDownLatch(4);

    public static void main(String[] args) {

        System.out.println("========================================");
        System.out.println("          LABORATORUL NR. 3");
        System.out.println("              VARIANTA 5");
        System.out.println("========================================");

        System.out.println("\nStudenti:");
        System.out.println("Pulbere Alexandrina");
        System.out.println("Serghei Stefan");
        System.out.println("Grupa: " + GRUPA);
        System.out.println("Disciplina: " + DISCIPLINA);

        // Generarea tabloului
        Random random = new Random();

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                array[i][j] = random.nextInt(100) + 16;
            }
        }

        // Afisarea tabloului initial
        System.out.println("\nTabloul initial:");

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                System.out.printf("%4d", array[i][j]);
            }
            System.out.println();
        }

        // Calcularea numarului de elemente pare
        int[] numbers = flattenArray();
        List<Integer> evenNumbers = new ArrayList<>();

        for (int number : numbers) {
            if (number % 2 == 0) {
                evenNumbers.add(number);
            }
        }

        System.out.println("\nNumarul elementelor pare: "
                + evenNumbers.size());

        System.out.println("\nPornirea firelor de executie...\n");

        // Crearea a patru fire de executie
        ExecutorService executor =
                Executors.newFixedThreadPool(4);

        executor.execute(Main::thread1);
        executor.execute(Main::thread2);
        executor.execute(Main::thread3);
        executor.execute(Main::thread4);

        // Asteptarea finalizarii tuturor firelor
        try {
            allFinished.await();
            finished12.await();

            executor.shutdown();

        } catch (InterruptedException e) {
            executor.shutdownNow();
            System.out.println("Executia a fost intrerupta.");
            return;
        }

        System.out.println("\n========================================");
        System.out.println("TOATE THREAD-URILE AU TERMINAT.");
        System.out.println("========================================");

    }

    // Transformarea tabloului bidimensional in unul unidimensional
    private static int[] flattenArray() {

        int[] result = new int[100];
        int index = 0;

        for (int[] row : array) {
            for (int number : row) {
                result[index++] = number;
            }
        }

        return result;
    }

    // Functie pentru afisarea sincronizata a unui rand
    private static void printLine(String text) {

        synchronized (outputLock) {
            System.out.println(text);
        }
    }

    // Afisarea caracterelor cu interval de 100 ms
    private static void printSlowly(String text) {

        synchronized (outputLock) {

            for (int i = 0; i < text.length(); i++) {

                System.out.print(text.charAt(i));
                System.out.flush();

                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            System.out.println();
        }
    }

    // Calcularea sumei produselor numerelor pare
    private static long calculateProducts(
            int threadNumber,
            boolean fromStart) {

        int[] numbers = flattenArray();
        List<Integer> even = new ArrayList<>();

        // Cautarea numerelor pare din directia indicata
        if (fromStart) {

            for (int number : numbers) {
                if (number % 2 == 0) {
                    even.add(number);
                }
            }

        } else {

            for (int i = numbers.length - 1; i >= 0; i--) {

                if (numbers[i] % 2 == 0) {
                    even.add(numbers[i]);
                }
            }
        }

        long sum = 0;

        // Inmultirea numerelor pare cate doua
        for (int i = 0; i + 1 < even.size(); i += 2) {

            int first = even.get(i);
            int second = even.get(i + 1);

            long product = (long) first * second;
            sum += product;

            printLine("Thread " + threadNumber + ": "
                    + first + " * " + second + " = " + product);
        }

        // Afisarea elementului ramas necuplat
        if (even.size() % 2 != 0) {

            printLine("Thread " + threadNumber
                    + " - Element necuplat: "
                    + even.get(even.size() - 1));
        }

        printLine("Rezultatul Thread " + threadNumber
                + ": " + sum);

        return sum;
    }

    // THREAD 1
    // Calculeaza de la primul element
    // Afiseaza prenumele studentilor
    private static void thread1() {

        try {

            printLine("Starting Thread 1");

            // Sincronizare cu Thread 2
            barrier12.await();

            long result = calculateProducts(1, true);

            finished12.countDown();

            printLine("\nThread 1 - Prenumele studentilor:");

            printSlowly(PRENUME);

            printLine("Rezultatul final Thread 1: " + result);

        } catch (InterruptedException |
                 BrokenBarrierException e) {

            printLine("Eroare Thread 1: " + e.getMessage());

        } finally {

            allFinished.countDown();
        }
    }

    // THREAD 2
    // Calculeaza de la ultimul element
    // Afiseaza numele studentilor
    private static void thread2() {

        try {

            printLine("Starting Thread 2");

            // Sincronizare cu Thread 1
            barrier12.await();

            long result = calculateProducts(2, false);

            finished12.countDown();

            printLine("\nThread 2 - Numele studentilor:");

            printSlowly(NUME);

            printLine("Rezultatul final Thread 2: " + result);

        } catch (InterruptedException |
                 BrokenBarrierException e) {

            printLine("Eroare Thread 2: " + e.getMessage());

        } finally {

            allFinished.countDown();
        }
    }

    // Afisarea intervalului in ordine crescatoare
    private static void printAscending(int start, int end) {

        synchronized (outputLock) {

            int count = 0;

            for (int i = start; i <= end; i++) {

                System.out.print(i + " ");
                count++;

                if (count % 20 == 0) {
                    System.out.println();
                }
            }

            System.out.println();
        }
    }

    // Afisarea intervalului in ordine descrescatoare
    private static void printDescending(int start, int end) {

        synchronized (outputLock) {

            int count = 0;

            for (int i = start; i >= end; i--) {

                System.out.print(i + " ");
                count++;

                if (count % 20 == 0) {
                    System.out.println();
                }
            }

            System.out.println();
        }
    }

    // THREAD 3
    // Parcurge intervalul [567, 1002]
    // De la inceput spre sfarsit
    // Afiseaza disciplina
    private static void thread3() {

        try {

            printLine("\nStarting Thread 3");

            semaphore34.acquire();

            try {

                printLine("\n========================================");
                printLine("THREAD 3 - DISCIPLINA");
                printLine("========================================");

                printLine("Interval: [567, 1002]");
                printLine("Sensul parcurgerii: crescator");

                printAscending(567, 1002);

                printLine("Numarul elementelor parcurse: "
                        + (1002 - 567 + 1));

            } finally {

                semaphore34.release();
            }

            lock34.lock();

            try {

                printLine("\nThread 3 - Disciplina:");

                printSlowly(DISCIPLINA);

            } finally {

                lock34.unlock();
            }

        } catch (InterruptedException e) {

            printLine("Eroare Thread 3: " + e.getMessage());

        } finally {

            allFinished.countDown();
        }
    }

    // THREAD 4
    // Parcurge intervalul [567, 1100]
    // De la sfarsit spre inceput
    // Afiseaza grupa
    private static void thread4() {

        try {

            printLine("\nStarting Thread 4");

            semaphore34.acquire();

            try {

                printLine("\n========================================");
                printLine("THREAD 4 - GRUPA");
                printLine("========================================");

                printLine("Interval: [567, 1100]");
                printLine("Sensul parcurgerii: descrescator");

                printDescending(1100, 567);

                printLine("Numarul elementelor parcurse: "
                        + (1100 - 567 + 1));

            } finally {

                semaphore34.release();
            }

            lock34.lock();

            try {

                printLine("\nThread 4 - Grupa:");

                printSlowly(GRUPA);

            } finally {

                lock34.unlock();
            }

        } catch (InterruptedException e) {

            printLine("Eroare Thread 4: " + e.getMessage());

        } finally {

            allFinished.countDown();
        }
    }
}
