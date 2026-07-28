package duolthdelivery.api.rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import duolthdelivery.api.rest.model.Logradouro;
import duolthdelivery.api.rest.repository.LogradouroRepository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

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
    
    public List<Logradouro> salvarTodos(List<Logradouro> logradouros) {
        logradouros.forEach(l -> {
            l.setRua(l.getRua().toUpperCase());
            l.setBairro(l.getBairro().toUpperCase());
            l.setCidade(l.getCidade().toUpperCase());
        });
        return logradouroRepository.saveAll(logradouros);
    }

    public void deletar(Long id) {
        logradouroRepository.deleteById(id);
    }
    
    public Page<Logradouro> listarPaginado(int pagina) {
        return logradouroRepository.findAll(PageRequest.of(pagina, 20));
    }

    public Page<Logradouro> buscarPaginado(String rua, int pagina) {
        return logradouroRepository.findByRuaContainingIgnoreCase(rua, PageRequest.of(pagina, 20));
    }
}