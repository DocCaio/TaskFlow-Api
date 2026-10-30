package br.com.TaskFlow;


import br.com.TaskFlow.dto.TaskRequest;
import br.com.TaskFlow.dto.TaskResponse;
import br.com.TaskFlow.exeption.TaskNotFoundException;
import br.com.TaskFlow.repository.TaskRepository;
import br.com.TaskFlow.service.TaskService;
import br.com.TaskFlow.task.Task;
import br.com.TaskFlow.task.TaskStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository repository;

    @InjectMocks
    private TaskService service;

    @Test
    void createUsaStatusTodoQuandoNaoInformado() {
        when(repository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        TaskResponse response = service.create(new TaskRequest("  Estudar Spring  ", null, null));

        assertThat(response.title()).isEqualTo("Estudar Spring");
        assertThat(response.status()).isEqualTo(TaskStatus.TODO);
    }

    @Test
    void updateMantemStatusQuandoNaoInformado() {
        Task existing = new Task();
        existing.setTitle("Antiga");
        existing.setStatus(TaskStatus.DOING);
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        TaskResponse response = service.update(1L, new TaskRequest("Nova", "desc", null));

        assertThat(response.title()).isEqualTo("Nova");
        assertThat(response.status()).isEqualTo(TaskStatus.DOING);
    }

    @Test
    void getLancaExcecaoQuandoNaoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get(99L))
                .isInstanceOf(TaskNotFoundException.class);
    }

    @Test
    void deleteNaoRemoveQuandoNaoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(TaskNotFoundException.class);
        verify(repository, never()).delete(any(Task.class));
    }
}
