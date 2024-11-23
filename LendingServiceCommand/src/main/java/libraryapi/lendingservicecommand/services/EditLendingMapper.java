package libraryapi.lendingservicecommand.services;

import org.mapstruct.Mapper;
import libraryapi.lendingservicecommand.model.Lending;

@Mapper(componentModel = "spring")
public abstract class EditLendingMapper {
    public abstract Lending create(CreateLendingRequest request);
}
