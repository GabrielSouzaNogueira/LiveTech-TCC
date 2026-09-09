package by.gabriel.gerenciadorEstoque.Services;

import by.gabriel.gerenciadorEstoque.Api.DTO.Servicos.ServicosDTO;
import by.gabriel.gerenciadorEstoque.Api.DTO.Servicos.Consultas.ServicosSelectDTO;
import by.gabriel.gerenciadorEstoque.Enum.Movimentacao.AcaoMovimentacao;
import by.gabriel.gerenciadorEstoque.Enum.Movimentacao.TipoEntidade;
import by.gabriel.gerenciadorEstoque.Enum.Servicos.ServicosStatus;
import by.gabriel.gerenciadorEstoque.Model.Movimentacao.Movimentacao;
import by.gabriel.gerenciadorEstoque.Model.Servicos.Servicos;
import by.gabriel.gerenciadorEstoque.Model.Usuario.Usuario;
import by.gabriel.gerenciadorEstoque.Repository.Movimentacao.MovimentacaoRepository;
import by.gabriel.gerenciadorEstoque.Repository.Servicos.ServicosRepository;
import by.gabriel.gerenciadorEstoque.Repository.Usuario.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ServicosService {

    private final ServicosRepository servicosRepository;
    private final MovimentacaoRepository movimentacaoRepository;
    private final UserRepository userRepository;

    public ServicosService(ServicosRepository servicosRepository, MovimentacaoRepository movimentacaoRepository, UserRepository userRepository) {
        this.servicosRepository = servicosRepository;
        this.movimentacaoRepository = movimentacaoRepository;
        this.userRepository = userRepository;
    }

    // --- LISTAGEM ---
    public List<ServicosSelectDTO> listarServicosAtivos() {
        return servicosRepository.findByStatusCustom(ServicosStatus.ATIVO);
    }

    // --- CADASTRO ---
    @Transactional
    public Servicos cadastrarServico(ServicosDTO dto, String usuarioLogado) {
        Usuario userLogado = userRepository.findByNomeIgnoreCase(usuarioLogado)
                .orElseThrow(() -> new RuntimeException("Usuário logado não encontrado: " + usuarioLogado));

        if (dto.descServico() == null || dto.descServico().isBlank()) {
            throw new IllegalArgumentException("A descrição do serviço é obrigatória");
        }

        if (dto.precoServico() == null || dto.precoServico().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O preço do serviço é inválido");
        }

        if (servicosRepository.existsByDescServicoIgnoreCaseAndStatus(dto.descServico(), ServicosStatus.ATIVO)) {
            throw new IllegalArgumentException("Serviço já cadastrado e ativo no sistema!");
        }

        Servicos novoServico = new Servicos(
                dto.descServico(),
                dto.precoServico(),
                ServicosStatus.ATIVO
        );

        Servicos servicoSalvo = servicosRepository.save(novoServico);

        movimentacaoRepository.save(new Movimentacao(
                AcaoMovimentacao.CRIACAO,
                TipoEntidade.SERVICO,
                null,
                servicoSalvo.getServicosId(),
                servicoSalvo.getDescServico(),
                "NENHUM",
                userLogado
        ));

        return servicoSalvo;
    }

    // --- ATUALIZAÇÃO ---
    @Transactional
    public Servicos atualizarServico(Long id, ServicosDTO dto, String usuarioLogado) {
        Usuario userLogado = userRepository.findByNomeIgnoreCase(usuarioLogado)
                .orElseThrow(() -> new RuntimeException("Usuário logado não encontrado"));

        Servicos servico = servicosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));

        if (dto.descServico() != null && !dto.descServico().equalsIgnoreCase(servico.getDescServico())) {
            if (dto.descServico().isBlank()) throw new IllegalArgumentException("Descrição não pode ser vazia");

            // Verifica se está tentando mudar para um nome que já existe
            if(servicosRepository.existsByDescServicoIgnoreCaseAndStatus(dto.descServico(), ServicosStatus.ATIVO)){
                throw new IllegalArgumentException("Já existe outro serviço com esta descrição!");
            }

            servico.setDescServico(dto.descServico());
            movimentacaoRepository.save(new Movimentacao(AcaoMovimentacao.ATUALIZACAO, TipoEntidade.SERVICO, null, servico.getServicosId(), servico.getDescServico(), "DESCRICAO", userLogado));
        }

        if (dto.precoServico() != null && dto.precoServico().compareTo(servico.getPrecoServico()) != 0) {
            if (dto.precoServico().compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Preço inválido");
            servico.setPrecoServico(dto.precoServico());
            movimentacaoRepository.save(new Movimentacao(AcaoMovimentacao.ATUALIZACAO, TipoEntidade.SERVICO, null, servico.getServicosId(), servico.getDescServico(), "PRECO", userLogado));
        }

        return servicosRepository.save(servico);
    }

    // --- DELEÇÃO LÓGICA ---
    @Transactional
    public boolean deletarServico(Long id, String usuarioLogado) {
        Usuario userLogado = userRepository.findByNomeIgnoreCase(usuarioLogado)
                .orElseThrow(() -> new RuntimeException("Usuário logado não encontrado"));

        Servicos servico = servicosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));

        servico.setStatus(ServicosStatus.INATIVO);
        servicosRepository.save(servico);

        movimentacaoRepository.save(new Movimentacao(
                AcaoMovimentacao.EXCLUSAO,
                TipoEntidade.SERVICO,
                null,
                servico.getServicosId(),
                servico.getDescServico(),
                "NENHUM",
                userLogado
        ));

        return true;
    }
}