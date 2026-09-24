import java.util.function.Consumer;

public class Th2 extends Thread {

    private final int[] mas;
    private final Consumer<String> afisare;

    public Th2(int[] mas, Consumer<String> afisare) {
        this.mas = mas;
        this.afisare = afisare;
    }

    @Override
    public void run() {
        int suma = 0;

        try {
            afisare.accept("[Th2] A început calculul de la sfârșit.");

            int ultimulIndicePar = mas.length - 1;

            if (ultimulIndicePar % 2 != 0) {
                ultimulIndicePar--;
            }

            for (int i = ultimulIndicePar; i >= 2; i -= 8) {
                int rezultat;

                if (i - 6 >= 0) {
                    // Calculăm toate cele 4 elemente într-o expresie.
                    rezultat = mas[i] * mas[i - 2]
                             + mas[i - 4] * mas[i - 6];

                    suma += rezultat;

                    afisare.accept(
                            "[Th2] mas[" + i + "] * mas[" + (i - 2)
                            + "] + mas[" + (i - 4) + "] * mas["
                            + (i - 6) + "] = "
                            + mas[i] + " * " + mas[i - 2]
                            + " + " + mas[i - 4] + " * " + mas[i - 6]
                            + " = " + rezultat
                            + " | Suma acumulată = " + suma
                    );

                } else {
                    // Au rămas doar două elemente.
                    rezultat = mas[i] * mas[i - 2];
                    suma += rezultat;

                    afisare.accept(
                            "[Th2] Ultima pereche: mas[" + i
                            + "] * mas[" + (i - 2) + "] = "
                            + mas[i] + " * " + mas[i - 2]
                            + " = " + rezultat
                            + " | Suma acumulată = " + suma
                    );
                }

                // Pauză doar între pașii compleți.
                Thread.sleep(500);
            }

            afisare.accept("[Th2] SUMA FINALĂ = " + suma);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            afisare.accept("[Th2] Calculul a fost întrerupt.");
        }
    }
}