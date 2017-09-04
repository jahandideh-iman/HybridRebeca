/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HPalang.Core;

import HPalang.Core.Statements.SendStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 *
 * @author Iman Jahandideh
 */
public class ModelDefinition
{
    private List<SoftwareActor> actors = new ArrayList<>();
    private MainBlock mainBlock;
    
    public void AddActor(SoftwareActor actor)
    {
        actors.add(actor);
    }

    public void SetMainBlock(MainBlock mainBlock)
    {
        this.mainBlock = mainBlock;
    }

    public List<SoftwareActor> GetActors()
    {
        return actors;
    }

    public Queue<SendStatement> GetInitialSends()
    {
        return mainBlock.GetSendStatements();
    }

    public SoftwareActor FindActor(String name)
    {
        for(SoftwareActor actor : actors)
            if(actor.Name().equals(name))
                return actor;
        return null;
    }

    public void AddType(ActorType actorType)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void AddActor(PhysicalActor actor)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
