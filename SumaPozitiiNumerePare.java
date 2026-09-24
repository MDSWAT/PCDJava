import java.util.Random;

/**
 * Fir de executie care cauta in tabloul de intregi perechi de numere PARE,
 * doua cate doua, si calculeaza SUMA POZITIILOR (indicilor) din fiecare pereche.
 *
 * Th1 -> cauta si insumeaza incepand de la PRIMUL element (stanga -> dreapta)
 * Th2 -> cauta si insumeaza incepand de la ULTIMUL element (dreapta -> stanga)
 */
class PairThread extends Thread {

    private final int from;   // indicele de start
    private final int to;     // indicele de oprire (exclusiv)
    private final int step;   // pasul de parcurgere: +1 sau -1
    private final int[] tablou;

    public PairThread(String nume, int from, int to, int step, int[] tablou) {
        super(nume);
        this.from = from;
        this.to = to;
        this.step = step;
        this.tablou = tablou;
    }

    @Override
    public void run() {
        int i = from;

        while (i != to) {
            if (tablou[i] % 2 == 0) {          // am gasit primul numar par din pereche
                int poz1 = i;
                i += step;

                // cautam al doilea numar par, continuand din locul unde am ramas
                while (i != to) {
                    if (tablou[i] % 2 == 0) {
                        int poz2 = i;
                        int sumaPozitii = poz1 + poz2;

                        System.out.println(getName()
                                + " | pozitii(" + poz1 + "," + poz2 + ")"
                                + " -> suma pozitiilor = " + sumaPozitii
                                + "   (valori: " + tablou[poz1] + ", " + tablou[poz2] + ")");

                        i += step;
                        break;
                    }
                    i += step;
                }
            } else {
                i += step;
            }
        }
    }
}

public class SumaPozitiiNumerePare {

    public static void main(String[] args) throws InterruptedException {

        int[] mas = new int[100];
        Random rnd = new Random();

        for (int i = 0; i < mas.length; i++) {
            mas[i] = rnd.nextInt(100) + 1;   // valori intre 1 si 100
            System.out.print(mas[i] + " ");
        }
        System.out.println();
        System.out.println();

        // Th1: Conditia 1 - cautare si sumare de la primul element
        PairThread th1 = new PairThread("Th1", 0, mas.length, 1, mas);
        // Th2: Conditia 2 - cautare si sumare de la ultimul element
        PairThread th2 = new PairThread("Th2", mas.length - 1, -1, -1, mas);

        th1.start();
        th2.start();

        th1.join();
        th2.join();

        // dupa terminarea ambelor fire de executie, thread-ul principal
        // afiseaza informatia despre studenti, litera cu litera, la 100 ms
        String info = "Numele Prenumele, grupa XXX - Lucrare laborator 1, varianta 2";
        for (char c : info.toCharArray()) {
            System.out.print(c);
            Thread.sleep(100);
        }
        System.out.println();
    }
}
