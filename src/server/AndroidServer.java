package server;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import msginfo.Msg.CMsg;
import msginfo.Msg.CMsgHead;
import msginfo.Msg.CMsgReg;

public class AndroidServer implements Runnable {

    public void run() {
        try {
            System.out.println("beign:");
            ServerSocket serverSocket = new ServerSocket(12345);
            while (true) {
                System.out.println("等待接收用户连接：");
                // 接受客户端请求
                Socket client = serverSocket.accept();

                DataOutputStream dataOutputStream;
                DataInputStream dataInputStream;

                try {
                    // 接受客户端信息
                    // BufferedReader in = new BufferedReader(
                    // new InputStreamReader(client.getInputStream()));
                    // String str = in.readLine();
                    // System.out.println("read length:  " + str.length());
                    // System.out.println("read:  " + str);

                    // InputStream inputstream = client.getInputStream();
                    // byte[] buffer = new byte[1024 * 4];
                    // int temp = 0;
                    // while ((temp = inputstream.read(buffer)) != -1) {
                    // str = new String(buffer, 0, temp);
                    // System.out.println("===str===" + str);

                    // File file = new File("user\\log\\login.log");
                    // appendLog(file, str);

                    InputStream inputstream = client.getInputStream();

                    dataOutputStream = new DataOutputStream(
                            client.getOutputStream());
                    //dataInputStream = new DataInputStream(inputstream);

                    // byte[] d = new BufferedReader(new InputStreamReader(
                    // dataInputStream)).readLine().getBytes();
                    // byte[] bufHeader = new byte[4];
                    // dataInputStream.readFully(bufHeader);
                    // int len = BytesUtil.Bytes4ToInt(bufHeader);
                    // System.out.println(d.length);
                    // System.out.println(dataInputStream.readLine().toString());
                    byte len[] = new byte[1024];
                    int count = inputstream.read(len);  
                
                    byte[] temp = new byte[count];
                    
                    for (int i = 0; i < count; i++) {   
                        
                            temp[i] = len[i];                              
                    } 

                    // 协议正文
//                     byte[] sendByte = new byte[30];
//                    
//                     dataInputStream.readFully(sendByte);
//                     for (byte b : sendByte) {
//                     System.out.println(""+b);
//                     }
                    CMsg msg = CMsg.parseFrom(temp);
                    //
                    //
                    CMsgHead head = CMsgHead.parseFrom(msg.getMsghead()
                            .getBytes());
                    System.out.println("==len===" + head.getMsglen());
                    System.out.println("==res===" + head.getMsgres());
                    System.out.println("==seq===" + head.getMsgseq());
                    System.out.println("==type===" + head.getMsgtype());
                    System.out.println("==Termid===" + head.getTermid());
                    System.out.println("==Termversion==="
                            + head.getTermversion());

                    CMsgReg body = CMsgReg.parseFrom(msg.getMsgbody()
                            .getBytes());
                    System.out.println("==area==" + body.getArea());
                    System.out.println("==Region==" + body.getRegion());
                    System.out.println("==shop==" + body.getShop());

                    // PrintWriter out = new PrintWriter(new BufferedWriter(
                    // new OutputStreamWriter(client.getOutputStream())),
                    // true);
                    // out.println("return    " +msg.toString());

                    // in.close();
                    // out.close();

                    sendProtoBufBack(dataOutputStream);

                    inputstream.close();
                    //dataInputStream.close();
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                    ex.printStackTrace();
                } finally {
                    client.close();
                    System.out.println("close");
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        Thread desktopServerThread = new Thread(new AndroidServer());
        desktopServerThread.start();
    }

    private byte[] getProtoBufBack() {

        // head
        CMsgHead head = CMsgHead.newBuilder().setMsglen(5)
                .setMsgtype(1).setMsgseq(3).setTermversion(41)
                .setMsgres(5).setTermid("11111111").build();

        // body
        CMsgReg body = CMsgReg.newBuilder().setArea(22)
                .setRegion(33).setShop(44).build();

        // Msg
        CMsg msg = CMsg.newBuilder()
                .setMsghead(head.toByteString().toStringUtf8())
                .setMsgbody(body.toByteString().toStringUtf8())
                .build();

        return msg.toByteArray();
    }

    private void sendProtoBufBack(DataOutputStream dataOutputStream) {

        byte[] backBytes = getProtoBufBack();
        // 协议头部
    //    Integer len2 = backBytes.length;
        // 前四个字节，标示协议正文长度
    //    byte[] cmdHead2 = BytesUtil.IntToBytes4(len2);

        try {
            //dataOutputStream.write(cmdHead2, 0, cmdHead2.length);
            dataOutputStream.write(backBytes, 0, backBytes.length);
            dataOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}