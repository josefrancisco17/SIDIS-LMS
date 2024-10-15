package libraryapi.lendingservice.lendingManagement.services;

import org.mapstruct.Mapper;
import libraryapi.lendingservice.lendingManagement.model.Lending;

@Mapper(componentModel = "spring")
public abstract class EditLendingMapper {
    public abstract Lending create(CreateLendingRequest request);
}
