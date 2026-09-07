package by.gabriel.gerenciadorEstoque.Api.DTO.OS;

import java.math.BigDecimal;
import java.time.LocalDateTime; // Não esqueça de importar!
import java.util.List;

public record OsDTO(
        Long clienteId,
        String equipamento,
        String defeitoRelatado,
        BigDecimal desconto,
        LocalDateTime dataPrevisao,
        List<ItensOsProdutoDTO> itensProduto,
        List<ItensOsServicoDTO> itensServico
) {}