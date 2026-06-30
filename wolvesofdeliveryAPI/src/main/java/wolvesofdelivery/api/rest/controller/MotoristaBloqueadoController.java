package wolvesofdelivery.api.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wolvesofdelivery.api.rest.model.MotoristaBloqueado;
import wolvesofdelivery.api.rest.service.MotoristaBloqueadoService;

import java.util.List;

@RestController
@RequestMapping("/v1/motoristasBloqueados")
public class MotoristaBloqueadoController {

    @Autowired
    private MotoristaBloqueadoService motoristaBloqueadoService;

	@CacheEvict(value = "cacheUser", allEntries = true)
	@CachePut("cacheUser")
    @GetMapping("/restaurante/{restauranteId}")
    public ResponseEntity<List<MotoristaBloqueado>> listarPorRestaurante(@PathVariable Long restauranteId) {
        return ResponseEntity.ok(motoristaBloqueadoService.listarPorRestaurante(restauranteId));
    }

	@CacheEvict(value = "cacheUser", allEntries = true)
	@CachePut("cacheUser")
    @PostMapping("/save/{restauranteId}/{motoristaId}")
    public ResponseEntity<MotoristaBloqueado> bloquear(@PathVariable Long restauranteId,
                                                         @PathVariable Long motoristaId) {
        return ResponseEntity.ok(motoristaBloqueadoService.bloquear(restauranteId, motoristaId));
    }

	@CacheEvict(value = "cacheUser", allEntries = true)
	@CachePut("cacheUser")
    @DeleteMapping("/delete/{restauranteId}/{motoristaId}")
    public ResponseEntity<Void> desbloquear(@PathVariable Long restauranteId,
                                             @PathVariable Long motoristaId) {
        motoristaBloqueadoService.desbloquear(restauranteId, motoristaId);
        return ResponseEntity.noContent().build();
    }
}