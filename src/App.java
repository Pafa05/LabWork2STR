import java.util.Scanner;
import java.util.concurrent.Semaphore;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Labwork2 from Java");

        // initialize native hardware (calls JNI)
        Storage.initializeHardwarePorts();

        CalibrationThread calibThread = new CalibrationThread(new AxisX());
        calibThread.start();
        CalibrationThread calibThread2 = new CalibrationThread(new AxisZ());
        calibThread2.start();

        AxisX axisX = new AxisX();
        AxisY axisY = new AxisY();
        AxisZ axisZ = new AxisZ();
        Mechanism mechanism = new Mechanism(axisY);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        Scanner scan = new Scanner(System.in);
        int op = -1;


        while (op != 0) {
            System.out.println("Enter an option (0=exit, 1=move right, 2=move left, 3=stop, 10=go to (x,z):");
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
                    System.out.println("ppp");
                    System.out.println("Inserir tipo da pallet");
                    String tipoPallet = scan.nextLine(); scan.nextLine();
                    System.out.println("Inserir humidade");
                    Double Humi = scan.nextDouble(); scan.nextLine();
                    System.out.println("Inserir ID");
                    String ID = scan.nextLine();
                    System.out.println("Inserir data de colheita");
                    String harvdate = scan.nextLine(); scan.nextLine();
                    System.out.println("Inserir destino");
                    String dest = scan.nextLine(); scan.nextLine();
                    System.out.println("Inserir data de entrega");
                    String shipdate = scan.nextLine(); scan.nextLine();
                    Pallet pallet = new Pallet(tipoPallet, Humi, ID, LocalDate.parse(harvdate, formatter), dest, LocalDate.parse(shipdate, formatter));
                    System.out.println("Inserir posição X");
                    int targetX1 = scan.nextInt(); scan.nextLine();
                    System.out.println("Inserir posição Z");
                    int targetZ1 = scan.nextInt(); scan.nextLine();
                    ManualDeliver manu = new ManualDeliver(mechanism,pallet, targetX1, targetZ1);
                    manu.run();
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
                default: System.out.println("Unknown option."); break;
            }
        }

        scan.close();
    }
}