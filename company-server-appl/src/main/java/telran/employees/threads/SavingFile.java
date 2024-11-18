package telran.employees.threads;

import telran.employees.Company;
import telran.io.Persistable;

public class SavingFile extends Thread {

    private long miliseconds;
    private Company company;
    private static final String FILE_NAME = "employees.data";

    public SavingFile(long miliseconds, Company company) {
        this.miliseconds = miliseconds;
        this.company = company;
    }

    @Override
    public void run() {
        if (company instanceof Persistable) {
            Persistable persistable = (Persistable) company;
            runSaving(persistable);
        }
    }

    private void runSaving(Persistable persistable) {
        while (true) {
            try {
                sleep(miliseconds);
                persistable.saveToFile(FILE_NAME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
