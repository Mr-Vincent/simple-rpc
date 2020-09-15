package top.weidong.common.util;

import java.util.ArrayList;

/**
 * @author dongwei
 * @since 2020/09/15
 * Time: 11:02
 */
public class ListenableArrayList<E> extends ArrayList<E> {

    public boolean add(E e,ElementAddListener<E> listener){
        boolean success = this.add(e);
        if(success){
            listener.success(e);
        }else {
            listener.fail(e,new IllegalAccessError());
        }
        return success;
    }

    public interface ElementAddListener<E>{
        /**
         * success
         * @param e
         */
        void success(E e);

        /**
         * fail
         * @param e
         * @param throwable
         */
        void fail(E e,Throwable throwable);
    }
}
