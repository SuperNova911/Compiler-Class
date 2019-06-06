package supernova;

public class Symbol
{
    private String name;
    private SymbolType symbolType;
    private boolean isPrime;

    public enum SymbolType
    {
        None, Terminal, NonTerminal, Epsilon, EndMarker
    }

    public Symbol(String name)
    {
        this(name, false);
    }

    public Symbol(String name, boolean isPrime)
    {
        if (IsSymbol(name) == false)
        {
            throw new IllegalArgumentException(String.format("'%s'은 심볼이 될 수 없습니다", name));
        }

        this.name = name;
        this.isPrime = isPrime;

        symbolType = FindSymbolType(name);
    }

    protected Symbol(String name, SymbolType symbolType, boolean isPrime)
    {
        this.name = name;
        this.symbolType = symbolType;
        this.isPrime = isPrime;
    }

    public static boolean IsSymbol(String name)
    {
        if (name == null || name.length() < 1)
        {
            return false;
        }

        return !name.equals(Grammar.DELIM_STRING) &&
               !name.equals(Grammar.EPSILON_STRING) &&
               !name.equals(Grammar.DERIVATION_STRING) &&
               !name.equals(Grammar.OR_STRING) &&
               !name.equals(Grammar.END_MARKER_STRING);
    }

    public static boolean IsTerminal(String name)
    {
        if (IsSymbol(name) == false)
        {
            return false;
        }

        return Character.isUpperCase(name.charAt(0));
    }

    public static SymbolType FindSymbolType(String name)
    {
        if (name == null)
        {
            return SymbolType.None;
        }

        if (IsSymbol(name))
        {
            return Character.isUpperCase(name.charAt(0)) ? SymbolType.NonTerminal : SymbolType.Terminal;
        }
        else
        {
            if (name.equals(Grammar.EPSILON_STRING))
            {
                return SymbolType.Epsilon;
            }
            else if (name.equals(Grammar.END_MARKER_STRING))
            {
                return SymbolType.EndMarker;
            }
            else
            {
                return SymbolType.None;
            }
        }
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof Symbol == false)
        {
            return false;
        }

        Symbol targetSymbol = (Symbol)obj;

        return name.equals(targetSymbol.name) && symbolType.equals(targetSymbol.symbolType) && isPrime == targetSymbol.isPrime;
    }

    @Override
    public String toString()
    {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(name);
        if (isPrime == true)
        {
            stringBuilder.append('\'');
        }
        stringBuilder.append(" ");

        return stringBuilder.toString();
    }

    public String GetName()
    {
        return name;
    }

    public SymbolType GetSymbolType()
    {
        return symbolType;
    }

    public boolean IsPrime()
    {
        return isPrime;
    }
}
