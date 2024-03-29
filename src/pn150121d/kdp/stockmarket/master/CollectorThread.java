package pn150121d.kdp.stockmarket.master;

import pn150121d.kdp.stockmarket.common.*;
import pn150121d.kdp.stockmarket.common.Base64;

import java.io.IOException;
import java.util.*;

/**
 * Nit koja periodično skuplja podatke o cenama sa podservera,
 * i potencijalno izbacuje mrtve podservere iz Router-a
 */
class CollectorThread extends Thread
{
    private Map<String, Price> prices= Collections.synchronizedMap(new HashMap<>());
    private boolean running=true;
    private int iteration=0;
    private UpdateListener updateListener;
    private Logger logger;
    private Server server;

    CollectorThread(Server server)
    {
        this.server=server;
    }

    void halt()
    {
        running=false;
        interrupt();
    }
    @Override
    public void run()
    {
        while (running)
        {
            iteration=(iteration+1)%2;
            try
            {

                List<Slave> slaveList;
                if(iteration==0)
                {
                    slaveList=Router.getAllSlaves();
                    log("Collecting prices from all slaves");
                }
                else
                {
                    log("Collecting prices from potentially dead slaves");
                    slaveList=new LinkedList<>(Router.potentiallyDeadSlaves);
                    Router.potentiallyDeadSlaves.clear();
                }
                List<Thread> collectors = new LinkedList<>();

                for (Slave slave : slaveList)
                {
                    Thread collector = new Thread(() -> collect(slave));
                    collector.start();
                    collectors.add(collector);
                }
                for (Thread collector : collectors)
                {
                    collector.join();
                }
                if(slaveList.size()>0) log("New prices collected");
                List<NetworkMessage> backlogs = Router.handleDeadSlavesAndGetAllBacklogs();
                server.handleDelayedRequests(backlogs);
                if(updateListener!=null) updateListener.dataUpdated();
                Thread.sleep(TimeConstants.PRICE_COLLECT_PERIOD*500);
            }
            catch (InterruptedException ignored)
            {

            }
        }
    }

    private void collect(Slave slave)
    {
        log("Collecting prices from "+slave.id);
        if(slave.getFailureCount()>=3)
        {
            log("Slave "+slave.id+" failed to respond "+slave.getFailureCount()+" times");
            log("Slave "+slave.id+" is now considered dead");
            return;
        }
        String response = slave.sendFromCollector(new GetPricesRequest());
        if(response==null)
        {
            Router.potentiallyDeadSlaves.add(slave);
            log("Slave "+slave.id+" failed to respond "+slave.getFailureCount()+" times");
            if(slave.getFailureCount()>=3) log("Slave "+slave.id+" is probably dead");
        }
        else
        {
            log(slave.id+" responded with prices");
            try
            {
                List<Price> receivedPrices = Base64.objectFrom64(response);
                for(Price price:receivedPrices)
                {
                    prices.put(price.item, price);
                }
            }
            catch (IOException | ClassNotFoundException | ClassCastException ignored)
            {
            }
        }
    }
    Price getPrice(String item)
    {
        return prices.getOrDefault(item, null);
    }

    void setUpdateListener(UpdateListener updateListener)
    {
        this.updateListener = updateListener;
    }

    void setLogger(Logger logger)
    {
        this.logger = logger;
    }
    private void log(String message)
    {
        if(logger!=null) logger.logMessage(message);
    }
    Map<String, Price> getPrices()
    {
        return new HashMap<>(prices);
    }
}
