import java.util.Random;

public class Main {

    public static void main(String[] args) {

        // Crearea tabloului comun
        int[] mas = new int[100];

        Random random = new Random();

        System.out.println("TABLOUL GENERAT:");
        System.out.println();

        for (int i = 0; i < mas.length; i++) {
            mas[i] = random.nextInt(100) + 1;
            System.out.print(mas[i] + " ");

            if ((i + 1) % 10 == 0) {
                System.out.println();
            }
        }

        System.out.println();
        System.out.println("========================================");
        System.out.println("PORNIREA CELOR 4 FIRE DE EXECUTIE");
        System.out.println("========================================");

        // Cele două clase folosesc același tablou mas[]
        Student1 student1 = new Student1(mas);
        Student2 student2 = new Student2(mas);

        // Pornim cele 4 thread-uri
        student1.start();
        student2.start();

        try {

            // Așteptăm terminarea tuturor celor 4 thread-uri
            student1.join();
            student2.join();

        } catch (InterruptedException e) {
            System.out.println("Thread-ul principal a fost întrerupt.");
            Thread.currentThread().interrupt();
        }

        System.out.println();
        System.out.println("========================================");
        System.out.println("TOATE FIRELE S-AU TERMINAT");
        System.out.println("========================================");
        System.out.println();

        // Informația despre studenți
        String informatie =
                "Studenti: Pulbere Alexandrina si Serghei Ștefan\n" +
                        "Varianta: 5\n" +
                        "Lucrarea de laborator: Crearea thread-urilor";

        // Afișarea caracter cu caracter la fiecare 100 ms
        for (int i = 0; i < informatie.length(); i++) {

            System.out.print(informatie.charAt(i));

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                System.out.println("\nThread-ul principal a fost întrerupt.");
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}