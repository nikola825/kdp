package pn150121d.kdp.stockmarket.master;

import pn150121d.kdp.stockmarket.common.Base64;
import pn150121d.kdp.stockmarket.common.NetworkMessage;
import pn150121d.kdp.stockmarket.common.SocketWrapper;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Correspondent
{
    final String ip;
    final int port;
    private List<NetworkMessage> backlog;
    public Correspondent(String ip, int port)
    {
        this.ip=ip;
        this.port=port;
        backlog=new LinkedList<>();
    }

    public synchronized String send(NetworkMessage message)
    {
        String response = sendMessage(message);
        if(response==null)
        {
            backlog.add(message);
        }
        return response;
    }
    private String sendMessage(NetworkMessage message)
    {
        SocketWrapper sock=null;
        try
        {
            sock = new SocketWrapper(ip, port);
            sock.write(Base64.objectTo64(message));
            return sock.read();
        }
        catch (IOException err)
        {
            return null;
        }
        finally
        {
            try
            {
                if(sock!=null) sock.close();
            }
            catch (IOException ignored)
            {

            }
        }
    }
}