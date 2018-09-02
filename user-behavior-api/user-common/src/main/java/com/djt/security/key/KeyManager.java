package com.djt.security.key;

import com.djt.utils.MyLogger;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * 密钥管理器.
 *
 */
public final class KeyManager
{
  private KeyManager()
  {
  }

  /**
   * 日志.
   */
  private static final MyLogger LOGGER = MyLogger.getLogger(KeyManager.class);

  /**
   * KEY大小.
   */
  private static final int KEY_SIZE = 512;

  /**
   * 种子.
   */
  private static final String SEED = "0f22507a10bbddd07d8a3082122966e3";       //for Amp

  private static final String SEED_LMS = "83940473623048da09837494ee374849";       //for LMS

  private static final String SEED_TES = "cd1edc22vfjtyya2ppagakitczmjl1o1";
  /**
   * 将X509格式的输入流转换成Certificate对象。
   *
   * @param ins
   * @return
   */
  //    public static Certificate getCertificateFromX509(InputStream ins) {
  //        try {
  //            Certificate certificate = CertificateFactory.getInstance("X.509").generateCertificate(ins);
  //
  //            return certificate;
  //        } catch (CertificateException ex) {
  //            logger.error("获取证书时发生异常：", ex);
  //            return null;
  //        }
  //    }

  /**
   * @param algorithm
   * @param ins
   * @return
   * @throws NoSuchAlgorithmException
   */
  public static PublicKey getPublicKeyFromX509(String algorithm, InputStream ins) throws NoSuchAlgorithmException
  {
    try {
      KeyFactory keyFactory = KeyFactory.getInstance(algorithm);

      ByteArrayOutputStream output = new ByteArrayOutputStream();
      IOUtils.copy(ins, output);

      byte[] encodedKey = output.toByteArray();

      // 先base64解码
      encodedKey = Base64.decodeBase64(encodedKey);

      return keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
    } catch (IOException ex) {
      LOGGER.error("获取公钥时发生异常：", ex);
    } catch (InvalidKeySpecException ex) {
      LOGGER.error("获取公钥时发生异常：", ex);
    }

    return null;
  }

  /**
   * @param algorithm
   * @param ins
   * @return
   * @throws NoSuchAlgorithmException
   */
  public static PrivateKey getPrivateKeyFromPKCS8(String algorithm, InputStream ins) throws NoSuchAlgorithmException
  {
    if (ins == null || StringUtils.isBlank(algorithm)) {
      return null;
    }

    try {
      KeyFactory keyFactory = KeyFactory.getInstance(algorithm);

      ByteArrayOutputStream output = new ByteArrayOutputStream();
      IOUtils.copy(ins, output);
      byte[] encodedKey = output.toByteArray();
      LOGGER.debug("KeyReader.encodedKey:" + new String(encodedKey));
      // 先base64解码
      encodedKey = Base64.decodeBase64(encodedKey);
      LOGGER.debug("KeyReader.encodedKey2:" + new String(encodedKey));
      return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(encodedKey));
    } catch (IOException ex) {
      LOGGER.error("获取私钥时发生异常：", ex);
    } catch (InvalidKeySpecException ex) {
      LOGGER.error("获取私钥时发生异常：", ex);
    }
    return null;
  }

  /**
   * 生产密钥对.
   *
   * @param algorithm 算法
   * @return 密钥对模型
   * @throws Exception
   */
  public static KeyModel generateKey(String algorithm) throws Exception
  {
    SecureRandom random = new SecureRandom();
    // random.setSeed(SEED.getBytes());
//        random.setSeed(SEED_LMS.getBytes());
    random.setSeed(SEED_TES.getBytes());

    KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance(algorithm);
    keyGenerator.initialize(KEY_SIZE, random);
    KeyPair keyPair = keyGenerator.generateKeyPair();
    PrivateKey privateKey = keyPair.getPrivate();
    PublicKey publicKey = keyPair.getPublic();

    KeyModel model = new KeyModel();
    model.setPrivateKey(new String(Base64.encodeBase64(privateKey.getEncoded())));
    model.setPublicKey(new String(Base64.encodeBase64(publicKey.getEncoded())));

    return model;
  }

  public static void main(String[] args) throws Exception
  {
    KeyModel model = generateKey("RSA");
    System.out.println("privateKey=" + model.getPrivateKey());
    System.out.println("publicKey=" + model.getPublicKey());
  }
}
