# Indented

Tools to make debugging methods whose String output compiles to valid Java or Kotlin and
is pretty-print indented for easy reading.
Indentation makes data structures much easier to understand at a glance.
Being able to compile your debugging output is a huge win for setting up data conditions for unit tests.

Usage examples are in the unit tests to ensure that they compile and work correctly:

* [Kotlin](src/test/kotlin/org/organicdesign/TestStringUtils.kt)
* [Java](src/test/kotlin/org/organicdesign/TestStringUtilsJava.java)

IntelliJ can format your code like this if you set:

Settings:
Editor:
Code Style:
Java:
Wrapping and Braces:
Method Call Arguments:
Align when multilign

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.organicdesign.indented/Indented/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.organicdesign.indented/Indented)

## Change Log
### Release 0.0.7 2019-04-04
 - Renamed StringUtils.objToStr() to indent().
 This is the method is now meant as a catch-all to throw any object at.
 It will treat objects as indented strings when practical, or just call .toString() when not.

#### Release 0.0.6 2019-03-06
 - Fixed StringUtils.iterableToStr() to only add the indent on the 2nd line.
 Same with .listToStr()

#### Release 0.0.5 2019-03-06
 - Allow StringUtils.iterableToStr() and .listToStr() to allow nulls in the collections.
 - Upgraded Kotlin to 1.3.21.

### Release 0.0.4 2018-11-05
 - Added StringUtils.bashSingleQuote() function.
 - Upgraded Kotlin to 1.3.0.

### Release 0.0.3 2018-08-09
 - Encoded $ as \u0024 so that behavior is consistent between Java and Kotlin.
 - Made spaces() return a String instead of a StringBuilder and throw an exception if passed a negative length
 - Added usage examples in unit tests in both Java and Kotlin
 - Updated documentation.

### Release 0.0.2 ".stringify() Handles All Characters"
 - StringUtils.stringify() should now turn *any* text into a valid Kotlin/Java String.
 If you can prove this wrong, please report it!
 - The output of .stringify() should therefore also be safe to write to a log file.
   We encode \n instead of a line-feed, \d instead of delete,
   and escape (e.g. `\ufffe`) any invalid values instead of writing them out raw.
 - High unicode characters (above the BMP) are correctly handled as well.
 - The dollar-sign is a special character in Kotlin strings, but not in Java.
   We escape it (as `\$` to make it safe in Kotlin) which Java may or may not care about.
 - Escaping high Unicode characters will also make visible "overbyte" encoding where you can use more bytes to encode simple characters,
 and thus hide a second, secret message in the number of bytes used to encode each ascii character (steganography).
 It neither promotes, nor prevents overbyte encoding, just makes it more visible in some cases.

### Release 0.0.1 - Initial Release
