public class AxisZ implements Axis {
    @Override
    public void moveForward() {
        Storage.moveZUp();
    }

    @Override
    public void moveBackward() {
        Storage.moveZDown();
    }

    @Override
    public void stop() {
        Storage.stopZ();
    }

    @Override
    public int getPos() {
        return Storage.getZPos();
    }

    @Override
    public void gotoPos(int pos) {
        if (pos != getPos()) {
            if (pos == 1) {
                moveBackward();
                while (getPos() != pos) {
                }
                stop();
            }

            if (pos == 2) {
                moveForward();
                while (getPos() != pos) {
                }
                stop();
            }

            if (pos == 3) {
                moveForward();
                while (getPos() != pos) {
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
