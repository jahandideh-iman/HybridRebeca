/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HPalang.LTSGeneration;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 *
 * @author Iman Jahandideh
 */
public class CompositeStateTest
{
    class ConcreteStateT extends CompositeStateT<ConcreteStateT>
    {
        public int value;
        
        public ConcreteStateT(int value)
        {
            this.value = value;
        }
        
        public ConcreteStateT() 
        {
            this.value = 0;
        }

        @Override
        protected boolean DataEquals(ConcreteStateT other)
        {
            return this.value == other.value;
        }

        @Override
        protected ConcreteStateT NewInstance()
        {
            return new ConcreteStateT();
        }

        @Override
        protected void CloneData(ConcreteStateT copy)
        {
           copy.value = this.value;
        }
    }
    
    class ConcreteSubstateA extends ConcreteStateT
    {
        public ConcreteSubstateA(int value)
        {
            super(value);
        }

        public ConcreteSubstateA()
        {
        }
        
        @Override
        protected ConcreteStateT NewInstance()
        {
            return new ConcreteSubstateA();
        }
    }
    
    class ConcreteSubstateB extends ConcreteStateT
    {
        public ConcreteSubstateB(int value)
        {
            super(value);
        }

        public ConcreteSubstateB()
        {
        }
        
        @Override
        protected ConcreteStateT NewInstance()
        {
            return new ConcreteSubstateB();
        }
    }
    
    @Test
    public void HasTheAddedSubstate()
    {
        CompositeStateT state = new ConcreteStateT();
        CompositeStateT substateA = new ConcreteSubstateA();
        CompositeStateT substateB = new ConcreteSubstateB();

        state.AddSubstate(substateA);
        state.AddSubstate(substateB);

        assertThat(state.FindSubState(ConcreteSubstateA.class),is(sameInstance(substateA)));
        assertThat(state.FindSubState(ConcreteSubstateB.class),is(sameInstance(substateB)));
    }
    
    @Test
    public void CanHaveMultipleSubstatesOfTheSameKine()
    {
        CompositeState state = new ConcreteStateT();
        CompositeState substateOne = new ConcreteSubstateA();
        CompositeState substateTwo = new ConcreteSubstateA();

        state.AddSubstate(substateOne);
        state.AddSubstate(substateTwo);
        
        assertThat(state.FindSubStates(ConcreteSubstateA.class),hasItem(sameInstance(substateOne)));
        assertThat(state.FindSubStates(ConcreteSubstateA.class),hasItem(sameInstance(substateTwo)));
    }
    
    @Test
    public void CanCloneTheSubstates()
    {
        CompositeState state = new ConcreteStateT(69);
        CompositeState substateA = new ConcreteSubstateA(42);
        CompositeState substateB = new ConcreteSubstateB(13);
        
        state.AddSubstate(substateA);
        state.AddSubstate(substateB);
        
        CompositeState copy = (CompositeState) state.DeepCopy();
        
        assertThat(copy, is(not(sameInstance(state))));
        assertThat(copy, is(equalTo(state)));
        
        assertThat(copy.FindSubState(ConcreteSubstateA.class),is(not(sameInstance(substateA))));
        assertThat(copy.FindSubState(ConcreteSubstateA.class),is(equalTo(substateA)));

        assertThat(copy.FindSubState(ConcreteSubstateB.class),is(not(sameInstance(substateB))));
        assertThat(copy.FindSubState(ConcreteSubstateB.class),is(equalTo(substateB)));
    }
    
}
