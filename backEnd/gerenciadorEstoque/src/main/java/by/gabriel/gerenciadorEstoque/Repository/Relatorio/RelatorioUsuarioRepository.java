package by.gabriel.gerenciadorEstoque.Repository.Relatorio;

import by.gabriel.gerenciadorEstoque.Api.DTO.Relatorio.Usuario.UsuariosInativadosDTO;
import by.gabriel.gerenciadorEstoque.Enum.Usuario.UserStatus;
import by.gabriel.gerenciadorEstoque.Model.Usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RelatorioUsuarioRepository extends JpaRepository<Usuario, Long> {

    @Query("""
            SELECT new by.gabriel.gerenciadorEstoque.Api.DTO.Relatorio.Usuario.UsuariosInativadosDTO(
                usr.id,
                usr.email,
                usr.nome,
                usr.telefone,
                usr.userCargo,
                usr.userStatus
            )
            FROM Usuario usr
            WHERE usr.userStatus = :status
            ORDER BY usr.nome ASC
            """)
    List<UsuariosInativadosDTO> relatorioUsuariosInativados(@Param("status") UserStatus status);

}