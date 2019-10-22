
package ai.idylnlp.training.definition.xml;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


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
 *         &lt;element name="algorithm"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" default="perceptron" /&gt;
 *                 &lt;attribute name="cutoff" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" default="0" /&gt;
 *                 &lt;attribute name="iterations" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" default="100" /&gt;
 *                 &lt;attribute name="threads" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" default="2" /&gt;
 *                 &lt;attribute name="l1" type="{http://www.w3.org/2001/XMLSchema}decimal" default="0.1" /&gt;
 *                 &lt;attribute name="l2" type="{http://www.w3.org/2001/XMLSchema}decimal" default="0.1" /&gt;
 *                 &lt;attribute name="m" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" default="15" /&gt;
 *                 &lt;attribute name="max" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" default="30000" /&gt;
 *                 &lt;attribute name="windowSize" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" default="5" /&gt;
 *                 &lt;attribute name="vectors" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="trainingdata"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;attribute name="file" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                 &lt;attribute name="format" type="{http://www.w3.org/2001/XMLSchema}string" default="opennlp" /&gt;
 *                 &lt;attribute name="annotations" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="evaluationdata"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;attribute name="file" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                 &lt;attribute name="format" type="{http://www.w3.org/2001/XMLSchema}string" default="opennlp" /&gt;
 *                 &lt;attribute name="annotations" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="model"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                 &lt;attribute name="file" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                 &lt;attribute name="encryptionkey" type="{http://www.w3.org/2001/XMLSchema}string" default="none" /&gt;
 *                 &lt;attribute name="language" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                 &lt;attribute name="type" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="features"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="generators"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;sequence&gt;
 *                             &lt;element name="cache"&gt;
 *                               &lt;complexType&gt;
 *                                 &lt;complexContent&gt;
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                                     &lt;sequence&gt;
 *                                       &lt;element name="generators" type="{http://www.mtnfog.com}generators"/&gt;
 *                                     &lt;/sequence&gt;
 *                                   &lt;/restriction&gt;
 *                                 &lt;/complexContent&gt;
 *                               &lt;/complexType&gt;
 *                             &lt;/element&gt;
 *                           &lt;/sequence&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *       &lt;/all&gt;
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
@XmlRootElement(name = "trainingdefinition")
public class Trainingdefinition {

    @XmlElement(required = true)
    protected Trainingdefinition.Algorithm algorithm;
    @XmlElement(required = true)
    protected Trainingdefinition.Trainingdata trainingdata;
    @XmlElement(required = true)
    protected Trainingdefinition.Evaluationdata evaluationdata;
    @XmlElement(required = true)
    protected Trainingdefinition.Model model;
    @XmlElement(required = true)
    protected Trainingdefinition.Features features;

    /**
     * Gets the value of the algorithm property.
     * 
     * @return
     *     possible object is
     *     {@link Trainingdefinition.Algorithm }
     *     
     */
    public Trainingdefinition.Algorithm getAlgorithm() {
        return algorithm;
    }

    /**
     * Sets the value of the algorithm property.
     * 
     * @param value
     *     allowed object is
     *     {@link Trainingdefinition.Algorithm }
     *     
     */
    public void setAlgorithm(Trainingdefinition.Algorithm value) {
        this.algorithm = value;
    }

    /**
     * Gets the value of the trainingdata property.
     * 
     * @return
     *     possible object is
     *     {@link Trainingdefinition.Trainingdata }
     *     
     */
    public Trainingdefinition.Trainingdata getTrainingdata() {
        return trainingdata;
    }

    /**
     * Sets the value of the trainingdata property.
     * 
     * @param value
     *     allowed object is
     *     {@link Trainingdefinition.Trainingdata }
     *     
     */
    public void setTrainingdata(Trainingdefinition.Trainingdata value) {
        this.trainingdata = value;
    }

    /**
     * Gets the value of the evaluationdata property.
     * 
     * @return
     *     possible object is
     *     {@link Trainingdefinition.Evaluationdata }
     *     
     */
    public Trainingdefinition.Evaluationdata getEvaluationdata() {
        return evaluationdata;
    }

    /**
     * Sets the value of the evaluationdata property.
     * 
     * @param value
     *     allowed object is
     *     {@link Trainingdefinition.Evaluationdata }
     *     
     */
    public void setEvaluationdata(Trainingdefinition.Evaluationdata value) {
        this.evaluationdata = value;
    }

