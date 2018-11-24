/**
 * Created: 03.10.2018
 */

package de.freese.jigsaw.web;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.function.Supplier;

/**
 * @author Thomas Freese
 */
public class HttpQuery implements Supplier<String>
{
    /**
     *
     */
    private final HttpClient httpClient;

    /**
     * Erstellt ein neues {@link HttpQuery} Object.
     */
    public HttpQuery()
    {
        super();

        this.httpClient = HttpClient.newBuilder().version(Version.HTTP_2).build();
    }

    /**
     * @see java.util.function.Supplier#get()
     */
    @Override
    public String get()
    {

        // @formatter:off
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://jsonplaceholder.typicode.com/todos/1"))
                .header("Accept", "application/json;charset=UTF-8")
                .GET()
                .build();
        // @formatter:on

        String result = null;

        try
        {
            HttpResponse<String> response = this.httpClient.send(request, BodyHandlers.ofString());
            result = response.body();
        }
        catch (RuntimeException nex)
        {
            throw nex;
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }

        return result;
    }
}
