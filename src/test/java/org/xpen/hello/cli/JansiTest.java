package org.xpen.hello.cli;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Color;
import org.fusesource.jansi.AnsiConsole;
import org.junit.jupiter.api.Test;

/**
 * 演示Jansi让控制台输出彩色文字
 * (必须写到main方法然后在cmd下运行才可看到效果)
 *
 */
public class JansiTest {
    
    //mvn test -Dtest=org.xpen.hello.cli.JansiTest
    //测试下来看不到效果，可能是maven的关系
    @Test
    public void testSimple() {
        AnsiConsole.systemInstall();
        System.out.println(Ansi.ansi().eraseScreen().fg(Color.RED).a("Hello").fg(Color.GREEN).a(" World").reset());
        System.out.println(Ansi.ansi().eraseScreen().render("@|red Hello|@ @|yellow World|@") );
        AnsiConsole.systemUninstall();
    }

}
