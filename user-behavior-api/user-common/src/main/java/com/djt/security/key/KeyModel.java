package com.djt.security.key;

/**
 * 密钥模型.
 *
 */
public class KeyModel
{

  private String privateKey;

  private String publicKey;

  /**
   * Getter method for property <tt>privateKey</tt>.
   *
   * @return property value of privateKey
   */
  public String getPrivateKey()
  {
    return privateKey;
  }

  /**
   * Setter method for property <tt>privateKey</tt>.
   *
   * @param privateKey value to be assigned to property privateKey
   */
  public void setPrivateKey(String privateKey)
  {
    this.privateKey = privateKey;
  }

  /**
   * Getter method for property <tt>publicKey</tt>.
   *
   * @return property value of publicKey
   */
  public String getPublicKey()
  {
    return publicKey;
  }

  /**
   * Setter method for property <tt>publicKey</tt>.
   *
   * @param publicKey value to be assigned to property publicKey
   */
  public void setPublicKey(String publicKey)
  {
    this.publicKey = publicKey;
  }

}
