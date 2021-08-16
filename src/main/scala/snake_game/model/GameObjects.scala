package snake_game.model

sealed trait GameObject {
  //the same object could be placed to the the point more than once, thus return Seq (not Set) here
  def getOccupiedPoints: Seq[GamePoint]
}

case class Apple (position: GamePoint) extends GameObject {
  override def getOccupiedPoints: Seq[GamePoint] = Seq(position)
}

case class Wall (position: GamePoint) extends GameObject {
  override def getOccupiedPoints: Seq[GamePoint] = Seq(position)
}

case class Snake (body: IndexedSeq[GamePoint], direction: Direction, lastVisitedCell: Option[GamePoint]) extends GameObject {
  override def getOccupiedPoints: Seq[GamePoint] = body
  def changeDirection(newDirection: Direction): Snake = this.copy(direction = newDirection)
}

case class Teleport(position: GamePoint, out: GamePoint) extends GameObject {
  override def getOccupiedPoints: Seq[GamePoint] = Seq(position)
}
