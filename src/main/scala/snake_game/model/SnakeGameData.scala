package snake_game.model

case class SnakeGameData(walls: Set[Wall], teleports: Set[Teleport], apple: Option[Apple], snake: Option[Snake]) {
  lazy val allObjects: Set[GameObject] = (walls ++ teleports ++ apple ++ snake).toSet
}

object SnakeGameData {
  def apply(gameObjects: Set[GameObject]): SnakeGameData = {
    val walls = gameObjects.collect { case wall: Wall => wall }
    val teleports = gameObjects.collect { case teleport: Teleport => teleport}
    val apples = gameObjects.collectFirst { case apple: Apple => apple }
    val snakes = gameObjects.collectFirst { case snake: Snake => snake }
    new SnakeGameData(walls, teleports, apples, snakes)
  }
}


