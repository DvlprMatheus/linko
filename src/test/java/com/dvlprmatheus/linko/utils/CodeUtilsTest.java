package com.dvlprmatheus.linko.utils;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.regex.Pattern;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Code utilities")
class CodeUtilsTest {

  private static final Pattern CODE_PATTERN = Pattern.compile("[0-9a-f]{10}");

  @Test
  @DisplayName("Generates a ten-character lowercase hexadecimal code")
  void generatesCodeWithExpectedFormat() {
    String code = CodeUtils.genrateUniqueCode();

    assertTrue(CODE_PATTERN.matcher(code).matches());
  }
}
