public class AxisZ implements Axis {
    @Override
    public void moveForward() {
        if(Storage.getYPos()==2) {
            Storage.moveZUp();
        }
    }

    @Override
    public void moveBackward() {
        if (Storage.getYPos()==2) {
            Storage.moveZDown();
        }
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
        if (Storage.getYPos()==2) {
            if (pos != getPos()) {
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
}
