package select

import java.io.File

import scala.collection.mutable.ListBuffer

/**
  * A SelectionStrategy decides how to pick images from a
  * provided source.
  */
trait SelectionStrategy {

  def amount: Int

  /**
    * Creates test cases from the list of provided images and
    * returns them as a list.
    *
    * @param images The list of images with the candidates.
    * @return List[Test]
    */
  def select(images: List[File]): List[Test]

  /**
    * Returns a random image of images.
    *
    * @param images The list of images to pick from.
    * @return File
    */
  protected def getRandomImage(images: List[File]): File = {
    images((Math.random() * (images.length - 1)).toInt)
  }

}

/**
  * Creates amount many tests randomly from all images. Duplicates
  * included.
  *
  * @param amount The number of Tests to create.
  */
class PickFixedAmountStrategy(override val amount: Int) extends SelectionStrategy {

  override def select(images: List[File]): List[Test] = {
    val tests = ListBuffer[Test]()

    for (_ <- 0 until amount) {
      val image1 = getRandomImage(images)
      val image2 = getRandomImage(images)
      tests.+=(new Test(image1, image2))
    }
    tests.toList
  }

}

/**
  * Performs a check on every created test to decide if it should
  * be added to the test list.
  *
  * @param amount The number of tests to create.
  */
abstract class PickByPredicateStrategy(override val amount: Int) extends SelectionStrategy {

  /**
    * Returns true if the test should be added to the tests.
    *
    * @param test The test to check.
    */
  def check(test: Test): Boolean

  override def select(images: List[File]): List[Test] = {
    val tests = ListBuffer[Test]()
    var i = amount

    while (i > 0) {
      val image1 = getRandomImage(images)
      val image2 = getRandomImage(images)
      val test = new Test(image1, image2)

      if (check(test)) {
        tests.+=(test)
        i -= 1
      }
    }
    tests.toList
  }
}

/**
  * Only Adds a test if the images are in the same class (same folder).
  *
  * @param amount The number of tests to create.
  */
class PickSameClassStrategy(amount: Int) extends PickByPredicateStrategy(amount) {

  override def check(test: Test): Boolean = test.isSimilar

}

/**
  * Only Adds a test if the images differ in class (different folder).
  *
  * @param amount The number of tests to create.
  */
class PickDiffClassStrategy(amount: Int) extends PickByPredicateStrategy(amount) {

  override def check(test: Test): Boolean = !test.isSimilar

}
