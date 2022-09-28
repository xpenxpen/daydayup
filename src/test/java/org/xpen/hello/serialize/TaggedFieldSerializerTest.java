package org.xpen.hello.serialize;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.junit.jupiter.api.Test;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.TaggedFieldSerializer;
import com.esotericsoftware.kryo.serializers.TaggedFieldSerializer.Tag;

/**
 * Kryo向后兼容性演示
 * 1)先单独运行testVersion1，写入老版本二进制,再读入，发现xxx没有写入，证明没有标记Tag的字段不会写入
 * 2)将最后一行TestClass.yyy注释放开，再单独运行testVersion2，发现第一步写入的老的二进制仍然可读，证明向后兼容性OK
 */
public class TaggedFieldSerializerTest {
    
    @Test
    public void testVersion1() throws Exception {
        Kryo kryo = new Kryo();
        kryo.setReferences(false);
        kryo.setDefaultSerializer(TaggedFieldSerializer.class);
        kryo.register(TestClass.class, 0x21);
        
        TestClass object1 = new TestClass();
        object1.moo = 2;
        object1.xxx = 999;

        Output output = new Output(new FileOutputStream("target/kryo.bin"));
        kryo.writeObject(output, object1);
        output.close();

        Input input = new Input(new FileInputStream("target/kryo.bin"));
        TestClass object2 = kryo.readObject(input, TestClass.class);
        System.out.println(object2.moo);
        System.out.println(object2.xxx);
        input.close();
    }
    
    @Test
    public void testVersion2() throws Exception {
        Kryo kryo = new Kryo();
        kryo.setReferences(false);
        kryo.setDefaultSerializer(TaggedFieldSerializer.class);
        kryo.register(TestClass.class, 0x21);

        Input input = new Input(new FileInputStream("target/kryo.bin"));
        TestClass object2 = kryo.readObject(input, TestClass.class);
        System.out.println(object2.moo);
        System.out.println(object2.xxx);
        input.close();
    }

    public static class TestClass {
        @Tag(0) public String text = "something";
        @Tag(1) public int moo = 120;
        @Tag(2) public long moo2 = 1234120;
        @Tag(3) public TestClass child;
        @Tag(4) public int zzz = 123;
        //@Tag(5) public int yyy;
        
        public int xxx;

    }

}
