package com.venkat.couch_base_crud.dto;

import com.venkat.couch_base_crud.model.Phone;

import java.util.List;
import java.util.stream.Collectors;

public class PhoneMapper {
    public static Phone toDocument(PhoneDto dto) {
        if (dto == null) return null;

        Phone phone = new Phone();
        phone.setType(dto.getType());
        phone.setNumber(dto.getNumber());
        return phone;
    }

    public static List<Phone> toEntityList(List<PhoneDto> dtos) {
        if (dtos == null) return null;
        return dtos.stream().map(PhoneMapper::toDocument).collect(Collectors.toList());
    }

    public static PhoneDto toDto(Phone doc) {
        if (doc == null) return null;

        PhoneDto dto = new PhoneDto();
        dto.setType(doc.getType());
        dto.setNumber(doc.getNumber());
        return dto;
    }

    public static List<PhoneDto> toDtoList(List<Phone> entities) {
        if (entities == null) return null;
        return entities.stream().map(PhoneMapper::toDto).collect(Collectors.toList());
    }
}
