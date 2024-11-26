package br.edu.ifpe.web3.wordle.model;

public record MoveResult(Winner winner, String code, int[] hint) {}

