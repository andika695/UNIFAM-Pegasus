package model;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ArrayList<E> implements Iterable<E> {

    private Object[] arrayList;
    private static final int DEFAULT_CAPACITY = 10;
    private int size;

    

    public ArrayList() {
        this(DEFAULT_CAPACITY);
    }

    public ArrayList(int capacity) {
        if (capacity <= 0) {
            System.out.println("The size must be greater than 0.");
            return;
        }
        this.arrayList = new Object[capacity];
        this.size = 0;
    }

    public int size() {
        return this.size;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    public boolean contains(Object obj) {
        return indexOf(obj) >= 0;
    }

    public int indexOf(Object obj) {
        for (int i = 0; i < this.size(); i++) {
            if (obj.equals(this.arrayList[i])) {
                return i;
            }
        }
        return -1;
    }

    public void clear() {
        if (this.size() > 0) {
            this.arrayList = new Object[DEFAULT_CAPACITY];
            this.size = 0;
        }
    }

    private boolean isFull() {
        return this.arrayList.length == this.size;
    }

    private void resizeArray() {
        int oldCapacity = this.arrayList.length;
        int newCapacity = oldCapacity + (oldCapacity >> 1);

        Object[] tempArray = new Object[newCapacity];
        for (int i = 0; i < this.size(); i++) {
            tempArray[i] = this.arrayList[i];
        }

        this.arrayList = tempArray;
    }

    public void add(E obj) {
        if (this.isFull()) {
            this.resizeArray();
        }
        this.arrayList[this.size++] = obj;
    }

    public void add(int index, E obj) {
        if (index < 0 || index > this.size) {
            System.out.println("Index out of bounds");
            System.exit(-1);
        }

        if (this.isFull()) {
            this.resizeArray();
        }

        for (int i = this.size; i > index; i--) {
            this.arrayList[i] = this.arrayList[i - 1];
        }

        this.arrayList[index] = obj;
        this.size++;
    }

    public E get(int index) {
        if (index < 0 || index >= this.size) {
            System.out.println("Index out of bounds");
            System.exit(-1);
        }
        return (E) this.arrayList[index];
    }

    public void set(int index, E obj) {
        if (index < 0 || index >= this.size) {
            System.out.println("Index out of bounds");
            System.exit(-1);
        }
        this.arrayList[index] = obj;
    }

    public void remove(Object obj) {
        int index = indexOf(obj);
        if (index != -1) {
            remove(index);
        }
    }

    public void remove(int index) {
        if (index < 0 || index >= this.size) {
            System.out.println("Index out of bounds");
            System.exit(-1);
        }

        for (int i = index; i < this.size - 1; i++) {
            this.arrayList[i] = this.arrayList[i + 1];
        }

        this.arrayList[--this.size] = null;
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private int index = 0;

            public boolean hasNext() {
                return index < size;
            }

            public E next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return (E) arrayList[index++];
            }
        };
    }
}
