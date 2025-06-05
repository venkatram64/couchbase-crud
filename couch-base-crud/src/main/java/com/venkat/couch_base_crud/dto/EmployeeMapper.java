package com.venkat.couch_base_crud.dto;

import com.venkat.couch_base_crud.model.Employee;

import java.util.List;

public class EmployeeMapper {
    public static Employee toDocument(EmployeeDto dto) {
        if (dto == null) return null;
        Employee employee = new Employee();
        if(dto.getId() != null) {
            employee.setId(dto.getId());
        }
        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setEmail(dto.getEmail());
        employee.setAddress(AddressMapper.toDocument(dto.getAddress()));
        employee.setPhones(PhoneMapper.toDocumentList(dto.getPhones()));
        return employee;
    }

    public static EmployeeDto toDto(Employee doc) {
        if (doc == null) return null;
        EmployeeDto dto = new EmployeeDto();
        dto.setId(doc.getId());
        dto.setFirstName(doc.getFirstName());
        dto.setLastName(doc.getLastName());
        dto.setEmail(doc.getEmail());
        dto.setAddress(AddressMapper.toDto(doc.getAddress()));
        dto.setPhones(PhoneMapper.toDtoList(doc.getPhones()));
        return dto;
    }

    public static List<EmployeeDto> toDtoList(List<Employee> entities) {
        return entities.stream().map(EmployeeMapper::toDto).toList();
    }
}
