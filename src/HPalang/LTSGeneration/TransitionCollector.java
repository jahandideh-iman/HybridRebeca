/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HPalang.LTSGeneration;

import HPalang.Core.TransitionSystem.Label;
import HPalang.LTSGeneration.RunTimeStates.GlobalRunTimeState;

/**
 *
 * @author Iman Jahandideh
 */
public interface TransitionCollector
{
    public void AddTransition(Label label,GlobalRunTimeState destination);
}
