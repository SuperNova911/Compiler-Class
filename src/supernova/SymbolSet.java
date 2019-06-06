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
        for (Symbol symbol : symbolList)
        {
            AppendSymbol(symbol);
        }
    }

    public void AppendSymbol(Symbol symbol)
    {
        if (symbol.GetSymbolType().equals(Symbol.SymbolType.Epsilon))
        {
            throw new IllegalArgumentException("SymbolSet 에 Epsilon 을 추가하는것은 허용되지 않습니다");
        }
        symbolList.add(symbol);
    }

    public void RemoveMatchSymbol(Symbol symbol)
    {
        symbolList.remove(symbol);
    }

    public Symbol FindFrontSymbol()
    {
        return symbolList.isEmpty() ? null : symbolList.get(0);
    }

    public boolean HasSymbol(Symbol symbol)
    {
        return symbolList.contains(symbol);
    }

    public boolean IsSetEmpty()
    {
        return symbolList.isEmpty();
    }

    @Override
    public String toString()
    {
        StringBuilder stringBuilder = new StringBuilder();

        if (symbolList.isEmpty())
        {
            stringBuilder.append(new Epsilon().toString());
        }
        else
        {
            for (Symbol symbol : symbolList)
            {
                stringBuilder.append(symbol.toString());
            }
        }

        return stringBuilder.toString();
    }

    public ArrayList<Symbol> GetSymbolList()
    {
        return symbolList;
    }
}