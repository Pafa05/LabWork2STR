public interface Pallet {
    public int Humidity();
    public int ProducerId();
    public int HarvestDate();
    public int ShippingDate();

    public <string> string ProductType();
    public <string> string Destination();
}
