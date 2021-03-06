package com.example.hackathonendava.service;



import com.example.hackathonendava.exception.NotFoundException;
import com.example.hackathonendava.model.Task;
import com.example.hackathonendava.registration.User;
import com.example.hackathonendava.registration.UserInfo;
import com.example.hackathonendava.registration.UserRepository;
import com.example.hackathonendava.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private static String nameTask = "Not Enough Data";
    private static String descriptionTask =  "";

    private Logger logger = LoggerFactory.getLogger(TaskService.class);
    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Autowired
    UserRepository userRepository;

    public List<Task> getAllTasks(){


        return taskRepository.findAll();
    }

    public static void setNameTask(String nameTask) {
        TaskService.nameTask = nameTask;
    }

    public static void setDescriptionTask(String descriptionTask) {
        TaskService.descriptionTask = descriptionTask;
    }

    public List<Task> getAllTasksByUserName(){


        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = "";
        if (principal instanceof UserInfo) {
            username = ((UserInfo)principal).getUsername();

        } else {
            username = principal.toString();

        }
        return taskRepository.getAllTasksByUserName(username);
    }
    public List<Task> getAllTasksSortedByDeadline(){

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = "";
        if (principal instanceof UserInfo) {
            username = ((UserInfo)principal).getUsername();

        } else {
            username = principal.toString();

        }
        List<Task> taskListSortedByDeadline =  taskRepository.getAllTasksByUserName(username);
        taskListSortedByDeadline.sort(Comparator.comparing(Task::getDeadline));

//        for(Task task : taskListSortedByName) {
//            System.out.println(task.toString());
//        }
        return taskListSortedByDeadline;

    }
    public void getTaskCalendar(){

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = "";
        if (principal instanceof UserInfo) {
            username = ((UserInfo)principal).getUsername();

        } else {
            username = principal.toString();

        }
        List<Task> taskListSortedByDeadline =  taskRepository.getAllTasksByUserName(username);
        //taskListSortedByDeadline.sort(Comparator.comparing(Task::getDeadline));
        System.out.println(taskListSortedByDeadline.get(0).toString());

    }

    public static String getNameTaskCalendar() {
        return nameTask;
    }


    public static String getDescriptionTaskCalendar() {
        return descriptionTask;
    }

    public Task getTask(Long id) {
        Optional<Task> optionalTask = taskRepository.findById(id);

//        if (optionalTask.isPresent()) {
//            return optionalTask.get();
//        } else {
//            logger.warn("Task not found!");
//            throw new RuntimeException("Task not found!");
//        }
        return optionalTask.orElseThrow(() -> new NotFoundException("Task not found!", "task.not.found"));
    }

    public Task saveTask(Task task) {
        task.setStage("To do");

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = "";
        if (principal instanceof UserInfo) {
            username = ((UserInfo)principal).getUsername();

        } else {
            username = principal.toString();

        }

        User user = userRepository.getUserByEmail(username);
        user.setLastTaskName(task.getName());
        user.setLastTaskDescription(task.getDescription());
        userRepository.save(user);

        System.out.println(username);
        task.setUser_name(username);
        return taskRepository.save(task);
    }
    public void deleteTask(Long id){
        Optional<Task> taskOptional = taskRepository.findById(id);
        if(taskOptional.isPresent()){
            taskRepository.delete(taskOptional.get());
        }else{
            throw new NotFoundException("Task not found!", "task.not.found");
        }
    }

//    public Task updateTask(Long id, Task taskUpdated){
//        Optional<Task> taskOptional = taskRepository.findById(id);
//        if(taskOptional.isPresent()){
//            taskUpdated.setId(id);
//            return taskRepository.save(taskUpdated);
//
//        }else {
//            throw new RuntimeException("Task not found!");
//        }
//    }

    public Task updateTask(Long id, Task taskUpdated) {
        Optional<Task> taskOptional = taskRepository.findById(id);

        if (taskOptional.isPresent()) {
            taskUpdated.setId(id);
            taskUpdated.setName(taskUpdated.getName() == null ? taskOptional.get().getName() : taskUpdated.getName());
            taskUpdated.setDeadline(taskUpdated.getDeadline() == null ? taskOptional.get().getDeadline() : taskUpdated.getDeadline());
            taskUpdated.setDescription(taskUpdated.getDescription() == null ? taskOptional.get().getDescription() : taskUpdated.getDescription());
            taskUpdated.setStage(taskUpdated.getStage() == null ? taskOptional.get().getStage() : taskUpdated.getStage());

            return taskRepository.save(taskUpdated);
        } else {
            throw new NotFoundException("Task not found!", "task.not.found");
        }
    }


    public Task getDemoTask(){
        Task task = new Task();
        task.setId(1L);

        task.setName("Eseu biologie");
        task.setDeadline(LocalDate.now().plusDays(10));
        task.setStage("To do");
        task.setDescription("Generic task description..");

        return task;

    }
}
