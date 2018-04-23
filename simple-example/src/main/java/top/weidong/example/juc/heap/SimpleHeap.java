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

    private int size = 0;

    /**
     * 堆中堆数据
     */
    private int[] data;

    public SimpleHeap(int[] data) {
        this.data = data;
        this.size = data.length-1;
        heapify();
    }

    /**
     * 堆化
     */
    private void heapify() {
        //下标小于等于i的节点拥有子节点
        for (int i = size / 2;  i >= 1; i--) {
            fixDown(i);
        }
    }

    /**
     * 元素下沉
     * @param k
     */
    private void fixDown(int k) {
        int j;
        while ((j = k << 1) <= size && j > 0) {
            if (j < size &&
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
        // 问题是第一个元素没有参与 原因是当使用数组表示二叉树时，某一个节点的孩子节点为当前下标的2n和2n+1 当第0个元素为根节点没办法计算子节点 只能从下标1开始
        int[] a = {10,8,2,3,9};
        SimpleHeap heap = new SimpleHeap(a);
        System.out.println(Arrays.toString(a));
    }
}
