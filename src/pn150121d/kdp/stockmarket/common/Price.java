package pn150121d.kdp.stockmarket.common;

import java.io.Serializable;

/**
 * Klasa koja predstavlja cenu jedne hartije
 */
public class Price implements Serializable
{
    static final long serialVersionUID = 34589340958340958L;
    public final String item;
    public final Integer price;
    public final Float growth;

    public Price(String item, Integer price)
    {
        this(item, price, 0.0f);
    }
    public Price(String item, Integer price, Float growth)
    {
        this.item = item;
        this.price = price;
        this.growth=growth;
    }

}
