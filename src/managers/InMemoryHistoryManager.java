package managers;

import tasks.Task;
import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> mapHistory = new HashMap<>();
    private Node head = null;
    private Node tail = null;
    private int size = 0;

    @Override
    public void add(Task task) {
        remove(task.getId());
        linkLast(task);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        if (mapHistory.containsKey(id)) {
            Node node = mapHistory.get(id);
            if (node != null) {
                removeNode(node);
            }
            mapHistory.remove(id);
        }
    }

    private void removeNode(Node node) {
        size--;
        node.data = null;
        if ((node.next == null) && (node.prev == null)) {
            head = null;
            tail = null;
            return;
        } else if (node.next == null) {
            node.prev.next = null;
            tail = node.prev;
        } else if (node.prev == null) {
            node.next.prev = null;
            head = node.next;
        } else {
            //((node.prev != null) && (node.next != null)) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
    }

    private void linkLast(Task task) {
        final Node oldTail = tail;
        final Node newNode = new Node(oldTail, task, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
        size++;
        mapHistory.put(task.getId(), newNode);
    }

    private List<Task> getTasks() {
        List<Task> history = new ArrayList<>();
        Node node = tail;
        while (node != null) {
            history.add(node.data);
            node = node.prev;
        }
        return history;
    }

    private class Node {
        public Task data;
        public Node next;
        public Node prev;

        public Node(Node prev, Task data, Node next) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }
    }
}
