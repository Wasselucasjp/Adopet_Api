package br.com.alura.adopet.api.service;

import br.com.alura.adopet.api.dto.AtualizarTutorDto;
import br.com.alura.adopet.api.dto.CadastroTutorDto;
import br.com.alura.adopet.api.exception.ValidacaoException;
import br.com.alura.adopet.api.model.Tutor;
import br.com.alura.adopet.api.repository.TutorRepository;
import org.apache.tomcat.Jar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TutorService {

    @Autowired
    TutorRepository repository;

    public void cadastrar(CadastroTutorDto dto) {
        boolean jaCadastrado = repository.existsByTelefoneOrEmail(dto.telefone(), dto.email());
        if (jaCadastrado){
            throw new ValidacaoException("Dados já cadastrado em outro tutor! ");
        }repository.save(new Tutor(dto));
    }

    public void atualizar(AtualizarTutorDto dto){
        Tutor tutor = repository.getReferenceById(dto.id());
        tutor.atulizarDados(dto);
    }
}
