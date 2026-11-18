package br.com.espacocomunitario.sistemaagendamento.service;

import br.com.espacocomunitario.sistemaagendamento.Agendamento;
import br.com.espacocomunitario.sistemaagendamento.repository.AgendamentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgendamentoServiceTest {

    @Mock
    private AgendamentoRepository repository;

    @InjectMocks
    private AgendamentoService service;

    private Agendamento novoAgendamento;

    @BeforeEach
    void preparar() {
        novoAgendamento = new Agendamento();
        novoAgendamento.setNomeEvento("Reunião comunitária");
        novoAgendamento.setResponsavel("Responsável");
        novoAgendamento.setData(LocalDate.of(2026, 11, 20));
        novoAgendamento.setHoraInicio(LocalTime.of(8, 0));
        novoAgendamento.setHoraFim(LocalTime.of(10, 0));
        novoAgendamento.setDiaInteiro(false);
    }

    @Test
    void deveSalvarAgendamentoComHorarioValido() {
        when(repository.findByData(novoAgendamento.getData()))
                .thenReturn(List.of());
        when(repository.save(novoAgendamento)).thenReturn(novoAgendamento);

        Agendamento resultado = service.salvar(novoAgendamento);

        assertNotNull(resultado);
        verify(repository).save(novoAgendamento);
    }

    @Test
    void deveRejeitarAgendamentoSemData() {
        novoAgendamento.setData(null);

        assertThrows(IllegalArgumentException.class,
                () -> service.salvar(novoAgendamento));

        verify(repository, never()).save(any());
    }

    @Test
    void deveRejeitarAgendamentoSemHorario() {
        novoAgendamento.setHoraInicio(null);

        assertThrows(IllegalArgumentException.class,
                () -> service.salvar(novoAgendamento));

        verify(repository, never()).save(any());
    }

    @Test
    void deveRejeitarHorarioInicialIgualAoFinal() {
        novoAgendamento.setHoraFim(LocalTime.of(8, 0));

        assertThrows(IllegalArgumentException.class,
                () -> service.salvar(novoAgendamento));

        verify(repository, never()).save(any());
    }

    @Test
    void devePermitirEventosNoMesmoDiaSemSobreposicao() {
        Agendamento existente = new Agendamento();
        existente.setData(novoAgendamento.getData());
        existente.setHoraInicio(LocalTime.of(10, 0));
        existente.setHoraFim(LocalTime.of(12, 0));
        existente.setDiaInteiro(false);

        when(repository.findByData(novoAgendamento.getData()))
                .thenReturn(List.of(existente));
        when(repository.save(novoAgendamento)).thenReturn(novoAgendamento);

        assertNotNull(service.salvar(novoAgendamento));

        verify(repository).save(novoAgendamento);
    }

    @Test
    void deveImpedirEventosComHorariosSobrepostos() {
        Agendamento existente = new Agendamento();
        existente.setData(novoAgendamento.getData());
        existente.setHoraInicio(LocalTime.of(9, 0));
        existente.setHoraFim(LocalTime.of(11, 0));
        existente.setDiaInteiro(false);

        when(repository.findByData(novoAgendamento.getData()))
                .thenReturn(List.of(existente));

        assertThrows(IllegalArgumentException.class,
                () -> service.salvar(novoAgendamento));

        verify(repository, never()).save(any());
    }

    @Test
    void deveImpedirConflitoComEventoDeDiaInteiro() {
        Agendamento existente = new Agendamento();
        existente.setData(novoAgendamento.getData());
        existente.setDiaInteiro(true);

        when(repository.findByData(novoAgendamento.getData()))
                .thenReturn(List.of(existente));

        assertThrows(IllegalArgumentException.class,
                () -> service.salvar(novoAgendamento));

        verify(repository, never()).save(any());
    }

    @Test
    void devePermitirEventoDeDiaInteiroQuandoNaoExisteOutroAgendamento() {
        novoAgendamento.setDiaInteiro(true);
        novoAgendamento.setHoraInicio(null);
        novoAgendamento.setHoraFim(null);

        when(repository.findByData(novoAgendamento.getData()))
                .thenReturn(List.of());
        when(repository.save(novoAgendamento)).thenReturn(novoAgendamento);

        assertNotNull(service.salvar(novoAgendamento));

        verify(repository).save(novoAgendamento);
    }
}