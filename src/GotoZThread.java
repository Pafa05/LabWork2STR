public class GotoZThread extends Thread {
    private final AxisZ axisZ;
    private final int targetPos;

    // O construtor recebe a instância do AxisX e a posição alvo
    public GotoZThread(AxisZ axisZ, int targetPos) {
        this.axisZ = axisZ;
        this.targetPos = targetPos;
    }

    @Override
    public void run() {
            axisZ.gotoPos(targetPos);
            // Chama o método gotoPos() do AxisX que contém a lógica de movimento e paragem.
            axisZ.gotoPos(targetPos);
            // Em um sistema real, aqui você poderia sinalizar a conclusão do movimento.
            // Exemplo: sem_X_finished.release(); (usando um Semaphore) [cite: 4020]

    }
}