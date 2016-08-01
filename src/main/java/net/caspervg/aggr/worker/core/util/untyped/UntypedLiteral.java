package net.caspervg.aggr.worker.core.util.untyped;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;

import javax.xml.datatype.XMLGregorianCalendar;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

/**
 * Literal that does not have a datatype. Necessary for a hack in RDF4J to allow simple literal insertion.
 *
 * {@inheritDoc}
 */
public class UntypedLiteral implements Literal, Serializable {

    private static IRI datatype = null;
    private String value;

    public UntypedLiteral(String value) {
        this.value = value;
    }

    @Override
    public String stringValue() {
        return String.format("%s", value);
    }

    @Override
    public String getLabel() {
        return this.value;
    }

    @Override
    public Optional<String> getLanguage() {
        return Optional.empty();
    }

    @Override
    public IRI getDatatype() {
        return null;
    }

    public static void setDatatype(IRI datatype) {
        UntypedLiteral.datatype = datatype;
    }

    @Override
    public byte byteValue() {
        return 0;
    }

    @Override
    public short shortValue() {
        return 0;
    }

    @Override
    public int intValue() {
        return 0;
    }

    @Override
    public long longValue() {
        return 0;
    }

    @Override
    public BigInteger integerValue() {
        return null;
    }

    @Override
    public BigDecimal decimalValue() {
        return null;
    }

    @Override
    public float floatValue() {
        return 0;
    }

    @Override
    public double doubleValue() {
        return 0;
    }

    @Override
    public boolean booleanValue() {
        return false;
    }

    @Override
    public XMLGregorianCalendar calendarValue() {
        return null;
    }
}
