package com.djt.security.enums;

/**
 * 签名枚举.
 */
public enum SignatureEnum
{
  MD5("MD5"), RSA("RSA"), DSA("DSA");

  private String code;

  private SignatureEnum(String code)
  {
    this.code = code;
  }

  /**
   * Getter method for property <tt>code</tt>.
   *
   * @return property value of code
   */
  public String getCode()
  {
    return code;
  }

  /**
   * Setter method for property <tt>code</tt>.
   *
   * @param code value to be assigned to property code
   */
  public void setCode(String code)
  {
    this.code = code;
  }

}
