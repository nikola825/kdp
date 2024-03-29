package pn150121d.kdp.stockmarket.common;

/**
 * Zahtev koji podserveri šalju glavnom kad se pokrenu
 */
public class SlaveRegistrationRequest extends NetworkMessage
{
    static final long serialVersionUID = 599654609849608L;
    public final int port;

    public SlaveRegistrationRequest(int port)
    {
        this.port = port;
    }

    @Override
    public String getType()
    {
        return MessageTypes.REGISTER_SLAVE;
    }
}
