package supernova;

public class Symbol
{
    private String name;
    private SymbolType symbolType;
    private boolean isPrime;

    public enum SymbolType
    {
        Epsilon, Terminal, NonTerminal
    }

    public Symbol(String name, SymbolType symbolType)
    {
        this(name, symbolType, false);
    }

    public Symbol(String name, SymbolType symbolType, boolean isPrime)
    {
        this.name = name;
        this.symbolType = symbolType;
        this.isPrime = isPrime;
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

    public void SetPrime(boolean prime)
    {
        this.isPrime = prime;
    }
}
