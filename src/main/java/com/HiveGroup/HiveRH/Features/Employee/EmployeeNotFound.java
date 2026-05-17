package com.HiveGroup.HiveRH.Features.Employee;

public class EmployeeNotFound extends RuntimeException {
    public EmployeeNotFound(String message) {
        super(message);
    }
}
