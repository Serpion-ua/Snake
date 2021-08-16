# Snake not-good-in-graphic game

!!!THIS IS NOT READY TO PRODUCTION CODE!!!

Snake game core which allow you to setup game by placing objects like snake itself, apple to be eaten, walls and and even teleports!

SnakeGameApp contains example of such game, with cycled game field, for example 
if snake reach upper border then in appeared on bottom part of the board. 
Snake could be controlled by W(up) A(left) S(down) D(right) input character (you need to press enter after each control character)


# Implementation
Implemented by using MVC pattern.

Controller package contains game controller who is responsible for updating and processing game

Engine package contain information how each game object could process collisions with any other 
objects as well as processing Tik (minimum game time unit)

Model package contains information about all possible game objects

View package contains logic for drawing snake game
