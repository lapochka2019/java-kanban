package service;

import model.Task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {
    HashMap<Integer, Node> history = new HashMap<>();
    Node first;
    Node last;
    ArrayList<Task> historyList = new ArrayList<>();
    /**Я не очень поняла, для чего нам еще ArrayList*/

    @Override
    public ArrayList<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void add(Task task) {
       //Если Task пустой
       if(task==null){
           return;
       }
       //Если данный Task уже просмотрен
       if(history.containsKey(task.getId())){
           removeNode(history.get(task.getId()));
       }
       //Если это первый элемент связного списка
        Node newNode = new Node(task);
        if (last==null){
            first = newNode;
            last = newNode;
        }else{
            linkLast(newNode);
        }
        history.put(task.getId(), newNode);
    }
    @Override
    public void remove(int id){
        if(history.containsKey(id)){
            Node node = history.get(id);
            removeNode(node);
            history.remove(id);
        }
    }

    private void linkLast(Node newNode){
        //Нужно ли добавить проверку на пустой таск?
        if(last==null){
            first = newNode;
            last = newNode;
        }else{
            newNode.previous = last;
            last.next = newNode;
            last = newNode;
        }
    }

    private void removeNode(Node node){
        //Если узел пуст
        if(node==null){
            return;
        }
        Node nextNode = node.next;
        Node previousNode = node.previous;
        //Если это был единственный элемент списка
        if(nextNode==null&&previousNode==null){
            first = null;
            last = null;
        } else if(nextNode==null){ //Если node - хвост
            last = node.previous;
            last.next = null;
        } else if(previousNode==null){ //Если node - голова
            first = node.next;
            first.previous = null;
        }else {
            previousNode.next = node.next;
            nextNode.previous = node.previous;
        }
    }

    private ArrayList<Task> getTasks(){
        ArrayList <Task> tasks = new ArrayList<>();
        Node temp = first;
        while (temp!=null){
            tasks.add(temp.task);
            temp=temp.next;
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