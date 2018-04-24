package top.weidong.example.juc.heap;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * 堆的简单实现(小堆 小的在前 大的靠后)
 *
 * @author dongwei
 * @date 2018/04/23
 * Time: 16:36
 */
public class SimpleHeap {

    /**
     * 默认长度
     */
    private static final int DEFAULT_INITIAL_CAPACITY = 11;

    private int size = 0;

    /**
     * 堆中堆数据
     */
    private int[] data;

    public SimpleHeap(int[] data) {
        this.data = data;
        this.size = data.length;
        heapify();
    }

    public SimpleHeap() {
        this.data = new int[DEFAULT_INITIAL_CAPACITY];
    }

    public SimpleHeap(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must > 0");
        }
        this.data = new int[capacity];
    }

    /**
     * 添加元素
     *
     * @param x
     */
    public void add(int x) {
        if (size + 1 > data.length) {
            throw new IllegalStateException("add too many elements");
        }
        data[size] = x;
        fixUp(size);
        size++;
    }


    /**
     * 元素上浮
     *
     * @param k
     */
    private void fixUp(int k) {
        while (k > 1) {
            int j = (k - 1) >>> 1;
            if (data[j] <= data[k]) {
                break;
            }
            int tmp = data[j];
            data[j] = data[k];
            data[k] = tmp;
            k = j;
        }
    }

    /**
     * 堆化
     */
    private void heapify() {
        // 针对有子节点的元素进行处理
        for (int i = size / 2 - 1; i >= 0; i--) {
            fixDown(i);
        }
    }

    /**
     * 元素下沉
     *
     * @param k
     */
    private void fixDown(int k) {
        int j;
        while ((j = (k << 1) + 1) <= size && j > 0) {
            if (j < size - 1 &&
                    data[j] > data[j + 1]) {
                // j indexes smallest kid
                j++;
            }
            if (data[k] <= data[j]) {
                break;
            }
            int tmp = data[j];
            data[j] = data[k];
            data[k] = tmp;
            k = j;
        }
    }


    public int[] toArray() {
        return Arrays.copyOf(data, size);
    }


    public static void main(String[] args) {
        SimpleHeap heap = new SimpleHeap();
        for (int i = 9; i > 0; i--) {
            heap.add(i);
        }

        System.out.println(Arrays.toString(heap.toArray()));
    }
}
