package libraryapi.lendingservicequery.services;

import org.mapstruct.Mapper;
import libraryapi.lendingservicequery.model.Lending;

@Mapper(componentModel = "spring")
public abstract class EditLendingMapper {
    public abstract Lending create(CreateLendingRequest request);
}
