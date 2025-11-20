public class GotoXZThread extends Thread {
    private final AxisX axisX;
    private final AxisZ axisZ;
    private final AxisY axisY;

    // Posições alvo
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
            // FASE 1: Mover Y para a posição central (Y=2) para permitir movimento X/Z (NF4)
            // Esta é uma operação crítica para a segurança e deve ser concluída antes do movimento X/Z.
            if (axisY.getPos() != 2) {
                System.out.println("Thread_gotoXZ: Movendo Y para a posição segura (2)");
                axisY.gotoPos(2); // Y=2 é a posição 'in_cage' ou 'center position'
            }

            // FASE 2: Movimentar os eixos X e Z
            // Podemos movê-los sequencialmente ou em paralelo (se o hardware permitir e se a lógica de segurança não for violada)
            // Mover X
            if (axisX.getPos() != targetX) {
                System.out.println("Thread_gotoXZ: Movendo X para " + targetX);
                axisX.gotoPos(targetX);
            }

            // Mover Z
            if (axisZ.getPos() != targetZ) {
                System.out.println("Thread_gotoXZ: Movendo Z para " + targetZ);
                axisZ.gotoPos(targetZ);
            }
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