package top.weidong.example.netty.bytebuf;

import io.netty.buffer.*;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * Netty中的ByteBuf
 *
 * 相比原生的buffer来说，netty的buffer提供更多的api
 * 首先，对原生buffer中的position进行改进 使用2个index标识读和写
 * +-------------------+------------------+------------------+
 * | discardable bytes |  readable bytes  |  writable bytes  |
 * |                   |     (CONTENT)    |                  |
 * +-------------------+------------------+------------------+
 * |                   |                  |                  |
 * 0      <=      readerIndex   <=   writerIndex    <=    capacity
 *
 * 因此写模式<-->读模式相互转化的时候不需要使用flip和clear操作 简单很多
 *
 * 同时也延续原生的api操作方式，每写一个元素writerIndex就加1 每读一个元素readerIndex就加1
 *
 * clear方法调用后：和原生api还是有一定区别 当也有相似之处，相同的地方就是并没有将内容清理掉，只是将读写下标置为初始位置
 *
 * +---------------------------------------------------------+
 * |             writable bytes (got more space)             |
 * +---------------------------------------------------------+
 * |                                                         |
 * 0 = readerIndex = writerIndex            <=            capacity
 *
 * 都说netty的ByteBuf是对ByteBuffer的封装，而看了源码我觉得是一种重新设计，而且还对ByteBuffer的加强
 *
 * @author dongwei
 * @date 2018/05/18
 * Time: 10:34
 */
public class ByteBufDemo {

    private static void allocator(){
        UnpooledByteBufAllocator allocator = UnpooledByteBufAllocator.DEFAULT;
        ByteBuf buffer = allocator.buffer(100);
        System.out.println(buffer.isDirect());
        print(buffer);
    }

    private static void resourceLeak(){
        // 内存泄露检测只会出现在分配direct buffer 或者pooled buffer 的时候

        // 因为heap buffer由JVM去管理 一般而言不会出现泄露

        System.setProperty("io.netty.leakDetectionLevel","PARANOID");

        UnpooledByteBufAllocator allocator = UnpooledByteBufAllocator.DEFAULT;
        ByteBuf buffer = allocator.directBuffer();

        System.out.println(buffer.isDirect());

        print(buffer);




    }

    private static void simpleByteBuf() {
        ByteBuf buf = new UnpooledHeapByteBuf(ByteBufAllocator.DEFAULT, 100, Integer.MAX_VALUE);
        ByteBuf byteBuf = Unpooled.buffer(10);

        boolean direct = buf.isDirect();
        System.out.println(direct);

        for (int i = 0; i < byteBuf.capacity(); i ++) {
            byteBuf.writeByte((byte)65 + i);
        }


        System.out.println(byteBuf.readInt());
        print(byteBuf);

    }

    private static void print(ByteBuf byteBuf){
        System.out.println(byteBuf.toString());
        while (byteBuf.isReadable()) {
            System.out.print(byteBuf.readByte() + "--");
        }
    }

    public static void main(String[] args) {
//        simpleByteBuf();
//        allocator();
        resourceLeak();
    }
}
