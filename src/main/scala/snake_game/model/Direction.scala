package snake_game.model

sealed trait Direction
object Direction {
  final case object Up extends Direction
  final case object Right extends Direction
  final case object Down extends Direction
  final case object Left extends Direction
}
