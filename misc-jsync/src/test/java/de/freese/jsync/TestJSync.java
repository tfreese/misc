/**
 * Created: 22.10.2016
 */

package de.freese.jsync;

import java.net.URI;
import java.util.List;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import de.freese.jsync.api.Client;
import de.freese.jsync.api.Options;
import de.freese.jsync.api.Receiver;
import de.freese.jsync.api.Sender;
import de.freese.jsync.impl.SyncPair;
import de.freese.jsync.impl.client.DefaultClient;
import de.freese.jsync.impl.receiver.ReceiverFactory;
import de.freese.jsync.impl.sender.SenderFactory;

/**
 * @author Thomas Freese
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestJSync extends AbstractJSyncTest
{
    /**
     * Erstellt ein neues {@link TestJSync} Object.
     */
    public TestJSync()
    {
        super();
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test010LocalSync() throws Exception
    {
        Options options = new Options();
        options.setDelete(true);
        options.setDryRun(false);
        options.setFollowSymLinks(false);

        String source = PATH_QUELLE.toUri().toString();
        String target = PATH_ZIEL.toUri().toString();

        Sender sender = SenderFactory.createSenderFromURI(options, new URI(source));
        Receiver receiver = ReceiverFactory.createReceiverFromURI(options, new URI(target));

        Client client = new DefaultClient(options);

        List<SyncPair> syncList = client.createSyncList(sender, receiver);

        client.syncReceiver(sender, receiver, syncList);
    }
}
