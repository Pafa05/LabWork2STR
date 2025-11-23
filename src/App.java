import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;




public class App {

    public static Pallet readPalletInput(Scanner scan, DateTimeFormatter formatter) {
        String tipoPallet;
        double humidade;
        String producerID;
        LocalDate harvestDate;
        String destino;
        LocalDate shipDate;

        // Ler tipo de pallet (não vazio)
        while (true) {
            System.out.println("Inserir tipo da pallet (obrigatório):");
            tipoPallet = scan.nextLine().trim();
            if (!tipoPallet.isEmpty()) {
                break;
            }
            System.out.println("Erro: Tipo de pallet não pode ser vazio!");
        }

        // Ler humidade (0-100)
        while (true) {
            System.out.println("Inserir humidade (0-100):");
            if (scan.hasNextDouble()) {
                humidade = scan.nextDouble();
                scan.nextLine(); // limpar ENTER
                if (humidade >= 0 && humidade <= 100) {
                    break;
                }
                System.out.println("Erro: Humidade deve estar entre 0 e 100!");
            } else {
                System.out.println("Erro: Insira um número válido!");
                scan.nextLine(); // limpar buffer
            }
        }

        // Ler produtor ID (não vazio)
        while (true) {
            System.out.println("Inserir ID do produtor (obrigatório):");
            producerID = scan.nextLine().trim();
            if (!producerID.isEmpty()) {
                break;
            }
            System.out.println("Erro: ID do produtor não pode ser vazio!");
        }

        // Ler data de colheita (formato dd-MM-yyyy)
        while (true) {
            System.out.println("Inserir data de colheita (dd-MM-yyyy):");
            try {
                String harvdate = scan.nextLine().trim();
                harvestDate = LocalDate.parse(harvdate, formatter);
                break;
            } catch (Exception e) {
                System.out.println("Erro: Data inválida! Use formato dd-MM-yyyy");
            }
        }

        // Ler destino (não vazio)
        while (true) {
            System.out.println("Inserir destino (obrigatório):");
            destino = scan.nextLine().trim();
            if (!destino.isEmpty()) {
                break;
            }
            System.out.println("Erro: Destino não pode ser vazio!");
        }

        // Ler data de entrega (formato dd-MM-yyyy)
        while (true) {
            System.out.println("Inserir data de entrega (dd-MM-yyyy):");
            try {
                String shipdate = scan.nextLine().trim();
                shipDate = LocalDate.parse(shipdate, formatter);
                // Validar que data de entrega é depois de colheita
                if (shipDate.isAfter(harvestDate)) {
                    break;
                }
                System.out.println("Erro: Data de entrega deve ser após data de colheita!");
            } catch (Exception e) {
                System.out.println("Erro: Data inválida! Use formato dd-MM-yyyy");
            }
        }

        return new Pallet(tipoPallet, humidade, producerID,
                harvestDate,
                destino,
                shipDate,
                false);
    }
    public static void main(String[] args) throws Exception {
        System.out.println("Labwork2 from Java");

        // initialize native hardware (calls JNI)
        Storage.initializeHardwarePorts();

        AxisX axisX = new AxisX();
        AxisY axisY = new AxisY();
        AxisZ axisZ = new AxisZ();
        PalletGrid grid = new PalletGrid();
        CalibrationThread calibThread = new CalibrationThread(axisX);
        calibThread.start();
        CalibrationThread calibThread2 = new CalibrationThread(axisZ);
        calibThread2.start();

        Mechanism mechanism = new Mechanism(axisX, axisY, axisZ);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate currentDate = LocalDate.parse("13-09-2025", formatter);
        System.out.println("Data inicial do sistema: " + currentDate.format(formatter));

        StorageManager storageManager = new StorageManager(mechanism, grid, currentDate);
        storageManager.start();


        Scanner scan = new Scanner(System.in);
        int op = -1;

        while (op != 0) {
            System.out.println("Enter an option:" + "\t" +
                    "0=exit" + "\n" +
                    "1=move right" + "\n" +
                    "2=move left" + "\n" +
                    "3=stop" + "\n" +
                    "4 gotoX" + "\n" +
                    "5 gotoZ" + "\n" +
                    "6 gotoY" + "\n" +
                    "7 entrega manual" + "\n" +
                    "10=go to (x,z)" + "\n" +
                    "12 inventory" + "\n" +
                    "13 inventário(lista)" + "\n" +
                    "14 procurar por produtor" + "\n" );


            if (!scan.hasNextInt()) {
                System.out.println("Invalid input, try again.");
                scan.next(); // consume invalid token
                continue;
            }
            op = scan.nextInt();
            switch (op) {
                case 0:
                    System.out.println("Exiting...");
                    // Parar o StorageManager e aguardar que termine
                    try {
                        storageManager.stopManager();
                        storageManager.join(3000); // espera até 3s por limpeza
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    // Sair do loop e terminar o programa
                    break;
                case 1:
                    axisX.moveBackward();
                    break;
                case 2:
                    axisX.moveForward();
                    break;
                case 3:
                    axisX.stop();
                    break;
                case 4:
                    System.out.println("LEI É GAY");
                    axisX.gotoPos(scan.nextInt());
                    break;
                case 5:
                    System.out.println("LEI É GAY2");
                    axisZ.gotoPos(scan.nextInt());
                    break;
                case 6:
                    System.out.println("LEI É GAY3");
                    axisY.gotoPos(scan.nextInt());
                    break;
                case 7: { // Manual Storage
                    Pallet pallet = readPalletInput(scan, formatter);
                    System.out.println("Posição X alvo:");
                    int tx = scan.nextInt();
                    System.out.println("Posição Z alvo:");
                    int tz = scan.nextInt();

                    if (grid.isCellFree(tx, tz)) {
                        // Preparar a posição de entrada (1,1)
                        mechanism.goToPosition(1, 1);
                        mechanism.setY(1); // Outside
                        System.out.println("Coloque a caixa e pressione ENTER.");
                        scan.nextLine(); scan.nextLine(); // Consumir enter
                        mechanism.setY(2); // Recolher

                        // Adicionar pedido à fila do Manager (Não bloqueia o menu!)
                        ManualStore task = new ManualStore(mechanism, pallet, tx, tz);
                        storageManager.addRequest(task);

                        // Registar lógico (Assume sucesso ou gere depois)
                        grid.storePallet(pallet, tx, tz);
                        System.out.println("Pedido de armazenamento adicionado à fila.");
                    } else {
                        System.out.println("Erro: Célula ocupada.");
                    }
                    break;
                }
                case 8: { // Assisted Storage (AI)
                    Pallet p = readPalletInput(scan, formatter);
                    int[] bestPos = grid.findBestPosition(p.getProductType());

                    if (bestPos != null) {
                        int tx = bestPos[0];
                        int tz = bestPos[1];
                        System.out.println("--> AI recomenda: X=" + tx + " Z=" + tz);
                        System.out.println("Pressione ENTER para aceitar.");
                        scan.nextLine(); scan.nextLine();

                        mechanism.goToPosition(1, 1);
                        mechanism.setY(1);
                        System.out.println("Coloque a caixa e pressione ENTER.");
                        scan.nextLine();
                        mechanism.setY(2);

                        // Adicionar ao Manager
                        ManualStore task = new ManualStore(mechanism, p, tx, tz);
                        storageManager.addRequest(task);
                        grid.storePallet(p, tx, tz);
                        System.out.println("Pedido inteligente adicionado à fila.");
                    } else {
                        System.out.println("Armazém cheio!");
                    }
                    break;
                }
                case 10:
                    int targetX = scan.nextInt();
                    int targetZ = scan.nextInt();

                    GotoXZThread moveXZThread = new GotoXZThread(axisX, axisZ, axisY,
                            targetX,
                            targetZ
//                            completionSem // Passa o semáforo para sinalizar no fim
                    );

                    moveXZThread.start();
                    break;
                case 11:
                    CalibrationThread newCalibX = new CalibrationThread(axisX);
                    newCalibX.start();
                    CalibrationThread newCalibZ = new CalibrationThread(axisZ);
                    newCalibZ.start();
                    break;
                case 12:
                    grid.productlist();
                    break;
                case 13:
                    System.out.println("=== Procurar Paletes por Produtor/Tipo ===");
                    System.out.println("Insira o tipo de produto ou ID do produtor:");
                    scan.nextLine();
                    String searchProducer = scan.nextLine();
                    grid.palletlookup(searchProducer);
                    break;
                case 15: // Retirar Específico (Modo 2)
                    System.out.println("X:"); int rx = scan.nextInt();
                    System.out.println("Z:"); int rz = scan.nextInt();
                    Pallet pRem = grid.getPalletInfo(rx, rz);

                    if (pRem != null) {
                        // Adicionar ao Manager
                        Deliver2 job = new Deliver2(pRem, mechanism);
                        storageManager.addRequest(job);
                        grid.retrievePallet(rx, rz); // Atualizar grelha
                        System.out.println("Pedido de remoção adicionado à fila.");
                    } else {
                        System.out.println("Célula vazia.");
                    }
                    break;
                case 16: // Delivery by product type or producer
                {
                    System.out.println("=== Entrega por Tipo de Produto/Produtor ===");
                    System.out.println("Insira o tipo de produto ou ID do produtor:");
                    scan.nextLine();
                    String searchTerm = scan.nextLine().trim();

                    // Coletar pallets que correspondem ao critério
                    java.util.List<int[]> matchingPositions = new java.util.ArrayList<>();

                    System.out.println("\nPallets encontradas para entrega:");
                    int palletCount = 0;

                    for (int z = 3; z >= 1; z--) {
                        for (int x = 1; x <= 3; x++) {
                            Pallet tempPallet = grid.getPalletInfo(x, z);
                            if (tempPallet != null && (tempPallet.getProductType().equalsIgnoreCase(searchTerm) ||
                                    tempPallet.getProducerID().equalsIgnoreCase(searchTerm))) {
                                palletCount++;
                                matchingPositions.add(new int[]{x, z});
                                System.out.println(palletCount + ". Posição (" + x + ", " + z + ") - " +
                                        tempPallet.getProductType() + " (Produtor: " + tempPallet.getProducerID() + ")");
                            }
                        }
                    }

                    if (palletCount == 0) {
                        System.out.println("Nenhuma pallet encontrada com esse critério.");
                        break;
                    }

                    // Confirmar entrega
                    System.out.println("\nDeseja entregar todas as " + palletCount + " pallets? (S/N)");
                    String confirm = scan.nextLine().trim().toUpperCase();

                    if (confirm.equals("S")) {
                        System.out.println("Iniciando entrega de todas as pallets...");

                        for (int[] pos : matchingPositions) {
                            int x = pos[0];
                            int z = pos[1];
                            Pallet pallet = grid.getPalletInfo(x, z);

                            if (pallet != null) {
                                System.out.println("\nEntregando pallet em (" + x + ", " + z + ")...");

                                try {
                                    // Usar Deliver1 (parecido ao case 17)
                                    Deliver1 job = new Deliver1(pallet, mechanism);
                                    job.run();
                                    grid.retrievePallet(x, z);
                                    System.out.println("Pallet entregue com sucesso!");

                                } catch (Exception e) {
                                    System.out.println("Erro durante entrega: " + e.getMessage());
                                }
                            }
                        }

                        System.out.println("\n=== Entrega concluída ===");
                    } else {
                        System.out.println("Entrega cancelada.");
                    }
                    break;

                }

                case 17: // Entrega Modo 1 (Saída fixa em X=3)
                    System.out.println("--- Retirar Palete (Modo 1) ---");
                    System.out.println("X da palete:");
                    int x1 = scan.nextInt();
                    System.out.println("Z da palete:");
                    int z1 = scan.nextInt();

                    Pallet p1 = grid.getPalletInfo(x1, z1); // 1. Verificar se existe

                    if (p1 != null) {
                        // 2. Criar a tarefa de entrega Modo 1
                        // Nota: Certifica-te que a classe Deliver1 implementa 'Runnable' tal como a Deliver2
                        Deliver1 job = new Deliver1(p1, mechanism);

                        // 3. Adicionar à fila do Gestor (Não bloqueia o menu!)
                        storageManager.addRequest(job);

                        // 4. Atualizar a grelha lógica
                        grid.retrievePallet(x1, z1);
                        System.out.println("Pedido de entrega (Modo 1) adicionado à fila.");
                    } else {
                        System.out.println("Erro: Célula vazia ou inválida.");
                    }
                    break;

                case 20: // Avançar Dia (Simulação)
                    currentDate = currentDate.plusDays(1);
                    System.out.println("--> Novo dia: " + currentDate.format(formatter));

                    // Atualizar o Manager (para ele saber verificar o Switch 1 com a nova data)
                    storageManager.setCurrentDate(currentDate);

                    // Verificar alertas visualmente na consola
                    grid.checkAlerts(currentDate, mechanism);
                    break;
                default:
                    System.out.println("Unknown option.");
                    break;
            }
        }

        scan.close();
    }
}