public class GotoXThread extends Thread {
    private final AxisX axisX;
    private final int targetPos;

    // O construtor recebe a instância do AxisX e a posição alvo
    public GotoXThread(AxisX axisX, int targetPos) {
        this.axisX = axisX;
        this.targetPos = targetPos;
    }

    @Override
    public void run() {
       axisX.gotoPos(targetPos);
        // Chama o método gotoPos() do AxisX que contém a lógica de movimento e paragem.
        axisX.gotoPos(targetPos);
        // Em um sistema real, aqui você poderia sinalizar a conclusão do movimento.
    }
}