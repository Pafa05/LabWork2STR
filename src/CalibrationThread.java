public class CalibrationThread extends Thread {
    private final Axis axis;
    private Axis axis1x;
    private Axis axis2z;

    public CalibrationThread(Axis axis) {
        this.axis = axis;
    }

//    public CalibrationThread(Axis axisX, Axis axisZ) {
//        this.axis = null;
//        this.axis1x = axisX;
//        this.axis2z = axisZ;
//    }
    public void calibrateSelectedAxis(Axis axis) {
        if (axis.getPos() == -1) {
            axis.moveForward();
            while (axis.getPos() == -1 || axis.getPos() > 3) {
                try {
                    Thread.sleep(10); // Dá 10ms de folga ao CPU
                } catch (InterruptedException e) {
                    break;
                }
            }
            axis.stop();
        }
           else if (axis.getPos() > 1) {
                axis.gotoPos(1);
            }
        }
    public void initializeCalibration() {
        // Safely calibrate whichever axis(es) are present.
        if (this.axis != null) {
            calibrateSelectedAxis(this.axis);
        }

        // If this thread was created for X and Z together, also calibrate them (sequentially).
        // multiCalibrateInitialize() will handle the parallel case when run() calls it.
        if (this.axis1x != null) {
            calibrateSelectedAxis(this.axis1x);
        }
        if (this.axis2z != null) {
            calibrateSelectedAxis(this.axis2z);
        }
    }

    @Override
    public void run() {
        if (this.axis != null) {
            this.initializeCalibration();}
        else {}
    }
}