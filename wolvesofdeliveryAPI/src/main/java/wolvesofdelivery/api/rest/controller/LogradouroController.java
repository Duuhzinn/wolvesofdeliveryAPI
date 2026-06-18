package wolvesofdelivery.api.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
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

    @GetMapping(value = "/list/{pagina}", produces = "application/json")
    public ResponseEntity<Page<Logradouro>> listar(@PathVariable int pagina) {
        return new ResponseEntity<>(logradouroService.listarPaginado(pagina), HttpStatus.OK);
    }
    
    @GetMapping(value = "/listAll", produces = "application/json")
    public ResponseEntity<List<Logradouro>> listarTodos() {
        return new ResponseEntity<>(logradouroService.listar(), HttpStatus.OK);
    }

    @GetMapping(value = "/search/{rua}/{pagina}", produces = "application/json")
    public ResponseEntity<Page<Logradouro>> buscar(@PathVariable String rua, @PathVariable int pagina) {
        return new ResponseEntity<>(logradouroService.buscarPaginado(rua, pagina), HttpStatus.OK);
    }

    @PostMapping(value = "/save", produces = "application/json")
    public ResponseEntity<Logradouro> salvar(@RequestBody Logradouro logradouro) {
        return new ResponseEntity<>(logradouroService.salvar(logradouro), HttpStatus.OK);
    }

    @PostMapping(value = "/saveAll", produces = "application/json")
    public ResponseEntity<List<Logradouro>> salvarTodos(@RequestBody List<Logradouro> logradouros) {
        return new ResponseEntity<>(logradouroService.salvarTodos(logradouros), HttpStatus.OK);
    }

    @DeleteMapping(value = "/delete/{id}", produces = "application/json")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        logradouroService.deletar(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}