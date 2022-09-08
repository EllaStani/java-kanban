package managers;

import Server.KVServer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import utility.TypeTask;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerTest extends TaskManagerTest<TaskManager> {
    private KVServer kvServer;

    HttpTaskManagerTest() throws IOException {
        kvServer = Managers.getDefaultKVServer();
    }

    @Override
    TaskManager getManager() {
        return Managers.getDefault();
    }

    @Test
    void testEpicWithoutSubtask() throws IOException {
        TaskManager manager = Managers.getDefault();

        Epic epic1 = new Epic(TypeTask.EPIC, "Эпик1", "Эпик1.Описание", LocalDateTime.now(), 0L);
        manager.createEpic(epic1);

        TaskManager newManager = HttpTaskManager.loadFromServer(KVServer.PORT);

        final List<Task> tasks = newManager.getAllTasks();
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(0, tasks.size(), "Неверное количество задач.");

        final List<Epic> epics = newManager.getAllEpics();
        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество Эпиков.");
        assertEquals(epic1, epics.get(0), "Эпики не совпадают.");

        final List<Subtask> subtasks = newManager.getAllSubtasks();

        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(0, subtasks.size(), "Неверное количество Подзадач.");

        final List<Task> history = newManager.getHistory();
        assertNotNull(history, "История не возвращается.");
        assertEquals(0, history.size(), "История не пустая.");
    }

    @Test
    void testGetEmptyHistory() throws IOException {
        TaskManager manager = Managers.getDefault();

        Task task = new Task(TypeTask.TASK, "Задача1", "Задача1.Описание",
                LocalDateTime.of(2022, 8, 1, 0, 0),
                (long) (60 * 24 * 3));
        manager.createTask(task);

        TaskManager newManager = HttpTaskManager.loadFromServer(KVServer.PORT);

        final List<Task> tasks = newManager.getAllTasks();
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");

        final List<Epic> epics = newManager.getAllEpics();
        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(0, epics.size(), "Неверное количество Эпиков.");

        final List<Subtask> subtasks = newManager.getAllSubtasks();
        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(0, subtasks.size(), "Неверное количество Подзадач.");

        final List<Task> history = newManager.getHistory();
        assertNotNull(history, "История не возвращается.");
        assertEquals(0, history.size(), "История не пустая.");

    }

    @Test
    void testFullHistory() throws IOException {
        TaskManager manager = Managers.getDefault();

        Task task = new Task(TypeTask.TASK, "Задача1", "Задача1.Описание",
                LocalDateTime.of(2022, 9, 1, 0, 0),
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

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");

        final List<Epic> epics = newManager.getAllEpics();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество Эпиков.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");

        final List<Subtask> subtasks = newManager.getAllSubtasks();
        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество Подзадач.");
        assertEquals(subtask, subtasks.get(0), "Подзадачи не совпадают.");

        final List<Task> history = newManager.getHistory();
        assertNotNull(history, "История не возвращается.");
        assertEquals(3, history.size(), "Неверное количество задач  в истории.");
    }

    @Test
    void testGetPriorities() throws IOException {
        TaskManager manager = Managers.getDefault();

        Task task = new Task(TypeTask.TASK, "Задача1", "Задача1.Описание",
                LocalDateTime.of(2022, 9, 1, 0, 0),
                (long) (60 * 24 * 3));
        manager.createTask(task);


        Epic epic = new Epic(TypeTask.EPIC, "Эпик1", "Эпик1.Описание", LocalDateTime.now(), 0L);
        manager.createEpic(epic);

        Subtask subtask = new Subtask(TypeTask.SUBTASK, "Подзадача1", "Подзадача1.Описание",
                LocalDateTime.of(2022, 8, 5, 0, 0),
                (long) (60 * 24 * 5), epic.getId());
        manager.createSubTask(subtask);

        TaskManager newManager = HttpTaskManager.loadFromServer(KVServer.PORT);

        final List<Task> priorities = newManager.getPrioritizedTasks();

        assertNotNull(priorities, "Список задач по приоритетам пустой");
        assertEquals(2, priorities.size(), "Неверное количество задач в списке приоритетов.");
        assertEquals(newManager.getAllTasks().get(0), priorities.get(1), "Задача не соотвествует порядку приоритета");
        assertEquals(newManager.getAllSubtasks().get(0), priorities.get(0), "Задача не соотвествует порядку приоритета");
    }
}