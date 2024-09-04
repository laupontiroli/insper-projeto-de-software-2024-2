package br.insper.functional.musicas;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MusicaService {

    @Autowired
    private MusicaRepository musicaRepository;
    public List<MusicaDTO> getAllMusicas(String nome, String artista) {
        List<Musica> musicas = musicaRepository.findAll();
        //        for (Musica musica : musicas) {
        //            dtos.add(MusicaDTO.convert(musica));
        //        }
        //        return dtos;
        if (nome != null){
            musicas.stream().map(musica -> musica.getNome().contains(nome)).toList();
        }
        if (artista != null){
            musicas.stream().map(musica -> musica.getArtista().contains(artista)).toList();
        }
        return musicas.stream().map(musica -> MusicaDTO.convert(musica)).toList();
    }

}
