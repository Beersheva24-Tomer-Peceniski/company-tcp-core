package telran.employees;

import java.util.Scanner;

import telran.io.Persistable;
import telran.io.PersistableSaverThread;
import telran.net.TcpServer;

public class Main {
    private static final String FILE_NAME = "employees.data";
        private static final int PORT = 4000;
                private static final long TIME_INTERVAL = 60000;
                
                    public static void main(String[] args) {
                        Company company = new CompanyImpl();
                        if (company instanceof Persistable persistable) {
                            persistable.restoreFromFile(FILE_NAME);
                            PersistableSaverThread saverThread = new PersistableSaverThread(persistable, FILE_NAME, TIME_INTERVAL);
                            saverThread.start();
                            Runtime.getRuntime().addShutdownHook(new Thread(() -> persistable.saveToFile(FILE_NAME)));
            }
           
            TcpServer tcpServer = new TcpServer(new CompanyProtocol(company), PORT);
           new Thread(tcpServer).start();
           Scanner scanner = new Scanner(System.in);
           while(true) {
            System.out.println("enter shutdown for stopping server");
            String line = scanner.nextLine();
            if(line.equals(
                "shutdown"
            )) {
                tcpServer.shutdown();
                break;
            }
           }
    }
}