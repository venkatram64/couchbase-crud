package com.venkat.couch_base_crud.dto;

import com.venkat.couch_base_crud.model.Address;

public class AddressMapper {
    public static Address toDocument(AddressDto dto) {
        if (dto == null) return null;

        Address address = new Address();
        address.setStreet(dto.getStreet());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setZip(dto.getZip());
        return address;
    }

    public static AddressDto toDto(Address doc) {
        if (doc == null) return null;

        AddressDto dto = new AddressDto();
        dto.setStreet(doc.getStreet());
        dto.setCity(doc.getCity());
        dto.setState(doc.getState());
        dto.setZip(doc.getZip());
        return dto;
    }
}
