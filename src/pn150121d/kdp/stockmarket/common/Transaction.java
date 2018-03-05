package pn150121d.kdp.stockmarket.common;

public class Transaction implements NetworkMessage
{
    static final long serialVersionUID = 9809284092841L;
    public long timeStamp;
    public final String sender;
    public final TransactionType type;
    public final int item;
    public final int price;
    public int count;
    public String id;

    public Transaction(String sender, TransactionType type, int item, int price, int count)
    {
        this.sender=sender;
        this.type=type;
        this.item=item;
        this.price=price;
        this.count=count;
    }

    @Override
    public String toString()
    {
        return "{sender='"+sender+"'"+", type='"+type+"', item="+item+", price="+price+", count="+count+"}";
    }

    public String toShortString()
    {
        return sender+":"+item+":"+price+":"+count;
    }

    @Override
    public String getType()
    {
        return MessageTypes.PROCESS_TRANSACTION;
    }
}