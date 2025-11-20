public class AxisX implements Axis {

    @Override
    public void moveForward() {
        Storage.moveXRight();
    }

    @Override
    public void moveBackward() {
        Storage.moveXLeft();
    }

    @Override
    public void stop() {
        Storage.stopX();
    }

    @Override
    public int getPos() {
        return Storage.getXPos();
    }

    @Override
    public void gotoPos(int pos) {

        if (pos != getPos()){
            if (pos == 1) {
                moveBackward();
                while (getPos() != pos) {
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
                while (getPos() != pos) {
                    try {
                        Thread.sleep(10); // Dá 10ms de folga ao CPU
                    } catch (InterruptedException e) {
                        break;
                    }
                }
                stop();
            }

            if (pos == 3) {
                moveForward();
                while (getPos() != pos) {
                    try {
                        Thread.sleep(10); // Dá 10ms de folga ao CPU
                    } catch (InterruptedException e) {
                        break;
                    }
                }
                stop();
            }


           /* while (getThePos() != pos) {
                vTaskDelay(pdMS_TO_TICKS(10));
            } // espera atè chegar a posiçao */

            stop(); // pára o cilindro
        }
        else {
            return;
        }
    }
}