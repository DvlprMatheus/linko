package com.dvlprmatheus.linko.utils;

import java.util.UUID;

public class CodeUtils {

  private CodeUtils() {
    throw new IllegalStateException("Utility class");
  }

  public static String genrateUniqueCode() {
    return UUID.randomUUID().toString().replace("-", "").substring(0, 10);
  }
}
