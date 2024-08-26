package br.insper.aposta.aposta.controller;

import br.insper.aposta.aposta.service.ApostaService;
import br.insper.aposta.aposta.model.Aposta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/aposta")
public class ApostaController {

    @Autowired
    private ApostaService apostaService;

    @GetMapping
    public List<Aposta> listar(@RequestParam(required = false)String status) {
        return apostaService.listar(status);
    }

    @PostMapping
    public void salvar(@RequestBody Aposta aposta) {
        apostaService.salvar(aposta);
    }

    @GetMapping("/{id}")
    public Aposta getAposta(@PathVariable String id) {
        return apostaService.getAposta(id);
    }
}
