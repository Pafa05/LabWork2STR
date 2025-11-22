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

    // Método auxiliar para converter o ID do sensor em altura física
    private int getPhysicalHeight(int sensorPos) {
        switch (sensorPos) {
            case 1:  return 1; // Nível 1 Baixo
            case 10: return 2; // Nível 1 Cima
            case 2:  return 3; // Nível 2 Baixo
            case 20: return 4; // Nível 2 Cima
            case 3:  return 5; // Nível 3 Baixo
            case 30: return 6; // Nível 3 Cima
            default: return -1; // Desconhecido
        }
    }

    @Override
    public void gotoPos(int targetPos) {
        int currentPos = getPos();

        // Se já lá estamos, não faz nada
        if (currentPos == targetPos) return;

        // 1. Calcular alturas físicas
        int currentHeight = getPhysicalHeight(currentPos);
        int targetHeight = getPhysicalHeight(targetPos);

        // 2. Decidir direção com base na altura física
        // Se altura atual < altura alvo -> SUBIR (Forward)
        // Se altura atual > altura alvo -> DESCER (Backward)
        if (targetHeight > currentHeight) {
            moveForward(); // Sobe
        } else {
            moveBackward(); // Desce
        }

        // 3. Loop de espera com sleep (Correção de Busy-Waiting)
        while (getPos() != targetPos) {
            try {
                Thread.sleep(10); // Liberta CPU
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        // 4. Parar ao chegar
        stop();
    }
}