import java.time.LocalDate;
public class PalletGrid {
    // Matriz 3x3 para guardar as paletes.
    // null = célula vazia
    // Objeto Pallet = célula ocupada
    private final Pallet[][] cells = new Pallet[3][3];

    // Adicionar uma palete numa posição específica (X, Z)
    // Retorna true se guardou com sucesso, false se já estava ocupada
    public boolean storePallet(Pallet pallet, int x, int z) {
        // Converter coordenadas 1-3 para índices 0-2
        int col = x - 1;
        int row = z - 1;

        if (!isValidPos(col, row)) {
            System.out.println("Erro: Coordenadas inválidas.");
            return false;
        }

        if (cells[col][row] != null) {
            System.out.println("Erro: A célula (" + x + "," + z + ") já está ocupada!");
            return false;
        }

        // Atualizar a posição interna da palete e guardar na matriz
        pallet.setPosition(x, z);
        cells[col][row] = pallet;
        System.out.println("Registo: Palete guardada em " + x + "," + z);
        return true;
    }

    // Retirar (e apagar) uma palete de uma posição
    public Pallet retrievePallet(int x, int z) {
        int col = x - 1;
        int row = z - 1;

        if (!isValidPos(col, row) || cells[col][row] == null) {
            System.out.println("Erro: Não existe palete na posição " + x + "," + z);
            return null;
        }

        Pallet p = cells[col][row];
        cells[col][row] = null; // Libertar o espaço
        System.out.println("Registo: Palete removida de " + x + "," + z);
        return p;
    }

    // Verificar se uma célula está vazia
    public boolean isCellFree(int x, int z) {
        int col = x - 1;
        int row = z - 1;
        return isValidPos(col, row) && cells[col][row] == null;
    }

    // Obter informação da palete sem a remover (para listagens)
    public Pallet getPalletInfo(int x, int z) {
        int col = x - 1;
        int row = z - 1;
        if (isValidPos(col, row)) {
            return cells[col][row];
        }
        return null;
    }

    // Método auxiliar para validar limites
    private boolean isValidPos(int c, int r) {
        return c >= 0 && c < 3 && r >= 0 && r < 3;
    }

    // Imprimir o estado do armazém (Útil para debug e RH12)
    public void printStatus() {
        System.out.println("Estado do Armazém:");
        for (int z = 3; z >= 1; z--) { // Imprimir de cima para baixo (Z=3 no topo)
            for (int x = 1; x <= 3; x++) {
                Pallet p = getPalletInfo(x, z);
                String status = (p == null) ? "[ Vazio ]" : "[ " + p.getProductType() + " ]";
                System.out.print(status + "\t");
            }
            System.out.println(); // Nova linha para o próximo nível Z
        }
    }

    public void productlist() {
        System.out.println("=== Lista de Paletes Armazenadas ===");
        boolean any = false;
        for (int z = 3; z >= 1; z--) { // Imprimir de cima para baixo (Z=3 no topo)
            for (int x = 1; x <= 3; x++) {
                Pallet p = getPalletInfo(x, z);
                if (p != null) {
                    any = true;
                    System.out.println("Posição (" + x + "," + z + "): "
                            + "Produto=" + p.getProductType()
                            + ", Produtor=" + p.getProducerID()
                            + ", Humidade=" + p.getHumidity() + "%"
                            + ", Data de envio=" + p.getShippingDate());
                }
            }
        }
        if (!any) System.out.println("Nenhuma palete armazenada.");
    }

