package managers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.lang.reflect.Executable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    private Task task;
    private Epic epic;
    private Subtask subtask;

    private TaskManager manager;

    abstract T getManager();

    protected int createTaskForTest(Task task) {
        manager.createTask(task);
        return task.getId();
    }

    protected int createEpicForTest(Epic epic) {
        manager.createEpic(epic);
        return epic.getId();
    }

    protected int createSubtaskForTest(Subtask subtask) {
        manager.createSubTask(subtask);
        return subtask.getId();
    }

    @BeforeEach
    public void init() {
        manager = getManager();
    }

    @AfterEach
    public void clear() {
        manager = null;
    }

    @Test
    void testAddNewTask() {
        Task task = new Task(TypeTask.TASK, "Задача1", "Задача1.Описание", LocalDateTime.now(), 0L);

        manager.createTask(task);
        Task newCreatedTask = manager.getAllTasks().get(0);

        final int taskId = newCreatedTask.getId();
        final Task savedTask = manager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = manager.getAllTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void testUpdatedTask() {
        Task task = new Task(TypeTask.TASK, "Задача1", "Задача1.Описание",
                LocalDateTime.of(2022, 8, 1, 0, 0),
                (long) (60 * 24 * 3));
        int idTask = createTaskForTest(task);
        Task taskUpdate = new Task(TypeTask.TASK, "Задача1 - new", "Задача1.Описание - new",
                LocalDateTime.of(2022, 8, 1, 0, 0),
                (long) (60 * 24 * 3));
        taskUpdate.setId(idTask);

        manager.updatedTask(taskUpdate);

        final List<Task> tasks = manager.getAllTasks();
        Task newUpdateTask = manager.getAllTasks().get(0);

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(taskUpdate, newUpdateTask, "Задача не обновлена.");
    }

    @Test
    void testUpdatedTaskWithPeriodsOverlap() {
        Task task = new Task(TypeTask.TASK, "Задача1", "Задача1.Описание",
                LocalDateTime.of(2022, 8, 1, 0, 0),
                (long) (60 * 24 * 3));
        int idTask = createTaskForTest(task);
        Task task1 = new Task(TypeTask.TASK, "Задача2", "Задача2.Описание",
                LocalDateTime.of(2022, 8, 5, 0, 0),
                (long) (60 * 24 * 3));
        int idTask1 = createTaskForTest(task1);

        Task taskUpdate = new Task(TypeTask.TASK, "Задача1 - new", "Задача1.Описание - new",
                LocalDateTime.of(2022, 8, 3, 0, 0),
                (long) (60 * 24 * 3));
        taskUpdate.setId(task1.getId());

        manager.updatedTask(taskUpdate);

        final List<Task> tasks = manager.getAllTasks();
        Task newUpdateTask = manager.getAllTasks().get(0);

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(2, tasks.size(), "Неверное количество задач.");
        assertEquals(task, newUpdateTask, "Задача обновляется при пересечении периодов.");
    }

    @Test
    void testAddNewSubtask() {
        Epic epic = new Epic(TypeTask.EPIC, "Эпик", "Эпик.Описание", LocalDateTime.now(), 0L);
        int id = createEpicForTest(epic);

        Subtask subtask = new Subtask(TypeTask.SUBTASK, "Подзадача1", "Подзадача1.Описание",
                LocalDateTime.now(), 0L, id);

        manager.createSubTask(subtask);
        Subtask newCreatedSubtask = manager.getAllSubtasks().get(0);

        final int idSubtask = newCreatedSubtask.getId();
        final Subtask savedSubtask = manager.getSubTaskById(idSubtask);

        assertNotNull(savedSubtask, "Задача не найдена.");
        assertEquals(subtask, savedSubtask, "Задачи не совпадают.");

        final List<Subtask> subtasks = manager.getAllSubtasks();

        assertNotNull(subtasks, "Задачи на возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество задач.");
        assertEquals(subtask, subtasks.get(0), "Задачи не совпадают.");

        final List<Subtask> subtasksEpic = epic.getListSubTask();

        assertEquals(1, subtasksEpic.size(), "Неверное количество подзадач у Эпика.");
        assertEquals(subtask, subtasksEpic.get(0), "Задачи не совпадают.");
    }

    @Test
    void testAddNewSubtaskWithNotExistEpic() {
        Subtask subtask = new Subtask(TypeTask.SUBTASK, "Подзадача1", "Подзадача1.Описание",
                LocalDateTime.now(), 0L, 3);
        manager.createSubTask(subtask);

        final List<Subtask> subtasks = manager.getAllSubtasks();
        assertEquals(0, subtasks.size(), "Неверное количество подзадач.");
    }

    @Test
    void testUpdatedSubtask() {
        Epic epic = new Epic(TypeTask.EPIC, "Эпик", "Эпик.Описание", LocalDateTime.now(), 0L);
        int idEpic = createEpicForTest(epic);

        Subtask subtask = new Subtask(TypeTask.SUBTASK, "Подзадача1", "Подзадача1.Описание",
                LocalDateTime.of(2022, 8, 1, 0, 0), (long) (60 * 24 * 5), idEpic);
        int idSubtask = createSubtaskForTest(subtask);

        Subtask subtaskUpdate = new Subtask(TypeTask.SUBTASK, "Подзадача1 - new",
                "Подзадача1.Описание - new",
                LocalDateTime.of(2022, 8, 1, 0, 0), (long) (60 * 24 * 5), idEpic);
        subtaskUpdate.setId(idSubtask);

        manager.updatedSubTask(subtaskUpdate);

        Subtask newUpdateSubtask = manager.getAllSubtasks().get(0);

        assertNotNull(newUpdateSubtask, "Задача не найдена.");

        final List<Subtask> subtasks = epic.getListSubTask();

        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач у Эпика.");
        assertEquals(subtaskUpdate, newUpdateSubtask, "Ошибка обновления. Подзадачи не совпадают.");
    }

    @Test
    void testUpdatedSubtaskWithPeriodsOverlap() {
        Task task = new Task(TypeTask.TASK, "Задача1", "Задача1.Описание",
                LocalDateTime.of(2022, 8, 5, 0, 0),
                (long) (60 * 24 * 5));
        int idTask = createTaskForTest(task);
        Epic epic = new Epic(TypeTask.EPIC, "Эпик", "Эпик.Описание", LocalDateTime.now(), 0L);
        int idEpic = createEpicForTest(epic);

        Subtask subtask = new Subtask(TypeTask.SUBTASK, "Подзадача1", "Подзадача1.Описание",
                LocalDateTime.of(2022, 8, 1, 0, 0), (long) (60 * 24 * 3), idEpic);
        int idSubtask = createSubtaskForTest(subtask);

        Subtask subtaskUpdate = new Subtask(TypeTask.SUBTASK, "Подзадача1 - new",
                "Подзадача1.Описание - new",
                LocalDateTime.of(2022, 8, 1, 0, 0), (long) (60 * 24 * 5), idEpic);
        subtaskUpdate.setId(idSubtask);

        manager.updatedSubTask(subtaskUpdate);

        Subtask newUpdateSubtask = manager.getAllSubtasks().get(0);

        assertNotNull(newUpdateSubtask, "Задача не найдена.");

        final List<Subtask> subtasks = epic.getListSubTask();

        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач у Эпика.");
        assertEquals(subtask, newUpdateSubtask, "Подзадача обновляется при пересечении периодов.");
    }

    @Test
    void testStatusForEmptyEpic() {
        Epic epic = new Epic(TypeTask.EPIC, "Эпик", "Эпик.Описание", LocalDateTime.now(), 0L);
        int id = createEpicForTest(epic);

        StatusTask epicStatus = manager.getStatusById(id);
        assertNotNull(epicStatus, "Эпик не найден!");
        assertEquals(1, manager.getAllEpics().size(), "Эпик должен быть один!");
        assertEquals(StatusTask.NEW, epicStatus, "У пустого эпика статус должен быть NEW!");
    }

    @Test
    void testEpicStatusForAllNewSubtasks() {
        Epic epic = new Epic(TypeTask.EPIC, "Эпик", "Эпик.Описание", LocalDateTime.now(), 0L);
        int id = createEpicForTest(epic);

        Subtask task1 = new Subtask(TypeTask.SUBTASK, "Подзадача1", "Подзадача1.Описание", LocalDateTime.now(), 0L, id);
        Subtask task2 = new Subtask(TypeTask.SUBTASK, "Подзадача2", "Подзадача2.Описание", LocalDateTime.now(), 0L, id);

        manager.createSubTask(task1);
        manager.createSubTask(task2);

        StatusTask epicStatus = manager.getStatusById(id);
        assertNotNull(epicStatus, "Эпик не найден!");
        assertEquals(1, manager.getAllEpics().size(), "Эпик должен быть один!");
        assertEquals(StatusTask.NEW, epicStatus,
                String.format("Для данного Эпика статус должен быть NEW! Текущий статус: %s", epicStatus));
    }

    @Test
    void testEpicStatusForAllDoneSubtasks() {
        Epic epic = new Epic(TypeTask.EPIC, "Эпик", "Эпик.Описание", LocalDateTime.now(), 0L);
        int id = createEpicForTest(epic);

        Subtask task1 = new Subtask(TypeTask.SUBTASK, "Подзадача1", "Подзадача1.Описание", LocalDateTime.now(), 0L, id);
        Subtask task2 = new Subtask(TypeTask.SUBTASK, "Подзадача2", "Подзадача2.Описание", LocalDateTime.now(), 0L, id);
        task1.setStatus(StatusTask.DONE);
        task2.setStatus(StatusTask.DONE);

        manager.createSubTask(task1);
        manager.createSubTask(task2);

        StatusTask epicStatus = manager.getStatusById(id);
        assertNotNull(epicStatus, "Эпик не найден!");
        assertEquals(1, manager.getAllEpics().size(), "Эпик должен быть один!");
        assertEquals(StatusTask.DONE, epicStatus, String.format("Для данного Эпика статус должен быть DONE! Текущий статус: %s", epicStatus));
    }

    @Test
    void testEpicStatusForNewAndDoneSubtasks() {
        Epic epic = new Epic(TypeTask.EPIC, "Эпик", "Эпик.Описание", LocalDateTime.now(), 0L);
        int id = createEpicForTest(epic);

        Subtask task1 = new Subtask(TypeTask.SUBTASK, "Подзадача1", "Подзадача1.Описание", LocalDateTime.now(), 0L, id);
        Subtask task2 = new Subtask(TypeTask.SUBTASK, "Подзадача2", "Подзадача2.Описание", LocalDateTime.now(), 0L, id);
        task2.setStatus(StatusTask.DONE);

        manager.createSubTask(task1);
        manager.createSubTask(task2);

        StatusTask epicStatus = manager.getStatusById(id);
        assertNotNull(epicStatus, "Эпик не найден!");
        assertEquals(1, manager.getAllEpics().size(), "Эпик должен быть один!");
        assertEquals(StatusTask.IN_PROGRESS, epicStatus, String.format("Для данного Эпика статус должен быть IN_PROGRESS! Текущий статус: %s", epicStatus));
    }

    @Test
    void testEpicStatusForAllInProgressSubtasks() {
        Epic epic = new Epic(TypeTask.EPIC, "Эпик", "Эпик.Описание", LocalDateTime.now(), 0L);
        int id = createEpicForTest(epic);

        Subtask task1 = new Subtask(TypeTask.SUBTASK, "Подзадача1", "Подзадача1.Описание", LocalDateTime.now(), 0L, id);
        Subtask task2 = new Subtask(TypeTask.SUBTASK, "Подзадача2", "Подзадача2.Описание", LocalDateTime.now(), 0L, id);
        task1.setStatus(StatusTask.IN_PROGRESS);
        task2.setStatus(StatusTask.IN_PROGRESS);

        manager.createSubTask(task1);
        manager.createSubTask(task2);

        StatusTask epicStatus = manager.getStatusById(id);
        assertNotNull(epicStatus, "Эпик не найден!");
        assertEquals(1, manager.getAllEpics().size(), "Эпик должен быть один!");
        assertEquals(StatusTask.IN_PROGRESS, epicStatus,
                String.format("Для данного Эпика статус должен быть IN_PROGRESS! Текущий статус: %s", epicStatus));
    }

    @Test
    void testSetStartTimeEndTimeForEpic() {
        Epic epic = new Epic(TypeTask.EPIC, "Эпик", "Эпик.Описание", LocalDateTime.now(), 0L);
        int id = createEpicForTest(epic);

        Subtask task1 = new Subtask(TypeTask.SUBTASK, "Подзадача1", "Подзадача1.Описание",
                LocalDateTime.of(2022, 8, 15, 0, 0), (long) (60 * 24 * 5), id);
        Subtask task2 = new Subtask(TypeTask.SUBTASK, "Подзадача2", "Подзадача2.Описание",
                LocalDateTime.of(2022, 8, 5, 0, 0), (long) (60 * 24 * 5), id);

        manager.createSubTask(task1);
        manager.createSubTask(task2);

        LocalDateTime epicStart = epic.getStartTime();
        LocalDateTime epicEnd = epic.getEndTimeEpic();

        assertEquals(task2.getStartTime(), epicStart, "Неправильная дата старта Эпика");
        assertEquals(task1.getEndTime(), epicEnd, "Неправильная дата окончания Эпика");
    }

    @Test
    void testGetAllTasks() {
        Task task = new Task(TypeTask.TASK, "Задача1", "Задача1.Описание",
                LocalDateTime.of(2022, 8, 1, 0, 0),
                (long) (60 * 24 * 3));
        int idTask = createTaskForTest(task);

        final List<Task> tasks = manager.getAllTasks();
        Task newUpdateTask = manager.getAllTasks().get(0);

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, newUpdateTask, "Задачи не совпадают.");
    }

    @Test
    void testGetAllTasksForEmptyListTask() {

        final List<Task> tasks = manager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(0, tasks.size(), "Неверное количество задач.");
    }

    @Test
    void testGetAllEpics() {
        Epic epic = new Epic(TypeTask.EPIC, "Эпик", "Эпик.Описание",
                LocalDateTime.now(), 0l);
        int idTask = createEpicForTest(epic);

        final List<Epic> epics = manager.getAllEpics();
        Task newUpdateEpic = manager.getAllEpics().get(0);

        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic, newUpdateEpic, "Эпики не совпадают.");
    }

    @Test
    void testGetAllEpicsForEmptyListEpic() {

        final List<Epic> epics = manager.getAllEpics();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(0, epics.size(), "Неверное количество эпиков.");
    }

    @Test
    void testGetAllSubtasks() {
        Epic epic = new Epic(TypeTask.EPIC, "Эпик", "Эпик.Описание", LocalDateTime.now(), 0L);
        int id = createEpicForTest(epic);

        Subtask subtask = new Subtask(TypeTask.SUBTASK, "Подзадача1", "Подзадача1.Описание",
                LocalDateTime.now(), 0L, id);
        manager.createSubTask(subtask);

        final List<Subtask> subtasks = manager.getAllSubtasks();
        Task newUpdateTask = manager.getAllSubtasks().get(0);

        assertNotNull(subtasks, "Подзадачи на возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask, newUpdateTask, "Подзадачи не совпадают.");
    }

    @Test
    void testGetAllSubtasksForEmptyListSubtask() {

        final List<Subtask> subtasks = manager.getAllSubtasks();

        assertNotNull(subtasks, "Задачи не возвращаются.");
        assertEquals(0, subtasks.size(), "Неверное количество задач.");
    }

    @Test
    void testGetTaskById() {
        Task task = new Task(TypeTask.TASK, "Задача1", "Задача1.Описание", LocalDateTime.now(), 0L);

        final int taskId = createTaskForTest(task);

        final Task savedTask = manager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
    }

    @Test
    void testGetTaskByIdForNotExistID() {
        Task task = new Task(TypeTask.TASK, "Задача1", "Задача1.Описание", LocalDateTime.now(), 0L);

        final int taskId = createTaskForTest(task);

        final Task savedTask = manager.getTaskById(2);

        assertNull(savedTask, "Задача существует.");
        assertNotEquals(task, savedTask, "Задачи совпадают.");
    }

    @Test
    void testGetEpicById() {
        Epic epic = new Epic(TypeTask.EPIC, "Эпик", "Эпик.Описание",
                LocalDateTime.now(), 0l);
        int idEpic = createEpicForTest(epic);

        final Epic savedEpic = manager.getEpicById(idEpic);

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");
    }

    @Test
    void testGetEpicByIdForNotExistID() {
        Epic epic = new Epic(TypeTask.EPIC, "Эпик", "Эпик.Описание",
                    LocalDateTime.now(), 0l);
        final int idEpic = createEpicForTest(epic);
        final Epic savedEpic = manager.getEpicById(2);

        assertNull(savedEpic, "Эпик существует.");
        assertNotEquals(epic, savedEpic, "Эпики совпадают.");
    }

    @Test
    void testGetSubtaskById() {
        Epic epic = new Epic(TypeTask.EPIC, "Эпик", "Эпик.Описание", LocalDateTime.now(), 0L);
        int id = createEpicForTest(epic);

        Subtask subtask = new Subtask(TypeTask.SUBTASK, "Подзадача1", "Подзадача1.Описание",
                LocalDateTime.now(), 0L, id);
        final int idSubtask = createSubtaskForTest(subtask);

        final Subtask savedSubtask = manager.getSubTaskById(idSubtask);

        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(subtask, savedSubtask, "Подзадачи не совпадают.");
    }

    @Test
    void testGetSubtaskByIdForNotExistID() {
        Epic epic = new Epic(TypeTask.EPIC, "Эпик", "Эпик.Описание", LocalDateTime.now(), 0L);
        int id = createEpicForTest(epic);

        Subtask subtask = new Subtask(TypeTask.SUBTASK, "Подзадача1", "Подзадача1.Описание",
                LocalDateTime.now(), 0L, id);
        final int idSubtask = createSubtaskForTest(subtask);

        final Subtask savedSubtask = manager.getSubTaskById(3);

        assertNull(savedSubtask, "Подзадача существует.");
        assertNotEquals(subtask, savedSubtask, "Подзадачи совпадают.");
    }

    @Test
    void testPeriodsNotOverlapForCreateTask() {
        // Периоды не пересекаются
        Task task1 = new Task(TypeTask.TASK, "Задача1", "Задача1.Описание",
                LocalDateTime.of(2022, 8, 5, 0, 0), (long) (60 * 24 * 5));
        Task task2 = new Task(TypeTask.TASK, "Задача2", "Задача2.Описание",
                LocalDateTime.of(2022, 8, 1, 0, 0), (long) (60 * 24 * 4));
        manager.createTask(task1);
        manager.createTask(task2);

        final List<Task> tasks = manager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(2, tasks.size(), "Неверное количество задач.");

    }

    @Test
    void testPeriodsOverlapForCreateTask() {
        // Периоды пересекаются
        Task task1 = new Task(TypeTask.TASK, "Задача1", "Задача1.Описание",
                LocalDateTime.of(2022, 8, 5, 0, 0), (long) (60 * 24 * 5));
        Task task2 = new Task(TypeTask.TASK, "Задача2", "Задача2.Описание",
                LocalDateTime.of(2022, 8, 1, 0, 0), (long) (60 * 24 * 5));
        manager.createTask(task1);
        manager.createTask(task2);

        final List<Task> tasks = manager.getAllTasks();

        assertEquals(1, tasks.size(), "Неверное количество задач.");

    }

    @Test
    void testDeleteTaskById() {
        Task task = new Task(TypeTask.TASK, "Задача1", "Задача1.Описание", LocalDateTime.now(), 0L);

        manager.createTask(task);
        Task newCreatedTask = manager.getAllTasks().get(0);

        manager.deleteTaskById(newCreatedTask.getId());

        final List<Task> tasks = manager.getAllTasks();
        final List<Task> history = manager.getHistory();
        final List<Task> priorTasks = manager.getPrioritizedTasks();

        assertEquals(0, tasks.size(), "Неверное количество задач.");
        assertEquals(0, history.size(), "Неверное количество задач в истории.");
        assertEquals(0, priorTasks.size(), "Неверное количество приоритетных задач.");
    }

    @Test
    void testDeleteSubTaskById() {
        Epic epic = new Epic(TypeTask.EPIC, "Эпик", "Эпик.Описание", LocalDateTime.now(), 0L);
        int id = createEpicForTest(epic);

        Subtask task1 = new Subtask(TypeTask.SUBTASK, "Подзадача1", "Подзадача1.Описание",
                LocalDateTime.of(2022, 8, 10, 0, 0), (long) (60 * 24 * 5), id);
        Subtask task2 = new Subtask(TypeTask.SUBTASK, "Подзадача2", "Подзадача2.Описание",
                LocalDateTime.of(2022, 8, 5, 0, 0), (long) (60 * 24 * 3), id);

        manager.createSubTask(task1);
        manager.createSubTask(task2);

        final List<Subtask> newSubtasks = manager.getAllSubtasks();
        assertEquals(2, newSubtasks.size(), "Неверное количество новых задач.");

        manager.deleteSubTaskById(task1.getId());

        final List<Subtask> subtasks = manager.getAllSubtasks();
        final List<Subtask> epicSubtasks = epic.getListSubTask();
        final List<Task> history = manager.getHistory();
        final List<Task> priorTasks = manager.getPrioritizedTasks();

        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(1, epicSubtasks.size(), "Неверное количество задач у эпика.");
        assertEquals(1, history.size(), "Неверное количество задач в истории.");
        assertEquals(1, priorTasks.size(), "Неверное количество приоритетных задач.");
    }

    @Test
    void testDeleteEpicById() {
        Epic epic = new Epic(TypeTask.EPIC, "Эпик", "Эпик.Описание", LocalDateTime.now(), 0L);
        int id = createEpicForTest(epic);

        Subtask task1 = new Subtask(TypeTask.SUBTASK, "Подзадача1", "Подзадача1.Описание",
                LocalDateTime.of(2022, 8, 10, 0, 0), (long) (60 * 24 * 5), id);
        Subtask task2 = new Subtask(TypeTask.SUBTASK, "Подзадача2", "Подзадача2.Описание",
                LocalDateTime.of(2022, 8, 5, 0, 0), (long) (60 * 24 * 3), id);

        manager.createSubTask(task1);
        manager.createSubTask(task2);

        final List<Epic> newEpics = manager.getAllEpics();
        final List<Subtask> newSubtasks = manager.getAllSubtasks();
        final List<Subtask> epicSubtasks = epic.getListSubTask();

        assertEquals(1, newEpics.size(), "Неверное количество новых эпиков.");
        assertEquals(2, newSubtasks.size(), "Неверное количество новых подзадач.");
        assertEquals(2, epicSubtasks.size(), "Неверное количество задач у эпика.");

        manager.deleteEpicById(epic.getId());

        final List<Epic> epics = manager.getAllEpics();
        final List<Subtask> subtasks = manager.getAllSubtasks();
        final List<Task> history = manager.getHistory();
        final List<Task> priorTasks = manager.getPrioritizedTasks();

        assertEquals(0, epics.size(), "Неверное количество оставшихся эпиков.");
        assertEquals(0, subtasks.size(), "Неверное количество оставшихся подзадач.");
        assertEquals(0, history.size(), "Неверное количество задач в истории .");
        assertEquals(0, priorTasks.size(), "Неверное количество приоритетных задач.");
    }

    @Test
    void testDeleteAllTask() {
        Task task = new Task(TypeTask.TASK, "Задача1", "Задача1.Описание",
                LocalDateTime.of(2022, 8, 1, 0, 0), (long) (60 * 24 * 3));
        manager.createTask(task);

        Epic epic = new Epic(TypeTask.EPIC, "Эпик", "Эпик.Описание", LocalDateTime.now(), 0L);
        int id = createEpicForTest(epic);

        Subtask task1 = new Subtask(TypeTask.SUBTASK, "Подзадача1", "Подзадача1.Описание",
                LocalDateTime.of(2022, 8, 10, 0, 0), (long) (60 * 24 * 5), id);
        Subtask task2 = new Subtask(TypeTask.SUBTASK, "Подзадача2", "Подзадача2.Описание",
                LocalDateTime.of(2022, 8, 5, 0, 0), (long) (60 * 24 * 3), id);

        manager.createSubTask(task1);
        manager.createSubTask(task2);

        manager.deleteAllTask();

        final List<Task> tasks = manager.getAllTasks();
        final List<Epic> epics = manager.getAllEpics();
        final List<Subtask> subtasks = manager.getAllSubtasks();
        final List<Task> priorTasks = manager.getPrioritizedTasks();

        assertEquals(0, tasks.size(), "Неверное количество оставшихся задач.");
        assertEquals(0, epics.size(), "Неверное количество оставшихся эпиков.");
        assertEquals(0, subtasks.size(), "Неверное количество оставшихся подзадач.");
        assertEquals(0, priorTasks.size(), "Неверное количество приоритетных задач.");
    }

    @Test
    void testPrioritizedTasksForEmptyListTask() {
        final List<Task> emptyPriorities = manager.getPrioritizedTasks();
        assertNotNull(emptyPriorities, "Список задач по пиоритетам не пустой");
    }

    @Test
    void testPrioritizedTasks() {
        Task task1 = new Task(TypeTask.TASK, "Задача1", "Задача1.Описание",
                LocalDateTime.now(), 0l);
        Task task2 = new Task(TypeTask.TASK, "Задача2", "Задача2.Описание",
                LocalDateTime.of(2022, 8, 1, 0, 0),
                (long) (60 * 24 * 3));
        Task task3 = new Task(TypeTask.TASK, "Задача3", "Задача3.Описание",
                LocalDateTime.of(2022, 6, 1, 0, 0),
                (long) (60 * 24 * 2));
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);

        final List<Task> priorities = manager.getPrioritizedTasks();

        assertNotNull(priorities, "Список задач по приоритетам пустой");
        assertEquals(3, priorities.size(), "Неверное количество задач в списке приоритетов.");
        assertEquals(manager.getAllTasks().get(0), priorities.get(2), "Задача не соотвествует порядку приоритета");
        assertEquals(manager.getAllTasks().get(1), priorities.get(1), "Задача не соотвествует порядку приоритета");
        assertEquals(manager.getAllTasks().get(2), priorities.get(0), "Задача не соотвествует порядку приоритета");
    }

}