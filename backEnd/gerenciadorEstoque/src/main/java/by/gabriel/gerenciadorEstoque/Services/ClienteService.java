package by.gabriel.gerenciadorEstoque.Services;

import by.gabriel.gerenciadorEstoque.Api.DTO.Cliente.ClienteDTO;
import by.gabriel.gerenciadorEstoque.Api.DTO.Cliente.Consultas.ClienteSelectDTO;
import by.gabriel.gerenciadorEstoque.Enum.Cliente.ClienteStatus;
import by.gabriel.gerenciadorEstoque.Enum.Movimentacao.AcaoMovimentacao;
import by.gabriel.gerenciadorEstoque.Enum.Movimentacao.TipoEntidade;
import by.gabriel.gerenciadorEstoque.Exception.Cliente.ClienteNaoEncontrado;
import by.gabriel.gerenciadorEstoque.Exception.Cliente.EmailClienteJaExiste;
import by.gabriel.gerenciadorEstoque.Exception.Cliente.NomeClienteNotNull;
import by.gabriel.gerenciadorEstoque.Exception.Cliente.TelefoneNaoEncontrado;
import by.gabriel.gerenciadorEstoque.Exception.Usuario.UserNotFoundException;
import by.gabriel.gerenciadorEstoque.Model.Cliente.Cliente;
import by.gabriel.gerenciadorEstoque.Model.Cliente.ClienteTelefone;
import by.gabriel.gerenciadorEstoque.Model.Movimentacao.Movimentacao;
import by.gabriel.gerenciadorEstoque.Model.Usuario.Usuario;
import by.gabriel.gerenciadorEstoque.Repository.Cliente.ClienteRepository;
import by.gabriel.gerenciadorEstoque.Repository.Cliente.ClienteTelefoneRepository;
import by.gabriel.gerenciadorEstoque.Repository.Movimentacao.MovimentacaoRepository;
import by.gabriel.gerenciadorEstoque.Repository.Usuario.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final MovimentacaoRepository movimentacaoRepository;
    private final UserRepository userRepository;
    private final ClienteTelefoneRepository clienteTelefoneRepository;

    public ClienteService(ClienteRepository clienteRepository, MovimentacaoRepository movimentacaoRepository, UserRepository userRepository,ClienteTelefoneRepository clienteTelefoneRepository) {
        this.clienteRepository = clienteRepository;
        this.movimentacaoRepository = movimentacaoRepository;
        this.userRepository = userRepository;
        this.clienteTelefoneRepository = clienteTelefoneRepository;
    }

    // --- LISTAGEM ---
    public List<ClienteSelectDTO> listarClientesAtivos() {
        List<Cliente> clientes = clienteRepository.findByStatusCustomEntity(ClienteStatus.ATIVO);

        return clientes.stream().map(c -> {
            String telefonePrincipal = c.getTelefones().isEmpty()
                    ? "NÃO INFORMADO"
                    : c.getTelefones().get(0).getTelefone();

            return new ClienteSelectDTO(
                    c.getId(),
                    c.getNome(),
                    c.getSobrenome(),
                    c.getEmail(),
                    telefonePrincipal,
                    c.getStatus()
            );
        }).toList();
    }

    // --- CADASTRO ---
    @Transactional
    public Cliente cadastroCliente(ClienteDTO dto, String usuarioLogado) {

        Usuario userLogado = userRepository.findByNomeIgnoreCase(usuarioLogado)
                .orElseThrow(() -> new RuntimeException("Usuário logado não encontrado"));

        if (dto.nome() == null || dto.nome().isBlank()) {
            throw new NomeClienteNotNull("O nome do cliente é obrigatório");
        }

        if (dto.email() != null && !dto.email().isBlank() && clienteRepository.findByEmailIgnoreCase(dto.email()).isPresent()) {
            throw new EmailClienteJaExiste("Já existe um cliente cadastrado com este e-mail");
        }

        Cliente novoCliente = new Cliente(
                dto.nome(),
                dto.sobrenome(),
                dto.email(),
                ClienteStatus.ATIVO
        );

        Cliente clienteSalvo = clienteRepository.save(novoCliente);

        //VERIFICANDO SE O CLIENTE EXISTE ANTES DE SALVAR O TELEFONE
        clienteRepository.findById(clienteSalvo.getId()).orElseThrow(() -> new ClienteNaoEncontrado("Cliente não encontrado com o id: " + novoCliente.getId()));

        ClienteTelefone clienteTelefone = new ClienteTelefone(
                dto.telefone(),
                clienteSalvo
        );

        clienteTelefoneRepository.save(clienteTelefone);

        // Movimentação com UUID nulo e ID numérico preenchido
        movimentacaoRepository.save(new Movimentacao(
                AcaoMovimentacao.CRIACAO,
                TipoEntidade.CLIENTE,
                null,
                clienteSalvo.getId(),
                clienteSalvo.getNome(),
                "NENHUM",
                userLogado
        ));

        return clienteSalvo;
    }

    // --- ATUALIZAÇÃO ---
    @Transactional
    public Cliente atualizarCliente(Long id, ClienteDTO dto, String usuarioLogado) {

        Usuario userLogado = userRepository.findByNomeIgnoreCase(usuarioLogado)
                .orElseThrow(() -> new UserNotFoundException("Usuário logado não encontrado"));

        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNaoEncontrado("Cliente não encontrado"));

        if (dto.nome() != null && !dto.nome().equalsIgnoreCase(cliente.getNome())) {

            if (dto.nome().isBlank()) throw new NomeClienteNotNull("Nome não pode ser vazio");

            cliente.setNome(dto.nome());
            movimentacaoRepository.save(new Movimentacao(AcaoMovimentacao.ATUALIZACAO, TipoEntidade.CLIENTE, null, cliente.getId(), cliente.getNome(), "NOME", userLogado));
        }

        if (dto.sobrenome() != null && !dto.sobrenome().equalsIgnoreCase(cliente.getSobrenome())) {
            cliente.setSobrenome(dto.sobrenome());
            movimentacaoRepository.save(new Movimentacao(AcaoMovimentacao.ATUALIZACAO, TipoEntidade.CLIENTE, null, cliente.getId(), cliente.getNome(), "SOBRENOME", userLogado));
        }

        if (dto.email() != null && !dto.email().equalsIgnoreCase(cliente.getEmail())) {
            if (clienteRepository.findByEmailIgnoreCase(dto.email()).isPresent()) {
                throw new EmailClienteJaExiste("Já existe um cliente cadastrado com este e-mail");
            }
            cliente.setEmail(dto.email());
            movimentacaoRepository.save(new Movimentacao(AcaoMovimentacao.ATUALIZACAO, TipoEntidade.CLIENTE, null, cliente.getId(), cliente.getNome(), "EMAIL", userLogado));
        }

        if (dto.telefone() != null) {

            boolean telefoneJaExiste = clienteTelefoneRepository.existsByClienteIdAndTelefone(cliente.getId(), dto.telefone());

            if (!telefoneJaExiste) {

                // === LÓGICA DE SUBSTITUIÇÃO ===
                if (!cliente.getTelefones().isEmpty()) {
                    // Se o cliente já tem um telefone, pegamos o primeiro e atualizamos (UPDATE)
                    ClienteTelefone telefoneAtual = cliente.getTelefones().get(0);
                    telefoneAtual.setTelefone(dto.telefone());

                    clienteTelefoneRepository.save(telefoneAtual);
                } else {
                    // Se o cliente não tinha nenhum telefone, criamos um novo (INSERT)
                    ClienteTelefone novoTelefone = new ClienteTelefone();
                    novoTelefone.setTelefone(dto.telefone());
                    novoTelefone.setCliente(cliente); // Amarramos o telefone ao cliente

                    clienteTelefoneRepository.save(novoTelefone);
                }

                movimentacaoRepository.save(new Movimentacao(AcaoMovimentacao.ATUALIZACAO, TipoEntidade.CLIENTE, null, cliente.getId(), cliente.getNome(), "TELEFONE", userLogado));
            }
            else {
                return clienteRepository.save(cliente);
            }
        }

        return clienteRepository.save(cliente);
    }

    // --- DELEÇÃO LÓGICA ---
    @Transactional
    public boolean deletarCliente(Long id, String usuarioLogado) {
        Usuario userLogado = userRepository.findByNomeIgnoreCase(usuarioLogado)
                .orElseThrow(() -> new UserNotFoundException("Usuário logado não encontrado"));

        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNaoEncontrado("Cliente não encontrado"));

        cliente.setStatus(ClienteStatus.INATIVO);
        clienteRepository.save(cliente);

        movimentacaoRepository.save(new Movimentacao(
                AcaoMovimentacao.EXCLUSAO,
                TipoEntidade.CLIENTE,
                null,
                cliente.getId(),
                cliente.getNome(),
                "NENHUM",
                userLogado
        ));

        return true;
    }
}