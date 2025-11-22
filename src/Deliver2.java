public class Deliver2  implements Runnable{
        private Pallet pallet;
        private Mechanism mechanism;

        public Deliver2(Pallet pallet, Mechanism mechanism) {
            this.pallet = pallet;
            this.mechanism = mechanism;
        }

        public void run() {
            int targetX = pallet.getPosX();
            int targetZ = pallet.getPosZ();

            // Validação de segurança (opcional, mas boa prática)
            if (targetX == -1 || targetZ == -1) {
                System.out.println("Erro: A palete não tem posição definida no armazém!");
                return;
            }

             LedBlink blinker = new LedBlink(mechanism, 2);
             blinker.start();

            try {
                mechanism.goToPosition(targetX, targetZ);
                mechanism.takePartFromCell(targetZ);

                mechanism.goToPosition(targetX, 1);

                // 4. ENTREGAR A PEÇA (Y=1 Outside)
                System.out.println("Deliver2: A entregar ao cliente...");
                mechanism.setY(1);

                try { Thread.sleep(2000); } catch (InterruptedException e) {}

                // Recolher braço
                mechanism.setY(2);

            } finally {
                // 5. Parar o LED
                blinker.stopBlinking();
                // try { blinker.join(); } catch (Exception e) {}
            }

            // Limpar a posição da palete (pois já saiu do armazém)
            pallet.setPosition(-1, -1);
            System.out.println("--- Deliver Mode 2 Concluído ---");
        }

    }

