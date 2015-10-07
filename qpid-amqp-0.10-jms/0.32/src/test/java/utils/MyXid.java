package utils;

import javax.transaction.xa.Xid;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by schojak on 24.9.15.
 */
public class MyXid implements Xid
{
    private static AtomicInteger next = new AtomicInteger();
    private static Random random = new Random();

    private int formatId;
    private byte gtrid[];
    private byte bqual[];

    public static MyXid createRandom() {
        int formatId = next.getAndIncrement();

        //System.out.println("Max: " + MAXBQUALSIZE + ", " + MAXGTRIDSIZE);

        byte[] bq = new byte[MAXBQUALSIZE];
        random.nextBytes(bq);
        byte[] gt = new byte[MAXGTRIDSIZE];
        random.nextBytes(gt);
        return new MyXid(formatId, bq, gt);
    }

    public MyXid(int formatId, byte gtrid[], byte bqual[])
    {
        this.formatId = formatId;
        this.gtrid = gtrid;
        this.bqual = bqual;
    }

    public int getFormatId()
    {
        return formatId;
    }

    public byte[] getBranchQualifier()
    {
        return bqual;
    }

    public byte[] getGlobalTransactionId()
    {
        return gtrid;
    }
}
