

/*
Students Names 
Haneen Sami Mahmoud 202321075
Jana Yanal Jazzazi 202411746

*/

package ds_project;

import java.util.Scanner;

public class DS_Project {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        UnsortedArrayList unsorted = new UnsortedArrayList(200); // static array structure
        SortedLinkedList sortedByRoll = new SortedLinkedList();  // dynamic linked list

        ActionStack undoStack = new ActionStack(); // linked stack
        ActionStack redoStack = new ActionStack(); // linked stack

        RegistrationQueue regQueue = new RegistrationQueue(); // linked queue

        //Menu
        while (true) {
            System.out.println("\n======= Student Management System =======");
            System.out.println("1) Add Student");
            System.out.println("2) Delete Student (by Roll)");
            System.out.println("3) Search Student (by Roll)");
            System.out.println("4) Update Marks (by Roll)");
            System.out.println("5) Display Unsorted List (as entered)");
            System.out.println("6) Display Sorted List (by Roll - Linked List)");
            System.out.println("7) Display Sorted Unsorted-Array (by Roll)");
            System.out.println("8) Display Sorted Unsorted-Array (by Marks)");
            System.out.println("9) Registration Queue: Enqueue Student");
            System.out.println("10) Registration Queue: Dequeue Student");
            System.out.println("11) Registration Queue: Display");
            System.out.println("12) Undo");
            System.out.println("13) Redo");
            System.out.println("14) Performance Compare (static vs dynamic)");
            System.out.println("0) Exit");
            System.out.print("Choose: ");

            int choice = sc.nextInt();
            sc.nextLine(); // consume newline

            if (choice == 0) {
                System.out.println("Bye!");
                break;
            }

            if (choice == 1) {
                System.out.print("Roll No: ");
                int roll = sc.nextInt();
                sc.nextLine();
                //roll unique
                if (unsorted.searchByRoll(roll) != null) {
                    System.out.println("Error: Student with Roll No " + roll + " already exists!");
                    continue;
                }
                System.out.print("Name: ");
                String name = sc.nextLine();
                System.out.print("Major: ");
                String major = sc.nextLine();
                System.out.print("GPA: ");
                double gpa = sc.nextDouble();
                sc.nextLine();

                Student s = new Student(roll, name, gpa, major);

                boolean ok = unsorted.add(s);
                if (!ok) {
                    System.out.println("Unsorted list is full. Cannot add.");
                    continue;
                }

                // keep sorted list in sync
                sortedByRoll.insertSortedByRoll(s);

                // record action for undo
                undoStack.push(new Action("ADD", null, s));
                redoStack.clear();

                System.out.println("Student added.");

            } else if (choice == 2) {
                System.out.print("Enter roll to delete: ");
                int roll = sc.nextInt();
                sc.nextLine();

                Student removed = unsorted.deleteByRoll(roll);
                if (removed == null) {
                    System.out.println("Not found.");
                } else {
                    sortedByRoll.deleteByRoll(roll);

                    undoStack.push(new Action("DELETE", removed, null));
                    redoStack.clear();

                    System.out.println("Deleted: " + removed);
                }

            } else if (choice == 3) {
                System.out.print("Enter roll to search: ");
                int roll = sc.nextInt();
                sc.nextLine();

                Student found = unsorted.searchByRoll(roll);
                if (found == null) System.out.println("Not found.");
                else System.out.println("Found: " + found);

            } else if (choice == 4) {
                System.out.print("Enter roll to updateGPA: ");
                int roll = sc.nextInt();
                sc.nextLine();
                System.out.print("New GPA: ");
                double newMarks = sc.nextDouble();
                sc.nextLine();

                Student before = unsorted.searchByRoll(roll);
                if (before == null) {
                    System.out.println("Not found.");
                } else {
                    // copy "before" for undo
                    Student beforeCopy = new Student(before.rollNo, before.name, before.gpa, before.major);

                    boolean updated = unsorted.updateGPA(roll, newMarks);
                    if (!updated) {
                        System.out.println("Update failed.");
                    } else {
                        // also update in sorted list: simplest way -> delete then re-insert updated record
                        sortedByRoll.deleteByRoll(roll);
                        Student after = unsorted.searchByRoll(roll);
                        sortedByRoll.insertSortedByRoll(after);

                        Student afterCopy = new Student(after.rollNo, after.name, after.gpa, after.major);

                        undoStack.push(new Action("UPDATE", beforeCopy, afterCopy));
                        redoStack.clear();

                        System.out.println("Updated: " + after);
                    }
                }

            } else if (choice == 5) {
                unsorted.display();

            } else if (choice == 6) {
                sortedByRoll.display();

            } else if (choice == 7) {
                unsorted.sortByRoll();
                System.out.println("Sorted (array) by roll:");
                unsorted.display();

            } else if (choice == 8) {
                unsorted.sortByGPA();
                System.out.println("Sorted (array) by GPA:");
                unsorted.display();

            } else if (choice == 9) {
                System.out.print("Roll No: ");
                int roll = sc.nextInt();
                sc.nextLine();
                if (unsorted.searchByRoll(roll) != null) {
                    System.out.println("Error: Roll No already exists.");
                    continue;
                }
                System.out.print("Name: ");
                String name = sc.nextLine();
                System.out.print("Major: ");
                String major = sc.nextLine();
                System.out.print("GPA: ");
                double gpa = sc.nextDouble();
                sc.nextLine();

                regQueue.enqueue(new Student(roll, name, gpa, major));
                System.out.println("Enqueued to registration queue.");

            } else if (choice == 10) {
                Student s = regQueue.dequeue();
                if (s == null) System.out.println("Queue is empty.");
                else System.out.println("Dequeued: " + s);

            } else if (choice == 11) {
                regQueue.display();

            } else if (choice == 12) {
                if (undoStack.isEmpty()) {
                    System.out.println("Nothing to undo.");
                } else {
                    Action a = undoStack.pop();
                    applyUndo(a, unsorted, sortedByRoll);
                    redoStack.push(a);
                    System.out.println("Undo done: " + a.type);
                }

            } else if (choice == 13) {
                if (redoStack.isEmpty()) {
                    System.out.println("Nothing to redo.");
                } else {
                    Action a = redoStack.pop();
                    applyRedo(a, unsorted, sortedByRoll);
                    undoStack.push(a);
                    System.out.println("Redo done: " + a.type);
                }

            } else if (choice == 14) {
                performanceCompare();

            } else {
                System.out.println("Invalid choice.");
            }
        }

