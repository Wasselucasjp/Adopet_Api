package br.com.alura.adopet.api.service;

import br.com.alura.adopet.api.dto.PetDto;
import br.com.alura.adopet.api.repository.PetRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class PetService {
    @Autowired
    private PetRepository Repository;
    public List<PetDto> buscarPetsDisponivel(){
        return Repository
                .findByAllAdotadoFalse()
                .stream()
                .map(PetDto::new)
                .toList();
    }
}
