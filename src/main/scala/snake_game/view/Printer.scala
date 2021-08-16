package snake_game.view

import snake_game.GameConfig
import snake_game.model.GamePoint

case class ToPrint(point: GamePoint, symbol: Char)
object ToPrint {
  def apply(startPoint: GamePoint, string: String): Set[ToPrint] = {
    string.zipWithIndex.map{case(symbol, index) => ToPrint(startPoint.copy(x = startPoint.x + index), symbol)}.toSet
  }
}

class Printer(config: GameConfig) {
  private val xAdjustment = 2
  val width: Int = config.width
  val height: Int = config.height

  def printData(data: Set[ToPrint]): Unit = {
    val dataToPrint = Array.fill(height, width * xAdjustment)(' ')
    data
      .filter{case ToPrint(GamePoint(x, y), _) => x < width && y < height}
      .foreach{case ToPrint(GamePoint(x, y), symbol) => dataToPrint(height - y - 1)(x * xAdjustment) = symbol}

    dataToPrint.foreach(arr => println(arr.mkString))
  }
}
