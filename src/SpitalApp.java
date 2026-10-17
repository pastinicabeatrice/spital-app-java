import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class SpitalApp {

    public static final class Pacient implements Serializable {
        private final int cod;
        private final String nume;
        private final String sectie;

        public Pacient(int cod, String nume, String sectie) {
            this.cod = cod;
            this.nume = nume;
            this.sectie = sectie;
        }

        public int getCod() {
            return cod;
        }

        public String getNume() {
            return nume;
        }

        public String getSectie() {
            return sectie;
        }

        @Override
        public String toString() {
            return cod + " - " + nume + " - " + sectie;
        }
    }

    public static void main(String[] args) {
        List<Pacient> pacienti = new ArrayList<>();
        String inputFile = "pacienti.txt";
        String outputFile = "pacienti.dat";

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            String linie;
            while ((linie = br.readLine()) != null) {
                String[] valori = linie.split(",");
                if (valori.length == 3) {
                    int cod = Integer.parseInt(valori[0].trim());
                    String nume = valori[1].trim();
                    String sectie = valori[2].trim();
                    pacienti.add(new Pacient(cod, nume, sectie));
                }
            }
        } catch (IOException e) {
            System.err.println("Eroare la citirea fișierului: " + e.getMessage());
            return;
        }

        System.out.println("Număr total de pacienți: " + pacienti.size());

        Map<String, Long> nrPacientiPerSectie = pacienti.stream()
                .collect(Collectors.groupingBy(Pacient::getSectie, Collectors.counting()));

        List<Map.Entry<String, Long>> sectiiSortate = nrPacientiPerSectie.entrySet()
                .stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                .collect(Collectors.toList());

        System.out.println("\nSecții sortate descrescător după numărul de pacienți:");
        for (Map.Entry<String, Long> entry : sectiiSortate) {
            System.out.println(entry.getKey() + " - " + entry.getValue() + " pacienți");
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(outputFile))) {
            oos.writeObject(pacienti);
        } catch (IOException e) {
            System.err.println("Eroare la scrierea fișierului binar: " + e.getMessage());
        }

        System.out.println("\nConținutul fișierului binar:");
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(outputFile))) {
            List<Pacient> pacientiCititi = (List<Pacient>) ois.readObject();
            pacientiCititi.forEach(System.out::println);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Eroare la citirea fișierului binar: " + e.getMessage());
        }
    }
}
