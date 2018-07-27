package top.weidong.example.nio.buf;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * nio中的Buffer
 * <p>
 * 实在无聊 学习一下Buffer
 * <p>
 * Buffer是一个抽象类 其有很多子类 ByteBuffer, CharBuffer, DoubleBuffer, FloatBuffer, IntBuffer, LongBuffer, ShortBuffer
 * <p>
 * Buffer内部有几个变量 使用allocate创建的Buffer中的属性的值分别为：
 * -1  <=   0   <=  capacity <= capacity
 * <p>
 * mark <= position <= limit <= capacity
 *
 * @author dongwei
 * @date 2018/05/17
 * Time: 15:10
 */
public class BufferDemo {

    private static void simpleByteBuffer() {
        // 创建ByteBuffer的方式只有一种，通过它提供的静态方法
        // 实际上内部的实现是new出它的子类HeapByteBuffer
        // 而这个类没有公开，只能在JDK内部使用
        // allocate获取的buffer是堆中分配的 如果不想使用堆内的buffer可以调用allocateDirect方法
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        // 除了使用allocate这种方法去创建ByteBuffer对象 还能用wrap方法
        ByteBuffer wrap = ByteBuffer.wrap(new byte[]{1, 2, 3, 4});
        ByteBuffer wrap1 = ByteBuffer.wrap(new byte[]{2, 3, 4, 5}, 0, 4);

        int limit0 = byteBuffer.limit();
        System.out.println("limit:" + limit0);
        int capacity0 = byteBuffer.capacity();
        System.out.println("capacity:" + capacity0);
        int position0 = byteBuffer.position();
        System.out.println("position:" + position0);
        int remaining0 = byteBuffer.remaining();
        System.out.println("remaining:" + remaining0);

        // mark值获取不到 没有提供对应的方法去获取这个值 初始阶段capacity和limit是一样的 position为0 mark为-1

        // 一个char占2个字节
        byteBuffer.put((byte) 62);
        byteBuffer.putChar('#');
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 36);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 65);

        System.out.println("=================");
        System.out.println("remaining:" + byteBuffer.remaining());
        System.out.println("position:" + byteBuffer.position());
        System.out.println("limit:" + byteBuffer.limit());
        System.out.println("capacity:" + byteBuffer.capacity());
        printBufferContent(byteBuffer);

        // 向buffer中添加元素 只有position会发生变化
        System.out.println("=================");
        byte idx0 = byteBuffer.get(0);
        System.out.println("idx 0:" + idx0);
        System.out.println("remaining:" + byteBuffer.remaining());
        System.out.println("position:" + byteBuffer.position());
        System.out.println("limit:" + byteBuffer.limit());
        System.out.println("capacity:" + byteBuffer.capacity());

        // getChar会将2个挨着的字节合并成一个char 如果指定了下标，那就从下标处开始 没有指定下标就从position处开始
        char aChar = byteBuffer.getChar(1);
        System.out.println(aChar);

        byteBuffer.position(3);
        char aChar1 = byteBuffer.getChar();
        System.out.println(aChar1 + "  current position " + byteBuffer.position());

        // 此时的position为5 而下标为5处的值为0
        byte b = byteBuffer.get();
        System.out.println(b);

        // 每次向buffer中添加一个元素，position就回随之加1 但limit和capacity是不会变的
        // 当需要读的时候，要么直接读下标（get(index)方法） 要么直接读（get()方法）
        // 直接去读会有一个问题，因为此时position的值为当前写的位置，因此往后读一定没数据
        // 一般而言，读之前使用flip方法可以将把limit置为position的位置，把position置为0 这样读一定能读到数据
        // 读的时候 position也会随之移动 上限为limit
        // 读完了就要清空缓冲区 让它能够被再次写入 有两种方式清空缓冲区clear和compact
        // 如果调用的是clear()方法，position将被设回0，limit被设置成 capacity的值。
        // 换句话说，Buffer 被清空了。Buffer中的数据并未清除，只是这些标记告诉我们可以从哪里开始往Buffer里写数据。
        // 本质上，数据并没有清理掉，标记会告诉我们数据可以从哪里开始写，不论这些位置是否有数据 有也不管，覆盖掉就行了
        // 如果Buffer中仍有未读的数据，且后续还需要这些数据，但是此时想要先先写些数据，那么使用compact()方法。
        // compact方法的本质就在于在读的时候将还没读到的数据（position~limit之间的）全部挪到buffer的开头
        // 也就是将position~limit之间的n个数据全部覆盖到开头，但不是完全覆盖，仅仅是前position个位置被覆盖
        // 同时将limit置为capacity

        Buffer flip = byteBuffer.flip();
        System.out.println("pos:" + flip.position() + "  limit :" + flip.limit());
        byteBuffer.get();
        byteBuffer.get();
        byteBuffer.get();
        System.out.println("pos:" + flip.position() + "  limit :" + flip.limit());

        ByteBuffer compact = byteBuffer.compact();
        System.out.println("pos:" + flip.position() + "  limit :" + flip.limit());

        printBufferContent(byteBuffer);

        // mark方法为将mark置为position处，	reset()方法将position恢复到mark处


    }

    private static void duplicate() {
        byte[] bytes = {1, 2, 3, 4};
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        System.out.println("original buffer: ");
        printBufferContent(byteBuffer);

        bytes[1] = 10;
        ByteBuffer duplicate = byteBuffer.duplicate();
        System.out.println("copied buffer: ");
        printBufferContent(duplicate);
        System.out.println("original buffer: ");
        printBufferContent(byteBuffer);
    }

    private static void slice() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 2);
        byteBuffer.put((byte) 3);
        printBufferInfo(byteBuffer);

        // slice方法返回的是一个新buffer 这个新的buffer是原来buffer的一部分：没有元素的部分
        ByteBuffer slice = byteBuffer.slice();
        printBufferInfo(slice);

    }

    /**
     * 直接分配内存消耗要比在堆中分配内存要花的时间多
     * @throws IOException
     */
    private static void performance() throws IOException {
        for (int i = 0; i < 10000; i++) {
            ByteBuffer.allocate(1);
        }
        long heapBufferStart = System.nanoTime();
        for (int i = 0; i < 500; i++) {
            ByteBuffer.allocate(1024*1000);
        }
        System.out.println("heap buffer cost time :" + (System.nanoTime() - heapBufferStart));


        for (int i = 0; i < 10000; i++) {
            ByteBuffer.allocateDirect(1);
        }
        long noHeapBufferStart = System.nanoTime();
        for (int i = 0; i < 500; i++) {
            ByteBuffer.allocateDirect(1024*1000);
        }
        System.out.println("non heap buffer cost time :" + (System.nanoTime() - noHeapBufferStart));
        System.in.read();
    }

    private static void fileUsedBuffer(){
        ByteBuffer buff = ByteBuffer.allocate(128);
        FileChannel fin = null;
        FileChannel fout = null;
        try {
            fin = new FileInputStream("/Users/dongwei/IdeaProjects/simple-rpc/logs/log.log").getChannel();
            fout = new FileOutputStream("/Users/dongwei/IdeaProjects/simple-rpc/logs/log2.log").getChannel();

            while(fin.read(buff) != -1) {
                // 数据写到buff中后要读之前得flip一下
                buff.flip();
                fout.write(buff);
                // 读完后要继续写之前得clear一下
                buff.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if(fin != null) {
                    fin.close();
                }
                if(fout != null) {
                    fout.close();
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
//        simpleByteBuffer();
//        duplicate();
//        slice();
//        performance();
        fileUsedBuffer();
    }

    private static void printBufferContent(ByteBuffer buffer) {
        byte[] array = buffer.array();
        for (byte b : array) {
            System.out.print(b + " ");
        }
        System.out.println();
    }

    private static void printBufferInfo(ByteBuffer buffer) {
        System.out.println("position：" + buffer.position()
                + " limit: " + buffer.limit()
                + " capacity: " + buffer.capacity());
        System.out.println("=======================");
    }
}
