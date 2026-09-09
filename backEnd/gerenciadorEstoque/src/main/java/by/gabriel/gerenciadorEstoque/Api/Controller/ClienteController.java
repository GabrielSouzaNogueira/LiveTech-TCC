package by.gabriel.gerenciadorEstoque.Api.Controller;

import by.gabriel.gerenciadorEstoque.Api.DTO.Cliente.ClienteDTO;
import by.gabriel.gerenciadorEstoque.Api.DTO.Cliente.Consultas.ClienteSelectDTO;
import by.gabriel.gerenciadorEstoque.Api.DTO.Response.ResponseDTO;
import by.gabriel.gerenciadorEstoque.Services.ClienteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/cliente")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    // --- LISTAGEM DE CLIENTES ATIVOS ---
    @GetMapping("/listAll")
    public ResponseEntity<List<ClienteSelectDTO>> listarClientes() {
        List<ClienteSelectDTO> clientes = clienteService.listarClientesAtivos();
        return ResponseEntity.ok(clientes);
    }

    // --- CADASTRO DE CLIENTE ---
    @PostMapping("/cadastrar")
    public ResponseEntity<ResponseDTO> cadastrarCliente(
            @RequestBody ClienteDTO dto,
            @RequestHeader("X-Usuario-Logado") String userLogado) {

        clienteService.cadastroCliente(dto, userLogado);

        ResponseDTO response = new ResponseDTO(
                true,
                "Cliente cadastrado com sucesso!",
                LocalDateTime.now().toString()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // --- ATUALIZAÇÃO DE CLIENTE ---
    @PutMapping("/atualizar/{id}")
    public ResponseEntity<ResponseDTO> atualizarCliente(
            @PathVariable Long id,
            @RequestBody ClienteDTO dto,
            @RequestHeader("X-Usuario-Logado") String userLogado) {

        clienteService.atualizarCliente(id, dto, userLogado);

        ResponseDTO response = new ResponseDTO(
                true,
                "Cliente atualizado com sucesso!",
                LocalDateTime.now().toString()
        );
        return ResponseEntity.ok(response);
    }

    // --- DELEÇÃO LÓGICA DE CLIENTE ---
    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<ResponseDTO> deletarCliente(
            @PathVariable Long id,
            @RequestHeader("X-Usuario-Logado") String userLogado) {

        clienteService.deletarCliente(id, userLogado);

        ResponseDTO response = new ResponseDTO(
                true,
                "Cliente inativado com sucesso!",
                LocalDateTime.now().toString()
        );
        return ResponseEntity.ok(response);
    }
}