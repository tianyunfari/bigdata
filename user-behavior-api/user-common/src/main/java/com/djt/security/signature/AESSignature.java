package com.djt.security.signature;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Aes 加密方法
 */
public final class AESSignature
{
  private AESSignature()
  {
  }

  private static final String AES = "AES";

  private static final String CHAR_SET = "utf-8";

  /**
   * aes解密
   *
   * @param content
   * @param password
   * @return
   */
  public static String decrypt(byte[] content, String password)
  {
    try {
      SecretKeySpec securekey = new SecretKeySpec(password.getBytes(CHAR_SET), AES);
      // 创建密码器
      Cipher cipher = Cipher.getInstance(AES);
      // 初始化
      cipher.init(Cipher.DECRYPT_MODE, securekey);
      return new String(cipher.doFinal(content), CHAR_SET);
    } catch (Exception e) {
      //ExceptionUtil.caught(e, "Aes解密失败.");
    }
    return null;
  }

  /**
   * aes加密
   *
   * @param content
   * @param password
   * @return
   */
  public static byte[] encrypt(String content, String password)
  {
    try {
      SecretKeySpec securekey = new SecretKeySpec(password.getBytes(CHAR_SET), AES);
      // 创建密码器
      Cipher cipher = Cipher.getInstance(AES);
      byte[] byteContent = content.getBytes(CHAR_SET);
      // 初始化
      cipher.init(Cipher.ENCRYPT_MODE, securekey);
      // 加密
      return cipher.doFinal(byteContent);
    } catch (Exception e) {
      //ExceptionUtil.caught(e, "Aes加密失败.");
    }
    return null;
  }
}
