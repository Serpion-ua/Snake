package snake_game.view

import snake_game.SnakeGameApp.apple
import snake_game.model.{Apple, Direction, GameObject, Snake, SnakeGameData, Teleport, Wall}


object PrintingImplicits {
  trait ConvertToPrint[A] {
    def toPrintData(a: A): Set[ToPrint]
  }

  implicit val SnakeToPrint: ConvertToPrint[Snake] = (a: Snake) => {
    val head = a.direction match {
      case Direction.Up => ToPrint(a.body.head, '^')
      case Direction.Right => ToPrint(a.body.head, '>')
      case Direction.Down => ToPrint(a.body.head, 'V')
      case Direction.Left => ToPrint(a.body.head, '<')
    }
    val tail: Set[ToPrint] = a.body.tail.map(point => ToPrint(point, '*')).toSet
    tail + head
  }

  implicit val GameObjectToPrint: ConvertToPrint[GameObject] = {
    case apple: Apple => AppleToPrint.toPrintData(apple)
    case wall: Wall => WallToPrint.toPrintData(wall)
    case snake: Snake => SnakeToPrint.toPrintData(snake)
    case teleport: Teleport => TeleportToPrint.toPrintData(teleport)
  }

  implicit val AppleToPrint: ConvertToPrint[Apple] = (apple: Apple) => Set(ToPrint(apple.position, '@'))

  implicit val WallToPrint: ConvertToPrint[Wall] = (wall: Wall) => Set(ToPrint(wall.position, '#'))

  implicit val TeleportToPrint: ConvertToPrint[Teleport] = (teleport: Teleport) => Set(ToPrint(teleport.position, 'o'))

  implicit val SnakeGameDataToPrint: ConvertToPrint[SnakeGameData] = (snakeGameData: SnakeGameData) => {
    snakeGameData.allObjects.flatMap(_.toPrintData)
  }

  implicit class convertToPrintSyntax[A: ConvertToPrint](gameObject: A) {
    def toPrintData: Set[ToPrint] = implicitly[ConvertToPrint[A]].toPrintData(gameObject)
  }
}
