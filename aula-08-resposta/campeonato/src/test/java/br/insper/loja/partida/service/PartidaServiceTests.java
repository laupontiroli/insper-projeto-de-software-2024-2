package br.insper.loja.partida.service;

import br.insper.loja.partida.dto.EditarPartidaDTO;
import br.insper.loja.partida.dto.RetornarPartidaDTO;
import br.insper.loja.partida.dto.SalvarPartidaDTO;
import br.insper.loja.partida.exception.PartidaNaoEncontradaException;
import br.insper.loja.partida.model.Partida;
import br.insper.loja.partida.repository.PartidaRepository;
import br.insper.loja.time.model.Time;
import br.insper.loja.time.repository.TimeRepository;
import br.insper.loja.time.service.TimeService;
import jakarta.servlet.http.Part;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class PartidaServiceTests {

    @InjectMocks
    private PartidaService partidaService;

    @Mock
    private PartidaRepository partidaRepository;
    @Mock
    private TimeService timeService;



    @Test
    public void testGetPartidaWhenPartidaIsNotFound(){
        Assertions.assertThrows(PartidaNaoEncontradaException.class,()->partidaService.getPartida(0));
    }
    @Test
    public void testGetPartidaWhenPartidaIsFound() {
        // Configuração das instâncias de Partida e Time
        Partida partida = new Partida();
        Time timeMandante = new Time();
        Time timeVisitante = new Time();

        timeVisitante.setNome("Visitante");
        timeMandante.setNome("Mandante");

        partida.setVisitante(timeVisitante);
        partida.setMandante(timeMandante);

        RetornarPartidaDTO partidaDTO = RetornarPartidaDTO.getRetornarPartidaDTO(partida);

        // Configuração dos mocks
        Mockito.when(partidaRepository.findById(1)).thenReturn(Optional.of(partida));

        // Execução do método e verificação do resultado
        Assertions.assertEquals(partidaDTO.getId(), partidaService.getPartida(1).getId());
    }

    @Test
    public void testEditarPartida(){
        Partida partida = new Partida();
        Time timeMandante = new Time();
        Time timeVisitante = new Time();

        timeVisitante.setNome("Visitante");
        timeMandante.setNome("Mandante");

        partida.setVisitante(timeVisitante);
        partida.setMandante(timeMandante);

        EditarPartidaDTO partidaDTOnew = new EditarPartidaDTO();
        partidaDTOnew.setPlacarMandante(2);
        Mockito.when(partidaRepository.findById(1)).thenReturn(Optional.of(partida));
        Mockito.when(partidaRepository.save(partida)).thenReturn(partida);

        Assertions.assertEquals(partidaService.editarPartida(partidaDTOnew,1).getPlacarMandante(),2);
    }

    @Test
    public void testListaPartidasWhenFilterIsNull(){
        List<Partida> partidas = new ArrayList<>();
        Partida partida1 = new Partida();
        Partida partida2 = new Partida();

        Time timeMandante = new Time();
        Time timeVisitante = new Time();

        timeVisitante.setNome("Visitante");
        timeMandante.setNome("Mandante");

        partida1.setVisitante(timeVisitante);
        partida1.setMandante(timeMandante);
        partida2.setVisitante(timeVisitante);
        partida2.setMandante(timeMandante);

        partidas.add(partida1);
        partidas.add(partida2);

        List<RetornarPartidaDTO> partidasDTO = new ArrayList<>();
        partidasDTO.add(RetornarPartidaDTO.getRetornarPartidaDTO(partida1));
        partidasDTO.add(RetornarPartidaDTO.getRetornarPartidaDTO(partida2));

        Mockito.when(partidaRepository.findAll()).thenReturn(partidas);

        Assertions.assertEquals(partidaService.listarPartidas(null).size(),partidasDTO.size());
    }

    @Test
    public void testListaPartidasWhenFilterIsNotNull(){
        List<Partida> partidas = new ArrayList<>();
        Partida partida1 = new Partida();
        Partida partida2 = new Partida();

        Time timeMandante = new Time();
        Time timeVisitante = new Time();

        timeVisitante.setNome("Visitante");
        timeMandante.setNome("Mandante");
        timeVisitante.setIdentificador("Vs");
        timeMandante.setIdentificador("Md");

        partida1.setVisitante(timeVisitante);
        partida1.setMandante(timeMandante);
        partida2.setVisitante(timeMandante);
        partida2.setMandante(timeVisitante);

        partidas.add(partida1);
        partidas.add(partida2);

        List<RetornarPartidaDTO> partidasDTO = new ArrayList<>();
        partidasDTO.add(RetornarPartidaDTO.getRetornarPartidaDTO(partida1));
        partidasDTO.add(RetornarPartidaDTO.getRetornarPartidaDTO(partida2));

        Mockito.when(partidaRepository.findAll()).thenReturn(partidas);

        Assertions.assertEquals(partidaService.listarPartidas("Md").size(),partidasDTO.size()-1);
    }

    @Test
    public void testCadastrarPartida() {
        // Inicializar uma instância de Partida
        Partida partida = new Partida();
        partida.setId(1);
        partida.setStatus("AGENDADA");

        // Configurar times mandante e visitante
        Time timeMandante = new Time();
        Time timeVisitante = new Time();
        timeMandante.setNome("Mandante");
        timeMandante.setIdentificador("Md");
        timeMandante.setId(1);

        timeVisitante.setNome("Visitante");
        timeVisitante.setIdentificador("Vs");
        timeVisitante.setId(2);

        partida.setMandante(timeMandante);
        partida.setVisitante(timeVisitante);

        // Criar DTO de salvar partida
        SalvarPartidaDTO salvarPartidaDTO = new SalvarPartidaDTO();
        salvarPartidaDTO.setMandante(1);
        salvarPartidaDTO.setVisitante(2);

        // Mockar o comportamento do timeService e partidaRepository
        Mockito.when(timeService.getTime(1)).thenReturn(timeMandante);
        Mockito.when(timeService.getTime(2)).thenReturn(timeVisitante);
        Mockito.when(partidaRepository.save(Mockito.any(Partida.class))).thenReturn(partida);

        // Verificar se o ID da partida criada é o esperado
        Assertions.assertEquals(1, partidaService.cadastrarPartida(salvarPartidaDTO).getId());
    }
}
