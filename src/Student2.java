public class Student2 {

    private int[] mas;

    private Thread th1;
    private Thread th2;

    public Student2(int[] mas) {
        this.mas = mas;

        th1 = new Thread(new Th1(), "Student2-Th1");
        th2 = new Thread(new Th2(), "Student2-Th2");
    }

    // Th1 - Condiția 1:
    // Sumele produselor numerelor de pe poziții pare
    // două câte două, începând cu primul element
    class Th1 implements Runnable {

        @Override
        public void run() {

            long suma = 0;

            // Pozițiile pare:
            // 0, 2, 4, 6, 8, ...
            //
            // Le luăm două câte două:
            // (0,2), (4,6), (8,10), ...

            for (int i = 0; i + 2 < mas.length; i += 4) {

                int produs = mas[i] * mas[i + 2];

                suma += produs;

                System.out.println(
                        Thread.currentThread().getName()
                                + ": mas[" + i + "] * mas[" + (i + 2) + "] = "
                                + mas[i] + " * " + mas[i + 2]
                                + " = " + produs
                );
            }

            System.out.println(
                    Thread.currentThread().getName()
                            + " -> Suma produselor = " + suma
            );
        }
    }

    // Th2 - Condiția 2:
    // Sumele produselor numerelor de pe poziții pare
    // două câte două, începând cu ultimul element
    class Th2 implements Runnable {

        @Override
        public void run() {

            long suma = 0;

            // Ultima poziție pară pentru un tablou de 100 elemente este 98.
            //
            // Le luăm două câte două de la sfârșit:
            // (98,96), (94,92), (90,88), ...

            for (int i = mas.length - 2; i - 2 >= 0; i -= 4) {

                int produs = mas[i] * mas[i - 2];

                suma += produs;

                System.out.println(
                        Thread.currentThread().getName()
                                + ": mas[" + i + "] * mas[" + (i - 2) + "] = "
                                + mas[i] + " * " + mas[i - 2]
                                + " = " + produs
                );
            }

            System.out.println(
                    Thread.currentThread().getName()
                            + " -> Suma produselor = " + suma
            );
        }
    }

    // Pornirea celor două thread-uri
    public void start() {
        th1.start();
        th2.start();
    }

    // Așteptăm terminarea celor două thread-uri
    public void join() throws InterruptedException {
        th1.join();
        th2.join();
    }
}