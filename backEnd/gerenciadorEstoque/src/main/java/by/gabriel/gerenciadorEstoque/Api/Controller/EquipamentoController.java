package by.gabriel.gerenciadorEstoque.Api.Controller;

import by.gabriel.gerenciadorEstoque.Api.DTO.Equipamento.EquipamentoDTO;
import by.gabriel.gerenciadorEstoque.Model.Equipamento.Equipamento;
import by.gabriel.gerenciadorEstoque.Services.EquipamentoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/equipamento")
@CrossOrigin(originPatterns = "*")
public class EquipamentoController {

    private final EquipamentoService equipamentoService;

    public EquipamentoController(EquipamentoService equipamentoService) {
        this.equipamentoService = equipamentoService;
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<Equipamento> buscarPorId(@PathVariable Long id, @RequestHeader("X-Usuario-Logado") String usuarioLogado){

        Equipamento equipamento = equipamentoService.buscarPorId(id, usuarioLogado);
        return ResponseEntity.status(HttpStatus.OK).body(equipamento);

    }

    @GetMapping("/listar")
    public ResponseEntity<List<Equipamento>> listarEquipamento() {

        List<Equipamento> equipamentos = equipamentoService.listarEquipamento();

        return ResponseEntity.status(HttpStatus.OK).body(equipamentos);
    }

    @PostMapping("/cadastro")
    public ResponseEntity<Equipamento> cadastrar(@RequestBody EquipamentoDTO dto, @RequestHeader("X-Usuario-Logado") String usuarioLogado) {

        Equipamento equipamento = equipamentoService.cadastrar(dto, usuarioLogado);
        return ResponseEntity.status(HttpStatus.CREATED).body(equipamento);

    }

}
