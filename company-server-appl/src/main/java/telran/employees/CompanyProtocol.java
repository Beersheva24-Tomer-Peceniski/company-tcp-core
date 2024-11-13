package telran.employees;

import java.lang.reflect.Method;
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
        String methodName = request.requestType();
        String methodArgument = request.requestData();
        Response response = null;
        Method[] methodsArray = CompanyProtocol.class.getDeclaredMethods();
        Method method = methodSearch(methodsArray, methodName);
        if (method == null) {
            response = new Response(ResponseCode.WRONG_TYPE, methodName + ": Wrong type");
        } else {
            try {
                int numArguments = method.getParameterCount();
                response = switch (numArguments) {
                    case 0 -> (Response) method.invoke(this);
                    case 1 -> (Response) method.invoke(this, methodArgument);
                    default -> new Response(ResponseCode.WRONG_TYPE, methodName + ": Wrong type");
                };
            } catch (Exception e) {
                response = new Response(ResponseCode.WRONG_DATA, e.getMessage());
            }
        }
        return response;
    }

    private Method methodSearch(Method[] methodsArray, String methodName) {
        Method res = null;
        for (Method method : methodsArray) {
            if (method.getName().equals(methodName)) {
                res = method;
            }
        }
        return res;
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
        String[] departmentsArray = company.getDepartments();
        JSONArray jsonArray = new JSONArray(departmentsArray);
        return new Response(ResponseCode.OK, jsonArray.toString());
    }

    Response getEmployee(String data) {
        long id = Long.parseLong(data);
        Employee emp = company.getEmployee(id);
        return new Response(ResponseCode.OK, emp.toString());
    }

    Response getManagersWithMostFactor() {
        Manager[] managersArray = company.getManagersWithMostFactor();
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
