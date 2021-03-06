/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HPalang.LTSGeneration.SOSRules;

import HPalang.Core.Actor;
import HPalang.Core.DiscreteExpressions.FalseConst;
import HPalang.Core.DiscreteExpressions.TrueConst;
import HPalang.Core.Expression;
import HPalang.Core.Statements.IfStatement;
import HPalang.Core.ValuationContainer;
import HPalang.LTSGeneration.Labels.Guard;
import HPalang.LTSGeneration.Labels.SoftwareLabel;
import HPalang.LTSGeneration.RunTimeStates.ActorState;
import HPalang.LTSGeneration.RunTimeStates.GlobalRunTimeState;
import HPalang.LTSGeneration.TransitionCollector;
import static HPalang.LTSGeneration.SOSRules.Utilities.*;

/**
 *
 * @author Iman Jahandideh
 */
public class IfStatementRule extends ActorLevelRule
{

    @Override
    protected boolean IsRuleSatisfied(ActorState actorState, GlobalRunTimeState globalState)
    {
        return actorState.IsSuspended() == false &&
                actorState.ExecutionQueueState().Statements().IsEmpty() == false
                && actorState.ExecutionQueueState().Statements().Head().Is(IfStatement.class);
    }

    @Override
    protected void ApplyToActorState(ActorState actorState, GlobalRunTimeState globalState, TransitionCollector collector)
    {
        GlobalRunTimeState newGlobalState = globalState.DeepCopy();
        ActorState newActorState = newGlobalState.FindActorState(actorState.Actor());
        Actor actor = actorState.Actor();
        
        IfStatement ifStatement = (IfStatement) newActorState.ExecutionQueueState().Statements().Dequeue();
        if(ifStatement.Expression().IsComputable(actorState.ValuationState().Valuation()))
        {
            ValuationContainer valuations = actorState.ValuationState().Valuation();
            
            if(ifStatement.Expression().Evaluate(valuations) > 0)
                newActorState.ExecutionQueueState().Statements().Push(ifStatement.TrueStatements());
            else
                newActorState.ExecutionQueueState().Statements().Push(ifStatement.FalseStatements());
            
            collector.AddTransition(new SoftwareLabel(), newGlobalState);
        }
        else
        {
            Expression partialCondition = PartivalValuation(ifStatement.Expression(), actor, globalState);
            
            partialCondition = UnWrapExpressionScope(partialCondition, actor, actorState.ValuationState().Valuation());
            
            GlobalRunTimeState truePathGlobalState = newGlobalState.DeepCopy();
            EnqueueStatements(ifStatement.TrueStatements(), actor, truePathGlobalState);
            
            SoftwareLabel truePathLabel = new SoftwareLabel(new Guard(EqualityExpression(partialCondition, new TrueConst())));
            collector.AddTransition(truePathLabel, truePathGlobalState);
            
            GlobalRunTimeState falsePathGlobalState = newGlobalState.DeepCopy();
            EnqueueStatements(ifStatement.FalseStatements(), actor, falsePathGlobalState);
            
            SoftwareLabel falsePathLabel = new SoftwareLabel(new Guard(EqualityExpression(partialCondition, new FalseConst())));
            collector.AddTransition( falsePathLabel, falsePathGlobalState);
        }
        
    }
    
}
