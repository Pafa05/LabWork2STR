public class AxisY implements Axis{

    @Override
    public void moveForward() {
        Storage.moveYInside();
    }

    @Override
    public  void moveBackward() {
        Storage.moveYOutside();
    }

    @Override
    public void stop() {
        Storage.stopY();
    }

    @Override
    public int getPos() {
        return Storage.getYPos();
    }

    @Override
    public void gotoPos(int pos) {
        if (pos != getPos()) {
            if (pos == 3) {
                moveForward();
                while ( pos != getPos()) {
                    try {
                        Thread.sleep(10); // Dá 10ms de folga ao CPU
                    } catch (InterruptedException e) {
                        break;
                    }
                }
                stop();
            }

            if (pos == 1) {
                moveBackward();
                while (pos != getPos()) {
                    try {
                        Thread.sleep(10); // Dá 10ms de folga ao CPU
                    } catch (InterruptedException e) {
                        break;
                    }
                }
                stop();
            }

            if (pos == 2) {
                moveForward();
                while (pos != getPos()) {
                    try {
                        Thread.sleep(10); // Dá 10ms de folga ao CPU
                    } catch (InterruptedException e) {
                        break;
                    }
                }
                stop();
            }
        }

    }
}