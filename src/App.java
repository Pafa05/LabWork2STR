import java.util.Scanner;
import java.util.concurrent.Semaphore;

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