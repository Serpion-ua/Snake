package snake_game.engine

import snake_game.model.{Apple, GameObject, Snake, Teleport, Wall}


object Collisions {
  trait CollisionsEngine[A <: GameObject] {
    def processCollision(thisObject: A, collideToObjects: Set[GameObject]): Option[GameObject]
  }

  implicit val GameObjectCollisionProcessor: CollisionsEngine[GameObject] = (thisObject: GameObject, collideToObjects: Set[GameObject]) => {
    thisObject match {
      case apple: Apple => AppleCollisionProcessor.processCollision(apple, collideToObjects)
      case wall: Wall => WallCollisionProcessor.processCollision(wall, collideToObjects)
      case snake: Snake => SnakeCollisionProcessor.processCollision(snake, collideToObjects)
      case teleport: Teleport => TeleportCollisionProcessor.processCollision(teleport, collideToObjects)
    }
  }

  implicit val AppleCollisionProcessor: CollisionsEngine[Apple] = (_: Apple, _: Set[GameObject]) => None

  implicit val WallCollisionProcessor: CollisionsEngine[Wall] = (wall: Wall, _: Set[GameObject]) => Option(wall)

  implicit val TeleportCollisionProcessor: CollisionsEngine[Teleport] = (teleport: Teleport, _: Set[GameObject]) => Option(teleport)

  implicit val SnakeCollisionProcessor: CollisionsEngine[Snake] = (snake: Snake, collideToObjects: Set[GameObject]) => {
    require(collideToObjects.size == 1, "Snake could collide only with one object at once")

    collideToObjects.head match {
      case _: Apple => {
        val newBody = snake.lastVisitedCell.map(lastCell => snake.body :+ lastCell).getOrElse(snake.body)
        Option(snake.copy(body = newBody, lastVisitedCell = None))
      }
      case _: Wall => None
      case _: Snake => None
      case teleport: Teleport => {
        val newBody = teleport.out +: snake.body.tail
        Option(snake.copy(body = newBody))
      }
    }
  }


  implicit class CollisionsEngineSyntax[A <: GameObject](gameObject: A)(implicit collisionProcessor: CollisionsEngine[A]) {
    def processCollision(collideToObjects: Set[GameObject]): Option[GameObject] = collisionProcessor.processCollision(gameObject, collideToObjects)
  }

}