    /**
     * Gets the value of the model property.
     * 
     * @return
     *     possible object is
     *     {@link Trainingdefinition.Model }
     *     
     */
    public Trainingdefinition.Model getModel() {
        return model;
    }

    /**
     * Sets the value of the model property.
     * 
     * @param value
     *     allowed object is
     *     {@link Trainingdefinition.Model }
     *     
     */
    public void setModel(Trainingdefinition.Model value) {
        this.model = value;
    }

    /**
     * Gets the value of the features property.
     * 
     * @return
     *     possible object is
     *     {@link Trainingdefinition.Features }
     *     
     */
    public Trainingdefinition.Features getFeatures() {
        return features;
    }

    /**
     * Sets the value of the features property.
     * 
     * @param value
     *     allowed object is
     *     {@link Trainingdefinition.Features }
     *     
     */
    public void setFeatures(Trainingdefinition.Features value) {
        this.features = value;
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
     *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" default="perceptron" /&gt;
     *       &lt;attribute name="cutoff" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" default="0" /&gt;
     *       &lt;attribute name="iterations" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" default="100" /&gt;
     *       &lt;attribute name="threads" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" default="2" /&gt;
     *       &lt;attribute name="l1" type="{http://www.w3.org/2001/XMLSchema}decimal" default="0.1" /&gt;
     *       &lt;attribute name="l2" type="{http://www.w3.org/2001/XMLSchema}decimal" default="0.1" /&gt;
     *       &lt;attribute name="m" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" default="15" /&gt;
     *       &lt;attribute name="max" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" default="30000" /&gt;
     *       &lt;attribute name="windowSize" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" default="5" /&gt;
     *       &lt;attribute name="vectors" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Algorithm {

        @XmlAttribute(name = "name")
        protected String name;
        @XmlAttribute(name = "cutoff")
        @XmlSchemaType(name = "nonNegativeInteger")
        protected BigInteger cutoff;
        @XmlAttribute(name = "iterations")
        @XmlSchemaType(name = "nonNegativeInteger")
        protected BigInteger iterations;
        @XmlAttribute(name = "threads")
        @XmlSchemaType(name = "nonNegativeInteger")
        protected BigInteger threads;
        @XmlAttribute(name = "l1")
        protected BigDecimal l1;
        @XmlAttribute(name = "l2")
        protected BigDecimal l2;
        @XmlAttribute(name = "m")
        @XmlSchemaType(name = "nonNegativeInteger")
        protected BigInteger m;
        @XmlAttribute(name = "max")
        @XmlSchemaType(name = "nonNegativeInteger")
        protected BigInteger max;
        @XmlAttribute(name = "windowSize")
        @XmlSchemaType(name = "nonNegativeInteger")
        protected BigInteger windowSize;
        @XmlAttribute(name = "vectors")
        protected String vectors;

        /**
         * Gets the value of the name property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getName() {
            if (name == null) {
                return "perceptron";
            } else {
                return name;
            }
        }

        /**
         * Sets the value of the name property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setName(String value) {
            this.name = value;
        }

        /**
         * Gets the value of the cutoff property.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getCutoff() {
            if (cutoff == null) {
                return new BigInteger("0");
            } else {
                return cutoff;
            }
        }

        /**
         * Sets the value of the cutoff property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setCutoff(BigInteger value) {
            this.cutoff = value;
        }

        /**
         * Gets the value of the iterations property.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getIterations() {
            if (iterations == null) {
                return new BigInteger("100");
            } else {
                return iterations;
            }
        }

        /**
         * Sets the value of the iterations property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setIterations(BigInteger value) {
            this.iterations = value;
        }

        /**
         * Gets the value of the threads property.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getThreads() {
            if (threads == null) {
                return new BigInteger("2");
            } else {
                return threads;
            }
        }

        /**
         * Sets the value of the threads property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setThreads(BigInteger value) {
            this.threads = value;
        }

        /**
         * Gets the value of the l1 property.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getL1() {
            if (l1 == null) {
                return new BigDecimal("0.1");
            } else {
                return l1;
            }
        }

        /**
         * Sets the value of the l1 property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setL1(BigDecimal value) {
            this.l1 = value;
        }

        /**
         * Gets the value of the l2 property.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getL2() {
            if (l2 == null) {
                return new BigDecimal("0.1");
            } else {
                return l2;
            }
        }

        /**
         * Sets the value of the l2 property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setL2(BigDecimal value) {
            this.l2 = value;
        }

        /**
         * Gets the value of the m property.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getM() {
            if (m == null) {
                return new BigInteger("15");
            } else {
                return m;
            }
        }

        /**
         * Sets the value of the m property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setM(BigInteger value) {
            this.m = value;
        }

        /**
         * Gets the value of the max property.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getMax() {
            if (max == null) {
                return new BigInteger("30000");
            } else {
                return max;
            }
        }

        /**
         * Sets the value of the max property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setMax(BigInteger value) {
            this.max = value;
        }

        /**
         * Gets the value of the windowSize property.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getWindowSize() {
            if (windowSize == null) {
                return new BigInteger("5");
            } else {
                return windowSize;
            }
        }

        /**
         * Sets the value of the windowSize property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setWindowSize(BigInteger value) {
            this.windowSize = value;
        }

        /**
         * Gets the value of the vectors property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getVectors() {
            return vectors;
        }

        /**
         * Sets the value of the vectors property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setVectors(String value) {
            this.vectors = value;
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
     *       &lt;attribute name="file" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *       &lt;attribute name="format" type="{http://www.w3.org/2001/XMLSchema}string" default="opennlp" /&gt;
     *       &lt;attribute name="annotations" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Evaluationdata {

        @XmlAttribute(name = "file", required = true)
        protected String file;
        @XmlAttribute(name = "format")
        protected String format;
        @XmlAttribute(name = "annotations")
        protected String annotations;

        /**
         * Gets the value of the file property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getFile() {
            return file;
        }

        /**
         * Sets the value of the file property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setFile(String value) {
            this.file = value;
        }

        /**
         * Gets the value of the format property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getFormat() {
            if (format == null) {
                return "opennlp";
            } else {
                return format;
            }
        }

        /**
         * Sets the value of the format property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setFormat(String value) {
            this.format = value;
        }

        /**
         * Gets the value of the annotations property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getAnnotations() {
            return annotations;
        }

        /**
         * Sets the value of the annotations property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setAnnotations(String value) {
            this.annotations = value;
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
     *       &lt;sequence&gt;
     *         &lt;element name="generators"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;sequence&gt;
     *                   &lt;element name="cache"&gt;
     *                     &lt;complexType&gt;
     *                       &lt;complexContent&gt;
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                           &lt;sequence&gt;
     *                             &lt;element name="generators" type="{http://www.mtnfog.com}generators"/&gt;
     *                           &lt;/sequence&gt;
     *                         &lt;/restriction&gt;
     *                       &lt;/complexContent&gt;
     *                     &lt;/complexType&gt;
     *                   &lt;/element&gt;
     *                 &lt;/sequence&gt;
     *               &lt;/restriction&gt;
     *             &lt;/complexContent&gt;
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
    @XmlType(name = "", propOrder = {
        "generators"
    })
    public static class Features {

        @XmlElement(required = true)
        protected Trainingdefinition.Features.Generators generators;

        /**
         * Gets the value of the generators property.
         * 
         * @return
         *     possible object is
         *     {@link Trainingdefinition.Features.Generators }
         *     
         */
        public Trainingdefinition.Features.Generators getGenerators() {
            return generators;
        }

        /**
         * Sets the value of the generators property.
         * 
         * @param value
         *     allowed object is
         *     {@link Trainingdefinition.Features.Generators }
         *     
         */
        public void setGenerators(Trainingdefinition.Features.Generators value) {
            this.generators = value;
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
         *       &lt;sequence&gt;
         *         &lt;element name="cache"&gt;
         *           &lt;complexType&gt;
         *             &lt;complexContent&gt;
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *                 &lt;sequence&gt;
         *                   &lt;element name="generators" type="{http://www.mtnfog.com}generators"/&gt;
         *                 &lt;/sequence&gt;
         *               &lt;/restriction&gt;
         *             &lt;/complexContent&gt;
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
        @XmlType(name = "", propOrder = {
            "cache"
        })
        public static class Generators {

            @XmlElement(required = true)
            protected Trainingdefinition.Features.Generators.Cache cache;

            /**
             * Gets the value of the cache property.
             * 
             * @return
             *     possible object is
             *     {@link Trainingdefinition.Features.Generators.Cache }
             *     
             */
            public Trainingdefinition.Features.Generators.Cache getCache() {
                return cache;
            }

            /**
             * Sets the value of the cache property.
             * 
             * @param value
             *     allowed object is
             *     {@link Trainingdefinition.Features.Generators.Cache }
             *     
             */
            public void setCache(Trainingdefinition.Features.Generators.Cache value) {
                this.cache = value;
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
             *       &lt;sequence&gt;
             *         &lt;element name="generators" type="{http://www.mtnfog.com}generators"/&gt;
             *       &lt;/sequence&gt;
             *     &lt;/restriction&gt;
             *   &lt;/complexContent&gt;
             * &lt;/complexType&gt;
             * </pre>
             * 
             * 
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                "generators"
            })
            public static class Cache {

                @XmlElement(required = true)
                protected ai.idylnlp.training.definition.xml.Generators generators;

                /**
                 * Gets the value of the generators property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link ai.idylnlp.training.definition.xml.Generators }
                 *     
                 */
                public ai.idylnlp.training.definition.xml.Generators getGenerators() {
                    return generators;
                }

                /**
                 * Sets the value of the generators property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link ai.idylnlp.training.definition.xml.Generators }
                 *     
                 */
                public void setGenerators(ai.idylnlp.training.definition.xml.Generators value) {
                    this.generators = value;
                }

            }

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
     *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *       &lt;attribute name="file" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *       &lt;attribute name="encryptionkey" type="{http://www.w3.org/2001/XMLSchema}string" default="none" /&gt;
     *       &lt;attribute name="language" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *       &lt;attribute name="type" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Model {

        @XmlAttribute(name = "name", required = true)
        protected String name;
        @XmlAttribute(name = "file", required = true)
        protected String file;
        @XmlAttribute(name = "encryptionkey")
        protected String encryptionkey;
        @XmlAttribute(name = "language", required = true)
        protected String language;
        @XmlAttribute(name = "type", required = true)
        protected String type;

        /**
         * Gets the value of the name property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getName() {
            return name;
        }

        /**
         * Sets the value of the name property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setName(String value) {
            this.name = value;
        }

        /**
         * Gets the value of the file property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getFile() {
            return file;
        }

        /**
         * Sets the value of the file property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setFile(String value) {
            this.file = value;
        }

        /**
         * Gets the value of the encryptionkey property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getEncryptionkey() {
            if (encryptionkey == null) {
                return "none";
            } else {
                return encryptionkey;
            }
        }

        /**
         * Sets the value of the encryptionkey property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setEncryptionkey(String value) {
            this.encryptionkey = value;
        }

        /**
         * Gets the value of the language property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getLanguage() {
            return language;
        }

        /**
         * Sets the value of the language property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setLanguage(String value) {
            this.language = value;
        }

        /**
         * Gets the value of the type property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getType() {
            return type;
        }

        /**
         * Sets the value of the type property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setType(String value) {
            this.type = value;
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
     *       &lt;attribute name="file" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *       &lt;attribute name="format" type="{http://www.w3.org/2001/XMLSchema}string" default="opennlp" /&gt;
     *       &lt;attribute name="annotations" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Trainingdata {

        @XmlAttribute(name = "file", required = true)
        protected String file;
        @XmlAttribute(name = "format")
        protected String format;
        @XmlAttribute(name = "annotations")
        protected String annotations;

        /**
         * Gets the value of the file property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getFile() {
            return file;
        }

        /**
         * Sets the value of the file property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setFile(String value) {
            this.file = value;
        }

        /**
         * Gets the value of the format property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getFormat() {
            if (format == null) {
                return "opennlp";
            } else {
                return format;
            }
        }

        /**
         * Sets the value of the format property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setFormat(String value) {
            this.format = value;
        }

        /**
         * Gets the value of the annotations property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getAnnotations() {
            return annotations;
        }

        /**
         * Sets the value of the annotations property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setAnnotations(String value) {
            this.annotations = value;
        }

    }

}
