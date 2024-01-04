package br.com.alura.adopet.api.service;

import br.com.alura.adopet.api.dto.AbrigoDto;
import br.com.alura.adopet.api.dto.CadastroAbrigoDto;
import br.com.alura.adopet.api.exception.ValidacaoException;
import br.com.alura.adopet.api.model.Abrigo;
import br.com.alura.adopet.api.repository.AbrigoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AbrigoService {

    @Autowired
    private AbrigoRepository abrigoRepository;

    public AbrigoService(AbrigoRepository abrigoRepository) {
        this.abrigoRepository = abrigoRepository;
    }

    public List<AbrigoDto> listar(){
       return abrigoRepository
               .findAll()
               .stream()
               .map(AbrigoDto::new)
               .toList();
    }

    public void cadastrar(CadastroAbrigoDto dto){
        boolean jaCadastrado = abrigoRepository.existByNomeOrTeleFoneOrEmail(dto.nome(), dto.telefone(), dto.email());
        if (jaCadastrado){
            throw new ValidacaoException("Dados cadastrado em outro abrigo!");
        }
        abrigoRepository.save(new Abrigo(dto));
    }
}
