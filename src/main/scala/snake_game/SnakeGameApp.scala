package snake_game

import snake_game.controller.SnakeGameController
import snake_game.model._
import snake_game.view.Printer
import snake_game.view.PrintingImplicits.convertToPrintSyntax

import scala.util.Try


object SnakeGameApp extends App {
  val config = GameConfig(15, 10)
  val snake: Snake = Snake(IndexedSeq(GamePoint(5, 5), GamePoint(5, 4), GamePoint(5, 3), GamePoint(5, 2)), Direction.Right, None)
  val apple: Apple = Apple(GamePoint(6, 5))
  val board: BoardBorders = new BoardBorders(config)

  val printer = new Printer(config)
  val gameController = new SnakeGameController(config)

  println("Snake game! Snake could be controlled by W A S D symbols (you need to press Enter after each input)")

  var gameState: GameStatus = GameIsActive
  var gameData = SnakeGameData(Set.empty, board.walls, Option(apple), Option(snake))
  var tikCounter = 0

  printer.printData(gameData.toPrintData)
  do {
    var input: Option[Direction] = None
    do {
      input = Try(scala.io.StdIn.readChar()).toOption.flatMap(charToDirection)
    } while (input.isEmpty)

    val (dataAfterProcessInput, _) = gameController.processControlInput(gameData, input.get)

    tikCounter = tikCounter + 1
    val (gameDataAfterTik, stateAfterTick) = gameController.processTik(dataAfterProcessInput, tikCounter)

    gameData = gameDataAfterTik
    gameState = stateAfterTick

    printer.printData(gameData.toPrintData)
  } while(gameState == GameIsActive)
  println("Game over!")


  def charToDirection(char: Char): Option[Direction] = {
    char match {
      case 'w' | 'W' => Option(Direction.Up)
      case 'd' | 'D' => Option(Direction.Right)
      case 's' | 'S' => Option(Direction.Down)
      case 'a' | 'A' => Option(Direction.Left)
      case _ => None
    }
  }
}
