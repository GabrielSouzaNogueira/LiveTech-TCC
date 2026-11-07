package by.gabriel.gerenciadorEstoque.Services;

import by.gabriel.gerenciadorEstoque.Api.DTO.Equipamento.EquipamentoDTO;
import by.gabriel.gerenciadorEstoque.Api.DTO.Equipamento.UpdateEquipamentoDTO;
import by.gabriel.gerenciadorEstoque.Api.Headler.Equipamento.EquipamentoExceptionHeader;
import by.gabriel.gerenciadorEstoque.Enum.Equipamento.EquipamentoStatus;
import by.gabriel.gerenciadorEstoque.Enum.Movimentacao.AcaoMovimentacao;
import by.gabriel.gerenciadorEstoque.Enum.Movimentacao.TipoEntidade;
import by.gabriel.gerenciadorEstoque.Enum.Usuario.UserCargo;
import by.gabriel.gerenciadorEstoque.Enum.Usuario.UserStatus;
import by.gabriel.gerenciadorEstoque.Exception.Equipamento.*;
import by.gabriel.gerenciadorEstoque.Exception.Produto.AccessDeniedException;
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
import java.util.Optional;

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

        Usuario usuario = userRepository.findByNomeIgnoreCase(usuarioLogado)
                .orElseThrow(() -> new UserNotFoundException("Usuario não foi encontrado"));

        if (dto.modelo() == null || dto.modelo().isBlank()) {
            throw new ModeloNotNull("O Modelo não pode estar vazio");
        }

        if (dto.numeroSerie() == null || dto.numeroSerie().isBlank()) {
            throw new NumeroSerieNotNull("O numero de serie não pode estar vazio");
        }

        Optional<Equipamento> equipamentoExistente = equipamentoRepository.findByNumeroSerieIgnoreCase(dto.numeroSerie());

        Equipamento equipamento;
        AcaoMovimentacao acaoMovimentacao;

        if (equipamentoExistente.isPresent()) {
            equipamento = equipamentoExistente.get();

            if (equipamento.getStatus() != EquipamentoStatus.INATIVO) {
                throw new NumeroDeSerieJaExiste("Numero de serie já existente no produto " + equipamento.getId());
            }

            equipamento.setModelo(dto.modelo());
            equipamento.setStatus(EquipamentoStatus.ATIVO);

            acaoMovimentacao = AcaoMovimentacao.ATUALIZACAO;

        } else {
            equipamento = new Equipamento(dto.modelo(), dto.numeroSerie(), EquipamentoStatus.ATIVO);
            acaoMovimentacao = AcaoMovimentacao.CRIACAO;
        }

        equipamentoRepository.save(equipamento);

        Movimentacao movimentacao = new Movimentacao(
                acaoMovimentacao,
                TipoEntidade.EQUIPAMENTO,
                null,
                equipamento.getId(),
                equipamento.getModelo(),
                "NENHUM", // Como é um cadastro/reativação, afeta o registro como um todo
                usuario
        );

        movimentacaoRepository.save(movimentacao);

        return equipamento;
    }

    @Transactional
    public Equipamento atualizar(Long id, UpdateEquipamentoDTO dto, String usuarioLogado) {

        Usuario usuario = userRepository.findByNomeIgnoreCase(usuarioLogado).orElseThrow(() -> new UserNotFoundException("Usuario não encontrado no sistema"));

        if (!usuario.getUserCargo().toString().equals("ADMINISTRADOR") &&
                !usuario.getUserCargo().toString().equals("DEV") &&
                !usuario.getUserCargo().toString().equals("GERENTE")) {

            throw new AccessDeniedException("Usuario sem Permissão para realizar esta ação");
        }

        Equipamento equipamento = equipamentoRepository.findById(id).orElseThrow(() -> new EquipamentoNaoEncontrado("Equipamento não encontrado no sistema"));

        if (equipamento.getStatus() != EquipamentoStatus.ATIVO) {
            throw new StatusInvalido("Status invalido do Equipamento");
        }

        if (dto.modelo() != null && !dto.modelo().isBlank()) {

            equipamento.setModelo(dto.modelo());
            equipamentoRepository.save(equipamento);

            Movimentacao movimentacao = new Movimentacao(
                    AcaoMovimentacao.ATUALIZACAO,
                    TipoEntidade.EQUIPAMENTO,
                    null,
                    equipamento.getId(),
                    equipamento.getModelo(),
                    "MODELO",
                    usuario
            );

            movimentacaoRepository.save(movimentacao);
        }

        if (dto.numeroSerie() != null && !dto.numeroSerie().isBlank()) {

            equipamento.setNumeroSerie(dto.numeroSerie());
            equipamentoRepository.save(equipamento);

            Movimentacao movimentacao = new Movimentacao(
                    AcaoMovimentacao.ATUALIZACAO,
                    TipoEntidade.EQUIPAMENTO,
                    null,
                    equipamento.getId(),
                    equipamento.getModelo(),
                    "NUMSERIE",
                    usuario
            );

            movimentacaoRepository.save(movimentacao);
        }

        return equipamento;
    }

    @Transactional
    public Boolean deletar(Long id, String usuarioLogado) {

        Usuario usuario = userRepository.findByNomeIgnoreCase(usuarioLogado).orElseThrow(() -> new UserNotFoundException("Usuario não encontrado no sistema"));

        if(!(usuario.getUserCargo().toString().equals("ADMINISTRADOR")) && !(usuario.getUserCargo().toString().equals("DEV"))) {
            throw new AccessDeniedException("Usuario sem Permissão para realizar esta ação");
        }

        Equipamento equipamento = equipamentoRepository.findById(id).orElseThrow(() -> new EquipamentoNaoEncontrado("Equipamento não encontrado no sistema"));

        if (equipamento.getStatus() == EquipamentoStatus.INATIVO) {
            throw new StatusInvalido("O equipamento já está inativo no sistema!");
        }

        equipamento.setStatus(EquipamentoStatus.INATIVO);
        equipamentoRepository.save(equipamento);

        Movimentacao movimentacao = new Movimentacao(
                AcaoMovimentacao.EXCLUSAO,
                TipoEntidade.EQUIPAMENTO,
                null,
                equipamento.getId(),
                equipamento.getModelo(),
                "NENHUM",
                usuario
        );

        movimentacaoRepository.save(movimentacao);

        return true;

    }
}
