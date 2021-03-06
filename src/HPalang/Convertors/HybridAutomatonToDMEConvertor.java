/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HPalang.Convertors;

import HPalang.Core.ContinuousExpressions.DifferentialEquation;
import HPalang.Core.ContinuousExpressions.Invarient;
import HPalang.Core.TransitionSystem.Transition;
import HPalang.HybridAutomataGeneration.HybridAutomaton;
import HPalang.HybridAutomataGeneration.Location;

/**
 *
 * @author Iman Jahandideh
 */
public class HybridAutomatonToDMEConvertor
{
    public String Convert(HybridAutomaton hybridAutomaton)
    {
        String output = "";
        output += "<DMEComponent type=\"FiniteStateMachine\" >";
        output += "<States>\n";
        for(Location location : hybridAutomaton.GetLocations())
        {
            String stateStr = LocationToString(location);
            
            output +=("       <State  >\n");
            output += "           <Name>" + stateStr+"</Name>\n";
            output +=("       </State>\n");
        }
        output += "</States>\n";
        
        output += "<Transitions>\n";
        
        for(Transition<Location> transition : hybridAutomaton.Transitions())
        {
            String transitionStr = "\t<Transition>\n";
            
            String orignStr = LocationToString(transition.GetOrign().InnerState());
            String destinationStr = LocationToString(transition.GetDestination().InnerState());
            String labelStr = transition.GetLabel().toString();
            
            transitionStr+=("\t\t<From>" + orignStr+"</From>\n");
            transitionStr+=("\t\t<To>" + destinationStr+"</To>\n");
            transitionStr+=("\t\t<Condition>" + xmlEscapeText(labelStr)+"</Condition>\n");

            transitionStr += "\t</Transition>\n";
           
            output+= transitionStr;
        }
        
        output += "</Transitions>\n";
        output +="<InitialState>" + LocationToString(hybridAutomaton.GetInitialState())+"</InitialState>";
        output += "</DMEComponent>";
        return output;
    }

    private String LocationToString(Location location)
    {
        String locationStr = "";
        int lastIndex;
        
        
        String invarientsStr = "";
        for(Invarient invarient : location.GetInvarients())
            invarientsStr += invarient + "&&";
        

        lastIndex = invarientsStr.lastIndexOf("&&");
        if(lastIndex >= 0)
            invarientsStr =  invarientsStr.substring(0,lastIndex);
        
        String equationsStr = "";
        for(DifferentialEquation equation : location.GetEquations())
            equationsStr += equation.GetVariable().Name() + "' = " + equation.GetEquation() + "&&";
        
        lastIndex = equationsStr.lastIndexOf("&&");
        if(lastIndex >= 0)
        equationsStr =  equationsStr.substring(0,lastIndex);
        
        locationStr = invarientsStr + "\n" + equationsStr;
        
        return xmlEscapeText(locationStr);
    }
    
   String xmlEscapeText(String t) 
   {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < t.length(); i++)
        {
           char c = t.charAt(i);
           switch(c)
           {
            case '<': sb.append("&lt;"); break;
            case '>': sb.append("&gt;"); break;
            case '\"': sb.append("&quot;"); break;
            case '&': sb.append("&amp;"); break;
            case '\'': sb.append("&apos;"); break;
            default:
               if(c>0x7e) {
                  sb.append("&#"+((int)c)+";");
               }else
                  sb.append(c);
           }
       }
        return sb.toString();
    }
}
