package supernova;

import java.util.ArrayList;
import java.util.Arrays;

public class GrammarSet
{
    private ArrayList<Grammar> grammarList;

    public GrammarSet()
    {
        grammarList = new ArrayList<>();
    }

    public void AddGrammar(Grammar grammar)
    {
        Symbol leftSymbol = grammar.GetLeftSymbol();
        Grammar matchGrammar = leftSymbol.IsPrime() ? FindMatchGrammar(new Symbol(leftSymbol.GetName(), leftSymbol.GetSymbolType(), false)) : FindMatchGrammar(leftSymbol);

        if (matchGrammar == null)
        {
            grammarList.add(grammar);
            return;
        }

        if (leftSymbol.IsPrime())
        {
            grammarList.add(grammarList.indexOf(matchGrammar) + 1, grammar);
        }
        else
        {
            for (SymbolSet symbolSet : grammar.GetRightSymbolSetList())
            {
                matchGrammar.AddRightSymbolSet(symbolSet);
            }
        }
    }

    public void ReplaceGrammar(Grammar grammar)
    {
        Grammar matchGrammar = FindMatchGrammar(grammar.GetLeftSymbol());

        if (matchGrammar == null)
        {
            return;
        }

        matchGrammar.ClearRightSymbolSet();
        for (SymbolSet symbolSet : grammar.GetRightSymbolSetList())
        {
            matchGrammar.AddRightSymbolSet(symbolSet);
        }
    }

    public Grammar FindMatchGrammar(Symbol leftSymbol)
    {
        return grammarList.stream().filter(g -> g.GetLeftSymbol().equals(leftSymbol)).findFirst().orElse(null);
    }

    public void RemoveRightRecursive()
    {
        ArrayList<Grammar> recursiveGrammarList = new ArrayList<>();

        // 좌 재귀가 있는 문법을 검색
        for (Grammar grammar : grammarList)
        {
            for (Symbol symbol : grammar.FindFrontSymbolList())
            {
                if (grammar.GetLeftSymbol().equals(symbol))
                {
                    recursiveGrammarList.add(grammar);
                    break;
                }
            }
        }

        for (Grammar grammar : recursiveGrammarList)
        {
            Symbol leftSymbol = grammar.GetLeftSymbol();
            Symbol primeSymbol = new Symbol(leftSymbol.GetName(), leftSymbol.GetSymbolType(), true);

            Grammar fixedGrammar = new Grammar(leftSymbol);
            Grammar primeGrammar = new Grammar(primeSymbol);

            for (SymbolSet symbolSet : grammar.GetRightSymbolSetList())
            {
                if (leftSymbol.equals(symbolSet.FindFrontSymbol()))
                {
                    symbolSet.RemoveMatchSymbol(leftSymbol);
                    symbolSet.AppendSymbol(primeSymbol);
                    primeGrammar.AddRightSymbolSet(symbolSet);
                }
                else
                {
                    symbolSet.AppendSymbol(primeSymbol);
                    fixedGrammar.AddRightSymbolSet(symbolSet);
                }
            }

            primeGrammar.AddRightSymbolSet(new SymbolSet());
            ReplaceGrammar(fixedGrammar);
            AddGrammar(primeGrammar);
        }
    }

    public ArrayList<Symbol> FindFirstList(Symbol symbol)
    {
        if (symbol.GetSymbolType() == Symbol.SymbolType.Terminal)
        {
            return new ArrayList<>(Arrays.asList(symbol));
        }

        ArrayList<Symbol> firstList = new ArrayList<>();
        ArrayList<Symbol> firstSymbolList = new ArrayList<>();

        for (Grammar grammar : grammarList)
        {
            if (grammar.GetLeftSymbol().GetName().equals(symbol.GetName()) == true)
            {
                firstSymbolList = grammar.FindFrontSymbolList();
                break;
            }
        }

        for (Symbol firstSymbol : firstSymbolList)
        {
            if (firstSymbol.equals(symbol) == true)
            {
                continue;
            }

            if (firstSymbol.GetSymbolType() == Symbol.SymbolType.Terminal)
            {
                firstList.add(firstSymbol);
            }
            else
            {
                firstList.addAll(FindFirstList(firstSymbol));
            }
        }

        return firstList;
    }

    @Override
    public String toString()
    {
        StringBuilder stringBuilder = new StringBuilder();

        for (Grammar grammar : grammarList)
        {
            stringBuilder.append(grammar.toString());
            stringBuilder.append("\n");
        }

        return stringBuilder.toString();
    }
}
