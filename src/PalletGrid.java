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
}