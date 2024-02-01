package br.com.alura.adopet.api.service;

import br.com.alura.adopet.api.dto.AprovacaoAdocaoDto;
import br.com.alura.adopet.api.dto.ReprovacaoAdocaoDto;
import br.com.alura.adopet.api.dto.SolicitacaoAdocaoDto;
import br.com.alura.adopet.api.model.*;
import br.com.alura.adopet.api.repository.AdocaoRepository;
import br.com.alura.adopet.api.repository.PetRepository;
import br.com.alura.adopet.api.repository.TutorRepository;
import br.com.alura.adopet.api.validation.ValidacaoSolicitacaoAdocao;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class AdocaoServiceTest {

    @InjectMocks
    private AdocaoService service;

    @Mock
    private AdocaoRepository adocaoRepository;

    @Mock
    private PetRepository petRepository;

    @Mock
    private TutorRepository tutorRepository;

    @Mock
    private EmailService emailService;

    @Spy
    private List<ValidacaoSolicitacaoAdocao> validacoes = new ArrayList<>();

    @Mock
    private ValidacaoSolicitacaoAdocao validador1;

    @Mock
    private ValidacaoSolicitacaoAdocao validador2;

    @Mock
    private Tutor tutor;

    @Mock
    private Abrigo abrigo;

    private SolicitacaoAdocaoDto dto;

    @Mock
    private Pet pet;

    @Mock
    private AprovacaoAdocaoDto aprovacaoAdocaoDto;

    @Mock
    private ReprovacaoAdocaoDto reprovacaoAdocaoDto;

    @Spy
    private Adocao adocao;

    @Captor
    private ArgumentCaptor<Adocao> adocaoArgumentCaptor;

    @Test
    void deveriaSalvarAdocaoAoSolicitar(){
        //ARRANGE
        this.dto = new SolicitacaoAdocaoDto(10l, 20l, "Motivo qualquer");
        given(petRepository.getReferenceById(dto.idPet())).willReturn(pet);
        given(tutorRepository.getReferenceById(dto.idTutor())).willReturn(tutor);
        given(pet.getAbrigo()).willReturn(abrigo);

        //ACT
        service.solicitar(dto);
        //ASSERT
        then(adocaoRepository).should().save(adocaoArgumentCaptor.capture());
        Adocao adocaoSalva = adocaoArgumentCaptor.getValue();
        Assertions.assertEquals(pet,adocaoSalva.getPet());
        Assertions.assertEquals(tutor, adocaoSalva.getTutor());
        Assertions.assertEquals(dto.motivo(), adocaoSalva.getMotivo());

    }

    @Test
    void deveriaChamarValidadoresDeAdocaoAoSolicitar(){
        //ARRANGE
        this.dto = new SolicitacaoAdocaoDto(10l, 20l, "Motivo qualquer");
        given(petRepository.getReferenceById(dto.idPet())).willReturn(pet);
        given(tutorRepository.getReferenceById(dto.idTutor())).willReturn(tutor);
        given(pet.getAbrigo()).willReturn(abrigo);

        validacoes.add(validador1);
        validacoes.add(validador2);

        //ACT
        service.solicitar(dto);
        //ASSERT
        BDDMockito.then(validador1).should().validar(dto);
        BDDMockito.then(validador2).should().validar(dto);
    }

    @Test
    void deveriaEnviarEmailAoSolicitarAdocao(){

        //ARRANGE
        SolicitacaoAdocaoDto dto = new SolicitacaoAdocaoDto(1l, 3l, "motivo teste");
        given(petRepository.getReferenceById(dto.idPet())).willReturn(pet);
        given(tutorRepository.getReferenceById(dto.idTutor())).willReturn(tutor);
        given(pet.getAbrigo()).willReturn(abrigo);

        //ACT
        service.solicitar(dto);

        //ASSERT
        then(adocaoRepository).should().save(adocaoArgumentCaptor.capture());
        Adocao adocao = adocaoArgumentCaptor.getValue();
        then(emailService).should().enviarEmail(
                adocao.getPet().getAbrigo().getEmail(),
                "Solicitação de adoção",
                "Olá " +adocao.getPet().getAbrigo().getNome() +"!\n\nUma solicitação de adoção foi registrada hoje para o pet: " +adocao.getPet().getNome() +". \nFavor avaliar para aprovação ou reprovação."
        );
    }

    @Test
    void  deveriaAprovarUmaAdocao(){
        given(adocaoRepository.getReferenceById(aprovacaoAdocaoDto.idAdocao())).willReturn(adocao);
        given(adocao.getPet()).willReturn(pet);
        given(pet.getAbrigo()).willReturn(abrigo);
        given(abrigo.getEmail()).willReturn("emailteste@teste.com.br");
        given(adocao.getTutor()).willReturn(tutor);
        given(tutor.getNome()).willReturn("NameTest");
        given(adocao.getData()).willReturn(LocalDateTime.now());

        service.aprovar(aprovacaoAdocaoDto);
        then(adocao).should().marcarComoAprovado();
        assertEquals(StatusAdocao.APROVADO, adocao.getStatus());
    }
    
    @Test
    void  deveriaReprovarUmaAdocao(){
        given(adocaoRepository.getReferenceById(aprovacaoAdocaoDto.idAdocao())).willReturn(adocao);
        given(adocao.getPet()).willReturn(pet);
        given(pet.getAbrigo()).willReturn(abrigo);
        given(abrigo.getEmail()).willReturn("emailteste@teste.com.br");
        given(adocao.getTutor()).willReturn(tutor);
        given(tutor.getNome()).willReturn("NameTest");
        given(adocao.getData()).willReturn(LocalDateTime.now());

        service.reprovar(reprovacaoAdocaoDto);
        then(adocao).should().marcarComoReprovado(reprovacaoAdocaoDto.justificativa());
        assertEquals(StatusAdocao.REPROVADO, adocao.getStatus());
    }
}