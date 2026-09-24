import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Lucrare de laborator 1 - Crearea thread-urilor (Varianta 3)
 *
 * Th1 (Conditia 1, fisierul Th1.java): sumele numerelor impare doua cate doua, de la PRIMUL element.
 * Th2 (Conditia 2, definit mai jos):   sumele numerelor impare doua cate doua, de la ULTIMUL element.
 *
 * Firele sunt create prin interfata Runnable si citesc (doar citesc) acelasi tablou mas[],
 * deci nu este necesara sincronizarea. Dupa terminarea ambelor fire, thread-ul principal (main)
 * afiseaza informatia despre studenti, litera cu litera, la interval de 100 ms.
 *
 * Compilare: javac Main.java Th1.java   |   Rulare: java Main
 */
public class Main {
    private static final int SIZE = 100;
    private static final int TYPING_DELAY_MS = 100;

    // TODO: completati cu datele echipei dvs.
    private static final String STUDENTS =
            "Lucrare de laborator 1 - Crearea thread-urilor (Varianta 3)\n"
          + "Student 1: Ciubotaru Ali, grupa CR-231\n"
          + "Student 2: Vatamaniuc Cristian, grupa CR-231\n";

    public static void main(String[] args) throws Exception {
        // tablou generat aleatoriu: 100 de elemente cu valori intre 1 si 100
        int[] mas = new int[SIZE];
        for (int i = 0; i < SIZE; i++) {
            mas[i] = (int) (Math.random() * 100) + 1;
        }

        boolean gui = !GraphicsEnvironment.isHeadless();
        JTextArea arrArea = null, area1 = null, area2 = null, infoArea = null;

        if (gui) {
            JTextArea[] areas = new JTextArea[4];
            SwingUtilities.invokeAndWait(() -> buildWindow(areas));
            arrArea = areas[0]; area1 = areas[1]; area2 = areas[2]; infoArea = areas[3];
        }

        final JTextArea fArr = arrArea, f1 = area1, f2 = area2, fInfo = infoArea;
        Consumer<String> out1 = gui ? line(f1) : Main::consoleLine;
        Consumer<String> out2 = gui ? line(f2) : Main::consoleLine;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < SIZE; i++) sb.append(mas[i]).append(i < SIZE - 1 ? " " : "");
        if (gui) SwingUtilities.invokeLater(() -> fArr.setText(sb.toString()));
        else System.out.println("mas = " + sb + "\n");

        // cele doua fire de executie (Runnable)
        Thread th1 = new Thread(new Th1(mas, out1), "Th1");   // Conditia 1
        Thread th2 = new Thread(new Th2(mas, out2), "Th2");   // Conditia 2
        th1.start();
        th2.start();
        th1.join();
        th2.join();

        // thread-ul principal afiseaza studentii, litera cu litera, la 100 ms
        for (char c : STUDENTS.toCharArray()) {
            if (gui) SwingUtilities.invokeLater(() -> fInfo.append(String.valueOf(c)));
            else System.out.print(c);
            Thread.sleep(TYPING_DELAY_MS);
        }
    }

    private static synchronized void consoleLine(String s) {
        System.out.println(s);
    }

    private static Consumer<String> line(JTextArea area) {
        return s -> SwingUtilities.invokeLater(() -> area.append(s + "\n"));
    }

    /** Construieste fereastra (se apeleaza pe EDT). areas: [tablou, Th1, Th2, info] */
    private static void buildWindow(JTextArea[] areas) {
        JFrame f = new JFrame("Lab 1 - Thread-uri - Varianta 3");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLayout(new BorderLayout(6, 6));

        for (int i = 0; i < 4; i++) {
            areas[i] = new JTextArea();
            areas[i].setEditable(false);
            areas[i].setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        }
        areas[0].setLineWrap(true);
        areas[0].setRows(4);

        JScrollPane top = new JScrollPane(areas[0]);
        top.setBorder(BorderFactory.createTitledBorder("Tabloul mas[] (100 elemente, valori 1..100)"));

        JScrollPane s1 = new JScrollPane(areas[1]);
        s1.setBorder(BorderFactory.createTitledBorder("Th1 - Conditia 1 (impare doua cate doua, de la primul element)"));
        JScrollPane s2 = new JScrollPane(areas[2]);
        s2.setBorder(BorderFactory.createTitledBorder("Th2 - Conditia 2 (impare doua cate doua, de la ultimul element)"));
        JPanel center = new JPanel(new GridLayout(1, 2, 6, 6));
        center.add(s1);
        center.add(s2);

        areas[3].setRows(5);
        JScrollPane bottom = new JScrollPane(areas[3]);
        bottom.setBorder(BorderFactory.createTitledBorder("Studenti (afisat de thread-ul principal)"));

        f.add(top, BorderLayout.NORTH);
        f.add(center, BorderLayout.CENTER);
        f.add(bottom, BorderLayout.SOUTH);
        f.setSize(1000, 650);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
}

/**
 * Th2 - Conditia 2 (varianta 3):
 * sumele numerelor impare doua cate doua, cautarea si sumarea incep de la ULTIMUL element.
 */
class Th2 implements Runnable {
    private static final int DELAY_MS = 40; // pauza mica, ca sa se vada intercalarea firelor

    private final int[] mas;
    private final Consumer<String> out;

    Th2(int[] mas, Consumer<String> out) {
        this.mas = mas;
        this.out = out;
    }

    @Override
    public void run() {
        int firstPos = -1;   // pozitia primului numar impar din pereche (-1 = inca negasit)
        int pairNo = 0;

        try {
            for (int i = mas.length - 1; i >= 0; i--) {      // de la ultimul spre primul
                if (mas[i] % 2 == 0) continue;               // sarim peste numerele pare

                if (firstPos < 0) {
                    firstPos = i;                            // primul numar din pereche
                } else {
                    pairNo++;                                // al doilea numar -> avem perechea
                    int a = mas[firstPos], b = mas[i];
                    out.accept(String.format("Th2 | #%02d: mas[%d]=%d + mas[%d]=%d = %d",
                            pairNo, firstPos, a, i, b, a + b));
                    firstPos = -1;
                    Thread.sleep(DELAY_MS);
                }
            }
            if (firstPos >= 0) {                             // numar impar ramas fara pereche
                out.accept(String.format("Th2 | mas[%d]=%d ramas fara pereche",
                        firstPos, mas[firstPos]));
            }
            out.accept("Th2 | terminat.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            out.accept("Th2 | intrerupt.");
        }
    }
}
