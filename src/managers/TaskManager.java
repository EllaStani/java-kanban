package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import utility.StatusTask;

import java.util.List;

public interface TaskManager {
    List<Task> getHistory();

    List<Task> getPrioritizedTasks();

    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<Subtask> getAllSubtasks();

    void createTask(Task task);

    void createSubTask(Subtask subTask);

    void createEpic(Epic epic);

    void updatedTask(Task task);

    void updatedEpic(Epic epic);

    void updatedSubTask(Subtask subTask);

    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubTaskById(int id);

    List<Subtask> getListSubTasks(int id);

    StatusTask getStatusById(int id);

    void deleteTaskById(int id);

    void deleteSubTaskById(int id);

    void deleteEpicById(int id);

    void deleteAllTasks();

}
