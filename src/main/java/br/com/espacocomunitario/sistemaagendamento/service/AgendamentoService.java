
package br.com.espacocomunitario.sistemaagendamento.service;

import br.com.espacocomunitario.sistemaagendamento.Agendamento;
import br.com.espacocomunitario.sistemaagendamento.repository.AgendamentoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgendamentoService {

    private final AgendamentoRepository repository;

    public AgendamentoService(AgendamentoRepository repository) {
        this.repository = repository;
    }

    public Agendamento salvar(Agendamento novoAgendamento) {

        validarHorarios(novoAgendamento);

        List<Agendamento> agendamentosDoDia =
                repository.findByData(novoAgendamento.getData());

        for (Agendamento existente : agendamentosDoDia) {

            if (novoAgendamento.getId() != null
                    && novoAgendamento.getId().equals(existente.getId())) {
                continue;
            }

            if (novoAgendamento.isDiaInteiro()
                    || existente.isDiaInteiro()) {
                throw new IllegalArgumentException(
                        "Já existe um agendamento de dia inteiro ou ele ocupa o dia todo."
                );
            }

            boolean conflito =
                    novoAgendamento.getHoraInicio().isBefore(existente.getHoraFim())
                    && novoAgendamento.getHoraFim().isAfter(existente.getHoraInicio());

            if (conflito) {
                throw new IllegalArgumentException(
                        "Esse horário já está reservado. Escolha outro horário."
                );
            }
        }

        return repository.save(novoAgendamento);
    }

    private void validarHorarios(Agendamento agendamento) {

        if (agendamento.getData() == null) {
            throw new IllegalArgumentException(
                    "A data do evento é obrigatória."
            );
        }

        if (agendamento.isDiaInteiro()) {
            return;
        }

        if (agendamento.getHoraInicio() == null
                || agendamento.getHoraFim() == null) {
            throw new IllegalArgumentException(
                    "Informe o horário de início e término."
            );
        }

        if (!agendamento.getHoraInicio()
                .isBefore(agendamento.getHoraFim())) {
            throw new IllegalArgumentException(
                    "O horário de início deve ser anterior ao horário de término."
            );
        }
    }
}
