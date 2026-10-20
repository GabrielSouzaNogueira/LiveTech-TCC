package by.gabriel.gerenciadorEstoque.Services;

import java.util.List;

import by.gabriel.gerenciadorEstoque.Enum.Movimentacao.AcaoMovimentacao;
import by.gabriel.gerenciadorEstoque.Enum.Movimentacao.TipoEntidade;
import by.gabriel.gerenciadorEstoque.Model.Movimentacao.Movimentacao;
import by.gabriel.gerenciadorEstoque.Repository.Movimentacao.MovimentacaoRepository;
import org.springframework.stereotype.Service;

import by.gabriel.gerenciadorEstoque.Api.DTO.FormPagDTO.Consultas.SelectFormPagStatusDTO;
import by.gabriel.gerenciadorEstoque.Api.DTO.FormPagDTO.FormPagDTO;
import by.gabriel.gerenciadorEstoque.Api.DTO.FormPagDTO.UpdateFormPagDTO;
import by.gabriel.gerenciadorEstoque.Enum.FormaPag.FormaPagStatus;
import by.gabriel.gerenciadorEstoque.Enum.Usuario.UserCargo;
import by.gabriel.gerenciadorEstoque.Exception.FormaPag.FormPagAlreadyExistException;
import by.gabriel.gerenciadorEstoque.Exception.FormaPag.FormPagNotExistException;
import by.gabriel.gerenciadorEstoque.Exception.FormaPag.FormPagNotNullException;
import by.gabriel.gerenciadorEstoque.Exception.Usuario.UserLogadoNotNull;
import by.gabriel.gerenciadorEstoque.Exception.Usuario.UserNotFoundException;
import by.gabriel.gerenciadorEstoque.Exception.Usuario.UserNotPermission;
import by.gabriel.gerenciadorEstoque.Model.FormaPag.FormaPagto;
import by.gabriel.gerenciadorEstoque.Model.Usuario.Usuario;
import by.gabriel.gerenciadorEstoque.Repository.FormPagRespository.FormPagRepository;
import by.gabriel.gerenciadorEstoque.Repository.Usuario.UserRepository;
import jakarta.transaction.Transactional;

@Service
public class FormPagService {

    private final MovimentacaoRepository movimentacaoRepository;
    private final FormPagRepository formPagRepository;
    private final UserRepository userRepository;

    public FormPagService(FormPagRepository formPagRepository, UserRepository userRepository, MovimentacaoRepository movimentacaoRepository) {
        this.formPagRepository = formPagRepository;
        this.userRepository = userRepository;
        this.movimentacaoRepository = movimentacaoRepository;
    }

    public List<SelectFormPagStatusDTO> listar() {

        List<SelectFormPagStatusDTO> formaPagtos = formPagRepository.findByStatusCustom(FormaPagStatus.ATIVO);

        return formaPagtos;

    }

    @Transactional
    public FormaPagto cadastro(FormPagDTO dto, String usuarioLogado) {

        Usuario userLogado = userRepository.findByNomeIgnoreCase(usuarioLogado)
                .orElseThrow(() -> new UserNotFoundException("Usuario de nome: " + usuarioLogado + " Não foi encontrado"));


        if (userLogado.getUserCargo() != null && userLogado.getUserCargo() != UserCargo.ADMINISTRADOR && userLogado.getUserCargo() != UserCargo.DEV) {
            throw new UserNotPermission("Usuario não possui permissão para realizar está ação");
        }

        if (dto.descricao() == null || dto.descricao().isBlank()) {
            throw new FormPagNotNullException("A descrição não pode estar vazia!");
        }

        if (formPagRepository.existsByDescricaoIgnoreCaseAndStatus(dto.descricao(), FormaPagStatus.ATIVO)) {
            throw new FormPagAlreadyExistException("Forma de pagamento já existente no sistema!");
        }

        FormaPagto formaPagto = new FormaPagto(
                dto.descricao().toUpperCase(),
                FormaPagStatus.ATIVO);

        formPagRepository.save(formaPagto);

        movimentacaoRepository.save(new Movimentacao(
                AcaoMovimentacao.CRIACAO,
                TipoEntidade.FORMA_PAGAMENTO,
                null,
                formaPagto.getId(),
                formaPagto.getDescricao(),
                "NENHUM",
                userLogado)
        );

        return formaPagto;
    }

    @Transactional
    public Boolean atualizar(String descricaoAntiga, UpdateFormPagDTO dto, String usuarioLogado) {

        FormaPagto formaPagto;

        if (usuarioLogado == null || usuarioLogado.isBlank()) {
            throw new UserLogadoNotNull("Header de usuario no cabeçalho foi enviado como null ou vazio!");
        }

        Usuario userLogado = userRepository.findByNomeIgnoreCase(usuarioLogado)
                .orElseThrow(() -> new UserNotFoundException("Usuario de nome: " + usuarioLogado + " Não foi encontrado"));

        if (userLogado.getUserCargo() != null && userLogado.getUserCargo() != UserCargo.ADMINISTRADOR && userLogado.getUserCargo() != UserCargo.DEV) {
            throw new UserNotPermission("Usuario não possui permissão para realizar está ação");
        }

        // 1. Busca a forma de pagamento pelo nome ANTIGO
        formaPagto = formPagRepository.findByDescricaoIgnoreCase(descricaoAntiga)
                .orElseThrow(() -> new FormPagNotExistException("Forma de pagamento não existente no sistema"));

        if(dto.descricao() != null && !dto.descricao().isBlank()) {

            // 2. Verifica se o NOVO nome já existe (para não duplicar)
            if(!descricaoAntiga.equalsIgnoreCase(dto.descricao()) &&
                    formPagRepository.existsByDescricaoIgnoreCaseAndStatus(dto.descricao(), FormaPagStatus.ATIVO)){
                throw new FormPagAlreadyExistException("Forma de pagamento já existente no sistema!");
            }

            // 3. Atualiza com o NOVO nome
            formaPagto.setDescricao(dto.descricao().toUpperCase());
            formPagRepository.save(formaPagto);

            // OBS: Pode colar aqui também a sua lógica do movimentacaoRepository.save() se precisar manter o histórico!
        }

        return true;
    }

    @Transactional
    public Boolean delecao(String descricao, String usuarioLogado) {

        FormaPagto formaPagto;

        if (usuarioLogado == null || usuarioLogado.isBlank()) {
            throw new UserLogadoNotNull("Header de usuario no cabeçalho foi enviado como null ou vazio!");
        }

        Usuario userLogado = userRepository.findByNomeIgnoreCase(usuarioLogado)
                .orElseThrow(() -> new UserNotFoundException("Usuario de nome: " + usuarioLogado + " Não foi encontrado"));

        if (userLogado.getUserCargo() != null && userLogado.getUserCargo() != UserCargo.ADMINISTRADOR && userLogado.getUserCargo() != UserCargo.DEV) {

            throw new UserNotPermission("Usuario não possui permissão para realizar está ação");
        }

        formaPagto = formPagRepository.findByDescricaoIgnoreCase(descricao).orElseThrow(() -> new FormPagNotExistException("Forma de pagamento com o nome: " + descricao +" não foi encontrado"));

        formaPagto.setStatus(FormaPagStatus.DESATIVADO);
        formPagRepository.save(formaPagto);

        movimentacaoRepository.save(new Movimentacao(
                AcaoMovimentacao.EXCLUSAO,
                TipoEntidade.FORMA_PAGAMENTO,
                null,
                formaPagto.getId(),
                formaPagto.getDescricao(),
                "NENHUM",
                userLogado
        ));

        return true;
    }

}
