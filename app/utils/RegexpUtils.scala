package utils

object RegexpUtils {

  /* Minimum 8 characters at least 1 Uppercase Alphabet, 1 Lowercase Alphabet and 1 Number */
  val PasswordRegexp = """^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d]{8,}$""".r

}
