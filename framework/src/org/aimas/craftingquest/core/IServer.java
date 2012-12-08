
package org.aimas.craftingquest.core;

import org.aimas.craftingquest.state.PlayerState;
import org.aimas.craftingquest.state.Transition;


public interface IServer {

    public int addRemoteClient(Object client);

    //public Scenario getScenario();

    public PlayerState process(Transition action);

}
