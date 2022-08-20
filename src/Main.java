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
                LocalDateTime.of(2022, 7, 6, 10, 30, 0),
                (long) (60 * 24 * 14));
        manager.createTask(task1);
        Task task2 = new Task(TypeTask.TASK, "Задача2",
                "Задача2.Описание",
                LocalDateTime.of(2022, 6, 4, 14, 0, 0), (long) (60 * 24 * 7));
        manager.createTask(task2);

        Epic task3 = new Epic(TypeTask.EPIC, "Эпик3",
                "Эпик3.Описание",
                LocalDateTime.of(2022, 8, 1, 18, 0, 0), (long) (0));
        manager.createEpic(task3);

        Subtask task4 = new Subtask(TypeTask.SUBTASK, "Подзадача4",
                "Подзадача4.Описание",
                LocalDateTime.of(2022, 8, 15, 18, 0, 0),
                (long) (60 * 24 * 3), 3);
        manager.createSubTask(task4);
        task4.setStatus(StatusTask.DONE);
        Subtask task5 = new Subtask(TypeTask.SUBTASK, "Подзадача5",
                "Подзадача5.Описание",
                LocalDateTime.of(2022, 8, 19, 18, 0, 0), (long) (60 * 24 * 3), 3);
        manager.createSubTask(task5);
        Subtask task6 = new Subtask(TypeTask.SUBTASK, "Подзадача6",
                "Подзадача6.Описание",
                LocalDateTime.of(2022, 8, 05, 18, 0, 0), (long) (60 * 24 * 30), 3);
        manager.createSubTask(task6);
        Epic task7 = new Epic(TypeTask.EPIC, "Эпик7",
                "Эпик7.Описание",
                LocalDateTime.of(2022, 5, 20, 12, 0, 0), (long) (60 * 24 * 3));
        manager.createEpic(task7);
        Task task8 = new Task(TypeTask.TASK, "Задача8", "Задача8.Описание",
                null, (long) 0);
        manager.createTask(task8);

        Task task10 = new Task(TypeTask.TASK, "Задача10", "Задача10.Описание",
                null, (long) 0);
        manager.createTask(task10);

        System.out.println(manager.getPrioritizedTasks());
    }
}