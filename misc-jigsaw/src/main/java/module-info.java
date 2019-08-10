/**
 * @author Thomas Freese
 */
module freese.jigsaw
{
    // exports: Stellt eine Schnittstelle nach außen bereit
    // opens: Öffnet Package für Reflection

    // exports de.freese.jigsaw.jaxb;

    // opens de.freese.jigsaw.jaxb;
    opens de.freese.jigsaw.jaxb to java.xml.bind;

    requires transitive java.xml.bind;
    requires java.net.http;
}