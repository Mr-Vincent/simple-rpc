package top.weidong.serializer;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @author dongwei
 * @date 2018/03/22
 * Time: 18:12
 */
public class SerializerTest {

    public static void main(String[] args) {
        Serializer defaultSerializer = SerializationFactory.getDefaultSerializer();
        Person person = new Person("dongwei",25);
        byte[] bytes = defaultSerializer.writeObject(person);
        System.out.println(bytes.length);
    }

    static class Person implements Serializable{
        String name;
        int age;
        public Person(String name,int age){
            this.age = age;
            this.name = name;
        }

        @Override
        public String toString() {
            return "name= "+name + "age= "+age;
        }
    }
}
