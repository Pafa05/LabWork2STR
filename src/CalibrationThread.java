public class CalibrationThread extends Thread {
    private final Axis axis;
    private Axis axis1x;
    private Axis axis2z;

    public CalibrationThread(Axis axis) {
        this.axis = axis;
    }

    public CalibrationThread(Axis axisX, Axis axisZ) {
        this.axis = null;
        this.axis1x = axisX;
        this.axis2z = axisZ;
    }

    /**
     * Calibra um eixo individual para posição 1
     */
    public void calibrateSelectedAxis(Axis axis) {
        System.out.println("[CalibrationThread] Calibrando eixo...");

        // Se position é -1 (não calibrada), move forward até encontrar uma posição válida
        if (axis.getPos() == -1) {
            System.out.println("[CalibrationThread] Posição -1, movendo forward...");
            axis.moveForward();
            while (axis.getPos() == -1 || axis.getPos() > 3) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    break;
                }
            }
            axis.stop();
            System.out.println("[CalibrationThread] Eixo encontrou posição: " + axis.getPos());
        }

        // Se position é > 1, volta para posição 1
        if (axis.getPos() > 1) {
            System.out.println("[CalibrationThread] Posição atual: " + axis.getPos() + ", indo para 1...");
            axis.gotoPos(1);
            System.out.println("[CalibrationThread] Eixo agora em posição 1");
        }
    }

    /**
     * Calibra X e Z em paralelo (dual-axis)
     */
    public void multiCalibrateInitialize() {
        System.out.println("[CalibrationThread] Iniciando calibração dual-axis (X e Z em paralelo)");

        // Garantir que Y está em posição 2 para que gotoPos funcione
        AxisY axisY = new AxisY();
        System.out.println("[CalibrationThread] Colocando Y em posição 2 (necessário para X e Z)");
        axisY.gotoPos(2);

        Thread calibX = new Thread(() -> {
            System.out.println("[CalibrationThread] Calibrando X...");
            calibrateSelectedAxis(axis1x);
        });

        Thread calibZ = new Thread(() -> {
            System.out.println("[CalibrationThread] Calibrando Z...");
            calibrateSelectedAxis(axis2z);
        });

        calibX.start();
        calibZ.start();

        try {
            calibX.join();
            calibZ.join();
            System.out.println("[CalibrationThread] X e Z calibrados com sucesso em posições 1,1!");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Calibra um único eixo (single-axis)
     */
    public void initializeCalibration() {
        System.out.println("[CalibrationThread] Calibração single-axis");
        if (this.axis != null) {
            calibrateSelectedAxis(this.axis);
        }
    }

    @Override
    public void run() {
        // Single-axis calibration
        if (this.axis != null) {
            initializeCalibration();
        }
        // Dual-axis calibration (X + Z em paralelo)
        else if (this.axis1x != null && this.axis2z != null) {
            multiCalibrateInitialize();
        }
    }
}