
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Main {

    // Parametrii variantei 5
    private static final int X = 2;   // Numar producatori
    private static final int Y = 5;   // Numar consumatori
    private static final int Z = 3;   // Obiecte consumate de fiecare consumator
    private static final int D = 12;  // Capacitatea depozitului
    private static final int F = 2;   // Obiecte produse la fiecare operatie

    private static final String NUME_STUDENTI =
            "Pulbere Alexandrina si Serghei Stefan";

    public static void main(String[] args) throws InterruptedException {

        System.out.println("================================================");
        System.out.println("          LUCRAREA DE LABORATOR NR. 4");
        System.out.println("       SINCRONIZAREA THREAD-URILOR IN JAVA");
        System.out.println("================================================");

        System.out.println("Studenti: " + NUME_STUDENTI);
        System.out.println("Varianta: 5");
        System.out.println("Numar producatori: " + X);
        System.out.println("Numar consumatori: " + Y);
        System.out.println("Obiecte consumate de fiecare: " + Z);
        System.out.println("Capacitatea depozitului: " + D);
        System.out.println("Tip obiecte: Numere pare");
        System.out.println("Obiecte produse la fiecare operatie: " + F);
        System.out.println("Total obiecte necesare: " + (Y * Z));

        System.out.println("================================================\n");

        // Crearea depozitului comun
        Store store = new Store(D, Y * Z, F);

        // Crearea producatorilor
        Producer[] producers = new Producer[X];

        for (int i = 0; i < X; i++) {
            producers[i] = new Producer(store);
            producers[i].setName("Producator Nr. " + (i + 1));
        }

        // Crearea consumatorilor
        Consumer[] consumers = new Consumer[Y];

        for (int i = 0; i < Y; i++) {
            consumers[i] = new Consumer(store, Z);
            consumers[i].setName("Consumator Nr. " + (i + 1));
        }

        // Pornirea producatorilor
        for (Producer producer : producers) {
            producer.start();
        }

        // Pornirea consumatorilor
        for (Consumer consumer : consumers) {
            consumer.start();
        }

        // Asteptarea finalizarii tuturor thread-urilor
        for (Producer producer : producers) {
            producer.join();
        }

        for (Consumer consumer : consumers) {
            consumer.join();
        }

        System.out.println("\n================================================");
        System.out.println("       TOATE THREAD-URILE AU TERMINAT");
        System.out.println("================================================");

        System.out.println("Total obiecte produse: " +
                store.getTotalProduced());

        System.out.println("Total obiecte consumate: " +
                store.getTotalConsumed());

        System.out.println("Obiecte ramase in depozit: " +
                store.getStockSize());

        System.out.println("\nLucrarea a fost executata cu succes!");
    }


    // ====================================================
    // CLASA STORE - DEPOZITUL COMUN
    // ====================================================

    static class Store {

        private final ArrayList<Integer> stockList =
                new ArrayList<>();

        private final int capacity;
        private final int totalRequired;
        private final int objectsPerOperation;

        private int totalProduced = 0;
        private int totalConsumed = 0;
        private int remainingToProduce;

        public Store(int capacity, int totalRequired,
                     int objectsPerOperation) {

            this.capacity = capacity;
            this.totalRequired = totalRequired;
            this.objectsPerOperation = objectsPerOperation;
            this.remainingToProduce = totalRequired;
        }

        // Metoda pentru producerea obiectelor
        public synchronized boolean put(String producerName) {

            if (remainingToProduce == 0) {
                return false;
            }

            int count = Math.min(
                    objectsPerOperation,
                    remainingToProduce
            );

            // Asteptam daca depozitul nu are suficient spatiu
            while (capacity - stockList.size() < count) {

                System.out.println(
                        producerName +
                                ": Depozitul este plin! Asteptare..."
                );

                try {
                    wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return false;
                }

                if (remainingToProduce == 0) {
                    return false;
                }

                count = Math.min(
                        objectsPerOperation,
                        remainingToProduce
                );
            }

            // Producerea numerelor pare
            ArrayList<Integer> producedNumbers = new ArrayList<>();

            for (int i = 0; i < count; i++) {

                int number = ThreadLocalRandom.current()
                        .nextInt(0, 10) * 2;

                producedNumbers.add(number);
            }

            // Introducerea numerelor in depozit
            stockList.addAll(producedNumbers);

            totalProduced += count;
            remainingToProduce -= count;

            System.out.println(
                    producerName +
                            " a produs " + count +
                            " numere: " + producedNumbers
            );

            System.out.println(
                    "Depozitul contine " +
                            stockList.size() + "/" + capacity +
                            " obiecte: " + stockList
            );

            // Anuntam consumatorii
            notifyAll();

            return true;
        }


        // Metoda pentru consumarea obiectelor
        public synchronized int get(String consumerName) {

            // Asteptam daca depozitul este gol
            while (stockList.isEmpty()) {

                System.out.println(
                        consumerName +
                                ": Depozitul este gol! Asteptare..."
                );

                try {
                    wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return -1;
                }
            }

            // Extragerea unui obiect din depozit
            int number = stockList.remove(0);

            totalConsumed++;

            System.out.println(
                    consumerName +
                            " a consumat numarul: " + number
            );

            System.out.println(
                    "Depozitul contine " +
                            stockList.size() + "/" + capacity +
                            " obiecte: " + stockList
            );

            // Anuntam producatorii
            notifyAll();

            return number;
        }


        // Informatii despre depozit
        public synchronized int getTotalProduced() {
            return totalProduced;
        }

        public synchronized int getTotalConsumed() {
            return totalConsumed;
        }

        public synchronized int getStockSize() {
            return stockList.size();
        }
    }


    // ====================================================
    // CLASA PRODUCER - PRODUCATOR
    // ====================================================

    static class Producer extends Thread {

        private final Store store;

        public Producer(Store store) {
            this.store = store;
        }

        @Override
        public void run() {

            while (true) {

                boolean result = store.put(getName());

                if (!result) {
                    break;
                }

                try {
                    Thread.sleep(
                            ThreadLocalRandom.current()
                                    .nextInt(100, 501)
                    );
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            System.out.println(
                    getName() + " a terminat producerea."
            );
        }
    }


    // ====================================================
    // CLASA CONSUMER - CONSUMATOR
    // ====================================================

    static class Consumer extends Thread {

        private final Store store;
        private final int requiredObjects;

        public Consumer(Store store, int requiredObjects) {
            this.store = store;
            this.requiredObjects = requiredObjects;
        }

        @Override
        public void run() {

            int consumed = 0;

            while (consumed < requiredObjects) {

                int number = store.get(getName());

                if (number == -1) {
                    break;
                }

                consumed++;

                System.out.println(
                        getName() +
                                " - obiecte consumate: " +
                                consumed + "/" + requiredObjects
                );

                try {
                    Thread.sleep(
                            ThreadLocalRandom.current()
                                    .nextInt(200, 601)
                    );
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            System.out.println(
                    getName() +
                            " a consumat toate cele " +
                            consumed + " obiecte."
            );
        }
    }
}
