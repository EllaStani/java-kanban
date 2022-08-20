package managers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {

    private HistoryManager historyManager;

    private Task task;
    private Epic epic;
    private Subtask subtask;

    @BeforeEach
    void init() {
        historyManager = new InMemoryHistoryManager();

        task = new Task(TypeTask.TASK, "Задача1", "Задача1.Описание",
                LocalDateTime.of(2022, 8, 1, 0, 0),
                (long) (60 * 24 * 3));
        task.setId(1);
        epic = new Epic(TypeTask.EPIC, "Эпик", "Эпик.Описание", LocalDateTime.now(), 0L);
        epic.setId(2);
        subtask = new Subtask(TypeTask.SUBTASK, "Подзадача1", "Подзадача1.Описание",
                LocalDateTime.of(2022, 8, 5, 0, 0),
                (long) (60 * 24 * 5), epic.getId());
        subtask.setId(3);

    }

    @AfterEach
    void clear() {
        historyManager = null;
    }

    @Test
    void testAddHistory() {
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История пустая.");
        assertEquals(1, history.size(), "История пустая.");

    }

    @Test
    void testForEmptyListTask() {
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не возвращается.");
        assertEquals(0, history.size(), "История не пустая.");
    }

    @Test
    void testForDuplicationTasks() {
        historyManager.add(task);
        historyManager.add(task);

        final List<Task> history = historyManager.getHistory();

        assertNotNull(history, "История не возвращается.");
        assertEquals(1, history.size(), "Неверное количество задач в истории.");
    }

    @Test
    void testRemoveFirst() {
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);

        List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не возвращается.");
        assertEquals(3, history.size(), "Неверное количество задач в истории.");

        historyManager.remove(subtask.getId());
        history = historyManager.getHistory();
        assertNotNull(history, "История не возвращается.");
        assertEquals(2, history.size(), "Неверное количество задач в истории.");

        assertEquals(epic, history.get(0), "Неправильный порядок задач в истории.");
        assertEquals(task, history.get(1), "Неправильный порядок задач в истории.");
    }

    @Test
    void testRemoveMiddle() {
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);

        List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не возвращается.");
        assertEquals(3, history.size(), "Неверное количество задач в истории.");

        historyManager.remove(epic.getId());
        history = historyManager.getHistory();
        assertNotNull(history, "История не возвращается.");
        assertEquals(2, history.size(), "Неверное количество задач в истории.");

        assertEquals(subtask, history.get(0), "Неправильный порядок задач в истории.");
        assertEquals(task, history.get(1), "Неправильный порядок задач в истории.");
    }

    @Test
    void testRemoveLast() {
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);

        List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не возвращается.");
        assertEquals(3, history.size(), "Неверное количество задач в истории.");

        historyManager.remove(task.getId());
        history = historyManager.getHistory();
        assertNotNull(history, "История не возвращается.");
        assertEquals(2, history.size(), "Неверное количество задач в истории.");

        assertEquals(subtask, history.get(0), "Неправильный порядок задач в истории.");
        assertEquals(epic, history.get(1), "Неправильный порядок задач в истории.");
    }
}