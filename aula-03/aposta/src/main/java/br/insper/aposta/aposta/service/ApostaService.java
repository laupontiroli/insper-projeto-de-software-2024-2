package br.insper.aposta.aposta.service;

import br.insper.aposta.aposta.dto.RetornarPartidaDTO;
import br.insper.aposta.aposta.model.Aposta;
import br.insper.aposta.aposta.reporitory.ApostaRepository;
import com.fasterxml.jackson.databind.JsonSerializer;
import jakarta.validation.constraints.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ApostaService {

    @Autowired
    private ApostaRepository apostaRepository;

    public void salvar(Aposta aposta) {
        aposta.setId(UUID.randomUUID().toString());

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<RetornarPartidaDTO> partida = restTemplate.getForEntity(
                "http://54.162.220.11:8080/partida/" + aposta.getIdPartida(),
                RetornarPartidaDTO.class);

        if (partida.getStatusCode().is2xxSuccessful())  {
            if (partida.getBody().getStatus().equals("REALIZADA")){
                throw new RuntimeException("Partida já realizada");
            }
            apostaRepository.save(aposta);
        }
        else {
            throw new RuntimeException("Partida não encontrada");
        }
    }

    public List<Aposta> listar(String status) {
        if (status == null) {
            return apostaRepository.findAll();
        }
        return apostaRepository.findByStatus(status);
    }

    public Aposta getAposta(String id) {

        Optional<Aposta> op = apostaRepository.findById(id);

        if (op.isPresent()){
            Aposta aposta = op.get();
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<RetornarPartidaDTO> partida = restTemplate.getForEntity(
                    "http://54.162.220.11:8080/partida/" + aposta.getIdPartida(),
                    RetornarPartidaDTO.class);

            if (partida.getStatusCode().is2xxSuccessful())  {
                if (partida.getBody().getStatus().equals("REALIZADA")){
                    if (partida.getBody().getPlacarVisitante() > partida.getBody().getPlacarMandante() && aposta.getResultado().equals("VITORIA_VISITANTE")){
                        aposta.setStatus("GANHOU");
                    }
                    else if  (partida.getBody().getPlacarVisitante() < partida.getBody().getPlacarMandante() && aposta.getResultado().equals("VITORIA_MANDANTE")){
                        aposta.setStatus("GANHOU");
                    }
                    else if (partida.getBody().getPlacarVisitante() == partida.getBody().getPlacarMandante() && aposta.getResultado().equals("EMPATE")){
                        aposta.setStatus("GANHOU");
                    }
                    else {
                        aposta.setStatus("PERDIDA");
                    }
                }
            }
            return aposta;
        }
        throw new RuntimeException("Nao achou aposta");
    }
}



