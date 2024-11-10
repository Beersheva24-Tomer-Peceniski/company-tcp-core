package telran.employees;

import java.util.Iterator;

import org.json.JSONArray;

import telran.net.TcpClient;

public class CompanyTcpProxy implements Company {
    TcpClient tcpClient;

    public CompanyTcpProxy(TcpClient tcpClient) {
        this.tcpClient = tcpClient;
    }

    @Override
    public Iterator<Employee> iterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addEmployee(Employee empl) {
        tcpClient.sendAndReceive("addEmployee", empl.toString());
    }

    @Override
    public int getDepartmentBudget(String arg0) {
        return Integer.parseInt(tcpClient.sendAndReceive("getDepartmentBudget", arg0));
    }

    @Override
    public String[] getDepartments() {
        String jsonStr = tcpClient.sendAndReceive("getDepartments", "");
        JSONArray jsonArray = new JSONArray(jsonStr);
        String[] res = jsonArray.toList().toArray(String[]::new);
        return res;
    }

    @Override
    public Employee getEmployee(long arg0) {
        String jsonStr = tcpClient.sendAndReceive("getEmployee", String.valueOf(arg0));
        Employee emp = Employee.getEmployeeFromJSON(jsonStr);
        return emp;
    }

    @Override
    public Manager[] getManagersWithMostFactor() {
        String jsonStr = tcpClient.sendAndReceive("getManagersWithMostFactor", "");
        JSONArray jsonArray = new JSONArray(jsonStr);
        Manager[] res = getManagersFromJSONArray(jsonArray);
        return res;
    }

    private Manager[] getManagersFromJSONArray(JSONArray jsonArray) {
        Manager[] res = new Manager[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            String managerString = jsonArray.getString(i);
            Employee emp = Employee.getEmployeeFromJSON(managerString);
            Manager manager = new Manager();
            if (emp instanceof Manager) {
                manager = (Manager) emp;
            }
            res[i] = manager;
        }
        return res;
    }

    @Override
    public Employee removeEmployee(long arg0) {
        String responseJSON = tcpClient.sendAndReceive("removeEmployee", String.valueOf(arg0));
        return Employee.getEmployeeFromJSON(responseJSON);
    }

}