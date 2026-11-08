
import java.util.concurrent.CountDownLatch;

public class Main {

    public static void main(String[] args)
            throws InterruptedException {

        // ==================================================
        // PARTEA 1 - ALEXANDRINA
        // Crearea structurii grupurilor si a firelor
        // ==================================================

        // 1. Cream grupul principal Main.
        ThreadGroup mainGroup = new ThreadGroup("Main");

        // 2. Cream subgrupul GE in interiorul grupului Main.
        ThreadGroup ge = new ThreadGroup(mainGroup, "GE");

        // 3. Cream subgrupul GH in interiorul grupului GE.
        ThreadGroup gh = new ThreadGroup(ge, "GH");

        // 4. Cream un mecanism de sincronizare.
        // Firele vor astepta pana cand sunt enumerate.
        CountDownLatch latch = new CountDownLatch(1);

        // 5. Cream firele din grupul principal Main.
        // Fiecare fir primeste numele si prioritatea din varianta 5.
        Thread th1 = createThread(mainGroup, "Th1", 3, latch);
        Thread th2Main = createThread(mainGroup, "Th2", 7, latch);

        // 6. Cream firele din subgrupul GH.
        Thread tha = createThread(gh, "Tha", 4, latch);
        Thread thb = createThread(gh, "Thb", 3, latch);
        Thread thc = createThread(gh, "Thc", 2, latch);
        Thread thd = createThread(gh, "Thd", 1, latch);

        // 7. Cream firele care apartin direct grupului GE.
        Thread thA = createThread(ge, "ThA", 3, latch);
        Thread th2GE = createThread(ge, "Th2", 6, latch);
        Thread th3 = createThread(ge, "Th3", 3, latch);

        // 8. Introducem toate cele 9 fire intr-un tablou.
        Thread[] threads = {
                th1, th2Main, tha, thb, thc, thd,
                thA, th2GE, th3
        };

        // 9. Pornim toate firele de executie.
        // Metoda start() porneste fiecare fir separat.
        for (Thread t : threads) {
            t.start();
        }


        // ==================================================
        // PARTEA 2 - STEFAN
        // Enumerarea, afisarea si sincronizarea firelor
        // ==================================================

        // 10. Afisam informatii despre grupul principal.
        System.out.println("GRUPUL PRINCIPAL: Main");
        System.out.println("--------------------------------");

        // 11. Cream un tablou pentru firele active.
        // activeCount() estimeaza numarul firelor active.
        // Adaugam spatiu suplimentar pentru siguranta.
        Thread[] allThreads =
                new Thread[mainGroup.activeCount() * 2 + 10];

        // 12. Enumeram toate firele din grupul Main.
        // Parametrul true include si subgrupurile GE si GH.
        int count = mainGroup.enumerate(allThreads, true);

        // 13. Parcurgem firele gasite si afisam informatiile.
        for (int i = 0; i < count; i++) {

            Thread t = allThreads[i];

            // Afisam numele, grupul si prioritatea fiecarui fir.
            System.out.println(
                    "Nume fir: " + t.getName()
                            + " | Grup: " + t.getThreadGroup().getName()
                            + " | Prioritate: " + t.getPriority()
            );
        }

        // 14. Eliberam firele care asteapta la CountDownLatch.
        // countDown() reduce contorul la zero si permite
        // continuarea executiei firelor.
        latch.countDown();

        // 15. Asteptam terminarea tuturor firelor.
        // join() permite firului principal sa astepte
        // pana cand fiecare fir isi termina executia.
        for (Thread t : threads) {
            t.join();
        }
    }


    // ==================================================
    // PARTEA 2 - STEFAN
    // Metoda pentru crearea firelor de executie
    // ==================================================

    public static Thread createThread(
            ThreadGroup group,
            String name,
            int priority,
            CountDownLatch latch) {

        // Cream o sarcina Runnable.
        // Aceasta asteapta eliberarea mecanismului latch.
        Runnable task = () -> {
            try {
                latch.await();
            } catch (InterruptedException e) {

                // Restabilim starea de intrerupere a firului.
                Thread.currentThread().interrupt();
            }
        };

        // Cream firul si ii atribuim grupul, sarcina si numele.
        Thread thread = new Thread(group, task, name);

        // Stabilim prioritatea firului de executie.
        thread.setPriority(priority);

        // Returnam firul creat.
        return thread;
    }
}
