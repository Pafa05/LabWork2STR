public class SafetyManager {
    private static volatile boolean emergencyActive = false;
    private static volatile boolean resetRequested = false;

    // Objeto de bloqueio partilhado para sincronização
    public static final Object lock = new Object();

    public static boolean isEmergency() {
        return emergencyActive;
    }

    public static boolean isReset() {
        return resetRequested;
    }

    // Ativar/Desativar Emergência
    public static void setEmergency(boolean active) {
        emergencyActive = active;
        if (active) {
            System.out.println("!!! SAFETY MANAGER: EMERGÊNCIA ATIVADA !!!");
        } else {
            System.out.println("!!! SAFETY MANAGER: RETOMAR OPERAÇÕES !!!");
            // Acordar todas as threads que estão paradas
            synchronized (lock) {
                lock.notifyAll();
            }
        }
    }

    // Ativar Reset
    public static void triggerReset() {
        System.out.println("!!! SAFETY MANAGER: RESET SOLICITADO !!!");
        resetRequested = true;
        // Desativar emergência para libertar as threads e deixá-las "morrer" com o erro de reset
        setEmergency(false);
    }

    public static void clearReset() {
        resetRequested = false;
    }
}