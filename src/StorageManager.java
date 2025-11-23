import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;

public class StorageManager extends Thread {
    // Fila de pedidos (aceita qualquer objeto que seja Runnable, como Deliver2, ManualStore, etc.)
    private final BlockingQueue<Runnable> requestQueue = new LinkedBlockingQueue<>();

    private final Mechanism mechanism;
    private final PalletGrid grid;
    private volatile boolean running = true;

    // Referência para a data atual do sistema (necessária para verificar validade das paletes)
    private LocalDate currentDate;

    public StorageManager(Mechanism mechanism, PalletGrid grid, LocalDate startDate) {
        this.mechanism = mechanism;
        this.grid = grid;
        this.currentDate = startDate;
    }

    // Método para o App.java adicionar pedidos à fila (NF1)
    public void addRequest(Runnable task) {
        System.out.println("[Manager] Pedido adicionado à fila. Posição: " + (requestQueue.size() + 1));
        requestQueue.offer(task);
    }

    // Método para atualizar a data (chamado pelo comando 16 do App)
    public void setCurrentDate(LocalDate date) {
        this.currentDate = date;
    }

    private void EmergencyMode() {
        
    }

    @Override
    public void run() {
        System.out.println("StorageManager: A iniciar gestão de fila e sensores...");

        while (running) {
            try {
                //Verificar Emergência
                if(mechanism.bothSwitchesPressed()) {
                    System.out.println("EMERGENCY MODE!");

                }
                // 1. Verificar Switch 1 (RH7 - Remoção em Massa)
                if (mechanism.switch1Pressed()) {
                    System.out.println("!!! SWITCH 1 DETETADO: A iniciar Remoção em Massa !!!");
                    handleMassRemoval();

                    // Pequena pausa para dar tempo ao utilizador de largar o botão
                    // e não disparar 10 vezes seguidas
                    Thread.sleep(2000);
                }

                // 2. Processar a Fila (NF1)
                // Usamos .poll() com timeout em vez de .take() para não bloquear para sempre.
                // Assim, a cada 100ms, o loop roda e volta a verificar o Switch 1.
                Runnable task = requestQueue.poll(100, TimeUnit.MILLISECONDS);

                if (task != null) {
                    System.out.println("[Manager] A processar próximo pedido...");
                    task.run(); // Executa a tarefa (Deliver2, ManualStore, etc.) nesta thread
                    System.out.println("[Manager] Pedido concluído.");
                }

            } catch (InterruptedException e) {
                System.out.println("StorageManager interrompido.");
                running = false;
            }
        }
    }

    // Lógica do RH7
    private void handleMassRemoval() {
        // 1. Iniciar Thread para piscar LED 1 (Indicador de processo)
        LedBlink blinker = new LedBlink(mechanism, 1);
        blinker.start();

        try {
            System.out.println("--- A verificar paletes expiradas ou com humidade alta ---");

            // Percorrer a grelha à procura de problemas
            for (int z = 1; z <= 3; z++) {
                for (int x = 1; x <= 3; x++) {
                    Pallet p = grid.getPalletInfo(x, z);

                    if (p != null) {

                        if (p.getAlert()) {
                            System.out.println(">> A REMOVER PALETE DE ALERTA: " + p.getProductType() + " em (" + x + "," + z + ")");

                            // Criar e executar tarefa de remoção (Deliver2 - Modo Dinâmico)
                            // Nota: Executamos diretamente aqui para ter prioridade total
                            Deliver2 removalTask = new Deliver2(p, mechanism);
                            removalTask.run();

                            // Atualizar grelha
                            grid.retrievePallet(x, z);
                        }
                    }
                }
            }
            System.out.println("--- Remoção em Massa Concluída ---");

        } finally {
            // Parar o LED 1 quando acabar
            blinker.stopBlinking();
            try { blinker.join(); } catch (Exception e) {}
            mechanism.ledsOff();
        }
    }

    public void stopManager() {
        this.running = false;
        this.interrupt();
    }
}