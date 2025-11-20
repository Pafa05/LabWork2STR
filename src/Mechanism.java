public class Mechanism {
     
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
        return false;
    }

    public boolean bothSwitchesPressed() {
        return false;
    }

    public void putPartInCell() {

    }

    public void takePartFromCell() {

    }

}
