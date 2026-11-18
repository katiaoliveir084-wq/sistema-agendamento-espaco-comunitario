package br.com.espacocomunitario.sistemaagendamento;

import br.com.espacocomunitario.sistemaagendamento.repository.AgendamentoRepository;
import br.com.espacocomunitario.sistemaagendamento.service.AgendamentoService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/agendamentos")
public class AgendamentoController {

    private final AgendamentoService service;
    private final AgendamentoRepository repository;

    public AgendamentoController(
            AgendamentoService service,
            AgendamentoRepository repository) {
        this.service = service;
        this.repository = repository;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("agendamentos", repository.findAll());
        return "agendamentos";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("agendamento", new Agendamento());
        return "formulario";
    }

    @PostMapping("/salvar")
    public String salvar(
            @ModelAttribute Agendamento agendamento,
            Model model,
            RedirectAttributes redirect) {

        try {
            service.salvar(agendamento);
            redirect.addFlashAttribute(
                "sucesso", "Agendamento salvo com sucesso!");
            return "redirect:/agendamentos";

        } catch (IllegalArgumentException e) {
            model.addAttribute("agendamento", agendamento);
            model.addAttribute("erro", e.getMessage());
            return "formulario";
        }
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Agendamento agendamento = repository.findById(id)
            .orElseThrow(() ->
                new IllegalArgumentException("Agendamento não encontrado."));

        model.addAttribute("agendamento", agendamento);
        return "formulario";
    }

    @PostMapping("/excluir/{id}")
    public String excluir(
            @PathVariable Long id,
            RedirectAttributes redirect) {

        repository.deleteById(id);
        redirect.addFlashAttribute(
            "sucesso", "Agendamento excluído com sucesso!");

        return "redirect:/agendamentos";
    }
}
