package duolthdelivery.api.rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import duolthdelivery.api.rest.model.MotoristaBloqueado;
import duolthdelivery.api.rest.repository.MotoristaBloqueadoRepository;

import java.util.List;

@Service
public class MotoristaBloqueadoService {

    @Autowired
    private MotoristaBloqueadoRepository motoristaBloqueadoRepository;

    public List<MotoristaBloqueado> listarPorRestaurante(Long restauranteId) {
        return motoristaBloqueadoRepository.findByRestauranteId(restauranteId);
    }

    public MotoristaBloqueado bloquear(Long restauranteId, Long motoristaId) {
        if (motoristaBloqueadoRepository.existsByRestauranteIdAndMotoristaId(restauranteId, motoristaId)) {
            return motoristaBloqueadoRepository.findByRestauranteIdAndMotoristaId(restauranteId, motoristaId).get();
        }
        MotoristaBloqueado bloqueado = new MotoristaBloqueado(restauranteId, motoristaId);
        return motoristaBloqueadoRepository.save(bloqueado);
    }

    @Transactional
    public void desbloquear(Long restauranteId, Long motoristaId) {
        motoristaBloqueadoRepository.deleteByRestauranteIdAndMotoristaId(restauranteId, motoristaId);
    }

    public boolean isMotoristaBloqueado(Long restauranteId, Long motoristaId) {
        return motoristaBloqueadoRepository.existsByRestauranteIdAndMotoristaId(restauranteId, motoristaId);
    }
}