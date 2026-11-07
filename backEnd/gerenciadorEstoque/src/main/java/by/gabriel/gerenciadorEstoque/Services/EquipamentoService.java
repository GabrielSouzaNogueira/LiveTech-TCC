package by.gabriel.gerenciadorEstoque.Services;

import by.gabriel.gerenciadorEstoque.Api.DTO.Equipamento.EquipamentoDTO;
import by.gabriel.gerenciadorEstoque.Enum.Equipamento.EquipamentoStatus;
import by.gabriel.gerenciadorEstoque.Enum.Movimentacao.AcaoMovimentacao;
import by.gabriel.gerenciadorEstoque.Enum.Movimentacao.TipoEntidade;
import by.gabriel.gerenciadorEstoque.Enum.Usuario.UserCargo;
import by.gabriel.gerenciadorEstoque.Enum.Usuario.UserStatus;
import by.gabriel.gerenciadorEstoque.Exception.Equipamento.EquipamentoNaoEncontrado;
import by.gabriel.gerenciadorEstoque.Exception.Equipamento.ModeloNotNull;
import by.gabriel.gerenciadorEstoque.Exception.Equipamento.NumeroSerieNotNull;
import by.gabriel.gerenciadorEstoque.Exception.Usuario.UserNotFoundException;
import by.gabriel.gerenciadorEstoque.Exception.Usuario.UserNotPermission;
import by.gabriel.gerenciadorEstoque.Model.Equipamento.Equipamento;
import by.gabriel.gerenciadorEstoque.Model.Movimentacao.Movimentacao;
import by.gabriel.gerenciadorEstoque.Model.Usuario.Usuario;
import by.gabriel.gerenciadorEstoque.Repository.Equipamento.EquipamentoRepository;
import by.gabriel.gerenciadorEstoque.Repository.Movimentacao.MovimentacaoRepository;
import by.gabriel.gerenciadorEstoque.Repository.Usuario.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class EquipamentoService {

    private final EquipamentoRepository equipamentoRepository;
    private final UserRepository userRepository;
    private final MovimentacaoRepository movimentacaoRepository;

    public EquipamentoService(EquipamentoRepository equipamentoRepository, UserRepository userRepository, MovimentacaoRepository movimentacaoRepository) {
        this.equipamentoRepository = equipamentoRepository;
        this.userRepository = userRepository;
        this.movimentacaoRepository = movimentacaoRepository;
    }

    @Transactional
    public List<Equipamento> listarEquipamento() {

        return equipamentoRepository.findByStatusCustomEntity(EquipamentoStatus.ATIVO);
    }

    @Transactional
    public Equipamento buscarPorId(Long id, String usuarioLogado){

        Usuario usuario = userRepository.findByNomeIgnoreCase(usuarioLogado).orElseThrow(() -> new UserNotFoundException("Usuario não foi encontrado"));

        if (!usuario.equals(UserStatus.ATIVO.toString())){
            throw new UserNotPermission("Este usuario não está ativo!");
        }

        Equipamento equipamento = equipamentoRepository.findById(id).orElseThrow(() -> new EquipamentoNaoEncontrado("Equipamento não encontado"));

        return equipamento;
    }

    @Transactional
    public Equipamento cadastrar(EquipamentoDTO dto, String usuarioLogado) {

        Usuario usuario = userRepository.findByNomeIgnoreCase(usuarioLogado).orElseThrow(() -> new UserNotFoundException("Usuario não foi encontrado"));

        if (dto.modelo() == null || dto.modelo().isBlank()) {
            throw new ModeloNotNull("O Modelo não pode estar vazio");
        }

        if (dto.numeroSerie() == null || dto.numeroSerie().isBlank()) {
            throw new NumeroSerieNotNull("O numero de serie não pode estar vazio");
        }

        Equipamento equipamento = new Equipamento(
                dto.modelo(),
                dto.numeroSerie(),
                EquipamentoStatus.ATIVO
        );

        equipamentoRepository.save(equipamento);

        Movimentacao movimentacao = new Movimentacao(
                AcaoMovimentacao.CRIACAO,
                TipoEntidade.EQUIPAMENTO,
                null,
                equipamento.getId(),
                equipamento.getModelo(),
                "NENHUM",
                usuario
        );

        movimentacaoRepository.save(movimentacao);


        return equipamento;
    }

}
