package com.android.org.bouncycastle.jcajce.provider.asymmetric.ec;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Hashtable;

import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.SecretKeySpec;

import com.android.org.bouncycastle.asn1.DERObjectIdentifier;
import com.android.org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import com.android.org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import com.android.org.bouncycastle.asn1.x9.X9IntegerConverter;
import com.android.org.bouncycastle.crypto.BasicAgreement;
import com.android.org.bouncycastle.crypto.CipherParameters;
import com.android.org.bouncycastle.crypto.DerivationFunction;
import com.android.org.bouncycastle.crypto.agreement.ECDHBasicAgreement;
// BEGIN android-removed
// import com.android.org.bouncycastle.crypto.agreement.ECDHCBasicAgreement;
// import com.android.org.bouncycastle.crypto.agreement.ECMQVBasicAgreement;
// import com.android.org.bouncycastle.crypto.agreement.kdf.DHKDFParameters;
// import com.android.org.bouncycastle.crypto.agreement.kdf.ECDHKEKGenerator;
// END android-removed
import com.android.org.bouncycastle.crypto.digests.SHA1Digest;
import com.android.org.bouncycastle.crypto.params.ECDomainParameters;
import com.android.org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import com.android.org.bouncycastle.crypto.params.ECPublicKeyParameters;
// BEGIN android-removed
// import com.android.org.bouncycastle.crypto.params.MQVPrivateParameters;
// import com.android.org.bouncycastle.crypto.params.MQVPublicParameters;
// END android-removed
import com.android.org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import com.android.org.bouncycastle.jce.interfaces.ECPrivateKey;
import com.android.org.bouncycastle.jce.interfaces.ECPublicKey;
// BEGIN android-removed
// import com.android.org.bouncycastle.jce.interfaces.MQVPrivateKey;
// import com.android.org.bouncycastle.jce.interfaces.MQVPublicKey;
// END android-removed
import com.android.org.bouncycastle.util.Integers;

/**
 * Diffie-Hellman key agreement using elliptic curve keys, ala IEEE P1363
 * both the simple one, and the simple one with cofactors are supported.
 *
 * Also, MQV key agreement per SEC-1
 */
