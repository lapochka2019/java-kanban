package service;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {
    HashMap<Integer, Node> history = new HashMap<>();
    Node first;
    Node last;

    @Override
    public ArrayList<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void add(Task task) {
       //Если Task пустой
       if (task == null) {
           return;
       }
       //Получается я тут в любом случае вызываю метод удаления?
        removeNode(task.getId());
        Node newNode = new Node(task);
        linkLast(newNode);
        history.put(task.getId(), newNode);
    }

    @Override
    public void remove(int id) {
        if (history.containsKey(id)) {
            Node node = history.get(id);
            removeNode(id);
        }
    }

    private void linkLast(Node newNode) {
        if (last == null) {
            first = newNode;
            last = newNode;
        } else {
            newNode.previous = last;
            last.next = newNode;
            last = newNode;
        }
    }

    private void removeNode(int id) {
        //Если данный индекс не найден в истории, ничего не делаем?
        if (!history.containsKey(id)) {
            return;
        }
        Node node = history.get(id);
        //Если узел пуст
        if (node == null) {
            return;
        }
        Node nextNode = node.next;
        Node previousNode = node.previous;
        //Если это был единственный элемент списка
        if (nextNode == null && previousNode == null) {
            first = null;
            last = null;
        } else if (nextNode == null) { //Если node - хвост
            last = previousNode;
            last.next = null;
        } else if (previousNode == null) { //Если node - голова
            first = nextNode;
            first.previous = null;
        } else {
            previousNode.next = nextNode;
            nextNode.previous = previousNode;
        }
        history.remove(id);
    }

    private ArrayList<Task> getTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        Node temp = first;
        while (temp != null) {
            tasks.add(temp.task);
            temp = temp.next;
        }
        return tasks;
    }

    //Лектор рекомендовал сделать данный класс private static, так как в оригинальном LinkedList класс Node реализован именно так
    private static class Node {
        Task task;
        Node previous;
        Node next;

        public Node(Task task) {
            this.task = task;
        }
    }
}