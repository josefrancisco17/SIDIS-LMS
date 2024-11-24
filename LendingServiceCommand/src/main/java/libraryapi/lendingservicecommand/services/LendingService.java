package libraryapi.lendingservicecommand.services;

import libraryapi.lendingservicecommand.model.Lending;


public interface LendingService {
    Lending createLending(CreateLendingRequest resource);
    Lending manageInternalLending(Lending lending);
    Lending returnBook(EditLendingRequest resource);
}
