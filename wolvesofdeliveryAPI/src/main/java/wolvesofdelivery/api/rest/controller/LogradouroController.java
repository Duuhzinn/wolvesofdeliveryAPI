package wolvesofdelivery.api.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wolvesofdelivery.api.rest.model.Logradouro;
import wolvesofdelivery.api.rest.service.LogradouroService;
import java.util.List;

@RestController
@RequestMapping("/v1/logradouros")
@CrossOrigin(origins = "*")
public class LogradouroController {

    @Autowired
    private LogradouroService logradouroService;

    @GetMapping
    public ResponseEntity<List<Logradouro>> listar() {
        return ResponseEntity.ok(logradouroService.listar());
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Logradouro>> buscar(@RequestParam String rua) {
        return ResponseEntity.ok(logradouroService.buscar(rua));
    }

    @PostMapping
    public ResponseEntity<Logradouro> salvar(@RequestBody Logradouro logradouro) {
        return ResponseEntity.ok(logradouroService.salvar(logradouro));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        logradouroService.deletar(id);
        return ResponseEntity.ok().build();
    }
}