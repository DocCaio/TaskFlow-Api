package br.com.TaskFlow.dto;

import br.com.TaskFlow.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TaskRequest(
        @NotBlank(message = "O título é obrigatório")
        @Size(max = 120, message = "O título pode ter no máximo 120 caracteres")
        String title,

        @Size(max = 2000, message = "A descrição pode ter no máximo 2000 caracteres")
        String description,

        TaskStatus status
) {
}