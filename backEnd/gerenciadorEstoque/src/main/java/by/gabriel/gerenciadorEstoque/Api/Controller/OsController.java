package by.gabriel.gerenciadorEstoque.Api.Controller;

import by.gabriel.gerenciadorEstoque.Api.DTO.OS.OsDTO;
import by.gabriel.gerenciadorEstoque.Api.DTO.Response.OsResponseDTO;
import by.gabriel.gerenciadorEstoque.Model.OS.OrdemServico;
import by.gabriel.gerenciadorEstoque.Services.OsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/os")
@CrossOrigin(originPatterns = "*")
public class OsController {

    private final OsService osService;

    public OsController(OsService osService) {
        this.osService = osService;
    }

    // --- 1. CRIAR OS ---
    @PostMapping("/criar")
    public ResponseEntity<OsResponseDTO> criarOrdemServico(
            @RequestBody OsDTO dto,
            @RequestHeader("X-Usuario-Logado") String usuarioLogado) {

        OrdemServico novaOs = osService.criarOs(dto, usuarioLogado);
        return ResponseEntity.status(HttpStatus.CREATED).body(converterParaResponseDTO(novaOs, "Ordem de Serviço criada com sucesso!"));
    }

    // --- 2. LISTAR TODAS ---
    @GetMapping
    public ResponseEntity<List<OsResponseDTO>> listarTodas() {
        List<OrdemServico> ordens = osService.listarTodas();

        List<OsResponseDTO> response = ordens.stream()
                .map(os -> converterParaResponseDTO(os, null))
                .toList();

        return ResponseEntity.ok(response);
    }

    // --- 3. BUSCAR POR ID ---
    @GetMapping("/{id}")
    public ResponseEntity<OsResponseDTO> buscarPorId(@PathVariable Long id) {
        OrdemServico os = osService.buscarPorId(id);
        return ResponseEntity.ok(converterParaResponseDTO(os, null));
    }

    // --- 4. ATUALIZAR OS ---
    @PutMapping("/atualizar/{id}")
    public ResponseEntity<OsResponseDTO> atualizarOrdemServico(
            @PathVariable Long id,
            @RequestBody OsDTO dto) {

        OrdemServico osAtualizada = osService.atualizarOs(id, dto);
        return ResponseEntity.ok(converterParaResponseDTO(osAtualizada, "Ordem de Serviço atualizada com sucesso!"));
    }

    // --- 5. DELETAR OS ---
    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Void> deletarOrdemServico(@PathVariable Long id) {

        osService.deletarOs(id);
        return ResponseEntity.noContent().build();
    }

    private OsResponseDTO converterParaResponseDTO(OrdemServico os, String mensagem) {
        return new OsResponseDTO(
                os.getId(),
                os.getCliente().getNome(),
                os.getDescricaoEquipamento(),
                os.getObservacoes(),
                os.getValorTotalGeral(),
                os.getStatus(),
                os.getDataAbertura(),
                os.getDataPrevisao(),
                mensagem
        );
    }
}