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

        for (int i = 98; i >= 2; i -= 4) {
            int produs = mas[i] * mas[i - 2];
            int sumaVeche = suma;
            suma += produs;

            afisare.accept(
                    "mas[" + i + "] * mas[" + (i - 2) + "]\n"
                    + mas[i] + " × " + mas[i - 2]
                    + " = " + produs + "\n"
                    + "Suma: " + sumaVeche + " + " + produs
                    + " = " + suma + "\n\n"
            );
        }

        afisare.accept("SUMA FINALĂ Th2 = " + suma + "\n");
    }
}