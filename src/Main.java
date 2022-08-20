import managers.*;
import tasks.Task;
import tasks.Epic;
import tasks.Subtask;

import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager manager = Managers.getDefault();

        Task task1 = new Task(TypeTask.TASK, "Задача1",
                "Задача1.Описание",
                LocalDateTime.of(2022, 7, 1, 0, 0, 0),
                (long) (60 * 24 * 14));
        manager.createTask(task1);
        Task task2 = new Task(TypeTask.TASK, "Задача2",
                "Задача2.Описание",
                LocalDateTime.of(2022, 6, 1, 0, 0, 0), (long) (60 * 24 * 7));
        manager.createTask(task2);

        Epic epic1 = new Epic(TypeTask.EPIC, "Эпик1",
                "Эпик3.Описание", LocalDateTime.now(), (long) (0));
        manager.createEpic(epic1);

        Subtask subtask1 = new Subtask(TypeTask.SUBTASK, "Подзадача1",
                "Подзадача1.Описание",
                LocalDateTime.of(2022, 8, 1, 0, 0, 0),
                (long) (60 * 24 * 3), epic1.getId());
        manager.createSubTask(subtask1);
        subtask1.setStatus(StatusTask.DONE);
        Subtask subtask2 = new Subtask(TypeTask.SUBTASK, "Подзадача2",
                "Подзадача2.Описание",
                LocalDateTime.of(2022, 8, 19, 0, 0, 0),
                (long) (60 * 24 * 3), epic1.getId());
        manager.createSubTask(subtask2);
        Subtask subtask3 = new Subtask(TypeTask.SUBTASK, "Подзадача3",
                "Подзадача3.Описание",
                LocalDateTime.of(2022, 8, 05, 0, 0, 0),
                (long) (60 * 24 * 30), epic1.getId());
        manager.createSubTask(subtask3);

        Epic epic2 = new Epic(TypeTask.EPIC, "Эпик2",
                "Эпик2.Описание",
                LocalDateTime.of(2022, 8, 19, 12, 0, 0),
                (long) (60 * 24 * 3));
        manager.createEpic(epic2);

        Task task8 = new Task(TypeTask.TASK, "Задача8", "Задача8.Описание",
                LocalDateTime.now(), (long) 0);
        manager.createTask(task8);

        System.out.println(manager.getPrioritizedTasks());
    }
}