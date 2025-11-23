public class ManualStore implements Runnable{
    private  Pallet pallet;
    private  int targetX;
    private  int targetZ;
    private  AxisX axisX;
    private  AxisZ axisZ;
    private  AxisY axisY;
    private Mechanism mechanism;

    public ManualStore(Mechanism mechanism, Pallet pallet, int targetX, int targetZ) {
        this.mechanism = mechanism;
        this.pallet = pallet;
        this.targetX = targetX;
        this.targetZ = targetZ;
//        this.finishedSemaphore = finishedSemaphore;
    }


   public void run() {
//       GotoXZThread moveXZThread = new GotoXZThread(axisX, axisZ,  axisY, targetX, targetZ);
//       moveXZThread.start();
       mechanism.goToPosition(targetX, targetZ);


       mechanism.putPartInCell(targetZ);
   }

}
