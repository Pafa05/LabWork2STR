public class LedBlink extends Thread {
    private final Mechanism mechanism;
    private final int ledNumber;
    private volatile boolean running = true; // 'volatile' garante que a thread vê a mudança de valor imediatamente

    public LedBlink(Mechanism mechanism, int ledNumber) {
        this.mechanism = mechanism;
        this.ledNumber = ledNumber;
    }

    public void stopBlinking() {
        this.running = false;
        this.interrupt(); // Acorda a thread se ela estiver no sleep
    }

    @Override
    public void run() {
        System.out.println("BlinkThread: LED " + ledNumber + " a piscar...");
        while (running) {
            try {
                mechanism.ledOn(ledNumber);
                Thread.sleep(500); // 500ms ligado
                mechanism.ledsOff(); // Nota: o teu ledOff desliga TUDO. Se quiseres desligar só um, precisas ajustar o Mechanism.
                Thread.sleep(500); // 500ms desligado
            } catch (InterruptedException e) {
                // Se for interrompida, verifica se é para sair
                if (!running) break;
            }
        }
        mechanism.ledsOff(); // Garante que fica desligado no fim
    }
}