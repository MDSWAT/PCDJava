
public class Main {

    // ==================================================
    // PARTEA 1 - ALEXANDRINA
    // Crearea clasei MyThread si definirea comportamentului
    // firelor de executie
    // ==================================================

    // Cream o clasa proprie care extinde clasa Thread.
    // Astfel putem defini comportamentul fiecarui fir.
    static class MyThread extends Thread {

        // Constructorul clasei MyThread.
        // Primeste grupul, numele si prioritatea firului.
        public MyThread(
                ThreadGroup group,
                String name,
                int priority) {

            // Apelam constructorul clasei parinte Thread.
            // Ii transmitem grupul si numele firului.
            super(group, name);

            // Stabilim prioritatea firului de executie.
            setPriority(priority);
        }

        // Suprascriem metoda run() din clasa Thread.
        // Aceasta metoda este executata atunci cand apelam start().
        @Override
        public void run() {

            // Afisam informatiile despre firul curent:
            // getName() - numele firului.
            // getThreadGroup().getName() - numele grupului.
            // getPriority() - prioritatea firului.
            System.out.println(
                    "Nume fir: " + getName()
                            + " | Grup: " + getThreadGroup().getName()
                            + " | Prioritate: " + getPriority()
            );
        }
    }


    public static void main(String[] args)
            throws InterruptedException {


        // ==================================================
        // PARTEA 1 - ALEXANDRINA
        // Crearea grupurilor si a firelor de executie
        // ==================================================

        // 1. Cream grupul principal Main.
        ThreadGroup mainGroup = new ThreadGroup("Main");

        // 2. Cream subgrupul GE, care apartine grupului Main.
        ThreadGroup ge = new ThreadGroup(mainGroup, "GE");

        // 3. Cream subgrupul GH, care apartine grupului GE.
        ThreadGroup gh = new ThreadGroup(ge, "GH");


        // 4. Cream firele care apartin grupului principal Main.
        // Prioritatile sunt preluate din varianta 5.
        MyThread th1 = new MyThread(mainGroup, "Th1", 3);
        MyThread th2Main = new MyThread(mainGroup, "Th2", 7);


        // 5. Cream firele care apartin subgrupului GH.
        MyThread tha = new MyThread(gh, "Tha", 4);
        MyThread thb = new MyThread(gh, "Thb", 3);
        MyThread thc = new MyThread(gh, "Thc", 2);
        MyThread thd = new MyThread(gh, "Thd", 1);


        // 6. Cream firele care apartin direct grupului GE.
        MyThread thA = new MyThread(ge, "ThA", 3);
        MyThread th2GE = new MyThread(ge, "Th2", 6);
        MyThread th3 = new MyThread(ge, "Th3", 3);


        // ==================================================
        // PARTEA 2 - STEFAN
        // Organizarea, pornirea si controlul firelor
        // ==================================================

        // 7. Introducem toate cele 9 fire intr-un tablou.
        // Tabloul ne permite sa parcurgem toate firele
        // folosind o singura structura repetitiva.
        MyThread[] threads = {
                th1, th2Main,
                tha, thb, thc, thd,
                thA, th2GE, th3
        };


        // 8. Afisam titlul sectiunii din consola.
        System.out.println("FIRELE DE EXECUTIE:");
        System.out.println("--------------------------------");


        // 9. Pornim fiecare fir de executie.
        // Metoda start() porneste un fir nou si apeleaza run().
        for (MyThread t : threads) {
            t.start();
        }


        // 10. Asteptam terminarea tuturor firelor.
        // Metoda join() blocheaza firul principal pana cand
        // firul respectiv isi termina executia.
        for (MyThread t : threads) {
            t.join();
        }


        // 11. Dupa terminarea firelor, afisam prioritatile.
        System.out.println();
        System.out.println("PRIORITATILE FIRELOR:");
        System.out.println("--------------------------------");


        // 12. Parcurgem tabloul si extragem prioritatea
        // fiecarui fir folosind metoda getPriority().
        for (MyThread t : threads) {

            System.out.println(
                    t.getName() + " = " + t.getPriority()
            );
        }
    }
}
