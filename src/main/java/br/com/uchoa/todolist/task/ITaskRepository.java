package br.com.uchoa.todolist.task;

import java.util.UUID;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITaskRepository extends JpaRepository<TaskModel,UUID>{
    //find by user id
    List<TaskModel> findByUserId(UUID userId);

    
}
