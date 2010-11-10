package org.aimas.craftingquest.quest;

import java.util.List;

/**
 *
 * @author Razvan
 */

// exista un goal mare
// este creeat din subgoaluri
// creaturile fac subgoaluri pe care numai ele le pot face
public class GoalNode {

    public static enum NodeType {

	ANDNode, Unknown, ORNode
    };
    /* data */
    Goal goal;

    /* connections */
    NodeType type;
    List<GoalNode> adj;

    public boolean isResolved() {
	// verificari daca subgraful acesta de depdente AND-OR sunt satisfacute
	return false;
    }
}
