package select

import java.io.File

/**
  * A Test is a Product of two image files.
  *
  * @param image1 the first image file.
  * @param image2 the second image file.
  */
class Test(val image1: File, val image2: File, var similarity: Float = 0.0f) {

  /**
    * Returns true if and only if the parent of image1 is the same
    * as the parent of image2. Two images are assumed to be in the
    * same class, if there are in the same folder.
    *
    * @return Boolean
    */
  def isSameClass(): Boolean = {
    image1.getParentFile.equals(image2.getParentFile)
  }

}
