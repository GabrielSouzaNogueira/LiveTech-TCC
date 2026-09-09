package by.gabriel.gerenciadorEstoque.Api.Controller;

import by.gabriel.gerenciadorEstoque.Api.DTO.Response.ResponseDTO;
import by.gabriel.gerenciadorEstoque.Api.DTO.Servicos.ServicosDTO;
import by.gabriel.gerenciadorEstoque.Api.DTO.Servicos.Consultas.ServicosSelectDTO;
import by.gabriel.gerenciadorEstoque.Services.ServicosService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/servicos")
@CrossOrigin(originPatterns = "*")
public class ServicosController {

    private final ServicosService servicosService;

    public ServicosController(ServicosService servicosService) {
        this.servicosService = servicosService;
    }

    // --- LISTAGEM DE SERVIÇOS (Mantido o original para não quebrar o Angular) ---
    @GetMapping("/listAll")
    public ResponseEntity<List<ServicosSelectDTO>> listarServicos() {
        List<ServicosSelectDTO> servicos = servicosService.listarServicosAtivos();
        return ResponseEntity.ok(servicos);
    }

    // --- CADASTRO (Agora com ResponseDTO) ---
    @PostMapping("/cadastrar")
    public ResponseEntity<ResponseDTO> cadastrarServico(
            @RequestBody ServicosDTO dto,
            @RequestHeader("X-Usuario-Logado") String usuarioLogado) {

        servicosService.cadastrarServico(dto, usuarioLogado);

        ResponseDTO response = new ResponseDTO(
                true,
                "Serviço cadastrado com sucesso!",
                LocalDateTime.now().toString()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // --- ATUALIZAÇÃO (Agora com ResponseDTO) ---
    @PutMapping("/atualizar/{id}")
    public ResponseEntity<ResponseDTO> atualizarServico(
            @PathVariable Long id,
            @RequestBody ServicosDTO dto,
            @RequestHeader("X-Usuario-Logado") String usuarioLogado) {

        servicosService.atualizarServico(id, dto, usuarioLogado);

        ResponseDTO response = new ResponseDTO(
                true,
                "Serviço atualizado com sucesso!",
                LocalDateTime.now().toString()
        );
        return ResponseEntity.ok(response);
    }

    // --- DELEÇÃO LÓGICA (Agora com ResponseDTO) ---
    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<ResponseDTO> deletarServico(
            @PathVariable Long id,
            @RequestHeader("X-Usuario-Logado") String usuarioLogado) {

        servicosService.deletarServico(id, usuarioLogado);

        ResponseDTO response = new ResponseDTO(
                true,
                "Serviço inativado com sucesso!",
                LocalDateTime.now().toString()
        );
        return ResponseEntity.ok(response);
    }
}