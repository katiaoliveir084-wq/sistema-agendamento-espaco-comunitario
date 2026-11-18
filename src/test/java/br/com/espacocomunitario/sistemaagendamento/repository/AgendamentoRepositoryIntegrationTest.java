
package br.com.espacocomunitario.sistemaagendamento.repository;

import br.com.espacocomunitario.sistemaagendamento.Agendamento;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class AgendamentoRepositoryIntegrationTest {

    @Autowired
    private AgendamentoRepository repository;

    @Test
    void deveSalvarEBuscarAgendamentoNoBancoDeDados() {
        Agendamento agendamento = new Agendamento();
        agendamento.setNomeEvento("Reunião comunitária");
        agendamento.setResponsavel("Responsável");
        agendamento.setData(LocalDate.of(2026, 11, 20));
        agendamento.setHoraInicio(LocalTime.of(8, 0));
        agendamento.setHoraFim(LocalTime.of(10, 0));
        agendamento.setDiaInteiro(false);

        Agendamento salvo = repository.save(agendamento);

        assertNotNull(salvo.getId());

        Agendamento encontrado = repository
                .findById(salvo.getId())
                .orElseThrow();

        assertEquals("Reunião comunitária", encontrado.getNomeEvento());
        assertEquals(LocalDate.of(2026, 11, 20), encontrado.getData());
    }
}
