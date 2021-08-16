package snake_game

import snake_game.model.{GamePoint, Teleport}

class BoardBorders(gameConfig: GameConfig){
  private val availableWidth = gameConfig.width - 1
  private val availableHeight = gameConfig.height - 1

  private val LowerHorizontalTeleports: Set[Teleport] =
    (1 until availableWidth).map(index => Teleport(GamePoint(index, 0), GamePoint(index, availableHeight - 1))).toSet

  private val UpperHorizontalTeleports: Set[Teleport] =
    (1 until availableWidth).map(index => Teleport(GamePoint(index, availableHeight), GamePoint(index, 1))).toSet

  private val LeftVerticalWallTeleports: Set[Teleport] =
    (1 until availableHeight).map(index => Teleport(GamePoint(0, index), GamePoint(availableWidth - 1, index))).toSet

  private val RightVerticalWallTeleports: Set[Teleport] =
    (1 until availableHeight).map(index => Teleport(GamePoint(availableWidth, index), GamePoint(1, index))).toSet

  val walls: Set[Teleport] = LowerHorizontalTeleports ++ UpperHorizontalTeleports ++ LeftVerticalWallTeleports ++ RightVerticalWallTeleports
}
