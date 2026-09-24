import java.util.function.Consumer;

/**
 * Th1 - Conditia 1 (varianta 3):
 * sumele numerelor impare doua cate doua, cautarea si sumarea incep de la PRIMUL element.
 */
public class Th1 implements Runnable {
    private static final int DELAY_MS = 40; // pauza mica, ca sa se vada intercalarea firelor

    private final int[] mas;
    private final Consumer<String> out;

    public Th1(int[] mas, Consumer<String> out) {
        this.mas = mas;
        this.out = out;
    }

    @Override
    public void run() {
        int firstPos = -1;   // pozitia primului numar impar din pereche (-1 = inca negasit)
        int pairNo = 0;

        try {
            for (int i = 0; i < mas.length; i++) {           // de la primul spre ultimul
                if (mas[i] % 2 == 0) continue;               // sarim peste numerele pare

                if (firstPos < 0) {
                    firstPos = i;                            // primul numar din pereche
                } else {
                    pairNo++;                                // al doilea numar -> avem perechea
                    int a = mas[firstPos], b = mas[i];
                    out.accept(String.format("Th1 | #%02d: mas[%d]=%d + mas[%d]=%d = %d",
                            pairNo, firstPos, a, i, b, a + b));
                    firstPos = -1;
                    Thread.sleep(DELAY_MS);
                }
            }
            if (firstPos >= 0) {                             // numar impar ramas fara pereche
                out.accept(String.format("Th1 | mas[%d]=%d ramas fara pereche",
                        firstPos, mas[firstPos]));
            }
            out.accept("Th1 | terminat.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            out.accept("Th1 | intrerupt.");
        }
    }
}
