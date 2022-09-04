import Server.KVServer;

import managers.HttpTaskManager;
import managers.Managers;
import managers.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import utility.TypeTask;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        new KVServer().start();
        TaskManager manager = Managers.getDefault();

        Task task = new Task(TypeTask.TASK, "Задача1", "Задача1.Описание",
                LocalDateTime.of(2022, 8, 1, 0, 0),
                (long) (60 * 24 * 3));
        manager.createTask(task);

        Epic epic = new Epic(TypeTask.EPIC, "Эпик1", "Эпик1.Описание", LocalDateTime.now(), 0L);
        manager.createEpic(epic);

        Subtask subtask = new Subtask(TypeTask.SUBTASK, "Подзадача1", "Подзадача1.Описание",
                LocalDateTime.of(2022, 8, 5, 0, 0),
                (long) (60 * 24 * 5), epic.getId());
        manager.createSubTask(subtask);

        manager.getTaskById(task.getId());
        manager.getEpicById(epic.getId());
        manager.getSubTaskById(subtask.getId());

        TaskManager newManager = HttpTaskManager.loadFromServer(KVServer.PORT);

        final List<Task> tasks = newManager.getAllTasks();
        final List<Epic> epics = newManager.getAllEpics();
        final List<Subtask> subtasks = newManager.getAllSubtasks();
        System.out.println("tasks = " + tasks);
        System.out.println("epics = " + epics);
        System.out.println("subtasks = " + subtasks);
        System.out.println("History = " + manager.getHistory());

    }
}