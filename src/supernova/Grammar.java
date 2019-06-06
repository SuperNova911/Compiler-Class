package supernova;

import java.util.ArrayList;

public class Grammar
{
    public static final String DELIM_STRING = " ";
    public static final String EPSILON_STRING = "__";
    public static final String DERIVATION_STRING = "==>";
    public static final String OR_STRING = "||";

    private Symbol leftSymbol;
    private ArrayList<SymbolSet> rightSymbolSetList;

    public Grammar(Symbol leftSymbol)
    {
        this(leftSymbol, new ArrayList<>());
    }

    public Grammar(Symbol leftSymbol, ArrayList<SymbolSet> rightSymbolList)
    {
        this.leftSymbol = leftSymbol;
        this.rightSymbolSetList = rightSymbolList;
    }

    public ArrayList<Symbol> FindFrontSymbolList()
    {
        ArrayList<Symbol> frontSymbolList = new ArrayList<>();

        for (SymbolSet symbolSet : rightSymbolSetList)
        {
            frontSymbolList.add(symbolSet.FindFrontSymbol());
        }

        return frontSymbolList;
    }

    public void AddRightSymbolSet(SymbolSet symbolSet)
    {
        rightSymbolSetList.add(symbolSet);
    }

    public void RemoveMatchSymbolSet(SymbolSet symbolSet)
    {
        rightSymbolSetList.remove(symbolSet);
    }

    public void ClearRightSymbolSet()
    {
        rightSymbolSetList.clear();
    }

    @Override
    public String toString()
    {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(String.format("%s%s ", leftSymbol.toString(), Grammar.DERIVATION_STRING));

        if (rightSymbolSetList.isEmpty())
        {
            System.out.println("완성되지 않은 문법입니다.");
        }
        else
        {
            for (SymbolSet symbolSet : rightSymbolSetList)
            {
                stringBuilder.append(symbolSet.toString());
                stringBuilder.append("| ");
            }
            stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length() - 1);
        }

        return stringBuilder.toString();
    }

    public Symbol GetLeftSymbol()
    {
        return leftSymbol;
    }

    public ArrayList<SymbolSet> GetRightSymbolSetList()
    {
        return rightSymbolSetList;
    }
}
