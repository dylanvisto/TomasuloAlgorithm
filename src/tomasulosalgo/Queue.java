//Dylan Visto; Julian Thrash
//Interface that is implemented in the ArrayQueue class

package tomasulosalgo;

public interface Queue<E> {
    
    int size();
    
    boolean isEmpty();
    
    void enqueue(E e);
    
    E first();
    
    E dequeue();
    
}
