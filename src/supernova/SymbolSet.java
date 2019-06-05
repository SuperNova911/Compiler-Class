package supernova;

import java.util.ArrayList;

public class SymbolSet
{
    private ArrayList<Symbol> symbolList;

    public SymbolSet()
    {
        symbolList = new ArrayList<>();
    }

    public SymbolSet(Symbol symbol)
    {
        this();
        AppendSymbol(symbol);
    }

    public SymbolSet(ArrayList<Symbol> symbolList)
    {
        this();
        this.symbolList.addAll(symbolList);
    }

    public void AppendSymbol(Symbol symbol)
    {
        symbolList.add(symbol);
    }

    public void RemoveMatchSymbol(Symbol symbol)
    {
        if (symbolList.contains(symbol))
        {
            symbolList.remove(symbol);
        }
    }

    public Symbol FindFrontSymbol()
    {
        if (symbolList.isEmpty())
        {
            return new Epsilon();
        }
        else
        {
            return symbolList.get(0);
        }
    }

    public boolean HasSymbol(Symbol targetSymbol)
    {
        for (Symbol symbol : symbolList)
        {
            if (symbol.equals(targetSymbol))
            {
                return true;
            }
        }

        return false;
    }

    public boolean IsSetEmpty()
    {
        return symbolList.isEmpty();
    }

    @Override
    public String toString()
    {
        StringBuilder stringBuilder = new StringBuilder();

        for (Symbol symbol : symbolList)
        {
            stringBuilder.append(symbol.toString());
        }

        return stringBuilder.toString();
    }

    public ArrayList<Symbol> GetSymbolList()
    {
        return symbolList;
    }
}
