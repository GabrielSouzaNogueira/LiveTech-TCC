package by.gabriel.gerenciadorEstoque.Api.DTO.Equipamento;

import by.gabriel.gerenciadorEstoque.Enum.Equipamento.EquipamentoStatus;

public record EquipamentoDTO(

        String modelo,
        String numeroSerie,
        EquipamentoStatus status
) {


}
