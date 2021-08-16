package snake_game.controller

import snake_game.engine.Collisions.{CollisionsEngine, CollisionsEngineSyntax}
import snake_game.engine.Tiks.{TikEngine, TikEngineSyntax}
import snake_game.model._
import snake_game.GameConfig
import utils.{IndexedSeqOps, MultiMapBuilder}

import scala.annotation.tailrec
import scala.util.Random

class SnakeGameController(gameConfig: GameConfig)
                         (implicit collisionsEngine: CollisionsEngine[GameObject], tikEngine: TikEngine[GameObject]) {
  def processTik(gameData: SnakeGameData, tikNumber: Int): (SnakeGameData, GameStatus) = {
    val walls = gameData.walls.flatMap(_.consumeTik(tikNumber))
    val teleports = gameData.teleports.flatMap(_.consumeTik(tikNumber))
    val apples = gameData.apple.flatMap(_.consumeTik(tikNumber))
    val snakes = gameData.snake.flatMap(_.consumeTik(tikNumber))

    val allGameObjects = walls ++ teleports ++ apples ++ snakes
    updateGame(SnakeGameData(allGameObjects))
  }

  def processControlInput(gameData: SnakeGameData, control: Direction): (SnakeGameData, GameStatus) = {
    val snakeWithUpdatedDirection = gameData.snake.map(_.changeDirection(control))

    updateGame(gameData.copy(snake = snakeWithUpdatedDirection))
  }

  private def updateGame(gameData: SnakeGameData): (SnakeGameData, GameStatus) = {
    val snakeGameDateWithoutCollisions = processAllCollisions(gameData)
    val consistentSnakeGameData: SnakeGameData = createRequiredObjects(snakeGameDateWithoutCollisions)
    val gameStatus: GameStatus = getGameStatus(consistentSnakeGameData)

    (consistentSnakeGameData, gameStatus)
  }

  private def createRequiredObjects(data: SnakeGameData): SnakeGameData = {
    val apple = data.apple.orElse(createAppleOnFreePoint(data))
    data.copy(apple = apple)
  }

  private def createAppleOnFreePoint(data: SnakeGameData): Option[Apple] = {
    val freePoints = allNotOccupiedPoints(data)
    if (freePoints.nonEmpty) {
      val appleNextPosition = freePoints.toIndexedSeq(Random.nextInt(freePoints.size))
      Option(Apple(appleNextPosition))
    }
    else {
      None
    }
  }

  private lazy val allPossibleGamePoints = {
    val allPossiblePoints =
      for {
        x <- 0 until gameConfig.width
        y <- 0 until gameConfig.height
      } yield GamePoint(x, y)

    allPossiblePoints.toSet
  }

  private def allNotOccupiedPoints(data: SnakeGameData) = {
    allPossibleGamePoints -- data.allObjects.flatMap(_.getOccupiedPoints)
  }

  private def getGameStatus(data: SnakeGameData): GameStatus = {
    if (data.snake.isDefined && data.apple.isDefined) {
      GameIsActive
    }
    else {
      GameIsFinished
    }
  }

  @tailrec
  private def processAllCollisions(data: SnakeGameData): SnakeGameData = {
    val collisionOpt = calculateFirstCollision(data)

    collisionOpt match {
      case Some(collision) =>
        val dataWithProcessedCollision = processCollision(data, collision)
        processAllCollisions(dataWithProcessedCollision)
      case None => data
    }
  }

  private def calculateFirstCollision(data: SnakeGameData): Option[Collision] = {
    val gameObjects: IndexedSeq[GameObject] = data.allObjects.toIndexedSeq

    val pointToObject = for {
      gameObject <- gameObjects
      activePoints <- gameObject.getOccupiedPoints
    } yield (activePoints, gameObject)

    val pointToObjects: Map[GamePoint, Seq[GameObject]] = pointToObject.asMultiMap()

    val collision =
      pointToObjects.collectFirst { case (_, objects) if objects.size > 1 => Collision(objects) }

    collision
  }

  private def processCollision(data: SnakeGameData, collision: Collision): SnakeGameData = {
    val objectsWithoutCollisions = data.allObjects -- collision.objects

    val objectsAfterCollisions: Seq[GameObject] =
      collision.objects
        .mapToOneToOthersCombinations()
        .flatMap { case (gameObject, otherGameObjects) => gameObject.processCollision(otherGameObjects.toSet) }

    SnakeGameData(objectsWithoutCollisions ++ objectsAfterCollisions)
  }
}
