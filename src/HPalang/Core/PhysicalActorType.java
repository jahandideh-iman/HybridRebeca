/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HPalang.Core;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Iman Jahandideh
 */
public class PhysicalActorType extends ActorType
{
    private final Map<String,Mode> modes = new HashMap<>();
    private Mode initialMode = Mode.None();

    public PhysicalActorType(String name)
    {
        super(name, Arrays.asList(Variable.Type.real, Variable.Type.floatingPoint));
    }
    
    public void AddMode(Mode mode)
    {
        modes.put(mode.Name(),mode);
    }

    public void SetInitialMode(Mode mode)
    {
        assert (mode != null);
        this.initialMode = mode;
    }

    public Mode InitialMode()
    {
        return initialMode;
    }
    
    public Mode FindMode(String modeName)
    {
        return modes.get(modeName);
    }
}
