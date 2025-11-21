public class Mechanism {
     AxisX axisx = new AxisX();
     AxisY axisy = new AxisY();
     AxisZ axisz = new AxisZ();
    public void ledOn(int ledNumber) {
        Storage.ledOn(ledNumber);
    }

    public void ledOff(){
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

       GotoZThread movezup = new GotoZThread(axisz, z*10);
        movezup.start();
        axisy.gotoPos(3);
        GotoZThread movezdown = new GotoZThread(axisz, z);
       movezdown.start();
       axisy.gotoPos(2);

    }

    public void takePartFromCell(int z) {
        GotoZThread movezdown = new GotoZThread(axisz, z);
        movezdown.start();
       axisy.gotoPos(3);
        GotoZThread movezup = new GotoZThread(axisz, z*10);
        movezup.start();
       axisy.gotoPos(2);
    }

}
