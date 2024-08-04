package dzufferey.utils

object Misc {

  def graphvizToSvgDot(dot: String): String = {
    val (code, out, err) = SysCmd(Array("dot", "-Tsvg"), dot)
    if (code == 0) {
      out
    } else {
      //Logger("basic", Warning, "error running dot (code: "+code+").")
      "<pre>\n" + dot + "\n</pre>"
    }
  }

  def graphvizToSvgFdp(dot: String): String = {
    val (code, out, err) = SysCmd(Array("fdp", "-Tsvg", "-GK=1"), dot)
    if (code == 0) {
      out
    } else {
      //Logger("basic", Warning, "error running fdp (code: "+code+").")
      "<pre>\n" + dot + "\n</pre>"
    }
  }

  def quote(str: String) =  "\"" + str.replaceAll("\"", "\\\\\"") + "\""

  def quoteIfFancy(str: String) = if (str matches ".*(\"|\\$|#).*") quote(str) else str

  def indent(prefix: String, content: String) = {
    val builder = new StringBuilder(content)
    //if the last char is a line return, replace it by a space
    if ((! builder.isEmpty) && (builder(builder.size -1) == '\n')) builder.setLength(builder.size -1)
    var start = 0
    while (start < builder.length && builder.indexOf('\n', start) != -1) {
      val idx = builder.indexOf('\n', start)
      builder.insert(idx+1, prefix)
      start = idx + prefix.length + 1
    }
    prefix + builder
  }

  def safeCall[A,B](fct: A => B, arg: A): Option[B] = {
    try {
      Some(fct(arg))
    } catch {
      case _ : Throwable => None
    }
  }

  def toBoolean(str: String): Option[Boolean] = safeCall((x: String) => x.toBoolean, str)

  def toInt(str: String): Option[Int] = safeCall((x: String) => x.toInt, str)

  def toLong(str: String): Option[Long] = safeCall((x: String) => x.toLong, str)

  def toFloat(str: String): Option[Float] = safeCall((x: String) => x.toFloat, str)

  def toDouble(str: String): Option[Double] = safeCall((x: String) => x.toDouble, str)

  def allSubLists[A](lst: Seq[A]): Seq[Seq[A]] = lst.headOption match {
    case Some(e) =>
      var sub = allSubLists(lst.tail)
      sub.map( xs => e +: xs ) ++ sub
    case None => Seq(lst)
  }

  def allPartitions[A](lst: Seq[A]): Seq[(Seq[A],Seq[A])] = lst.headOption match {
    case Some(e) =>
      allPartitions(lst.tail).flatMap{ case (left, right) => 
        Seq( (e +: left) -> right, left -> ( e +: right) )
      }
    case None => Seq(lst -> lst)
  }

  //an iterator version (avoid creating large collections)
  def cartesianProductIterator[A](domains: IndexedSeq[IndexedSeq[A]]): Iterator[Vector[A]] = new Iterator[Vector[A]] {
    protected val s = domains.size
    protected val indices = Array.fill(s)(0)

    protected def increment: Unit = {
      var carry = true
      var i = 0
      while (carry && i < s) {
        val v = indices(i) + 1
        if (v >= domains(i).size) {
          indices(i) = 0
        } else {
          indices(i) = v
          carry = false
        }
        i += 1
      }
      if (carry) {
        indices(s-1) = domains(s-1).size
      }
    }

    def hasNext: Boolean = {
      var i = 0
      while (i < s) {
        if (indices(i) >= domains(i).size) return false
        i += 1
      }
      s > 0
    }

    def next() = {
      val builder = Vector.newBuilder[A]
      builder.sizeHint(s)
      var i = 0
      while (i < s) {
        builder += domains(i)(indices(i))
        i += 1
      }
      increment
      builder.result()
    }

  }


  //Cartesian product from many dimensions, but with homogeneous type.
  def cartesianProduct[A](domains: Iterable[Iterable[A]]): Iterable[Seq[A]] = domains.headOption match {
    case Some(lst) => for (xs <- cartesianProduct(domains.tail); x <- lst) yield x +: xs
    case None => Seq(Nil)
  }

  def subSeqences[A](seq: Iterable[A]): Iterable[List[A]] = seq.headOption match {
    case Some(x) => subSeqences(seq.tail).flatMap(xs => List(xs, x::xs))
    case None => Seq(Nil)
  }

  def commonPrefix(x: String, y: String): Int = {
    val bound = math.min(x.length, y.length)
    var i = 0
    while (i < bound) {
      if (x(i) != y(i)) {
        return i
      } else {
        i += 1
      }
    }
    bound
    //(v1.name zip v2.name).takeWhile{ case (a,b) => a == b}.length
  }
  
  def mapFold[A,B,C](seq: List[A], acc: B, fct: (A,B) => (C,B)): (List[C], B) = {
    def rec(lst: List[A], acc: B): (List[C], B) = lst match {
      case x :: xs =>
        val (y, acc2) = fct(x, acc)
        val (ys, acc3) = rec(xs, acc2)
        (y :: ys, acc3)
      case Nil => (Nil, acc)
    }
    rec(seq, acc)
  }

}
