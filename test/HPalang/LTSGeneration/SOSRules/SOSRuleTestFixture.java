/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HPalang.LTSGeneration.SOSRules;

import Builders.GlobalRunTimeStateBuilder;
import HPalang.LTSGeneration.LTSGenerator;
import HPalang.LTSGeneration.LabeledTransitionSystem;
import HPalang.LTSGeneration.SOSRule;
import org.junit.Before;

/**
 *
 * @author Iman Jahandideh
 */
public class SOSRuleTestFixture
{
    protected LTSGenerator ltsGenerator = new LTSGenerator();
    protected LabeledTransitionSystem generatedLTS;
    protected GlobalRunTimeStateBuilder globalState = new GlobalRunTimeStateBuilder();
   
}
