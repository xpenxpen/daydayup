package org.xpen.hello.serialize;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class HelloKryoTest {
    public static void main(String[] args) throws Exception {
        Kryo kryo = new Kryo();
        kryo.register(SomeClass.class, 0x21);

        SomeClass object = new SomeClass();
        object.value1 = "Hello Kryo中文123";
        object.value2 = "Hello Kryo中文123";

        Output output = new Output(new FileOutputStream("target/kryo.bin"));
        kryo.writeObject(output, object);
        output.close();

        Input input = new Input(new FileInputStream("target/kryo.bin"));
        SomeClass object2 = kryo.readObject(input, SomeClass.class);
        System.out.println(object2.value1);
        System.out.println(object2.value2);
        input.close();
    }

    public static class SomeClass {
        String value1;
        String value2;
    }

}
