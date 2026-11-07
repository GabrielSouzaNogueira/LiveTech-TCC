package by.gabriel.gerenciadorEstoque.Api.Controller;

import by.gabriel.gerenciadorEstoque.Api.DTO.Response.ResponseDTO;
import by.gabriel.gerenciadorEstoque.Model.Equipamento.Equipamento;
import by.gabriel.gerenciadorEstoque.Services.EquipamentoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/equipamento")
@CrossOrigin(originPatterns = "*")
public class EquipamentoController {

    private final EquipamentoService equipamentoService;

    public EquipamentoController(EquipamentoService equipamentoService) {
        this.equipamentoService = equipamentoService;
    }

    @RequestMapping("/buscar/{id}")
    public ResponseEntity<Equipamento> buscarPorId(@RequestHeader Long id, @RequestHeader("X-Usuario-Logado") String usuarioLogado){

        Equipamento equipamento = equipamentoService.buscarPorId(id, usuarioLogado);
        return ResponseEntity.status(HttpStatus.OK).body(equipamento);

    }

}
