package wolvesofdelivery.api.rest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import wolvesofdelivery.api.rest.model.MotoristaBloqueado;
import wolvesofdelivery.api.rest.repository.MotoristaBloqueadoRepository;

@Service
public class MotoristaBloqueadoService {

	@Autowired
    private MotoristaBloqueadoRepository motoristaBloqueadoRepository;
	@Autowired
	private MotoristaBloqueado motoristaBloqueado;
	
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
	
	public void desbloquear(Long restauranteId, Long motoristaId) {
        motoristaBloqueadoRepository.deleteByRestauranteIdAndMotoristaId(restauranteId, motoristaId);
    }
	
	public boolean isMotoristaBloqueado(Long restauranteId, Long motoristaId) {
        return motoristaBloqueadoRepository.existsByRestauranteIdAndMotoristaId(restauranteId, motoristaId);
    }
	
}
