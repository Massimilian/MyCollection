package org.maslov;

import java.util.*;
import java.util.stream.Collectors;

public class MyCollection<E extends Comparable<? super E>> implements Iterable<E> {
    private E[] es;
    private int filling = 0;
    private boolean isSorted = true;

    public E[] getEs() {
        return es;
    }

    public int getFilling() {
        return filling;
    }

    public MyCollection(int size) {
        this.es = (E[]) new Comparable[size];
    }

    public MyCollection() {
        this.es = (E[]) new Comparable[10];
    }


    public MyCollection(E... e) {
        this.es = e;
        this.filling = this.es.length;
    }

    public MyCollection(Collection<? extends E> coll) {
        this.filling = coll.size();
        this.es = (E[]) new Comparable[this.filling];
        this.addAll(coll);
    }

    public void add(E e) {
        isSorted = false;
        if (this.needRebuild()) {
            E[] newest = (E[]) new Comparable[this.filling * 2];
            System.arraycopy(this.es, 0, newest, 0, this.filling);
            this.es = newest;
            this.add(e);
        } else {
            es[filling++] = e;
        }
    }

    public E get(int position) {
        return es[position];
    }

    public boolean remove(E e) {
        this.isSorted = false;
        boolean hasRemoved = false;
        for (int i = 0; i < es.length; i++) {
            if (es[i] != null && es[i].equals(e)) {
                es[i] = null;
                hasRemoved = true;
                break;
            }
        }
        return hasRemoved;
    }

    public boolean remove(int i) {
        this.isSorted = false;
        boolean hasRemoved = false;
        if (es.length > i && i >= 0) {
            es[i] = null;
            hasRemoved = true;
        }
        return hasRemoved;
    }

    public void addAll(Collection<? extends E> coll) {
        Iterator<? extends E> iterator = coll.iterator();
        while (iterator.hasNext()) {
            this.add(iterator.next());
        }
    }

    public void addAll(MyCollection<? extends E> coll) {
        if (this == coll) {
            this.addAll(Arrays.asList(Arrays.copyOfRange(coll.getEs(), 0, coll.getEs().length)));
        } else {
            for (int i = 0; i < coll.filling; i++) {
                if (coll.getEs()[i] == null) {
                    continue;
                } else {
                    this.add(coll.getEs()[i]);
                }
            }
        }
    }

    public void trim() {
        MyCollection<E> temp = new MyCollection<>();
        for (int i = 0; i < this.es.length; i++) {
            if (es[i] != null) {
                temp.add(es[i]);
            }
        }
        this.es = temp.getEs();
        this.filling = temp.getFilling();
    }

    /**
     * Сортировка "пузырьком"
     */
    public void sort() {
        for (int i = 0; i < this.es.length - 1; i++) {
            if (es[i] == null) {
                continue;
            }
            for (int j = i + 1; j < this.es.length; j++) {
                if (es[j] == null) {
                    continue;
                }
                if (es[i].compareTo(es[j]) > 0) {
                    E temp = es[i];
                    es[i] = es[j];
                    es[j] = temp;
                }
            }
        }
        this.isSorted = true;
    }


    /**
     * Сортировка 'quick sort' - снова пузырьком повторяться не хотелось, сделал максимально оптимизированным способом
     *
     * @param coll - неотсортированная коллекция
     * @return отсортированный лист
     */
    public static List quickSort(Collection<? extends Comparable<?>> coll) {
        Comparable[] comps = prepareArray(coll);
        comps = quickSort(comps, 0, comps.length - 1);
        return Arrays.stream(comps).collect(Collectors.toList());
    }

    private static Comparable[] prepareArray(Collection<? extends Comparable<?>> coll) {
        Iterator it = coll.iterator();
        Comparable[] comps = new Comparable[coll.size()];
        int count = 0;
        while (it.hasNext()) {
            comps[count++] = (Comparable) it.next();
        }
        return comps;
    }

    private static Comparable[] quickSort(Comparable[] array, int low, int high) {
        if (array.length == 0) { // возврат, если массив нулевой
            return null;
        }

        if (low >= high) { // возврат, если массив минимален
            return array;
        }

        int middle = low + (high - low) / 2;
        Comparable center = array[middle];

        int i = low;
        int j = high;
        while (i <= j) {
            while (array[i].compareTo(center) < 0) {
                i++;
            }

            while (array[j].compareTo(center) > 0) {
                j--;
            }

            if (i <= j) {
                Comparable temp = array[i];
                array[i] = array[j];
                array[j] = temp;
                i++;
                j--;
            }
        }
        if (low < j) {
            quickSort(array, low, j);
        }
        if (high > i) {
            quickSort(array, i, high);
        }
        return array;
    }

    @Override
    public Iterator<E> iterator() {
        int maxSize = this.es.length - 1;
        while (this.es[maxSize] == null) {
            maxSize--;
        }
        E[] temp = this.es;
        int finalMaxSize = maxSize;
        return new Iterator<E>() {
            final E[] e = temp;
            int count = 0;
            final int max = finalMaxSize;

            @Override
            public boolean hasNext() {
                return count <= finalMaxSize;
            }

            @Override
            public E next() {
                E temp = null;
                while (count < e.length && temp == null) {
                    temp = e[count++];
                }
                return temp;
            }
        };
    }

    /**
     * Метод автопроверки необходимости расширения внутреннего массива, вызывается автоматически
     *
     * @return проверяет заполненность массива
     */
    private boolean needRebuild() {
        return this.es.length == this.filling;
    }
}
