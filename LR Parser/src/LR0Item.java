import java.util.Objects;

public class LR0Item extends Rule
{
    private int dotSymbolPosition;

    public LR0Item(LR0Item lr0Item)
    {
        super(lr0Item);
        dotSymbolPosition = lr0Item.GetDotSymbolPosition();
    }

    public LR0Item(Rule rule)
    {
        super(rule.GetLeftSymbol(), rule.GetRightSymbolArray());
        this.dotSymbolPosition = 0;
    }

    public LR0Item(String leftSymbol, String[] rightSymbolArray, int dotSymbolPosition)
    {
        super(leftSymbol, rightSymbolArray);
        this.dotSymbolPosition = dotSymbolPosition;
    }

    public void ShiftDotPosition()
    {
        dotSymbolPosition++;
    }

    public String GetMarkSymbol()
    {
        return dotSymbolPosition == rightSymbolArray.length ? null : rightSymbolArray[dotSymbolPosition];
    }

    public int GetDotSymbolPosition()
    {
        return dotSymbolPosition;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        if (!super.equals(o))
        {
            return false;
        }
        LR0Item lr0Item = (LR0Item) o;
        return dotSymbolPosition == lr0Item.dotSymbolPosition;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), dotSymbolPosition);
    }

    @Override
    public String toString()
    {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(leftSymbol);
        stringBuilder.append(" ==> ");

        for (int index = 0; index < rightSymbolArray.length; index++)
        {
            if (index == dotSymbolPosition)
            {
                stringBuilder.append(".");
            }

            stringBuilder.append(rightSymbolArray[index]);
            if (index != rightSymbolArray.length - 1)
            {
                stringBuilder.append(" ");
            }
        }

        if (rightSymbolArray.length == dotSymbolPosition)
        {
            stringBuilder.append(".");
        }

        return stringBuilder.toString();
    }
}