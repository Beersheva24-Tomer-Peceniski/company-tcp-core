package telran.employees;

import telran.employees.threads.SavingFile;
import telran.io.Persistable;
import telran.net.TcpServer;

public class Main {
    private static final String FILE_NAME = "employees.data";
    private static final int PORT = 4000;
    private static final long MILISECONDS_TO_SAVE = 60_000; // 10 MINUTES

    public static void main(String[] args) {
        Company company = new CompanyImpl();
        if (company instanceof Persistable persistable) {
            persistable.restoreFromFile(FILE_NAME);
        }
        TcpServer tcpServer = new TcpServer(new CompanyProtocol(company), PORT);
        Thread savingFile = new SavingFile(MILISECONDS_TO_SAVE, company);
        savingFile.start();
        tcpServer.run();
    }
}