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
    * Returns a random image of images.
    *
    * @param images The list of images to pick from.
    * @return File
    */
  protected def getRandomImage(images: List[File]): File = {
    images((Math.random() * (images.length - 1)).toInt)
  }

  /**
    * Creates test cases from the list of provided images and
    * returns them as a list.
    *
    * @param images The list of images with the candidates.
    * @return List[Test]
    */
  def select(images: List[File]): List[Test]

}

abstract class PredicateStrategy extends SelectionStrategy {

  /**
    * Returns true if the test should be added to the tests.
    *
    * @param test The test to check.
    */
  def check(test: Test): Boolean

  def select(images: List[File]): List[Test] = {
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
class PickSameClassStrategy(override val amount: Int) extends PredicateStrategy {

  override def check(test: Test): Boolean = test.isSameClass

}

/**
  * Only Adds a test if the images differ in class (different folder).
  *
  * @param amount The number of tests to create.
  */
class PickDiffClassStrategy(override val amount: Int) extends PredicateStrategy {

  override def check(test: Test): Boolean = !test.isSameClass

}

/**
  * Picks half of amount same class and half of amount different class many
  * tests.
  *
  * @param amount The number of tests to create.
  */
class PickEvenStrategy (override val amount: Int) extends SelectionStrategy {

  private val sameStrategy = new PickSameClassStrategy(amount / 2)
  private val diffStrategy = new PickDiffClassStrategy(amount / 2)

  override def select(images: List[File]): List[Test] = {
    sameStrategy.select(images).++(diffStrategy.select(images))
  }

}