    public void palletlookup(String searchTerm) {
        System.out.println("Procurando por tipo de produto ou ID do produtor: " + searchTerm);
        boolean found = false;
        for (int z = 3; z >= 1; z--) {
            for (int x = 1; x <= 3; x++) {
                Pallet p = getPalletInfo(x, z);
                if (p != null && (p.getProductType().equalsIgnoreCase(searchTerm) ||
                        p.getProducerID().equalsIgnoreCase(searchTerm))) {
                    System.out.println("\n--- Pallet encontrada ---");
                    System.out.println("Localização: (" + p.getPosX() +  ", " + p.getPosZ() + ")");
                    System.out.println("Tipo de produto: " + p.getProductType());
                    System.out.println("ID Produtor: " + p.getProducerID());
                    System.out.println("Humidade: " + p.getHumidity() + "%");
                    System.out.println("Destino: " + p.getDestination());
                    System.out.println("Data de entrega: " + p.getShippingDate());
                    found = true;
                }
            }
        }
        if (!found) {
            System.out.println("Nenhuma pallet encontrada com esse tipo de produto ou ID do produtor.");
        }
    }
    public int[] findBestPosition(String productType) {
        int bestX = -1, bestZ = -1;
        double bestScore = -Double.MAX_VALUE;

        for (int x = 1; x <= 3; x++) {
            for (int z = 1; z <= 3; z++) {
                if (isCellFree(x, z)) {
                    double score = calculateScore(x, z, productType);

                    // Se encontrarmos um score melhor (ou igual mas mais perto), atualizamos
                    if (score > bestScore) {
                        bestScore = score;
                        bestX = x;
                        bestZ = z;
                    }
                }
            }
        }

        if (bestX != -1) {
            System.out.println("AI: Posição recomendada (" + bestX + "," + bestZ + ") com score " + bestScore);
            return new int[]{bestX, bestZ};
        }
        return null; // Armazém cheio
    }

    private double calculateScore(int x, int z, String productType) {
        double score = 0;

        // 1. Minimizar Distância (de 1,1)
        // Distância Manhattan: |x-1| + |z-1|. Quanto maior a distância, MENOR o score.
        double dist = (x - 1) + (z - 1);
        score -= dist * 1.0; // Peso 1.0 para distância

        // 2. Agrupar por Tipo
        // Se a coluna já tiver este produto, damos um bónus GRANDE
        if (columnHasType(x, productType)) {
            score += 10.0;
        }

        // 3. Balancear Ocupação
        // Preferir colunas mais vazias. Subtraímos pontos por cada item na coluna.
        int colCount = countItemsInColumn(x);
        score -= colCount * 2.0;

        return score;
    }

    // Verifica se existe algum produto deste tipo na coluna X
    private boolean columnHasType(int x, String type) {
        int colIndex = x - 1;
        for (int r = 0; r < 3; r++) {
            if (cells[colIndex][r] != null && cells[colIndex][r].getProductType().equalsIgnoreCase(type)) {
                return true;
            }
        }
        return false;
    }

    // Conta quantas paletes existem na coluna X
    private int countItemsInColumn(int x) {
        int count = 0;
        int colIndex = x - 1;
        for (int r = 0; r < 3; r++) {
            if (cells[colIndex][r] != null) count++;
        }
        return count;
    }

    public void checkAlerts(LocalDate today, Mechanism mechanism) {
        boolean alertFound = false;
        System.out.println("--- A verificar alertas para o dia " + today + " ---");

        for (int z = 1; z <= 3; z++) {
            for (int x = 1; x <= 3; x++) {
                Pallet p = getPalletInfo(x, z);

                if (p != null) {
                    // 1. Verificar Data de Envio (Shipping Date)
                    // Se hoje for igual ou depois da data de envio, ALERTA!
                    boolean dateAlert = !today.isBefore(p.getShippingDate()); // !isBefore é o mesmo que >=

                    // 2. Verificar Humidade (Exemplo: > 80%)
                    boolean humidityAlert = p.getHumidity() > 80.0;

                    if (dateAlert || humidityAlert) {
                        alertFound = true;
                        p.setAlert(true);
                        String motivo = dateAlert ? "[DATA ENVIO]" : "[HUMIDADE]";
                        if (dateAlert && humidityAlert) motivo = "[DATA + HUMIDADE]";

                        System.out.println("ALERTA " + motivo + ": Palete " + p.getProductType() +
                                " em (" + x + "," + z + ")");
                    }
                }
            }
        }

        // RH6: Ligar LED 1 se houver algum alerta
        if (alertFound) {
            LedBlink blinker = new LedBlink(mechanism, 1);
            blinker.start();
        } else {
            mechanism.ledsOff();
        }
    }
}