package org.torquebox.injection;

import org.jruby.ast.Node;

public interface InjectableHandler {
    
    String getType();
    Injectable handle(Node node);

}
