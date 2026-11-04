package by.gabriel.gerenciadorEstoque.Repository.Usuario;

import java.util.List;
import java.util.UUID;
import java.util.Optional;


import by.gabriel.gerenciadorEstoque.Api.DTO.Usuario.Consultas.UserSelectDTO;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import by.gabriel.gerenciadorEstoque.Model.Usuario.Usuario;
import by.gabriel.gerenciadorEstoque.Enum.Usuario.UserStatus;

@Repository
public interface UserRepository extends JpaRepository<Usuario, UUID>{

    // Busca usuário pelo nome (para login)
    Optional<Usuario> findByNomeIgnoreCase(String nome);


    Optional<Usuario> findByEmailIgnoreCase(String email);

    @EntityGraph(attributePaths = "telefones")
    @Query("SELECT u FROM Usuario u WHERE u.userStatus = :status ORDER BY u.nome ASC")
    List<Usuario> findByStatusCustomEntity(@Param("status") UserStatus status);

}
