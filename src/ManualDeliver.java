public class ManualDeliver {
    private  Pallet pallet;
    private  int targetX;
    private  int targetZ;
    private  AxisX axisX;
    private  AxisZ axisZ;
    private  AxisY axisY;
    private Mechanism mechanism;

    public ManualDeliver(Pallet pallet, int targetX, int targetZ) {
        this.pallet = pallet;
        this.targetX = targetX;
        this.targetZ = targetZ;
//        this.finishedSemaphore = finishedSemaphore;
    }

   @Override
   public void run() {
       GotoXZThread moveXZThread = new GotoXZThread(axisX, axisZ,  axisY, targetX, targetZ);
       moveXZThread.start();
       mechanism.putPartInCell(targetZ);
   }

}
