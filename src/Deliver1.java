public class Deliver1 {
    private Pallet pallet;
    private Mechanism mechanism;

    public Deliver1(Pallet pallet, Mechanism mechanism) {
        this.pallet = pallet;
        this.mechanism = mechanism;
    }

    public void run() {
        int z = pallet.getPosZ();
        int x = pallet.getPosX();
        mechanism.goToPosition(x, z);
        mechanism.takePartFromCell(z);
        mechanism.goToPosition(3, 1);
        mechanism.setY(1);
        try { Thread.sleep(2000); } catch (Exception e) {}
        mechanism.setY(2);
    }

}