        sc.close();
    }

    // Undo = reverse the action
    private static void applyUndo(Action a, UnsortedArrayList unsorted, SortedLinkedList sortedByRoll) {
        if (a.type.equals("ADD")) {
            // undo add => delete "after"
            unsorted.deleteByRoll(a.after.rollNo);
            sortedByRoll.deleteByRoll(a.after.rollNo);
        } else if (a.type.equals("DELETE")) {
            // undo delete => re-add "before"
            unsorted.add(a.before);
            sortedByRoll.insertSortedByRoll(a.before);
        } else if (a.type.equals("UPDATE")) {
            // undo update => restore "before"
            unsorted.updateGPA(a.before.rollNo, a.before.gpa);
            sortedByRoll.deleteByRoll(a.before.rollNo);
            Student restored = unsorted.searchByRoll(a.before.rollNo);
            sortedByRoll.insertSortedByRoll(restored);
        }
    }

    // Redo = apply action again
    private static void applyRedo(Action a, UnsortedArrayList unsorted, SortedLinkedList sortedByRoll) {
        if (a.type.equals("ADD")) {
            unsorted.add(a.after);
            sortedByRoll.insertSortedByRoll(a.after);
        } else if (a.type.equals("DELETE")) {
            unsorted.deleteByRoll(a.before.rollNo);
            sortedByRoll.deleteByRoll(a.before.rollNo);
        } else if (a.type.equals("UPDATE")) {
            unsorted.updateGPA(a.after.rollNo, a.after.gpa);
            sortedByRoll.deleteByRoll(a.after.rollNo);
            Student updated = unsorted.searchByRoll(a.after.rollNo);
            sortedByRoll.insertSortedByRoll(updated);
        }
    }

    private static void performanceCompare() {
        int N = 5000;

        // static array list
        UnsortedArrayList arrList = new UnsortedArrayList(N + 5);
        long t1 = System.nanoTime();
        for (int i = 0; i < N; i++) {
            arrList.add(new Student(100000 + i, "S" + i, (i % 4.0), "CS"));
        }
        long t2 = System.nanoTime();

        // dynamic sorted linked list
        SortedLinkedList linkedSorted = new SortedLinkedList();
        long t3 = System.nanoTime();
        for (int i = 0; i < N; i++) {
            linkedSorted.insertSortedByRoll(new Student(100000 + i, "S" + i, (i % 4.0), "CS"));
        }
        long t4 = System.nanoTime();

        System.out.println("\n--- Performance Compare (N=" + N + ") ---");
        System.out.println("Static Array (insert at end): " + (t2 - t1) + " ns");
        System.out.println("Dynamic Linked (sorted insert): " + (t4 - t3) + " ns");
        System.out.println("Note: sorted insert is naturally slower because it searches position.");
    }
}

/* ===================== Data Models ===================== */

class Student {
    int rollNo;
    String name;
    double gpa;
    String major;

    Student(int rollNo, String name, double gpa, String major) {
        this.rollNo = rollNo;
        this.name = name;
        this.gpa = gpa;
        this.major = major;
    }

    public String toString() {
        return "Roll = " + rollNo + "\n" 
                + "Name = " + name + "\n" 
                + "Major = " + major + "\n" 
                + "GPA = " + gpa;
    }
}

