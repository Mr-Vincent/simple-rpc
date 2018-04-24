package top.weidong.example.juc.heap;

import java.util.Arrays;
import java.util.PriorityQueue;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * 优先队列demo
 * @author dongwei
 * @date 2018/04/24
 * Time: 11:17
 */
public class PriorityQueueDemo {


    private static void run0(){
        PriorityQueue<Integer> queue = new PriorityQueue<Integer>(Arrays.asList(10,9,8,7,6,5,4,3,2,1));
        // [1, 2, 4, 3, 6, 5, 8, 10, 7, 9]
        System.out.println(Arrays.toString(queue.toArray()));
    }

    private static void run(){
        PriorityQueue<Integer> queue = new PriorityQueue<Integer>();
        // 构造一个10-》1的数组 将其add进去
        for (int i = 10; i > 0 ; i--) {
            queue.add(i);
        }
        // [1, 2, 5, 4, 3, 9, 6, 10, 7, 8]
        System.out.println(Arrays.toString(queue.toArray()));
    }

    public static void main(String[] args) {
        run0();
    }
}
