
package org.aimas.craftingquest.core;

public interface IClient {

    public Long getSecret(int n);

    public void onEvent(Event e);
}
