package br.insper.aposta.aposta;

import br.insper.aposta.partida.PartidaNaoEncontradaException;
import br.insper.aposta.partida.PartidaNaoRealizadaException;
import br.insper.aposta.partida.PartidaService;
import br.insper.aposta.partida.RetornarPartidaDTO;
import jakarta.validation.constraints.Null;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ApostaServiceTests {

    @InjectMocks
    ApostaService apostaService;

    @Mock
    PartidaService partidaService;

    @Mock
    ApostaRepository apostaRepository;

    @Test
    public void testGetApostaWhenApostaIsNull() {

        Mockito.when(apostaRepository.findById("1"))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(ApostaNaoEncontradaException.class,
                () -> apostaService.getAposta("1"));
    }

    @Test
    public void testGetApostaWhenApostaIsNotNullStatusNotRealizada() {

        Aposta aposta = new Aposta();
        aposta.setStatus("GANHOU");

        Mockito.when(apostaRepository.findById("1"))
                .thenReturn(Optional.of(aposta));

        Aposta apostaRetorno = apostaService.getAposta("1");
        Assertions.assertNotNull(apostaRetorno);

    }

    @Test
    public void testGetApostaWhenApostaIsNotNullStatusRealizadaAndPartidaNotFound() {
        // Arrange

        Aposta aposta = new Aposta();
        aposta.setStatus("REALIZADA");
        aposta.setIdPartida(0);

        Mockito.when(apostaRepository.findById("1"))
                .thenReturn(Optional.of(aposta));

        Mockito.when(partidaService.getPartida(0))
                .thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        // Act
        Assertions.assertThrows(PartidaNaoEncontradaException.class, () -> apostaService.getAposta("1"));
    }
    @Test
    public void testGetApostaWhenApostaIsNotNullStatusRealizadaAndPartidaNotRealizada() {
        // Arrange

        Aposta aposta = new Aposta();
        aposta.setStatus("REALIZADA");
        aposta.setIdPartida(1);

        RetornarPartidaDTO retornarPartidaDTO = new RetornarPartidaDTO();
        retornarPartidaDTO.setStatus("AGENDADA");

        ResponseEntity<RetornarPartidaDTO> partidaDTOResponseEntity= new ResponseEntity<>(retornarPartidaDTO,HttpStatus.OK);

        Mockito.when(apostaRepository.findById("1"))
                .thenReturn(Optional.of(aposta));

        Mockito.when(partidaService.getPartida(1))
                .thenReturn(partidaDTOResponseEntity);

        // Assert
        Assertions.assertThrows(PartidaNaoRealizadaException.class,()-> apostaService.getAposta("1"));
    }
    @Test
    public void testGetApostaWhenApostaIsNotNullStatusRealizadaAndGanhouMandante() {
        // Arrange

        Aposta aposta = new Aposta();
        aposta.setStatus("REALIZADA");
        aposta.setIdPartida(1);
        aposta.setResultado("VITORIA_MANDANTE");

        RetornarPartidaDTO retornarPartidaDTO = new RetornarPartidaDTO();
        retornarPartidaDTO.setStatus("REALIZADA");
        retornarPartidaDTO.setPlacarMandante(2);
        retornarPartidaDTO.setPlacarVisitante(1);

        ResponseEntity<RetornarPartidaDTO> partidaDTOResponseEntity= new ResponseEntity<>(retornarPartidaDTO,HttpStatus.OK);

        Mockito.when(apostaRepository.findById("1"))
                .thenReturn(Optional.of(aposta));

        Mockito.when(partidaService.getPartida(1))
                .thenReturn(partidaDTOResponseEntity);

        // Mock para salvar a aposta atualizada
        Mockito.when(apostaRepository.save(Mockito.any(Aposta.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act

        Aposta apostaRetorno = apostaService.getAposta("1");

        // Assert
        Assertions.assertNotNull(apostaRetorno);
        Assertions.assertEquals("GANHOU", apostaRetorno.getStatus());
    }

    @Test
    public void testGetApostaWhenApostaIsNotNullStatusRealizadaAndPerdeuMandante() {
        // Arrange

        Aposta aposta = new Aposta();
        aposta.setStatus("REALIZADA");
        aposta.setIdPartida(1);
        aposta.setResultado("VITORIA_MANDANTE");

        RetornarPartidaDTO retornarPartidaDTO = new RetornarPartidaDTO();
        retornarPartidaDTO.setStatus("REALIZADA");
        retornarPartidaDTO.setPlacarMandante(1);
        retornarPartidaDTO.setPlacarVisitante(1);

        ResponseEntity<RetornarPartidaDTO> partidaDTOResponseEntity= new ResponseEntity<>(retornarPartidaDTO,HttpStatus.OK);

        Mockito.when(apostaRepository.findById("1"))
                .thenReturn(Optional.of(aposta));

        Mockito.when(partidaService.getPartida(1))
                .thenReturn(partidaDTOResponseEntity);

        // Mock para salvar a aposta atualizada
        Mockito.when(apostaRepository.save(Mockito.any(Aposta.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act

        Aposta apostaRetorno = apostaService.getAposta("1");

        // Assert
        Assertions.assertNotNull(apostaRetorno);
        Assertions.assertEquals("PERDEU", apostaRetorno.getStatus());
    }

    @Test
    public void testGetApostaWhenApostaIsNotNullStatusRealizadaAndGanhouVisitante() {
        // Arrange

        Aposta aposta = new Aposta();
        aposta.setStatus("REALIZADA");
        aposta.setIdPartida(1);
        aposta.setResultado("VITORIA_VISITANTE");

        RetornarPartidaDTO retornarPartidaDTO = new RetornarPartidaDTO();
        retornarPartidaDTO.setStatus("REALIZADA");
        retornarPartidaDTO.setPlacarMandante(1);
        retornarPartidaDTO.setPlacarVisitante(2);

        ResponseEntity<RetornarPartidaDTO> partidaDTOResponseEntity= new ResponseEntity<>(retornarPartidaDTO,HttpStatus.OK);

        Mockito.when(apostaRepository.findById("1"))
                .thenReturn(Optional.of(aposta));

        Mockito.when(partidaService.getPartida(1))
                .thenReturn(partidaDTOResponseEntity);

        // Mock para salvar a aposta atualizada
        Mockito.when(apostaRepository.save(Mockito.any(Aposta.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act

        Aposta apostaRetorno = apostaService.getAposta("1");

        // Assert
        Assertions.assertNotNull(apostaRetorno);
        Assertions.assertEquals("GANHOU", apostaRetorno.getStatus());
    }

    @Test
    public void testGetApostaWhenApostaIsNotNullStatusRealizadaAndPerdeuVisitante() {
        // Arrange

        Aposta aposta = new Aposta();
        aposta.setStatus("REALIZADA");
        aposta.setIdPartida(1);
        aposta.setResultado("VITORIA_VISITANTE");

        RetornarPartidaDTO retornarPartidaDTO = new RetornarPartidaDTO();
        retornarPartidaDTO.setStatus("REALIZADA");
        retornarPartidaDTO.setPlacarMandante(1);
        retornarPartidaDTO.setPlacarVisitante(1);

        ResponseEntity<RetornarPartidaDTO> partidaDTOResponseEntity= new ResponseEntity<>(retornarPartidaDTO,HttpStatus.OK);

        Mockito.when(apostaRepository.findById("1"))
                .thenReturn(Optional.of(aposta));

        Mockito.when(partidaService.getPartida(1))
                .thenReturn(partidaDTOResponseEntity);

        // Mock para salvar a aposta atualizada
        Mockito.when(apostaRepository.save(Mockito.any(Aposta.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act

        Aposta apostaRetorno = apostaService.getAposta("1");

        // Assert
        Assertions.assertNotNull(apostaRetorno);
        Assertions.assertEquals("PERDEU", apostaRetorno.getStatus());
    }
    @Test
    public void testGetApostaWhenApostaIsNotNullStatusRealizadaAndGanhouEmpate() {
        // Arrange

        Aposta aposta = new Aposta();
        aposta.setStatus("REALIZADA");
        aposta.setIdPartida(1);
        aposta.setResultado("EMPATE");

        RetornarPartidaDTO retornarPartidaDTO = new RetornarPartidaDTO();
        retornarPartidaDTO.setStatus("REALIZADA");
        retornarPartidaDTO.setPlacarMandante(1);
        retornarPartidaDTO.setPlacarVisitante(1);

        ResponseEntity<RetornarPartidaDTO> partidaDTOResponseEntity= new ResponseEntity<>(retornarPartidaDTO,HttpStatus.OK);

        Mockito.when(apostaRepository.findById("1"))
                .thenReturn(Optional.of(aposta));

        Mockito.when(partidaService.getPartida(1))
                .thenReturn(partidaDTOResponseEntity);

        // Mock para salvar a aposta atualizada
        Mockito.when(apostaRepository.save(Mockito.any(Aposta.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act

        Aposta apostaRetorno = apostaService.getAposta("1");

        // Assert
        Assertions.assertNotNull(apostaRetorno);
        Assertions.assertEquals("GANHOU", apostaRetorno.getStatus());
    }

    @Test
    public void testGetApostaWhenApostaIsNotNullStatusRealizadaAndPerdeuEmpate() {
        // Arrange

        Aposta aposta = new Aposta();
        aposta.setStatus("REALIZADA");
        aposta.setIdPartida(1);
        aposta.setResultado("EMPATE");

        RetornarPartidaDTO retornarPartidaDTO = new RetornarPartidaDTO();
        retornarPartidaDTO.setStatus("REALIZADA");
        retornarPartidaDTO.setPlacarMandante(1);
        retornarPartidaDTO.setPlacarVisitante(0);

        ResponseEntity<RetornarPartidaDTO> partidaDTOResponseEntity= new ResponseEntity<>(retornarPartidaDTO,HttpStatus.OK);

        Mockito.when(apostaRepository.findById("1"))
                .thenReturn(Optional.of(aposta));

        Mockito.when(partidaService.getPartida(1))
                .thenReturn(partidaDTOResponseEntity);

        // Mock para salvar a aposta atualizada
        Mockito.when(apostaRepository.save(Mockito.any(Aposta.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act

        Aposta apostaRetorno = apostaService.getAposta("1");

        // Assert
        Assertions.assertNotNull(apostaRetorno);
        Assertions.assertEquals("PERDEU", apostaRetorno.getStatus());
    }
    @Test
    public void testListApostasWhenListIsNull() {

    Mockito.when(apostaRepository.findAll())
            .thenReturn(null);

    List<Aposta> apostasRetorno = apostaService.listar();
    Assertions.assertNull(apostasRetorno);

}
    @Test
    public void testSalvarApostaWhenPartidaIsFound() {
        // Arrange
        Aposta aposta = new Aposta();
        aposta.setIdPartida(1);

        RetornarPartidaDTO retornarPartidaDTO = new RetornarPartidaDTO();
        ResponseEntity<RetornarPartidaDTO> partidaEntity = new ResponseEntity<>(retornarPartidaDTO, HttpStatus.OK);

        Mockito.when(partidaService.getPartida(1)).thenReturn(partidaEntity);
        Mockito.when(apostaRepository.save(aposta)).thenReturn(aposta);

        // Act
        Aposta apostaRetorno = apostaService.salvar(aposta);

        // Assert
        Assertions.assertNotNull(apostaRetorno);
        Assertions.assertEquals("REALIZADA", apostaRetorno.getStatus());
        Assertions.assertNotNull(apostaRetorno.getDataAposta());
    }


    @Test
    public void testSalvarApostaWhenPartidaIsNotFound() {
        // Arrange
        Aposta aposta = new Aposta();
        aposta.setIdPartida(0);

        ResponseEntity<RetornarPartidaDTO> partidaEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);

        Mockito.when(partidaService.getPartida(0)).thenReturn(partidaEntity);

        // Act & Assert
        Assertions.assertThrows(PartidaNaoEncontradaException.class, () -> apostaService.salvar(aposta));
    }
}




