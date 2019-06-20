import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public class Rule implements Serializable
{
    protected final String leftSymbol;
    protected final String[] rightSymbolArray;

    public Rule(String leftSymbol, String[] rightSymbolArray)
    {
        this.rightSymbolArray = rightSymbolArray;
        this.leftSymbol = leftSymbol;
    }

    public Rule(Rule rule)
    {
        this.leftSymbol = rule.GetLeftSymbol();
        this.rightSymbolArray = rule.rightSymbolArray.clone();
    }

    public String GetLeftSymbol()
    {
        return leftSymbol;
    }

    public String[] GetRightSymbolArray()
    {
        return rightSymbolArray;
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
        Rule rule = (Rule) o;
        return leftSymbol.equals(rule.leftSymbol) && Arrays.equals(rightSymbolArray, rule.rightSymbolArray);
    }

    @Override
    public int hashCode()
    {
        int result = Objects.hash(leftSymbol);
        result = 31 * result + Arrays.hashCode(rightSymbolArray);
        return result;
    }

    @Override
    public String toString()
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(leftSymbol);
        stringBuilder.append(" ==> ");
        for (String symbol : rightSymbolArray)
        {
            stringBuilder.append(symbol);
            stringBuilder.append(" ");
        }
        return stringBuilder.toString();
    }
}
