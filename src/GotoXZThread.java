public class GotoXZThread extends Thread {
    private final AxisX axisX;
    private final AxisZ axisZ;
    private final AxisY axisY;
    private final int targetX;
    private final int targetZ;

    // Semáforo para sinalizar que o movimento terminou (opcional, para outras threads)
//    private final Semaphore finishedSemaphore;

    public GotoXZThread(AxisX axisX, AxisZ axisZ, AxisY axisY,
                        int targetX, int targetZ) {
        this.axisX = axisX;
        this.axisZ = axisZ;
        this.axisY = axisY;
        this.targetX = targetX;
        this.targetZ = targetZ;
//        this.finishedSemaphore = finishedSemaphore;
    }

    @Override
    public void run() {
        System.out.println("Thread_gotoXZ: Iniciando movimento para (" + targetX + ", " + targetZ + ")");

        try {
            if (axisY.getPos() != 2) {
                axisY.gotoPos(2); // Y=2 é a posição 'in_cage' ou 'center position'
            }

            Thread moveX = new Thread(() -> {
                if (axisX.getPos() != targetX) {
                    System.out.println("   -> X: A mover para " + targetX);
                    axisX.gotoPos(targetX);
                    System.out.println("   -> X: Chegou ao destino.");
                }
            });

            // Thread para mover Z
            Thread moveZ = new Thread(() -> {
                if (axisZ.getPos() != targetZ) {
                    System.out.println("   -> Z: A mover para " + targetZ);
                    axisZ.gotoPos(targetZ);
                    System.out.println("   -> Z: Chegou ao destino.");
                }
            });

            moveX.start();
            moveZ.start();

            moveX.join();
            moveZ.join();

//

        } catch (Exception e) {
            System.err.println("LEI É GAY" + e.getMessage());
        }
//        finally {
//            // FASE 3: Sinalizar conclusão (se o semáforo foi fornecido)
//            if (finishedSemaphore != null) {
//                finishedSemaphore.release();
//            }
//        }
    }
}