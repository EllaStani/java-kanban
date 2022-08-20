package managers;

import org.junit.jupiter.api.Test;
import tasks.Task;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    @Override
    FileBackedTasksManager getManager() {
        return new FileBackedTasksManager("file_backed_tasks_manager_file_name");
    }

    @Test
    void testSystemOutPrint() {
        TaskManager manager = Managers.getDefault();
        Task task = new Task(TypeTask.TASK, "Задача1", "Задача1.Описание",
                LocalDateTime.of(2022, 8, 1, 0, 0),
                (long) (60 * 24 * 3));
        manager.createTask(task);
        manager.getTaskById(task.getId());

        TaskManager managerFile = FileBackedTasksManager.loadFromFile("resources/tasks.csv");
        final List<Task> tasks = managerFile.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }
}