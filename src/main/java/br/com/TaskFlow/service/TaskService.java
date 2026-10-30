package br.com.TaskFlow.service;



import br.com.TaskFlow.dto.TaskRequest;
import br.com.TaskFlow.dto.TaskResponse;
import br.com.TaskFlow.exeption.TaskNotFoundException;
import br.com.TaskFlow.repository.TaskRepository;
import org.springframework.scheduling.config.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TaskService {

    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> list() {
        return repository.findAllByOrderByCreatedAtDesc().stream()
                .map(TaskResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public TaskResponse get(Long id) {
        return TaskResponse.from(findOrThrow(id));
    }

    public TaskResponse create(TaskRequest request) {
        Task task = new Task();
        task.setTitle(request.title().trim());
        task.setDescription(request.description());
        task.setStatus(request.status() != null ? request.status() : TaskStatus.TODO);
        return TaskResponse.from(repository.save(task));
    }

    public TaskResponse update(Long id, TaskRequest request) {
        Task task = findOrThrow(id);
        task.setTitle(request.title().trim());
        task.setDescription(request.description());
        if (request.status() != null) {
            task.setStatus(request.status());
        }
        return TaskResponse.from(repository.save(task));
    }

    public void delete(Long id) {
        repository.delete(findOrThrow(id));
    }

    private Task findOrThrow(Long id) {
        return repository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
    }
}
