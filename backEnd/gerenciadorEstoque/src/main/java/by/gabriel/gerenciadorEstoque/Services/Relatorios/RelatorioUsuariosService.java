package by.gabriel.gerenciadorEstoque.Services.Relatorios;

import by.gabriel.gerenciadorEstoque.Api.DTO.Relatorio.Usuario.UsuariosInativadosDTO;
import by.gabriel.gerenciadorEstoque.Enum.Usuario.UserStatus;
import by.gabriel.gerenciadorEstoque.Exception.Produto.AccessDeniedException;
import by.gabriel.gerenciadorEstoque.Exception.Usuario.UserNotFoundException;
import by.gabriel.gerenciadorEstoque.Model.Usuario.Usuario;
import by.gabriel.gerenciadorEstoque.Repository.Relatorio.RelatorioUsuarioRepository;
import by.gabriel.gerenciadorEstoque.Repository.Usuario.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RelatorioUsuariosService {

    private final UserRepository userRepository;
    private final RelatorioUsuarioRepository relatorioUsuarioRepository;

    public RelatorioUsuariosService(UserRepository userRepository, RelatorioUsuarioRepository relatorioUsuarioRepository) {
        this.userRepository = userRepository;
        this.relatorioUsuarioRepository = relatorioUsuarioRepository;
    }

    @Transactional
    public List<UsuariosInativadosDTO> relatorioUsuariosInativados(String usuarioLogado) {

        Usuario usuario = userRepository.findByNomeIgnoreCase(usuarioLogado).orElseThrow(() -> new UserNotFoundException("Usuario não encontrado no sistemas"));

        if (!(usuario.getUserCargo().toString().equals("DEV") || usuario.getUserCargo().toString().equals("ADMINISTRADOR"))) {
            throw new AccessDeniedException("Usuario sem permissão para Realizar essa ação");
        }

        return relatorioUsuarioRepository.relatorioUsuariosInativados(UserStatus.INATIVO);
    }

}

