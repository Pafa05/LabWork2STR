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
                case 4: axisX.gotoPos(3); break;
                case 5: axisX.gotoPos(2); break;
                case 6: axisX.gotoPos(1); break;
                case 7: axisZ.gotoPos(1); break;
                case 8: axisZ.gotoPos(2); break;
                case 9: axisZ.gotoPos(3); break;
                case 10:
                    int targetX = 3;
                    int targetZ = 3;

                    // 2. Instancie a thread com os parâmetros
                    GotoXZThread moveXZThread = new GotoXZThread(
                            axisX,
                            axisZ,
                            axisY,
                            targetX,
                            targetZ
//                            completionSem // Passa o semáforo para sinalizar no fim
                    );

                    // 3. Chame o método .start() para iniciar a execução da thread
                    moveXZThread.start();

                    // Opção para a thread principal aguardar a conclusão
                    /* System.out.println("Aguardando conclusão do movimento...");
                    completionSem.acquire(); // Bloqueia até a thread chamar release()
                    System.out.println("Movimento XZ concluído com sucesso!");
                    */
                    break;
                case 11: axisY.gotoPos(2); break;
                case 12: axisY.gotoPos(1); break;
                case 13: axisY.gotoPos(3); break;
                default: System.out.println("Unknown option."); break;
            }
        }

        scan.close();
    }
}