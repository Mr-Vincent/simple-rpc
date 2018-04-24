package top.weidong.example.juc.heap;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * 堆的简单实现(小堆 小的在前 大的靠后)
 *
 *
 * @author dongwei
 * @date 2018/04/23
 * Time: 16:36
 */
public class SimpleHeap {

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

    /**
     * 堆化
     */
    private void heapify() {
        // 针对有子节点的元素进行处理
        for (int i = size / 2 - 1;  i >= 0; i--) {
            fixDown(i);
        }
    }

    /**
     * 元素下沉
     * @param k
     */
    private void fixDown(int k) {
        int j;
        while ((j = (k << 1) + 1) <= size && j > 0) {
            if (j < size-1 &&
                    data[j] > data[j+1]) {
                // j indexes smallest kid
                j++;
            }
            if (data[k] <= data[j]) {
                break;
            }
            int tmp = data[j];  data[j] = data[k]; data[k] = tmp;
            k = j;
        }
    }

    public static void main(String[] args) {
        int[] a = {5,4,3,2,1,0};
        SimpleHeap heap = new SimpleHeap(a);
        System.out.println(Arrays.toString(a));
    }
}
