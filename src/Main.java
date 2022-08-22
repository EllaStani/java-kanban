import managers.*;
import tasks.Task;
import tasks.Epic;
import tasks.Subtask;

import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        /*
        Александр, добрый день!
        Спасибо за комментарии, все попыталась исправить.
        Писала в Слэк, но решила послать новую версию не дожидаясь ответа.
В ТЗ сказано: "Отсортируйте все задачи по приоритету — то есть по startTime. Если дата старта не задана,
добавьте задачу в конец списка задач, подзадач, отсортированных по startTime."
Мое понимание такое: если не задан startTime, то по-умолчанию ставится now(), если не задан duration, то - 0.
Эти задачи уменя добавляются в конец списка всех задач в списке приоритетов. Это илюстрируют задачи 10 (самая первая) и
8 (самая последняя).
Если будет несколько таких задач, то я подумала, что now() вроде должно давать уникальную дату, и повторений не будет.
А если duration = 0, то считаю, что "сроки задачи не определены" (отразила это в toString), и в проверке на пересечение
 периодов эти задачи не участвуют. Прошу уточнить, как нужно правильно сделать. Спасибо.
         */
        System.out.println("Поехали!");

        TaskManager manager = Managers.getDefault();

        Task task10 = new Task(TypeTask.TASK, "Задача10", "Задача10.Описание",
                LocalDateTime.now(), (long) 0);
        manager.createTask(task10);

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