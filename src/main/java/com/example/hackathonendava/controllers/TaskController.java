package com.example.hackathonendava.controllers;



import com.example.hackathonendava.model.Task;

import com.example.hackathonendava.repository.TaskRepository;
import com.example.hackathonendava.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class TaskController {
    private final TaskService taskService;
    private  boolean nameSort = false;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }
    @GetMapping({"/tasks"})
    public ModelAndView showAllTasks() {
        ModelAndView mav = new ModelAndView("tasks");
        //mav.addObject("tasks", taskService.getAllTasksByUserName());
        mav.addObject("tasks", taskService.getAllTasksByUserName());
        return mav;
    }


    @GetMapping("/addTaskForm")
    public ModelAndView addTaskForm() {
        ModelAndView mav = new ModelAndView("add-task-form");
        Task newTask = new Task();
        mav.addObject("task", newTask);
        return mav;
    }
    @PostMapping("/saveTask")
    public String saveTask(@ModelAttribute Task task) {
        taskService.saveTask(task);
        taskService.setNameTask(task.getName());
        taskService.setDescriptionTask(task.getDescription());
        return "redirect:/tasks";
    }

    //    public String changeNameVar() {
//        Boolean nameSort = false;
//        System.out.println(nameSort);
//        return "redirect:/tasks";
//    }
    @GetMapping("/showUpdateForm")
    public ModelAndView showUpdateForm(@RequestParam Long taskId) {
        ModelAndView mav = new ModelAndView("add-task-form");
        Task task = taskService.getTask(taskId);
        mav.addObject("task", task);
        return mav;
    }
    @GetMapping("/deleteTask")
    public String deleteTask(@RequestParam Long taskId) {
        taskService.deleteTask(taskId);
        return "redirect:/tasks";
    }



}