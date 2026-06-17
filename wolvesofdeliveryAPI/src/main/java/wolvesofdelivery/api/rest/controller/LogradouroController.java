package wolvesofdelivery.api.rest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wolvesofdelivery.api.rest.model.Logradouro;
import wolvesofdelivery.api.rest.service.LogradouroService;

@RestController
@RequestMapping("/v1/logradouros")
@CrossOrigin(origins = "*")
public class LogradouroController {

    @Autowired
    private LogradouroService logradouroService;

    @GetMapping(value = "/list", produces = "application/json")
    public ResponseEntity<List<Logradouro>> listar() {
        return new ResponseEntity<List<Logradouro>>(logradouroService.listar(), HttpStatus.OK);
    }

    @GetMapping(value = "/search/{street}", produces = "application/json")
    public ResponseEntity<List<Logradouro>> buscar(@PathVariable String rua) {
        return new ResponseEntity<List<Logradouro>>(logradouroService.buscar(rua), HttpStatus.OK);
    }

    @PostMapping(value = "/save", produces = "application/json")
    public ResponseEntity<Logradouro> salvar(@RequestBody Logradouro logradouro) {
        return new ResponseEntity<Logradouro>(logradouroService.salvar(logradouro), HttpStatus.OK);
    }
    
    @PostMapping(value = "/saveAll", produces = "application/json")
    public ResponseEntity<List<Logradouro>> salvarTodos(@RequestBody List<Logradouro> logradouros) {
        return new ResponseEntity<List<Logradouro>>(logradouroService.salvarTodos(logradouros), HttpStatus.OK);
    }

    @DeleteMapping(value = "/delete/{id}", produces = "application/json")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        logradouroService.deletar(id);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}