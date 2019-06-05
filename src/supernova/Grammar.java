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
        this.leftSymbol = leftSymbol;
        rightSymbolSetList = new ArrayList<>();
    }

    public Grammar(Symbol leftSymbol, ArrayList<SymbolSet> rightSymbolList)
    {
        this.leftSymbol = leftSymbol;
        this.rightSymbolSetList = rightSymbolList;
    }

    public ArrayList<Symbol> GetFirstSymbolList()
    {
        ArrayList<Symbol> firstList = new ArrayList<>();
        Symbol firstSymbol;

        for (SymbolSet symbolSet : rightSymbolSetList)
        {
            if (symbolSet.IsSetEmpty())
            {
                continue;
            }

            firstSymbol = symbolSet.GetSymbolList().get(0);
            if (firstSymbol.GetSymbolType() == Symbol.SymbolType.Epsilon)
            {
                continue;
            }

            firstList.add(firstSymbol);
        }

        return firstList;
    }

    public void AddRightSymbolSet(SymbolSet symbolSet)
    {
        rightSymbolSetList.add(symbolSet);
    }

    public void RemoveMatchSymbolSet(SymbolSet symbolSet)
    {
        if (rightSymbolSetList.contains(symbolSet))
        {
            rightSymbolSetList.remove(symbolSet);
        }
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

        if (rightSymbolSetList.isEmpty() == false)
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
