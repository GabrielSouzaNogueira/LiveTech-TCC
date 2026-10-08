package by.gabriel.gerenciadorEstoque.Api.Controller;

import by.gabriel.gerenciadorEstoque.Api.DTO.Pedido.Consultas.PedidoListDTO;
import by.gabriel.gerenciadorEstoque.Api.DTO.Response.PedidoResponseDTO;
import by.gabriel.gerenciadorEstoque.Api.DTO.Pedido.PagPedidoDTO;
import by.gabriel.gerenciadorEstoque.Api.DTO.Pedido.PedidoDTO;
import by.gabriel.gerenciadorEstoque.Model.Pedido.Pedido;
import by.gabriel.gerenciadorEstoque.Services.PedidoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pedido")
@CrossOrigin(originPatterns = "*") // Importante para o seu frontend Angular
public class PedidoController {

    private final PedidoService vendaService;

    public PedidoController(PedidoService vendaService) {
        this.vendaService = vendaService;
    }

    // --- 1. CRIAR VENDA (ABERTA) ---
    @PostMapping("/criar")
    public ResponseEntity<PedidoResponseDTO> criarPedidoAberta (@RequestBody PedidoDTO dto, @RequestHeader ("X-Usuario-Logado") String usuarioLogado) {

        Pedido novoPedido = vendaService.criarPedidoAberta(dto, usuarioLogado);

        PedidoResponseDTO response = new PedidoResponseDTO(
                novoPedido.getId(),
                novoPedido.getCliente().getNome(),
                novoPedido.getValorTotal(),
                novoPedido.getStatus(),
                "Venda aberta com sucesso!"
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // --- 2. FINALIZAR VENDA (BAIXA NO ESTOQUE) ---
    @PostMapping("/finalizar/{id}")
    public ResponseEntity<PedidoResponseDTO> finalizarPedido (@PathVariable Long id, @RequestBody List<PagPedidoDTO> pagDto, @RequestHeader ("X-Usuario-Logado") String usuarioLogado) {

        Pedido pedidoFinalizado = vendaService.finalizarPedido(id, pagDto, usuarioLogado);

        PedidoResponseDTO response = new PedidoResponseDTO(
                pedidoFinalizado.getId(),
                pedidoFinalizado.getCliente().getNome(),
                pedidoFinalizado.getValorTotal(),
                pedidoFinalizado.getStatus(),
                "Venda finalizada com sucesso!"
        );

        return ResponseEntity.ok(response);
    }

    // --- 3. ATUALIZAR VENDA (ABERTA) ---
    @PutMapping("/atualizar/{id}")
    public ResponseEntity<PedidoResponseDTO> atualizarPedidoAberta(
            @PathVariable Long id,
            @RequestBody PedidoDTO dto,
            @RequestHeader("X-Usuario-Logado") String usuarioLogado) {

        Pedido pedidoAtualizado = vendaService.atualizarPedidoAberta(id, dto, usuarioLogado);

        PedidoResponseDTO response = new PedidoResponseDTO(
                pedidoAtualizado.getId(),
                pedidoAtualizado.getCliente().getNome(),
                pedidoAtualizado.getValorTotal(),
                pedidoAtualizado.getStatus(),
                "Venda atualizada com sucesso!"
        );

        return ResponseEntity.ok(response);
    }

    // --- 4. DEVOLUÇÃO (ESTORNO DE ESTOQUE APÓS FINALIZADA) ---
    @PostMapping("/devolucao/{id}")
    public ResponseEntity<PedidoResponseDTO> devolverPedidoFinalizado(
            @PathVariable Long id,
            @RequestHeader("X-Usuario-Logado") String usuarioLogado) {

        Pedido pedidoDevolvido = vendaService.devolverPedidoinalizada(id, usuarioLogado);

        PedidoResponseDTO response = new PedidoResponseDTO(
                pedidoDevolvido.getId(),
                pedidoDevolvido.getCliente().getNome(),
                pedidoDevolvido.getValorTotal(),
                pedidoDevolvido.getStatus(),
                "Devolução concluída! O estoque dos produtos foi restaurado."
        );

        return ResponseEntity.ok(response);
    }

    // --- 5. CANCELAR VENDA (DESISTÊNCIA ANTES DE FINALIZAR) ---
    @DeleteMapping("/cancelar/{id}")
    public ResponseEntity<String> cancelarPedidoAberto(
            @PathVariable Long id,
            @RequestHeader("X-Usuario-Logado") String usuarioLogado) {

        vendaService.cancelarPedidoAberto(id, usuarioLogado);

        return ResponseEntity.ok("Venda cancelada com sucesso. Nenhum estoque foi alterado.");
    }

    // --- LISTAR TODAS (Para a tabela do frontend) ---
    @GetMapping("/listAll")
    public ResponseEntity<List<PedidoListDTO>> listarVendas() {
        List<PedidoListDTO> lista = vendaService.listarTodosOsPedidos();
        return ResponseEntity.ok(lista);
    }

    // --- BUSCAR POR ID (Para preencher a tela de edição) ---
    @GetMapping("/buscar/{id}")
    public ResponseEntity<Pedido> buscarPedidoPorId(@PathVariable Long id) {
        Pedido pedido = vendaService.buscarVendaPorId(id);
        return ResponseEntity.ok(pedido);
    }
}