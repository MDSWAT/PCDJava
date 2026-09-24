public class Student1 {

    private int[] mas;

    private Thread th1;
    private Thread th2;

    public Student1(int[] mas) {
        this.mas = mas;

        th1 = new Thread(new Th1(), "Student1-Th1");
        th2 = new Thread(new Th2(), "Student1-Th2");
    }

    // Th1 - Condiția 1
    class Th1 implements Runnable {

        @Override
        public void run() {

            long suma = 0;

            // Pozițiile pare: 0, 2, 4, 6...
            // Le luăm câte două.
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

    // Th2 - Condiția 2
    class Th2 implements Runnable {

        @Override
        public void run() {

            long suma = 0;

            // Pornim de la ultimul index par: 98
            // și mergem spre început.
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

    public void start() {
        th1.start();
        th2.start();
    }

    public void join() throws InterruptedException {
        th1.join();
        th2.join();
    }
}