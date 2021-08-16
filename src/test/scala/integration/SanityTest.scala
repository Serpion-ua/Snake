package integration

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import snake_game.GameConfig
import snake_game.controller.SnakeGameController
import snake_game.engine.{Collisions, Tiks}
import snake_game.model.{Apple, Direction, GameIsActive, GameIsFinished, GamePoint, Snake, SnakeGameData, Teleport}


class SanityTest extends AnyFlatSpec with Matchers {
  val simpleGameController =
    new SnakeGameController(GameConfig(10, 10))(Collisions.GameObjectCollisionProcessor, Tiks.GameObjectTikProcessor)

  behavior of "Snake"
  it should "eats an apple and grow, and new apple shall be created, game is still active" in {
    val appleToEatOpt = Option(Apple(GamePoint(5, 5)))
    val snakeData =
      SnakeGameData(Set.empty, Set.empty, appleToEatOpt, Option(Snake(IndexedSeq(GamePoint(5, 4)), Direction.Up, None)))

    val (snakeGameData, snakeGameStatus) = simpleGameController.processTik(snakeData, 0)

    snakeGameData.apple should not be appleToEatOpt
    snakeGameData.snake shouldBe Option(Snake(IndexedSeq(GamePoint(5, 5), GamePoint(5, 4)), Direction.Up, None))
    snakeGameData.apple.isDefined shouldBe true
    snakeGameStatus shouldBe GameIsActive
  }

  "Snake" should "not collide in case if moving to previous occupied cell" in {
    val appleToEatOpt = Option(Apple(GamePoint(0, 0)))

    val snake =
      Snake(IndexedSeq(GamePoint(2, 2), GamePoint(1, 2), GamePoint(1, 1), GamePoint(2, 1)), Direction.Down, None)

    val snakeData = SnakeGameData(Set.empty, Set.empty, appleToEatOpt, Option(snake))

    val (snakeGameData, snakeGameStatus) = simpleGameController.processTik(snakeData, 0)

    val expectedSnake =
      Snake(IndexedSeq(GamePoint(2, 1), GamePoint(2, 2), GamePoint(1, 2), GamePoint(1, 1)), Direction.Down, Option(GamePoint(2, 1)))

    snakeGameData.snake shouldBe Option(expectedSnake)
    snakeGameStatus shouldBe GameIsActive
  }

  "Snake" should "disappeared and game is finished in case of self colliding" in {
    val appleToEatOpt = Option(Apple(GamePoint(0, 0)))

    val snake =
      Snake(IndexedSeq(GamePoint(2, 2), GamePoint(1, 2), GamePoint(1, 1), GamePoint(2, 1), GamePoint(2, 0)), Direction.Down, None)

    val snakeData = SnakeGameData(Set.empty, Set.empty, appleToEatOpt, Option(snake))

    val (snakeGameData, snakeGameStatus) = simpleGameController.processTik(snakeData, 0)

    snakeGameData.snake.isDefined shouldBe false
    snakeGameStatus shouldBe GameIsFinished
  }

  "Snake" should "be teleported if enter to the teleport, teleport shall be stable" in {
    val appleToEatOpt = Option(Apple(GamePoint(0, 0)))

    val snake = Snake(IndexedSeq(GamePoint(2, 2), GamePoint(1, 2)), Direction.Down, None)
    val teleport = Teleport(GamePoint(2, 1), GamePoint(5, 5))

    val snakeData = SnakeGameData(Set.empty, Set(teleport), appleToEatOpt, Option(snake))

    val (snakeGameData, snakeGameStatus) = simpleGameController.processTik(snakeData, 0)

    val expectedSnake = Snake(IndexedSeq(GamePoint(5, 5), GamePoint(2, 2)), Direction.Down, Option(GamePoint(1, 2)))
    snakeGameData.snake shouldBe Option(expectedSnake)
    snakeGameStatus shouldBe GameIsActive
    snakeGameData.teleports shouldBe Set(teleport)
  }

  "Snake" should "change direction if control had been set" in {
    val appleToEatOpt = Option(Apple(GamePoint(0, 0)))

    val snake = Snake(IndexedSeq(GamePoint(2, 2), GamePoint(1, 2)), Direction.Down, None)

    val snakeData = SnakeGameData(Set.empty, Set.empty, appleToEatOpt, Option(snake))

    val (snakeGameData, snakeGameStatus) = simpleGameController.processControlInput(snakeData, Direction.Right)

    val expectedSnake = snake.copy(direction = Direction.Right)
    snakeGameData.snake shouldBe Option(expectedSnake)
    snakeGameStatus shouldBe GameIsActive
  }

}
