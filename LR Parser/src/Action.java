import java.io.Serializable;

public class Action implements Serializable
{
    private final ActionType actionType;
    private final int targetStateNumber;

    public enum ActionType
    {
        Shift, Reduce, Accept
    }

    public Action(ActionType actionType, int targetStateNumber)
    {
        this.actionType = actionType;
        this.targetStateNumber = targetStateNumber;
    }

    public ActionType GetActionType()
    {
        return actionType;
    }

    public int GetTargetStateNumber()
    {
        return targetStateNumber;
    }

    @Override
    public String toString()
    {
        return actionType + " " + (actionType == ActionType.Accept ? "" : targetStateNumber);
    }
}
