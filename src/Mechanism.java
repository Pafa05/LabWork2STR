public class Mechanism {
    private final AxisX axisX;
    private final AxisY axisY;
    private final AxisZ axisZ;

    public Mechanism(AxisX axisX, AxisY axisY, AxisZ axisZ) {
        this.axisX = axisX;
        this.axisY = axisY;
        this.axisZ = axisZ;
    }
    public void ledOn(int ledNumber) {
        Storage.ledOn(ledNumber);
    }

    public void ledsOff(){
        Storage.ledsOff();
    }

    public boolean switch1Pressed() {
        if(Storage.getSwitch1() == 1) return true;
        return false;
    }

    public boolean switch2Pressed() {
        if(Storage.getSwitch2() == 1) return true;
        return false;
    }

    public boolean bothSwitchesPressed() {
        if(Storage.getSwitch1_2()== 1) return true;
        return false;
    }

    public void putPartInCell(int z) {

        int zzz=Math.multiplyExact(z, 10);
        axisZ.gotoPos(zzz);

        axisY.gotoPos(3);

        axisZ.gotoPos(z);

        axisY.gotoPos(2);

    }

    public void takePartFromCell(int z) {
        axisZ.gotoPos(z);

        // 2. Avançar o Y para dentro (Pos 3)
        axisY.gotoPos(3);
        int zzzz= Math.multiplyExact(z, 10);
        // 3. Baixar o Z para pousar (moveZDown) -> posição normal 1, 2, 3
        axisZ.gotoPos(zzzz);

        // 4. Recuar o Y (Pos 2 - Centro)
        axisY.gotoPos(2);
    }

    public void goToPosition(int x, int z) {
        // O Mechanism tem os eixos, por isso pode criar a thread!
        GotoXZThread moveThread = new GotoXZThread(this.axisX, this.axisZ, this.axisY, x, z);

        moveThread.start();
        try {
            // --- A CORREÇÃO MÁGICA ---
            // O .join() diz à thread principal: "Pára aqui e espera que a moveThread morra/termine"
            moveThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void setY(int y) {
        axisY.gotoPos(y);
    }

}
