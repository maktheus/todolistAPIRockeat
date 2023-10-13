package br.com.uchoa.todolist.task;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.uchoa.todolist.utils.Utils;
import jakarta.persistence.PostUpdate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/tasks")
public class TaskController {
    @Autowired
    private ITaskRepository taskRepository;

    @PostMapping("/")
    public ResponseEntity<TaskModel> create(@RequestBody TaskModel task, HttpServletRequest request) {
        var userId = (UUID) request.getAttribute("userId");

        task.setUserId(userId);

        var currentDateTime = java.time.LocalDateTime.now();

        if (task.getStartAt() == null) {
            task.setStartAt(currentDateTime);
        }

        if (task.getEndAt() == null) {
            task.setEndAt(currentDateTime);
        }

        if(currentDateTime.isBefore(task.getStartAt()) || currentDateTime.isAfter(task.getEndAt())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if(task.getStartAt().isAfter(task.getEndAt())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if(task.getTitle() == null || task.getTitle().isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }


        var taskCreated = taskRepository.save(task);
        if (taskCreated == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return  ResponseEntity.status(HttpStatus.CREATED).body(taskCreated);

    }


    //list
    @GetMapping("/")
    public List<TaskModel> list(HttpServletRequest request) {
        var userId = (UUID) request.getAttribute("userId");
        var tasks = taskRepository.findByUserId(userId);
        return tasks;
    }


    @PutMapping(value="/{id}")
    public ResponseEntity<TaskModel> update(@PathVariable("id") UUID id, @RequestBody TaskModel task, HttpServletRequest request) {
        var userId = (UUID) request.getAttribute("userId");
        var tasks = taskRepository.findByUserId(userId);
        var taskToUpdate = tasks.stream().filter(t -> t.getId().equals(id)).findFirst();
        if (taskToUpdate.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Utils.copyNonNullProperties(task, taskToUpdate.get());
        var taskUpdated = taskRepository.save(taskToUpdate.get());
        return ResponseEntity.status(HttpStatus.OK).body(taskUpdated);
    }
}
