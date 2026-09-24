import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.Random;
import java.util.function.Consumer;

public class Main extends JFrame {

    // O singură zonă pentru tabloul generat și ambele fire.
    private final JTextArea jurnalArea = new JTextArea();

    private final JButton startButton =
            new JButton("Pornește firele");

    private final JLabel stareLabel =
            new JLabel("Apasă butonul pentru a începe.");

    private int numarMesaj = 0;
    private String primulPornit = null;
    private String primulTerminat = null;

    public Main() {
        setTitle("Urmărirea firelor de execuție");
        setSize(950, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        jurnalArea.setEditable(false);
        jurnalArea.setFont(
                new Font("Monospaced", Font.PLAIN, 14)
        );
        jurnalArea.setLineWrap(true);
        jurnalArea.setWrapStyleWord(true);

        JPanel jos = new JPanel(new BorderLayout(10, 10));
        jos.setBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        );

        jos.add(stareLabel, BorderLayout.CENTER);
        jos.add(startButton, BorderLayout.EAST);

        add(new JScrollPane(jurnalArea), BorderLayout.CENTER);
        add(jos, BorderLayout.SOUTH);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pornesteCalculul();
            }
        });
    }

    private void pornesteCalculul() {
        startButton.setEnabled(false);
        jurnalArea.setText("");

        numarMesaj = 0;
        primulPornit = null;
        primulTerminat = null;

        stareLabel.setText("Firele execută calculele...");

        int[] mas = new int[100];
        Random random = new Random();

        for (int i = 0; i < mas.length; i++) {
            mas[i] = random.nextInt(100) + 1;
        }

        jurnalArea.append("TABLOUL GENERAT:\n");
        jurnalArea.append(Arrays.toString(mas) + "\n\n");
        jurnalArea.append("JURNALUL COMUN AL FIRELOR:\n\n");

        final Th1 th1 = new Th1(mas, new Consumer<String>() {
            @Override
            public void accept(String text) {
                inregistreazaMesaj("Th1", text);
            }
        });

        final Th2 th2 = new Th2(mas, new Consumer<String>() {
            @Override
            public void accept(String text) {
                inregistreazaMesaj("Th2", text);
            }
        });

        th1.start();
        th2.start();

        // Așteptăm într-un fir separat pentru a nu bloca fereastra.
        Thread asteptare = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    th1.join();
                    th2.join();

                    // Ambele fire s-au terminat.
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            jurnalArea.append(
                                    "\nAMBELE FIRE S-AU TERMINAT.\n"
                                    + "Primul mesaj de început: "
                                    + primulPornit + "\n"
                                    + "Primul rezultat final: "
                                    + primulTerminat + "\n"
                            );

                            stareLabel.setText(
                                    "Calcul terminat. Primul rezultat final: "
                                    + primulTerminat
                            );

                            afiseazaNumele();
                        }
                    });

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();

                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            stareLabel.setText(
                                    "Așteptarea a fost întreruptă."
                            );
                            startButton.setEnabled(true);
                        }
                    });
                }
            }
        });

        asteptare.start();
    }

    /*
     * synchronized permite înregistrarea unui singur mesaj odată.
     * Numerotarea și trimiterea mesajelor către interfață păstrează
     * astfel aceeași ordine.
     */
    private synchronized void inregistreazaMesaj(
            final String fir, String text) {

        numarMesaj++;

        // Eliminăm prefixul existent, deoarece îl adăugăm în jurnal.
        final String mesaj = text
                .replace("[" + fir + "]", "")
                .trim();

        final String linie = String.format(
                "%03d | %s | %s%n",
                numarMesaj, fir, mesaj
        );

        final boolean inceput =
                mesaj.startsWith("A început calculul");

        final boolean terminat =
                mesaj.startsWith("SUMA FINALĂ");

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                jurnalArea.append(linie);

                if (inceput && primulPornit == null) {
                    primulPornit = fir;

                    jurnalArea.append(
                            "      >>> " + fir
                            + " a raportat primul începutul.\n"
                    );
                }

                if (terminat) {
                    if (primulTerminat == null) {
                        primulTerminat = fir;

                        jurnalArea.append(
                                "      >>> " + fir
                                + " a terminat PRIMUL calculul.\n"
                        );
                    } else {
                        jurnalArea.append(
                                "      >>> " + fir
                                + " a terminat AL DOILEA calculul.\n"
                        );
                    }
                }

                stareLabel.setText(
                        "Ultimul mesaj primit: " + fir
                );

                jurnalArea.setCaretPosition(
                        jurnalArea.getDocument().getLength()
                );
            }
        });
    }

    private void afiseazaNumele() {
        final String info =
                "Litiuc Daniel, Musteata Ghenadie, Buzdugan Liviu"
                + " — grupa Cr-231fr";

        jurnalArea.append("\n");

        Timer timer = new Timer(100, new ActionListener() {
            private int pozitie = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                jurnalArea.append(
                        String.valueOf(info.charAt(pozitie))
                );

                pozitie++;

                jurnalArea.setCaretPosition(
                        jurnalArea.getDocument().getLength()
                );

                if (pozitie == info.length()) {
                    ((Timer) e.getSource()).stop();
                    jurnalArea.append("\n");
                    startButton.setEnabled(true);
                }
            }
        });

        timer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Main().setVisible(true);
            }
        });
    }
}