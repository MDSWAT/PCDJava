import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Lucrare de laborator 1 - Crearea thread-urilor (Varianta 3)
 *
 * Conditia 1 (Th1): sumele numerelor impare doua cate doua, cautarea incepe de la PRIMUL element.
 * Conditia 2 (Th2): sumele numerelor impare doua cate doua, cautarea incepe de la ULTIMUL element.
 *Q
 * Firele sunt create prin interfata Runnable. AmbelAe citesc (doar citesc) acelasi tablou mas[],
 * deci nu este necesara sincronizarea. Dupa terminarea ambelor fire, thread-ul principal (main)
 * afiseaza informatia despre studenti, litera cu litera, la interval de 100 ms.
 */

public class Main {
    private static final int SIZE = 100;
    private static final int TYPING_DELAY_MS = 100;

    // TODO: completati cu datele echipei dvs.
    private static final String STUDENTS =
            "Lucrare de laborator 1 - Crearea thread-urilor (Varianta 3)\n"
          + "Student 1: Nume Prenume, grupa XXX-000\n"
          + "Student 2: Nume Prenume, grupa XXX-000\n";

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

        // pornim ambele fire si asteptam terminarea lor
        Varianta3 v = new Varianta3(mas, out1, out2);
        v.start();
        v.await();

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

/** Sarcina unui fir: gaseste numerele impare si afiseaza suma fiecarei perechi consecutive. */
class OddPairSum implements Runnable {
    private static final int DELAY_MS = 40; // pauza mica, ca sa se vada intercalarea firelor

    private final String name;
    private final int[] mas;
    private final boolean fromEnd;        // false -> Conditia 1, true -> Conditia 2
    private final Consumer<String> out;

    OddPairSum(String name, int[] mas, boolean fromEnd, Consumer<String> out) {
        this.name = name;
        this.mas = mas;
        this.fromEnd = fromEnd;
        this.out = out;
    }

    @Override
    public void run() {
        int step = fromEnd ? -1 : 1;
        int i = fromEnd ? mas.length - 1 : 0;
        int firstPos = -1;   // pozitia primului numar impar din pereche (-1 = inca negasit)
        int pairNo = 0;

        try {
            for (; i >= 0 && i < mas.length; i += step) {
                if (mas[i] % 2 == 0) continue;           // sarim peste numerele pare

                if (firstPos < 0) {
                    firstPos = i;                        // primul numar din pereche
                } else {
                    pairNo++;                            // al doilea numar -> avem perechea
                    int a = mas[firstPos], b = mas[i];
                    out.accept(String.format("%s | #%02d: mas[%d]=%d + mas[%d]=%d = %d",
                            name, pairNo, firstPos, a, i, b, a + b));
                    firstPos = -1;
                    Thread.sleep(DELAY_MS);
                }
            }
            if (firstPos >= 0) {                         // numar impar ramas fara pereche
                out.accept(String.format("%s | mas[%d]=%d ramas fara pereche",
                        name, firstPos, mas[firstPos]));
            }
            out.accept(name + " | terminat.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            out.accept(name + " | intrerupt.");
        }
    }
}

/** Clasa cu doua fire de executie: Th1 (Conditia 1) si Th2 (Conditia 2). */
class Varianta3 {
    private final Thread th1;
    private final Thread th2;

    Varianta3(int[] mas, Consumer<String> out1, Consumer<String> out2) {
        th1 = new Thread(new OddPairSum("Th1", mas, false, out1), "Th1"); // de la primul element
        th2 = new Thread(new OddPairSum("Th2", mas, true, out2), "Th2");  // de la ultimul element
    }

    void start() {
        th1.start();
        th2.start();
    }

    void await() throws InterruptedException {
        th1.join();
        th2.join();
    }
}
