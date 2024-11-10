package telran.employees;

import java.util.Arrays;

import org.json.JSONArray;

import telran.net.*;

public class CompanyProtocol implements Protocol {

    CompanyImpl company;

    public CompanyProtocol() {
        this.company = new CompanyImpl();
    }

    @Override
    public Response getResponse(Request request) {
        String type = request.requestType();
        String data = request.requestData();
        Response response = null;
        try {
            response = switch (type) {
                case "addEmployee" -> addEmployee(data);
                case "getDepartmentBudget" -> getDepartmentBudget(data);
                case "getDepartments" -> getDepartments();
                case "getEmployee" -> getEmployee(data);
                case "getManagersWithMostFactor" -> getManagersWithMostFactor();
                case "removeEmployee" -> removeEmployee(data);
                default -> new Response(ResponseCode.WRONG_TYPE, type + " is wrong type");
            };
        } catch (Exception e) {
            response = new Response(ResponseCode.WRONG_DATA, e.getMessage());
        }
        return response;
    }

    Response addEmployee(String data) {
        Employee empl = Employee.getEmployeeFromJSON(data);
        company.addEmployee(empl);
        return new Response(ResponseCode.OK, "");
    }

    Response getDepartmentBudget(String department) {
        int departmentBudget = company.getDepartmentBudget(department);
        return new Response(ResponseCode.OK, String.valueOf(departmentBudget));
    }

    Response getDepartments() {
        String [] departmentsArray = company.getDepartments();
        JSONArray jsonArray = new JSONArray(departmentsArray);
        return new Response(ResponseCode.OK, jsonArray.toString());
    }

    Response getEmployee(String data) {
        long id = Long.parseLong(data);
        Employee emp = company.getEmployee(id);
        return new Response(ResponseCode.OK, emp.toString());
    }

    Response getManagersWithMostFactor() {
        Manager [] managersArray = company.getManagersWithMostFactor();
        JSONArray jsonArray = new JSONArray();
        if (managersArray.length > 0) {
            Arrays.stream(managersArray).forEach(m -> jsonArray.put(m.toString()));
        }
        return new Response(ResponseCode.OK, jsonArray.toString());
    }

    Response removeEmployee(String data) {
        long id = Long.parseLong(data);
        Employee empl = company.removeEmployee(id);
        return new Response(ResponseCode.OK, empl.toString());
    }
}
