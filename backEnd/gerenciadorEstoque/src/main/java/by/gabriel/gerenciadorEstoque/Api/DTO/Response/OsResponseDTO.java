package by.gabriel.gerenciadorEstoque.Api.DTO.Response;

import by.gabriel.gerenciadorEstoque.Enum.OS.OsStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime; // Importante para as datas

public record OsResponseDTO(
        Long id,
        String nomeCliente,
        String equipamento,
        String defeitoRelatado, // <-- Sua excelente sugestão aqui!
        BigDecimal valorTotalGeral,
        OsStatus status,
        LocalDateTime dataAbertura, // <-- As datas que arrumamos
        LocalDateTime dataPrevisao,
        String mensagem
) {}