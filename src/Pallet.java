import java.time.LocalDate;

public class Pallet {
    private String productType;
    private double humidity; // Em percentagem
    private String producerID;
    private LocalDate harvestDate; // Pode ser String ou LocalDate
    private String destination;
    private LocalDate shippingDate;// Pode ser String ou LocalDate
    private Boolean alert;

    // Coordenadas de armazenamento (X, Z). -1 indica que não está armazenada.
    private int posX = -1;
    private int posZ = -1;

    // Construtor
    public Pallet(String productType, double humidity, String producerID,
                  LocalDate harvestDate, String destination, LocalDate shippingDate, Boolean alert) {
        this.productType = productType;
        this.humidity = humidity;
        this.producerID = producerID;
        this.harvestDate = harvestDate;
        this.destination = destination;
        this.shippingDate = shippingDate;
        this.alert= alert;
    }

    // --- Getters ---
    public String getProductType() { return productType; }
    public double getHumidity() { return humidity; }
    public String getProducerID() { return producerID; }
    public LocalDate getHarvestDate() { return harvestDate; }
    public String getDestination() { return destination; }
    public LocalDate getShippingDate() { return shippingDate; }
    public Boolean getAlert() { return alert; }

    public int getPosX() { return posX; }
    public int getPosZ() { return posZ; }

    // --- Setters ---
    // Úteis se precisares de atualizar a posição quando a palete for guardada
    public void setPosition(int x, int z) {
        this.posX = x;
        this.posZ = z;
    }

    // Método para facilitar a impressão (útil para o RH12 - Listar Produtos)
    @Override
    public String toString() {
        return "Pallet [" +
                "Produto='" + productType + '\'' +
                ", Humidade=" + humidity + "%" +
                ", Produtor='" + producerID + '\'' +
                ", Data Colheita='" + harvestDate + '\'' +
                ", Destino='" + destination + '\'' +
                ", Envio='" + shippingDate + '\'' +
                ", Pos=(" + (posX == -1 ? "N/A" : posX) + "," + (posZ == -1 ? "N/A" : posZ) + ")" +
                ']';
    }


    /*public static Pallet readFromScanner(Scanner sc) {
        System.out.println("--- Introdução dos dados da palete ---");

        System.out.print("Tipo de produto: ");
        String productType = readNonEmptyLine(sc);

        double humidity = readDoubleInRange(sc, "Humidade (0-100): ", 0.0, 100.0);

        System.out.print("Producer ID: ");
        String producerID = readNonEmptyLine(sc);

        LocalDate harvestDate = readDate(sc, "Data de colheita (yyyy-MM-dd): ");

        System.out.print("Destino: ");
        String destination = readNonEmptyLine(sc);

        LocalDate shippingDate = readDate(sc, "Data de envio (yyyy-MM-dd): ");

        Pallet p = new Pallet(productType, humidity, producerID, harvestDate, destination, shippingDate);
        System.out.println("Palete criada: " + p);
        return p;
    }


    private static String readNonEmptyLine(Scanner sc) {
        while (true) {
            String line = sc.nextLine().trim();
            if (!line.isEmpty()) return line;
            System.out.print("Valor vazio. Tente novamente: ");
        }
    }

    private static double readDoubleInRange(Scanner sc, String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();
            try {
                double v = Double.parseDouble(s);
                if (v < min || v > max) {
                    System.out.println("Valor fora do intervalo (" + min + " - " + max + "). Tente novamente.");
                    continue;
                }
                return v;
            } catch (NumberFormatException e) {
                System.out.println("Formato inválido. Introduza um número (ex: 12.5). Tente novamente.");
            }
        }
    }


    private static LocalDate readDate(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();
            try {
                return LocalDate.parse(s, DATE_FMT);
            } catch (DateTimeParseException e) {
                System.out.println("Data inválida. Use formato yyyy-MM-dd (ex: 2025-11-21). Tente novamente.");
            }
        }
    } */
}