/**
 * Created: 17.09.2019
 */

package de.freese.maven.proxy.repository.http;

import de.freese.maven.proxy.repository.RemoteRepository;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * Interface eines HTTP-Repositories.
 *
 * @author Thomas Freese
 */
public interface HttpRepository extends RemoteRepository
{
    /**
     * (0x0D, 0x0A), (13,10), (\r\n)
     */
    public static final String CRLF = "\r\n";

    /**
    *
    */
    public static final int HTTP_OK = HttpResponseStatus.OK.code();
}
