package snake_game.engine

import snake_game.model._

object Tiks {
  trait TikEngine[A <: GameObject] {
    def consumeTik(thisObject: A, tik: Tik): Option[GameObject]
  }

  implicit val GameObjectTikProcessor: TikEngine[GameObject] = (thisObject: GameObject, tik: Tik) => {
    thisObject match {
      case apple: Apple => AppleTikProcessor.consumeTik(apple, tik)
      case wall: Wall => WallTikProcessor.consumeTik(wall, tik)
      case snake: Snake => SnakeTikProcessor.consumeTik(snake, tik)
      case teleport: Teleport => TeleportTikProcessor.consumeTik(teleport, tik)
    }
  }

  implicit val AppleTikProcessor: TikEngine[Apple] = (apple: Apple, _: Tik) => Option(apple)

  implicit val WallTikProcessor: TikEngine[Wall] = (wall: Wall, _: Tik) => Option(wall)

  implicit val TeleportTikProcessor: TikEngine[Teleport] = (teleport: Teleport, _: Tik) => Option(teleport)

  implicit val SnakeTikProcessor: TikEngine[Snake] = (snake: Snake, _: Tik) => {
    val newHead = getNextPoint(snake.body.head, snake.direction)
    val lastVisitedPoint = snake.body.lastOption
    val newBody = newHead +: snake.body.dropRight(1)

    Option(snake.copy(body = newBody, lastVisitedCell = lastVisitedPoint))
  }

  private def getNextPoint(point: GamePoint, direction: Direction): GamePoint = {
    direction match {
      case Direction.Up => point.copy(y = point.y + 1)
      case Direction.Right => point.copy(x = point.x + 1)
      case Direction.Down => point.copy(y = point.y - 1)
      case Direction.Left => point.copy(x = point.x - 1)
    }
  }

  implicit class TikEngineSyntax[A <: GameObject](gameObject: A){
    def consumeTik(tik: Tik)(implicit tikConsumerProcessor: TikEngine[A]): Option[GameObject] = {
      tikConsumerProcessor.consumeTik(gameObject, tik)
    }
  }
}
