package snake_game.model

sealed trait GameStatus
case object GameIsActive extends GameStatus
case object GameIsFinished extends GameStatus
