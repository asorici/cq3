package org.aimas.craftingquest.user;

public interface IPlayerHooks {

    public void initGame();

    public void finishGame();

    public void beginRound();

	public void initPlayer();
}
