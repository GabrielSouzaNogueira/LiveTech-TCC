package by.gabriel.gerenciadorEstoque.Api.Controller;

import by.gabriel.gerenciadorEstoque.Api.DTO.Equipamento.EquipamentoDTO;
import by.gabriel.gerenciadorEstoque.Api.DTO.Equipamento.EquipamentoResumoDTO;
import by.gabriel.gerenciadorEstoque.Api.DTO.Equipamento.UpdateEquipamentoDTO;
import by.gabriel.gerenciadorEstoque.Api.DTO.Response.ResponseDTO;
import by.gabriel.gerenciadorEstoque.Model.Equipamento.Equipamento;
import by.gabriel.gerenciadorEstoque.Services.EquipamentoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
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

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<Equipamento> atualizar(@PathVariable Long id, @RequestBody UpdateEquipamentoDTO dto, @RequestHeader("X-Usuario-Logado") String usuarioLogado) {

        Equipamento equipamentoAtualizado = equipamentoService.atualizar(id, dto, usuarioLogado);

        return ResponseEntity.status(HttpStatus.OK).body(equipamentoAtualizado);
    }

    @PutMapping("/deletar/{id}")
    public ResponseEntity<ResponseDTO> atualizar(@PathVariable Long id, @RequestHeader("X-Usuario-Logado") String usuarioLogado) {

        Boolean equipamentoInativado = equipamentoService.deletar(id, usuarioLogado);

        if (equipamentoInativado) {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO(
                    true,
                    "Produto inativado com sucesso",
                    Instant.now().toString()
            ));
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDTO(
                    false,
                    "Algo deu errado na inativação do equipamento",
                    Instant.now().toString()
            ));
        }
    }

    @GetMapping("/buscar-modal")
    public ResponseEntity<List<EquipamentoResumoDTO>> buscarParaModal(
            @RequestParam(value = "termo", required = false, defaultValue = "") String termo,
            @RequestHeader("X-Usuario-Logado") String usuarioLogado) {

        List<EquipamentoResumoDTO> equipamentos = equipamentoService.buscarParaModal(termo, usuarioLogado);
        return ResponseEntity.status(HttpStatus.OK).body(equipamentos);
    }
}
