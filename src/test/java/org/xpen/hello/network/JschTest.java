package org.xpen.hello.network;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

/**
 * SSH演示
 */
public class JschTest {
    
    private static final String USER = "root";
    private static final String PASS = "pass";
    private static final String HOST = "test-host";

    @Test
    public void testExec() throws Exception {
        JSch jsch = new JSch();
        Session session = jsch.getSession(USER, HOST, 22);
        session.setPassword(PASS);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();

        String command = "pwd";

        Channel channel = session.openChannel("exec");
        ((ChannelExec) channel).setCommand(command);

        channel.setInputStream(null);
        InputStream in = channel.getInputStream();

        channel.connect();
        
        String outputStr = IOUtils.toString(in, Charset.defaultCharset());
        System.out.print(outputStr);
        System.out.println("exit-status: " + channel.getExitStatus());

        channel.disconnect();
        session.disconnect();
    }
    
    @Test
    //有问题，会卡住
    public void testShell() throws Exception {
        JSch jsch = new JSch();
        Session session = jsch.getSession(USER, HOST, 22);
        session.setPassword(PASS);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();

        String command = "ls";

        ChannelShell channel = (ChannelShell)session.openChannel("shell");

        channel.setInputStream(null);
        InputStream in = channel.getInputStream();

        channel.setPtyType("xterm");
        channel.connect();
        
        OutputStream out = channel.getOutputStream();
        PrintWriter printWriter = new PrintWriter(out);
        printWriter.println(command);
        printWriter.flush();
        
        String outputStr = IOUtils.toString(in, Charset.defaultCharset());
        System.out.print(outputStr);
        System.out.println("exit-status: " + channel.getExitStatus());

        out.close();
        in.close();
        channel.disconnect();
        session.disconnect();
    }
}
