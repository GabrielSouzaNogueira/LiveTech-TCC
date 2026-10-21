package by.gabriel.gerenciadorEstoque.Api.DTO.OS.consultas;

import by.gabriel.gerenciadorEstoque.Enum.OS.OsStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OsListDTO(
        Long id,
        String nomeCliente,
        String descricaoEquipamento,
        BigDecimal valorTotalGeral,
        OsStatus status,
        LocalDateTime dataAbertura,
        LocalDateTime dataPrevisao
) {}