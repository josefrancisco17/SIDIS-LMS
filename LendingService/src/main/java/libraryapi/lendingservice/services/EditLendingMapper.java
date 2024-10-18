package libraryapi.lendingservice.services;

import org.mapstruct.Mapper;
import libraryapi.lendingservice.model.Lending;

@Mapper(componentModel = "spring")
public abstract class EditLendingMapper {
    public abstract Lending create(CreateLendingRequest request);
}
