package com.venkat.couch_base_crud.dto;

import com.venkat.couch_base_crud.model.Address;

public class AddressMapper {
    public static Address toEntity(AddressDto dto) {
        if (dto == null) return null;

        Address address = new Address();
        address.setStreet(dto.getStreet());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setZip(dto.getZip());
        return address;
    }

    public static AddressDto toDto(Address entity) {
        if (entity == null) return null;

        AddressDto dto = new AddressDto();
        dto.setStreet(entity.getStreet());
        dto.setCity(entity.getCity());
        dto.setState(entity.getState());
        dto.setZip(entity.getZip());
        return dto;
    }
}
