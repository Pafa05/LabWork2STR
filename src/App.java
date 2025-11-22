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


        Scanner scan = new Scanner(System.in);
        int op = -1;



        while (op != 0) {
            System.out.println("Enter an option:" + "\t" +
                    "0=exit" + "\t" +
                    "1=move right" + "\t" +
                    "2=move left" + "\t" +
                    "3=stop" + "\t" +
                    "4 gotoX" + "\t" +
                    "5 gotoZ" + "\t" +
                    "6 gotoY" + "\t" +
                    "7 entrega manual" + "\t" +
                    "10=go to (x,z)" + "\t" +
                    "12 inventory" + "\t" +
                    "13 inventário(lista)" + "\t" +
                    "14 procurar por produtor" + "\t" );


            if (!scan.hasNextInt()) {
                System.out.println("Invalid input, try again.");
                scan.next(); // consume invalid token
                continue;
            }
            op = scan.nextInt();
            switch (op) {
                case 0: System.out.println("Exiting..."); break;
                case 1: axisX.moveBackward(); break;
                case 2: axisX.moveForward(); break;
                case 3: axisX.stop(); break;
                case 4: System.out.println("LEI É GAY");
                    axisX.gotoPos(scan.nextInt()); break;
                case 5: System.out.println("LEI É GAY2");
                    axisZ.gotoPos(scan.nextInt()); break;
                case 6: System.out.println("LEI É GAY3");
                    axisY.gotoPos(scan.nextInt()); break;
                case 7: {
                    Pallet pallet = readPalletInput(scan, formatter);
                    System.out.println("Inserir posição X");
                    int targetX1 = scan.nextInt(); scan.nextLine();
                    System.out.println("Inserir posição Z");
                    int targetZ1 = scan.nextInt(); scan.nextLine();

                    if (grid.isCellFree(targetX1, targetZ1)) {
                        mechanism.goToPosition(1,1);
                        mechanism.setY(1);
                        System.out.println("A aguardar caixa...");
                        scan.nextLine();
                        mechanism.setY(2);
                        ManualStore manu = new ManualStore(mechanism, pallet, targetX1, targetZ1);
                        manu.run();
                        grid.storePallet(pallet, targetX1, targetZ1);

                    } else {
                        System.out.println("Operação cancelada: A célula destino já está ocupada!");
                    }
                    break;
                }
                case 8: { // RH3 - Assisted Storage
                    Pallet p = readPalletInput(scan, formatter);
                    // 2. Pedir ao "AI" a melhor posição
                    int[] bestPos = grid.findBestPosition(p.getProductType());

                    if (bestPos != null) {
                        int targetX = bestPos[0];
                        int targetZ = bestPos[1];

                        System.out.println("--> O sistema recomenda a posição: X=" + targetX + " Z=" + targetZ);
                        System.out.println("Pressione ENTER para confirmar e iniciar transporte...");
                        scan.nextLine();

                        // 3. Executar o transporte (igual ao manual)
                        // Primeiro movemos o mecanismo para a entrada (1,1) para receber a caixa
                        // (Assumindo que a entrega começa em 1,1 externo)
                        mechanism.goToPosition(1, 1);
                        mechanism.setY(1); // Outside para o humano colocar

                        System.out.println("Coloque a caixa e pressione ENTER.");
                        scan.nextLine();

                        mechanism.setY(2); // Recolher

                        // Usar a tua classe de entrega
                        ManualStore task = new ManualStore(mechanism, p, targetX, targetZ);
                        task.run();

                        // Registar na grelha
                        grid.storePallet(p, targetX, targetZ);

                    } else {
                        System.out.println("O armazém está cheio!");
                    }
                    break;
                }
                case 10:
                    int targetX = scan.nextInt();
                    int targetZ = scan.nextInt();

                    GotoXZThread moveXZThread = new GotoXZThread(axisX, axisZ,  axisY,
                            targetX,
                            targetZ
//                            completionSem // Passa o semáforo para sinalizar no fim
                    );

                    moveXZThread.start();
                    break;
                case 11: CalibrationThread newCalibX = new CalibrationThread(axisX);
                    newCalibX.start();

                    CalibrationThread newCalibZ = new CalibrationThread(axisZ);
                    newCalibZ.start();
                 break;
                case 12:
                    grid.printStatus();
                    break;
                case 13:
                    grid.productlist();
                    break;
                case 14:
                    System.out.println("Inserir ID do produtor:");
                    String tipoPallet = scan.nextLine();
                    grid.palletlookup(tipoPallet);
                    break;
                case 15:
                    System.out.println("Inserir posx:");
                    int xixi = scan.nextInt();
                    System.out.println("Inserir posz:");
                    int zizi = scan.nextInt();
                    Pallet p = grid.getPalletInfo(xixi, zizi); // 1. Consultar (ainda está na grelha)

                    if (p != null) {
                        // 2. Iniciar o transporte (o robot sabe para onde ir porque p.getPosX ainda é válido)
                        // Assumindo que tens a classe Deliver2 atualizada para receber (Mechanism, Pallet)
                        Deliver2 job = new Deliver2(p,mechanism);
                        Thread t = new Thread (job);
                        t.start();
                        t.join();
                        // 3. Agora que já saiu fisicamente, limpamos o registo lógico
                        grid.retrievePallet(xixi, zizi); // Isto vai chamar p.resetLocation() internamente
                    }
                    break;
                case 16:

                default: System.out.println("Unknown option."); break;
            }
        }

        scan.close();
    }
}