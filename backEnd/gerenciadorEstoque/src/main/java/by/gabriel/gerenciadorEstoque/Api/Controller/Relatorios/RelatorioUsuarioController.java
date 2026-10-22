package by.gabriel.gerenciadorEstoque.Api.Controller.Relatorios;

import by.gabriel.gerenciadorEstoque.Api.DTO.Relatorio.Usuario.UsuariosInativadosDTO;
import by.gabriel.gerenciadorEstoque.Services.Relatorios.RelatorioUsuariosService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/relatorio/usuario")
@CrossOrigin(originPatterns = "*")
public class RelatorioUsuarioController {

    private final RelatorioUsuariosService service;

    public RelatorioUsuarioController(RelatorioUsuariosService service) {
        this.service = service;
    }

    @GetMapping("/inativos")
    public ResponseEntity<List<UsuariosInativadosDTO>> relatorioUsuariosInativos(@RequestHeader("X-Usuario-Logado") String userLogado) {

        List<UsuariosInativadosDTO> usuarios = service.relatorioUsuariosInativados(userLogado);

        return ResponseEntity.status(HttpStatus.OK).body(usuarios);

    }

}
