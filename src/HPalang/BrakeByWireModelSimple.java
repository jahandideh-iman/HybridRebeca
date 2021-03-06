/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HPalang;

import HPalang.Core.CommunicationType;
import HPalang.Core.SoftwareActor;
import HPalang.Core.DelegationParameter;
import HPalang.Core.ContinuousExpressions.DifferentialEquation;
import HPalang.Core.ContinuousExpressions.Invarient;
import HPalang.Core.MessageHandler;
import HPalang.Core.Mode;
import HPalang.Core.MainBlock;
import HPalang.Core.Message;
import HPalang.Core.PhysicalActor;
import HPalang.Core.PhysicalActorType;
import HPalang.Core.ModelDefinition;
import HPalang.Core.SoftwareActorType;
import HPalang.Core.Variable;
import HPalang.Core.VariableParameter;
import HPalang.Core.Variables.FloatVariable;
import HPalang.Core.Variables.RealVariable;
import static HPalang.Core.ModelCreationUtilities.*;

/**
 *
 * @author Iman Jahandideh
 */
public class BrakeByWireModelSimple
{
    public static final float arbitrartDelay = 13.0f;
    public static final String Wheel__controller_instance = "controller";
    public static final String Wheel__rpm_delegation = "wheel_rpm_delegation";
    public static final String Wheel__torque_port = "torque_port";
    


    public static final String Global_Brake_Controller__wheel_rpm_FR_port = "wheel_rpm_FR_port";

   
    public static final String Global_Brake_Controller__control_handler = "control";


    
   
    static public ModelDefinition Create()
    {
        ModelDefinition definition = new ModelDefinition();
        
        PhysicalActorType wheelType = new PhysicalActorType("Wheel");
        SoftwareActorType globalBrakeControllerType = new SoftwareActorType("GlobalBrakeController");
        
        
        FillSkeletonForWheelType(wheelType, globalBrakeControllerType);
        FillSkeletonForGlobalBrakeControllerType(globalBrakeControllerType);
        
        FillFleshForWheelType(wheelType, globalBrakeControllerType);
        FillFleshForGlobalBrakeControllerType(globalBrakeControllerType);
        
        SoftwareActor global_brake_controller = new SoftwareActor("global_brake_controller", globalBrakeControllerType, 1);
        

        PhysicalActor wheel_FR = new PhysicalActor("wheel_FR", wheelType,1);       

        
        FillWheelActor(wheel_FR,global_brake_controller,Global_Brake_Controller__wheel_rpm_FR_port);

        FillGlobalBrakeController(global_brake_controller);
        
        definition.AddType(wheelType);
        definition.AddType(globalBrakeControllerType);
        
        definition.AddActor(global_brake_controller);
        

        definition.AddActor(wheel_FR);

        definition.SetMainBlock(new MainBlock());

        SetNetworkPriority(definition, global_brake_controller, Global_Brake_Controller__wheel_rpm_FR_port, 340);
        SetNetworkPriority(definition, global_brake_controller, Global_Brake_Controller__control_handler, 603);
        
        
        return definition;
    }

    private static void FillSkeletonForWheelType(PhysicalActorType wheelType, SoftwareActorType globalBrakeControllerType)
    {
        //wheelType.AddInstanceParameter(new InstanceParameter("instance", globalBrakeControllerType));
        wheelType.AddDelegationParameter(
                new DelegationParameter(
                        Wheel__rpm_delegation, 
                        DelegationParameter.TypesFrom(Variable.Type.floatingPoint)
                )
        ); 
                
        wheelType.AddVariable(new RealVariable("timer"));
        wheelType.AddVariable(new RealVariable("rpm"));

        wheelType.AddMode(new Mode("NoBrake"));
        
        wheelType.SetInitialMode(wheelType.FindMode("NoBrake"));
        
    }
    
    private static void FillFleshForWheelType(PhysicalActorType wheelType, SoftwareActorType globalBrakeControllerType)
    {
        
        //InstanceParameter instanceParam = wheelType.FindInstanceParameter("instance");
        DelegationParameter wheel_rpm_delegation = wheelType.FindDelegationParameter(Wheel__rpm_delegation);
        
        RealVariable timer = (RealVariable) wheelType.FindVariable("timer"); 
        RealVariable rpm = (RealVariable) wheelType.FindVariable("rpm");
        
        Mode noBrakeMode = wheelType.FindMode("NoBrake");

        
        noBrakeMode.SetInvarient(new Invarient(CreateBinaryExpression(timer, "<=", Const(0.01f))));
        noBrakeMode.SetGuard(CreateGuard(timer, "==", 0.01f));
        noBrakeMode.AddDifferentialEquation(new DifferentialEquation(timer, Const(1f)));
        noBrakeMode.AddDifferentialEquation(new DifferentialEquation(rpm, UnknowExpression("?!!!")));
        
        noBrakeMode.AddAction(CreateResetFor(timer));
        noBrakeMode.AddAction(CreateSendStatement(wheel_rpm_delegation, VariableExpression(rpm)));
        //noBrakeMode.AddAction(CreateSendStatement(instanceParam, globalBrakeControllerType.FindMessageHandler(Global_Brake_Controller__wheel_rpm_FR_port), rpm));

    }
    

    private static void FillSkeletonForGlobalBrakeControllerType(SoftwareActorType globalBrakeControllerType)
    {
        
        globalBrakeControllerType.AddVariable(new FloatVariable("wheel_rpm_FR"));
        globalBrakeControllerType.AddVariable(new FloatVariable("estimated_speed"));
        globalBrakeControllerType.AddVariable(new FloatVariable("global_torque"));
        
        
        //AddPort(globalBrakeControllerType, Global_Brake_Controller__wheel_rpm_FR_port, globalBrakeControllerType.FindVariable("wheel_rpm_FR"));

        MessageHandler handler = new MessageHandler(Message.MessageType.Data);
        handler.Parameters().Add(new VariableParameter(new FloatVariable("floatvar")));
        globalBrakeControllerType.AddMessageHandler(Global_Brake_Controller__wheel_rpm_FR_port, handler);
        //globalBrakeControllerType.AddMessageHandler(Global_Brake_Controller__control_handler, new MessageHandler(Message.MessageType.Control));
    }

    private static void FillFleshForGlobalBrakeControllerType(SoftwareActorType globalBrakeControllerType)
    {

        FloatVariable global_torque = (FloatVariable) globalBrakeControllerType.FindVariable("global_torque");
        FloatVariable brake_percent = (FloatVariable) globalBrakeControllerType.FindVariable("brake_percent");
        FloatVariable estimated_speed = (FloatVariable) globalBrakeControllerType.FindVariable("estimated_speed");
        
        //MessageHandler control = globalBrakeControllerType.FindMessageHandler(Global_Brake_Controller__control_handler);
        
        //control.AddStatement(new AssignmentStatement(estimated_speed, new VariableExpression(estimated_speed))); //?!!
        //control.AddStatement(new AssignmentStatement(global_torque, new VariableExpression(brake_percent))); //?!!
    }
    
    private static void FillWheelActor(
            PhysicalActor wheel,
            SoftwareActor global_brake_controller,
            String delegationHandlerName)
    {
        BindDelagation(wheel, Wheel__rpm_delegation, global_brake_controller, delegationHandlerName, CommunicationType.CAN);
    }


    private static void FillGlobalBrakeController(
            SoftwareActor global_brake_controller)
    {
        SoftwareActorType type = global_brake_controller.Type();
    }

}
