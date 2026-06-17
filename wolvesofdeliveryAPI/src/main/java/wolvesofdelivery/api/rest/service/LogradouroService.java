package wolvesofdelivery.api.rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wolvesofdelivery.api.rest.model.Logradouro;
import wolvesofdelivery.api.rest.repository.LogradouroRepository;
import java.util.List;

@Service
public class LogradouroService {

    @Autowired
    private LogradouroRepository logradouroRepository;

    public List<Logradouro> listar() {
        return logradouroRepository.findAll();
    }

    public List<Logradouro> buscar(String rua) {
        return logradouroRepository.findByRuaContainingIgnoreCase(rua);
    }

    public Logradouro salvar(Logradouro logradouro) {
        logradouro.setRua(logradouro.getRua().toUpperCase());
        logradouro.setBairro(logradouro.getBairro().toUpperCase());
        logradouro.setCidade(logradouro.getCidade().toUpperCase());
        return logradouroRepository.save(logradouro);
    }

    public void deletar(Long id) {
        logradouroRepository.deleteById(id);
    }
}