public class KeyAgreementSpi
    extends javax.crypto.KeyAgreementSpi
{
    private static final X9IntegerConverter converter = new X9IntegerConverter();
    private static final Hashtable algorithms = new Hashtable();

    static
    {
        Integer i128 = Integers.valueOf(128);
        Integer i192 = Integers.valueOf(192);
        Integer i256 = Integers.valueOf(256);

        algorithms.put(NISTObjectIdentifiers.id_aes128_CBC.getId(), i128);
        algorithms.put(NISTObjectIdentifiers.id_aes192_CBC.getId(), i192);
        algorithms.put(NISTObjectIdentifiers.id_aes256_CBC.getId(), i256);
        algorithms.put(NISTObjectIdentifiers.id_aes128_wrap.getId(), i128);
        algorithms.put(NISTObjectIdentifiers.id_aes192_wrap.getId(), i192);
        algorithms.put(NISTObjectIdentifiers.id_aes256_wrap.getId(), i256);
        algorithms.put(PKCSObjectIdentifiers.id_alg_CMS3DESwrap.getId(), i192);
    }

    private String                 kaAlgorithm;
    private BigInteger             result;
    private ECDomainParameters     parameters;
    private BasicAgreement         agreement;
    // BEGIN android-removed
    // private DerivationFunction     kdf;
    // END android-removed

    private byte[] bigIntToBytes(
        BigInteger    r)
    {
        return converter.integerToBytes(r, converter.getByteLength(parameters.getG().getX()));
    }

    protected KeyAgreementSpi(
        String kaAlgorithm,
        BasicAgreement agreement,
        DerivationFunction kdf)
    {
        this.kaAlgorithm = kaAlgorithm;
        this.agreement = agreement;
        // BEGIN android-removed
        // this.kdf = kdf;
        // END android-removed
    }

    protected Key engineDoPhase(
        Key     key,
        boolean lastPhase) 
        throws InvalidKeyException, IllegalStateException
    {
        if (parameters == null)
        {
            throw new IllegalStateException(kaAlgorithm + " not initialised.");
        }

        if (!lastPhase)
        {
            throw new IllegalStateException(kaAlgorithm + " can only be between two parties.");
        }

        CipherParameters pubKey;        
        // BEGIN android-removed
        // if (agreement instanceof ECMQVBasicAgreement)
        // {
        //     if (!(key instanceof MQVPublicKey))
        //     {
        //         throw new InvalidKeyException(kaAlgorithm + " key agreement requires "
        //             + getSimpleName(MQVPublicKey.class) + " for doPhase");
        //     }
        //
        //     MQVPublicKey mqvPubKey = (MQVPublicKey)key;
        //     ECPublicKeyParameters staticKey = (ECPublicKeyParameters)
        //         ECUtil.generatePublicKeyParameter(mqvPubKey.getStaticKey());
        //     ECPublicKeyParameters ephemKey = (ECPublicKeyParameters)
        //         ECUtil.generatePublicKeyParameter(mqvPubKey.getEphemeralKey());
        //
        //     pubKey = new MQVPublicParameters(staticKey, ephemKey);
        //
        //     // TODO Validate that all the keys are using the same parameters?
        // }
        // else
        // END android-removed
        {
            if (!(key instanceof PublicKey))
            {
                throw new InvalidKeyException(kaAlgorithm + " key agreement requires "
                    + getSimpleName(ECPublicKey.class) + " for doPhase");
            }

            pubKey = ECUtil.generatePublicKeyParameter((PublicKey)key);

            // TODO Validate that all the keys are using the same parameters?
        }

        result = agreement.calculateAgreement(pubKey);

        return null;
    }

    protected byte[] engineGenerateSecret()
        throws IllegalStateException
    {
        // BEGIN android-removed
        // if (kdf != null)
        // {
        //     throw new UnsupportedOperationException(
        //         "KDF can only be used when algorithm is known");
        // }
        // END android-removed

        return bigIntToBytes(result);
    }

    protected int engineGenerateSecret(
        byte[]  sharedSecret,
        int     offset) 
        throws IllegalStateException, ShortBufferException
    {
        byte[] secret = engineGenerateSecret();

        if (sharedSecret.length - offset < secret.length)
        {
            throw new ShortBufferException(kaAlgorithm + " key agreement: need " + secret.length + " bytes");
        }

        System.arraycopy(secret, 0, sharedSecret, offset, secret.length);
        
        return secret.length;
    }

    protected SecretKey engineGenerateSecret(
        String algorithm)
        throws NoSuchAlgorithmException
    {
        byte[] secret = bigIntToBytes(result);

        // BEGIN android-removed
        // if (kdf != null)
        // {
        //     if (!algorithms.containsKey(algorithm))
        //     {
        //         throw new NoSuchAlgorithmException("unknown algorithm encountered: " + algorithm);
        //     }
        //  
        //     int    keySize = ((Integer)algorithms.get(algorithm)).intValue();
        //
        //     DHKDFParameters params = new DHKDFParameters(new DERObjectIdentifier(algorithm), keySize, secret);
        //
        //     byte[] keyBytes = new byte[keySize / 8];
        //     kdf.init(params);
        //     kdf.generateBytes(keyBytes, 0, keyBytes.length);
        //     secret = keyBytes;
        // }
        // else
        // END android-removed
        {
            // TODO Should we be ensuring the key is the right length?
        }

        return new SecretKeySpec(secret, algorithm);
    }

    protected void engineInit(
        Key                     key,
        AlgorithmParameterSpec  params,
        SecureRandom            random) 
        throws InvalidKeyException, InvalidAlgorithmParameterException
    {
        // BEGIN android-added
        if (params != null)
        {
            throw new InvalidAlgorithmParameterException("No algorithm parameters supported");
        }
        // END android-added
        initFromKey(key);
    }

    protected void engineInit(
        Key             key,
        SecureRandom    random) 
        throws InvalidKeyException
    {
        initFromKey(key);
    }

    private void initFromKey(Key key)
        throws InvalidKeyException
    {
        // BEGIN android-removed
        // if (agreement instanceof ECMQVBasicAgreement)
        // {
        //     if (!(key instanceof MQVPrivateKey))
        //     {
        //         throw new InvalidKeyException(kaAlgorithm + " key agreement requires "
        //             + getSimpleName(MQVPrivateKey.class) + " for initialisation");
        //     }
        //
        //     MQVPrivateKey mqvPrivKey = (MQVPrivateKey)key;
        //     ECPrivateKeyParameters staticPrivKey = (ECPrivateKeyParameters)
        //         ECUtil.generatePrivateKeyParameter(mqvPrivKey.getStaticPrivateKey());
        //     ECPrivateKeyParameters ephemPrivKey = (ECPrivateKeyParameters)
        //         ECUtil.generatePrivateKeyParameter(mqvPrivKey.getEphemeralPrivateKey());
        //
        //     ECPublicKeyParameters ephemPubKey = null;
        //     if (mqvPrivKey.getEphemeralPublicKey() != null)
        //     {
        //         ephemPubKey = (ECPublicKeyParameters)
        //             ECUtil.generatePublicKeyParameter(mqvPrivKey.getEphemeralPublicKey());
        //     }
        //
        //     MQVPrivateParameters localParams = new MQVPrivateParameters(staticPrivKey, ephemPrivKey, ephemPubKey);
        //     this.parameters = staticPrivKey.getParameters();
        //
        //     // TODO Validate that all the keys are using the same parameters?
        //
        //     agreement.init(localParams);
        // }
        // else
        // END android-removed
        {
            if (!(key instanceof PrivateKey))
            {
                throw new InvalidKeyException(kaAlgorithm + " key agreement requires "
                    + getSimpleName(ECPrivateKey.class) + " for initialisation");
            }

            ECPrivateKeyParameters privKey = (ECPrivateKeyParameters)ECUtil.generatePrivateKeyParameter((PrivateKey)key);
            this.parameters = privKey.getParameters();

            agreement.init(privKey);
        }
    }

    private static String getSimpleName(Class clazz)
    {
        String fullName = clazz.getName();

        return fullName.substring(fullName.lastIndexOf('.') + 1);
    }

    public static class DH
        extends KeyAgreementSpi
    {
        public DH()
        {
            super("ECDH", new ECDHBasicAgreement(), null);
        }
    }

    // BEGIN android-removed
    // public static class DHC
    //     extends KeyAgreementSpi
    // {
    //     public DHC()
    //     {
    //         super("ECDHC", new ECDHCBasicAgreement(), null);
    //     }
    // }
    //
    // public static class MQV
    //     extends KeyAgreementSpi
    // {
    //     public MQV()
    //     {
    //         super("ECMQV", new ECMQVBasicAgreement(), null);
    //     }
    // }
    //
    // public static class DHwithSHA1KDF
    //     extends KeyAgreementSpi
    // {
    //     public DHwithSHA1KDF()
    //     {
    //         super("ECDHwithSHA1KDF", new ECDHBasicAgreement(), new ECDHKEKGenerator(new SHA1Digest()));
    //     }
    // }
    //
    // public static class MQVwithSHA1KDF
    //     extends KeyAgreementSpi
    // {
    //     public MQVwithSHA1KDF()
    //     {
    //         super("ECMQVwithSHA1KDF", new ECMQVBasicAgreement(), new ECDHKEKGenerator(new SHA1Digest()));
    //     }
    // }
    // END android-removed
}