class Action {
    String type;      // "ADD", "DELETE", "UPDATE"
    Student before;   // state before operation
    Student after;    // state after operation

    Action(String type, Student before, Student after) {
        this.type = type;
        this.before = before;
        this.after = after;
    }
}

/* ===================== Static Structure: Unsorted Array List ===================== */

class UnsortedArrayList {
    private Student[] arr;
    private int size;

    UnsortedArrayList(int capacity) {
        arr = new Student[capacity];
        size = 0;
    }

    boolean add(Student s) {
        if (size >= arr.length) return false;
        arr[size] = s;
        size++;
        return true;
    }

    Student searchByRoll(int roll) {
        for (int i = 0; i < size; i++) {
            if (arr[i].rollNo == roll) return arr[i];
        }
        return null;
    }

    Student deleteByRoll(int roll) {
        for (int i = 0; i < size; i++) {
            if (arr[i].rollNo == roll) {
                Student removed = arr[i];
                // shift left
                for (int j = i; j < size - 1; j++) {
                    arr[j] = arr[j + 1];
                }
                arr[size - 1] = null;
                size--;
                return removed;
            }
        }
        return null;
    }

    boolean updateGPA(int roll, double newGpa) {
        Student s = searchByRoll(roll);
        if (s == null) return false;
        s.gpa = newGpa;
        return true;
    }

    void sortByRoll() {
        // bubble sort (simple)
        for (int i = 0; i < size - 1; i++) {
            for (int j = 0; j < size - 1 - i; j++) {
                if (arr[j].rollNo > arr[j + 1].rollNo) {
                    Student tmp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = tmp;
                }
            }
        }
    }

    void sortByGPA() {
        for (int i = 0; i < size - 1; i++) {
            for (int j = 0; j < size - 1 - i; j++) {
                if (arr[j].gpa > arr[j + 1].gpa) {
                    Student tmp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = tmp;
                }
            }
        }
    }

    void display() {
        if (size == 0) {
            System.out.println("(empty)");
            return;
        }
        for (int i = 0; i < size; i++) {
            System.out.println(arr[i]);
        }
    }
}

/* ===================== Dynamic Structure: Sorted Linked List ===================== */

class SortedLinkedList {
    private Node head;

    private static class Node {
        Student data;
        Node next;

        Node(Student data) {
            this.data = data;
        }
    }

    void insertSortedByRoll(Student s) {
        Node newNode = new Node(s);

        if (head == null || s.rollNo < head.data.rollNo) {
            newNode.next = head;
            head = newNode;
            return;
        }

        Node cur = head;
        while (cur.next != null && cur.next.data.rollNo < s.rollNo) {
            cur = cur.next;
        }

        newNode.next = cur.next;
        cur.next = newNode;
    }

    Student searchByRoll(int roll) {
        Node cur = head;
        while (cur != null) {
            if (cur.data.rollNo == roll) return cur.data;
            cur = cur.next;
        }
        return null;
    }

    boolean deleteByRoll(int roll) {
        if (head == null) return false;

        if (head.data.rollNo == roll) {
            head = head.next;
            return true;
        }

        Node cur = head;
        while (cur.next != null) {
            if (cur.next.data.rollNo == roll) {
                cur.next = cur.next.next;
                return true;
            }
            cur = cur.next;
        }
        return false;
    }

    void display() {
        if (head == null) {
            System.out.println("(empty)");
            return;
        }
        Node cur = head;
        while (cur != null) {
            System.out.println(cur.data);
            cur = cur.next;
        }
    }
}

/* ===================== Dynamic Structure: Stack (Linked) for Undo/Redo ===================== */

class ActionStack {
    private StackNode top;

    private static class StackNode {
        Action data;
        StackNode next;

        StackNode(Action data) {
            this.data = data;
        }
    }

    void push(Action a) {
        StackNode n = new StackNode(a);
        n.next = top;
        top = n;
    }

    Action pop() {
        if (top == null) return null;
        Action a = top.data;
        top = top.next;
        return a;
    }

    boolean isEmpty() {
        return top == null;
    }

    void clear() {
        top = null;
    }
}

/* ===================== Dynamic Structure: Queue (Linked) for Registration ===================== */

class RegistrationQueue {
    private QNode front, rear;

    private static class QNode {
        Student data;
        QNode next;

        QNode(Student data) {
            this.data = data;
        }
    }

    void enqueue(Student s) {
        QNode n = new QNode(s);
        if (rear == null) {
            front = rear = n;
        } else {
            rear.next = n;
            rear = n;
        }
    }

    Student dequeue() {
        if (front == null) return null;
        Student val = front.data;
        front = front.next;
        if (front == null) rear = null;
        return val;
    }

    void display() {
        if (front == null) {
            System.out.println("(queue empty)");
            return;
        }
        QNode cur = front;
        while (cur != null) {
            System.out.println(cur.data);
            cur = cur.next;
        }
    }
}
