package select

import java.io.File

import scala.collection.mutable.ListBuffer
import scala.util.Random

class Test (val image1: File, val image2: File) {

  def isSameClass: Boolean = {
    image1.getParentFile.equals(image2.getParentFile)
  }

}

/**
  * A SelectionStrategy decides how to pick images from a
  * provided source.
  */
trait Selection {

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

abstract class PredicateSelection extends Selection {

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
  * Picks half of amount same class and half of amount different class many
  * tests.
  *
  * @param amount The number of tests to create.
  */
class PickEvenSelection(override val amount: Int) extends Selection {

  private val same = new PredicateSelection {
    override def amount: Int = PickEvenSelection.this.amount / 2
    override def check(test: Test): Boolean = test.isSameClass
  }

  private val diff = new PredicateSelection {
    override def amount: Int = PickEvenSelection.this.amount / 2
    override def check(test: Test): Boolean = !test.isSameClass
  }

  override def select(images: List[File]): List[Test] = {
    Random.shuffle(same.select(images).++(diff.select(images)))
  }

}