package libraryapi.readerservice.lendingManagement.services;

import libraryapi.readerservice.lendingManagement.model.Lending;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class EditLendingMapper {
    public abstract Lending create(CreateLendingRequest request);
}
