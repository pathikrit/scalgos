
public class Node<T> {
    T value;
    Node<T> next;
    
    public Node(T value) {
        this(value, null);
    }

    public Node(T value, Node<T> next) {
        this.value = value;
        setNext(next);
    }
    
    public void unsetNext() { setNext(null); }
    public void setNext(Node<T> next) { this.next = next; }

    public static Node reverse_recursive(Node head) {
        if (head == null || head.next == null) return head;
        Node reverse = reverse(head.next);
        head.next.next = head;
        head.next = null;
        return reverse;
    }

    public static void reverse_iterative(Node head) {
        for(Node prev = null; head != null; ) {
            Node next = head.next;
            head.next = prev;
            prev = head;
            head = next;
        }
    }
    
    public static void reverse(Node s) {
        for(Node p = null, n; s != null; n = s.next, s.next = p, p = s; s = n);
    } 
}