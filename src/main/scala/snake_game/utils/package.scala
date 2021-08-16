package object utils {
  implicit class IndexedSeqOps[+A](seq: Seq[A]) {
    /**
     * extract all possible combinations of (element, allOtherElementsInSequence)
     * For example: (1, 2, 3) => Seq((1, Seq(2, 3)), (2, Seq(1, 3)), (3, Seq(1, 2))
     */
    def mapToOneToOthersCombinations(): Seq[(A, Seq[A])] = {
      seq
        .zipWithIndex
        .map{case (currentElement, index) =>
          val lastElements = seq.length - index
          (currentElement, seq.take(index) ++ seq.takeRight(lastElements - 1))
        }
    }
  }

  implicit class MultiMapBuilder[A, B](seq: Seq[(A, B)]) {
    def asMultiMap(): Map[A, Seq[B]] = {
      seq.groupBy(_._1)
        .view
        .mapValues(value => value.map { case (_, gameObject) => gameObject })
        .toMap
    }
  }
}
