import java.awt.Point;

public class LinkList {
    public Node head;
    
     public LinkList() {
        head = null;
    }
       public void insert(Point p) {
        Node node = new Node(p);
        if(head==null)
        {
            head=node;
            node.Next=head;
            node.Prev=head;
            return;
        }
        Node temp=head;
        while(temp.Next!=head)
            temp=temp.Next;
        temp.Next = node;
        node.Next=head;
        node.Prev=temp;
        head.Prev=node;
    }
    
}
