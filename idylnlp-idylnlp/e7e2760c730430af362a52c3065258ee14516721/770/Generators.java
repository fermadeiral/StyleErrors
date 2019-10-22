
package ai.idylnlp.training.definition.xml;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * <p>Java class for generators complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="generators"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="window" maxOccurs="2" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;all&gt;
 *                   &lt;element name="tokenclass" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                   &lt;element name="token" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *                 &lt;/all&gt;
 *                 &lt;attribute name="prevLength" type="{http://www.w3.org/2001/XMLSchema}byte" /&gt;
 *                 &lt;attribute name="nextLength" type="{http://www.w3.org/2001/XMLSchema}byte" /&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="definition" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="prevmap" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="bigram" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="tokenclass" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="token" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="sentence" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;simpleContent&gt;
 *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
 *                 &lt;attribute name="begin" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                 &lt;attribute name="end" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *               &lt;/extension&gt;
 *             &lt;/simpleContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "generators", propOrder = {
    "window",
    "definition",
    "prevmap",
    "bigram",
    "tokenclass",
    "token",
    "sentence"
})
public class Generators {

    protected List<Generators.Window> window;
    protected String definition;
    protected String prevmap;
    protected String bigram;
    protected String tokenclass;
    protected String token;
    protected Generators.Sentence sentence;

    /**
     * Gets the value of the window property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the window property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getWindow().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Generators.Window }
     * 
     * 
     */
    public List<Generators.Window> getWindow() {
        if (window == null) {
            window = new ArrayList<Generators.Window>();
        }
        return this.window;
    }

    /**
     * Gets the value of the definition property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDefinition() {
        return definition;
    }

    /**
     * Sets the value of the definition property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefinition(String value) {
        this.definition = value;
    }

    /**
     * Gets the value of the prevmap property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrevmap() {
        return prevmap;
    }

    /**
     * Sets the value of the prevmap property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrevmap(String value) {
        this.prevmap = value;
    }

    /**
     * Gets the value of the bigram property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBigram() {
        return bigram;
    }

    /**
     * Sets the value of the bigram property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBigram(String value) {
        this.bigram = value;
    }

    /**
     * Gets the value of the tokenclass property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTokenclass() {
        return tokenclass;
    }

    /**
     * Sets the value of the tokenclass property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTokenclass(String value) {
        this.tokenclass = value;
    }

    /**
     * Gets the value of the token property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getToken() {
        return token;
    }

    /**
     * Sets the value of the token property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setToken(String value) {
        this.token = value;
    }

    /**
     * Gets the value of the sentence property.
     * 
     * @return
     *     possible object is
     *     {@link Generators.Sentence }
     *     
     */
    public Generators.Sentence getSentence() {
        return sentence;
    }

    /**
     * Sets the value of the sentence property.
     * 
     * @param value
     *     allowed object is
     *     {@link Generators.Sentence }
     *     
     */
    public void setSentence(Generators.Sentence value) {
        this.sentence = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;simpleContent&gt;
     *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
     *       &lt;attribute name="begin" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *       &lt;attribute name="end" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *     &lt;/extension&gt;
     *   &lt;/simpleContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "value"
    })
    public static class Sentence {

        @XmlValue
        protected String value;
        @XmlAttribute(name = "begin")
        protected String begin;
        @XmlAttribute(name = "end")
        protected String end;

        /**
         * Gets the value of the value property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getValue() {
            return value;
        }

        /**
         * Sets the value of the value property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setValue(String value) {
            this.value = value;
        }

        /**
         * Gets the value of the begin property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getBegin() {
            return begin;
        }

        /**
         * Sets the value of the begin property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setBegin(String value) {
            this.begin = value;
        }

        /**
         * Gets the value of the end property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getEnd() {
            return end;
        }

        /**
         * Sets the value of the end property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setEnd(String value) {
            this.end = value;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;all&gt;
     *         &lt;element name="tokenclass" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *         &lt;element name="token" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
     *       &lt;/all&gt;
     *       &lt;attribute name="prevLength" type="{http://www.w3.org/2001/XMLSchema}byte" /&gt;
     *       &lt;attribute name="nextLength" type="{http://www.w3.org/2001/XMLSchema}byte" /&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {

    })
    public static class Window {

        protected String tokenclass;
        protected String token;
        @XmlAttribute(name = "prevLength")
        protected Byte prevLength;
        @XmlAttribute(name = "nextLength")
        protected Byte nextLength;

        /**
         * Gets the value of the tokenclass property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTokenclass() {
            return tokenclass;
        }

        /**
         * Sets the value of the tokenclass property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTokenclass(String value) {
            this.tokenclass = value;
        }

        /**
         * Gets the value of the token property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getToken() {
            return token;
        }

        /**
         * Sets the value of the token property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setToken(String value) {
            this.token = value;
        }

        /**
         * Gets the value of the prevLength property.
         * 
         * @return
         *     possible object is
         *     {@link Byte }
         *     
         */
        public Byte getPrevLength() {
            return prevLength;
        }

        /**
         * Sets the value of the prevLength property.
         * 
         * @param value
         *     allowed object is
         *     {@link Byte }
         *     
         */
        public void setPrevLength(Byte value) {
            this.prevLength = value;
        }

        /**
         * Gets the value of the nextLength property.
         * 
         * @return
         *     possible object is
         *     {@link Byte }
         *     
         */
        public Byte getNextLength() {
            return nextLength;
        }

        /**
         * Sets the value of the nextLength property.
         * 
         * @param value
         *     allowed object is
         *     {@link Byte }
         *     
         */
        public void setNextLength(Byte value) {
            this.nextLength = value;
        }

    }

}
