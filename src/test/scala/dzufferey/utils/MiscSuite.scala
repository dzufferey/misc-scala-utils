package dzufferey.utils

import org.scalatest.funsuite.AnyFunSuite

class MiscSuite extends AnyFunSuite {

  import Misc._

  test("commonPrefix") {
    assert(commonPrefix("","") == 0)
    assert(commonPrefix("a","") == 0)
    assert(commonPrefix("","a") == 0)
    assert(commonPrefix("a","a") == 1)
    assert(commonPrefix("a","b") == 0)
    assert(commonPrefix("asdf","asdfgh") == 4)
  }

  test("allSubLists") {
    val t1 = List(1,2,3)
    val r1 = allSubLists(t1)
    assert(r1.size == 8)
    assert(r1 contains List[Int]())
    assert(r1 contains List(1))
    assert(r1 contains List(2))
    assert(r1 contains List(3))
    assert(r1 contains List(1,2))
    assert(r1 contains List(1,3))
    assert(r1 contains List(2,3))
    assert(r1 contains t1)
  }

  test("cartesianProduct") {
    val tmp = List(1,2)
    val t1 = List(tmp,tmp,tmp)
    val r1 = cartesianProduct(t1).toSeq
    assert(r1.size == 8, s"$t1 -> $r1")
    assert(r1 contains List(1,1,1))
    assert(r1 contains List(1,1,2))
    assert(r1 contains List(1,2,1))
    assert(r1 contains List(1,2,2))
    assert(r1 contains List(2,1,1))
    assert(r1 contains List(2,1,2))
    assert(r1 contains List(2,2,1))
    assert(r1 contains List(2,2,2))
  }

  test("cartesianProductIterator") {
    val tmp = Vector(1,2)
    val t1 = Vector(tmp,tmp,tmp)
    val r1 = cartesianProductIterator(t1)
    assert(r1.hasNext && r1.next() == Vector(1,1,1))
    assert(r1.hasNext && r1.next() == Vector(2,1,1))
    assert(r1.hasNext && r1.next() == Vector(1,2,1))
    assert(r1.hasNext && r1.next() == Vector(2,2,1))
    assert(r1.hasNext && r1.next() == Vector(1,1,2))
    assert(r1.hasNext && r1.next() == Vector(2,1,2))
    assert(r1.hasNext && r1.next() == Vector(1,2,2))
    assert(r1.hasNext && r1.next() == Vector(2,2,2))
    assert(!r1.hasNext)
  }
  
  test("cartesianProductIterator empty") {
    val t1 = Vector[Vector[Int]]()
    val r1 = cartesianProductIterator(t1)
    assert(!r1.hasNext)
  }
}
