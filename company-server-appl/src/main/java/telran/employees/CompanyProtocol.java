package telran.employees;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONObject;

import telran.net.*;

public class CompanyProtocol implements Protocol {
    private TreeMap<Long, Employee> employees = new TreeMap<>();
    private HashMap<String, List<Employee>> employeesDepartment = new HashMap<>();
    private TreeMap<Float, List<Manager>> managersFactor = new TreeMap<>();

    @Override
    public Response getResponse(Request request) {
        String type = request.requestType();
        String data = request.requestData();
        Response response = null;
        try {
            response = switch (type) {
                case "iterator" -> iterator();
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

    Response iterator() {
        JSONArray jsonArray = new JSONArray();
        employees.values().stream().forEach(e -> jsonArray.put(new JSONObject(e.toString())));
        ResponseCode responseCode = ResponseCode.OK;
        String responseData = jsonArray.toString();
        Response response = new Response(responseCode, responseData);
        return response;
    }

    Response addEmployee(String data) {
        Employee empl = Employee.getEmployeeFromJSON(data);
        long id = empl.getId();
        if (employees.putIfAbsent(id, empl) != null) {
            throw new IllegalStateException("Already exists employee " + id);
        }
        addIndexMaps(empl);
        return new Response(ResponseCode.OK, "");
    }

    private void addIndexMaps(Employee empl) {
        employeesDepartment.computeIfAbsent(empl.getDepartment(), k -> new ArrayList<>()).add(empl);
        if (empl instanceof Manager manager) {
            managersFactor.computeIfAbsent(manager.getFactor(), k -> new ArrayList<>()).add(manager);
        }
    }

    Response getDepartmentBudget(String department) {
        int departmentBudget = employeesDepartment.getOrDefault(department, Collections.emptyList())
                .stream().mapToInt(Employee::computeSalary).sum();
        return new Response(ResponseCode.OK, String.valueOf(departmentBudget));
    }

    Response getDepartments() {
        JSONArray jsonArray = new JSONArray(employeesDepartment.keySet());
        return new Response(ResponseCode.OK, jsonArray.toString());
    }

    Response getEmployee(String data) {
        long id = Long.parseLong(data);
        Employee emp = employees.get(id);
        return new Response(ResponseCode.OK, emp.toString());
    }

    Response getManagersWithMostFactor() {
        JSONArray jsonArray = new JSONArray();
        if (!managersFactor.isEmpty()) {
            managersFactor.lastEntry().getValue().forEach(m -> jsonArray.put(m.toString()));
        }
        return new Response(ResponseCode.OK, jsonArray.toString());
    }

    Response removeEmployee(String data) {
        long id = Long.parseLong(data);
        Employee empl = employees.remove(id);
        if (empl == null) {
            throw new NoSuchElementException("Not found employee " + id);
        }
        removeFromIndexMaps(empl);
        return new Response(ResponseCode.OK, empl.toString());
    }

    private void removeFromIndexMaps(Employee empl) {
        removeIndexMap(empl.getDepartment(), employeesDepartment, empl);
        if (empl instanceof Manager manager) {
            removeIndexMap(manager.getFactor(), managersFactor, manager);
        }
    }

    private <K, V extends Employee> void removeIndexMap(K key, Map<K, List<V>> map, V empl) {
        List<V> list = map.get(key);
        list.remove(empl);
        if (list.isEmpty()) {
            map.remove(key);
        }
    }
}
