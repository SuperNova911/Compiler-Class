import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;

public class LR1Item extends Rule
{
    private final HashSet<String> lookAheadSet;
    private int dotSymbolPosition;

    public LR1Item(LR1Item lr1Item)
    {
        super(lr1Item);

        this.dotSymbolPosition = lr1Item.dotSymbolPosition;

        lookAheadSet = new HashSet<>();
        this.lookAheadSet.addAll(lr1Item.lookAheadSet);
    }

    public LR1Item(String leftSymbol, String[] rightSymbolArray, int dotSymbolPosition, HashSet<String> lookAheadSet)
    {
        super(leftSymbol, rightSymbolArray);
        this.dotSymbolPosition = dotSymbolPosition;
        this.lookAheadSet = lookAheadSet;
    }

    public String GetMarkSymbol()
    {
        if (dotSymbolPosition == rightSymbolArray.length)
        {
            return null;
        }
        return rightSymbolArray[dotSymbolPosition];
    }

    public void ShiftDotPosition()
    {
        dotSymbolPosition++;
    }

    public int GetDotSymbolPosition()
    {
        return dotSymbolPosition;
    }

    public HashSet<String> GetLookAheadSet()
    {
        return lookAheadSet;
    }

    public boolean equalLR0(LR1Item item)
    {
        return leftSymbol.equals(item.GetLeftSymbol()) && Arrays.equals(rightSymbolArray, item.GetRightSymbolArray()) && dotSymbolPosition == item.GetDotSymbolPosition();
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
        LR1Item lr1Item = (LR1Item) o;
        return dotSymbolPosition == lr1Item.dotSymbolPosition && lookAheadSet.equals(lr1Item.lookAheadSet);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), lookAheadSet, dotSymbolPosition);
    }

    @Override
    public String toString()
    {
        StringBuilder stringBuilder = new StringBuilder(leftSymbol + " -> ");

        for (int i = 0; i < rightSymbolArray.length; i++)
        {
            if (i == dotSymbolPosition)
            {
                stringBuilder.append(".");
            }
            stringBuilder.append(rightSymbolArray[i]);
            if (i != rightSymbolArray.length - 1)
            {
                stringBuilder.append(" ");
            }
        }

        if (rightSymbolArray.length == dotSymbolPosition)
        {
            stringBuilder.append(".");
        }

        stringBuilder.append(" , ");
        stringBuilder.append(lookAheadSet);
        return stringBuilder.toString();
    }
}
