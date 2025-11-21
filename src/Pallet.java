import java.time.LocalDate;

public class Pallet {
    private String productType;
    private double humidity; // Em percentagem
    private String producerID;
    private LocalDate harvestDate; // Pode ser String ou LocalDate
    private String destination;
    private LocalDate shippingDate; // Pode ser String ou LocalDate

    // Coordenadas de armazenamento (X, Z). -1 indica que não está armazenada.
    private int posX = -1;
    private int posZ = -1;

    // Construtor
    public Pallet(String productType, double humidity, String producerID,
                  LocalDate harvestDate, String destination, LocalDate shippingDate) {
        this.productType = productType;
        this.humidity = humidity;
        this.producerID = producerID;
        this.harvestDate = harvestDate;
        this.destination = destination;
        this.shippingDate = shippingDate;
    }

    // --- Getters ---
    public String getProductType() { return productType; }
    public double getHumidity() { return humidity; }
    public String getProducerID() { return producerID; }
    public LocalDate getHarvestDate() { return harvestDate; }
    public String getDestination() { return destination; }
    public LocalDate getShippingDate() { return shippingDate; }

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
}