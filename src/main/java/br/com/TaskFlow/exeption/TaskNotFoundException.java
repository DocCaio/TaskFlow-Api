package br.com.TaskFlow.exeption;

public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(Long id) {
        super("Tarefa não encontrada: " + id);
    }
}
