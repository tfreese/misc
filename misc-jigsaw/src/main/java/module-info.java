/**
 * opens: Öffnet Package für Reflection<br>
 *
 * @author Thomas Freese
 */
// open module freese.jigsaw
module freese.jigsaw
{
    // exports de.freese.jigsaw.jaxb;

    //
    // opens de.freese.jigsaw.jaxb;
    opens de.freese.jigsaw.jaxb to java.xml.bind;

    requires transitive java.xml.bind;
    requires java.net.http;
}