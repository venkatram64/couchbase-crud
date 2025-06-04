package com.venkat.couch_base_crud.dto;

import com.venkat.couch_base_crud.model.Employee;

import java.util.List;

public class EmployeeMapper {
    public static Employee toEntity(EmployeeDto dto) {
        if (dto == null) return null;
        Employee employee = new Employee();
        if(dto.getId() != null) {
            employee.setId(dto.getId());
        }
        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setEmail(dto.getEmail());
        employee.setAddress(AddressMapper.toEntity(dto.getAddress()));
        employee.setPhones(PhoneMapper.toEntityList(dto.getPhones()));
        return employee;
    }

    public static EmployeeDto toDto(Employee entity) {
        if (entity == null) return null;
        EmployeeDto dto = new EmployeeDto();
        dto.setId(entity.getId());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setEmail(entity.getEmail());
        dto.setAddress(AddressMapper.toDto(entity.getAddress()));
        dto.setPhones(PhoneMapper.toDtoList(entity.getPhones()));
        return dto;
    }

    public static List<EmployeeDto> toDtoList(List<Employee> entities) {
        return entities.stream().map(EmployeeMapper::toDto).toList();
    }
}
