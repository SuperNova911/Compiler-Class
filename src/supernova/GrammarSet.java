package supernova;

import java.util.ArrayList;

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

    public void GenerateFirst()
    {
        for (Grammar grammar : grammarList)
        {
            grammar.ClearFirstSymbolList();

            for (Symbol firstSymbol : FindFirst(grammar.GetLeftSymbol()))
            {
                grammar.AddFirstSymbol(firstSymbol);
            }
        }
    }

    public void GenerateFollow()
    {
        for (Grammar targetGrammar : grammarList)
        {
            if (targetGrammar.equals(grammarList.get(0)))
            {
                targetGrammar.AddFollowSymbol(new EndMarker());
            }

            for (Grammar searchGrammar : grammarList)
            {
                for (SymbolSet containSymbolSet : searchGrammar.FindContainSymbolSet(targetGrammar.GetLeftSymbol()))
                {
                    Symbol behindSymbol = containSymbolSet.FindBehindSymbol(targetGrammar.GetLeftSymbol());

                    if (behindSymbol.GetSymbolType().equals(Symbol.SymbolType.NonTerminal))
                    {
                        for (Symbol symbol : FindFirst(behindSymbol))
                        {
                            if (symbol.GetSymbolType().equals(Symbol.SymbolType.Epsilon))
                            {
                                continue;
                            }

                            targetGrammar.AddFollowSymbol(symbol);
                        }
                    }

                    if (IsNullable(behindSymbol))
                    {
                        for (Symbol symbol : searchGrammar.GetFollowSymbolList())
                        {
                            targetGrammar.AddFollowSymbol(symbol);
                        }
                    }
                    else
                    {
                        for (Symbol symbol : FindFirst(behindSymbol))
                        {
                            if (symbol.GetSymbolType().equals(Symbol.SymbolType.Epsilon))
                            {
                                continue;
                            }

                            targetGrammar.AddFollowSymbol(symbol);
                        }
                    }
                }
            }
        }
    }

    public ArrayList<Symbol> FindFirst(Symbol symbol)
    {
        ArrayList<Symbol> firstSymbolList = new ArrayList<>();

        if (symbol.GetSymbolType().equals(Symbol.SymbolType.Terminal) || symbol.GetSymbolType().equals(Symbol.SymbolType.Epsilon))
        {
            firstSymbolList.add(symbol);
            return firstSymbolList;
        }

        Grammar matchGrammar = FindMatchGrammar(symbol);

        if (matchGrammar == null)
        {
            return firstSymbolList;
        }

        for (Symbol frontSymbol : matchGrammar.FindFrontSymbolList())
        {
            if (frontSymbol == null)
            {
                firstSymbolList.add(new Epsilon());
                continue;
            }

            if (frontSymbol.GetSymbolType().equals(Symbol.SymbolType.Terminal))
            {
                if (firstSymbolList.contains(frontSymbol) == false)
                {
                    firstSymbolList.add(frontSymbol);
                }
            }
            else
            {
                for (Symbol foundSymbol : FindFirst(frontSymbol))
                {
                    if (firstSymbolList.contains(foundSymbol) == false)
                    {
                        firstSymbolList.add(foundSymbol);
                    }
                }
            }
        }

        return firstSymbolList;
    }

    public ArrayList<Symbol> FindFollow(Symbol symbol)
    {
        Grammar matchGrammar = FindMatchGrammar(symbol);
        if (matchGrammar == null)
        {
            throw new IllegalArgumentException(String.format("'%s'의 이름을 가진 문법이 없습니다", symbol.GetName()));
        }

        ArrayList<Symbol> followSymbolList = new ArrayList<>();
        if (matchGrammar.equals(grammarList.get(0)))
        {
            followSymbolList.add(new EndMarker());
        }

        for (Grammar searchGrammar : grammarList)
        {
            for (SymbolSet containSymbolSet : searchGrammar.FindContainSymbolSet(symbol))
            {
                Symbol behindSymbol = containSymbolSet.FindBehindSymbol(symbol);
                if (behindSymbol.GetSymbolType().equals(Symbol.SymbolType.NonTerminal))
                {
                    Grammar behindGrammar = FindMatchGrammar(behindSymbol);
                    if (behindGrammar == null)
                    {
                        throw new IllegalArgumentException(String.format("'%s'의 이름을 가진 문법이 없습니다", symbol.GetName()));
                    }

                    for (SymbolSet behindGrammarSymbolSet : behindGrammar.GetRightSymbolSetList())
                    {
                        if (behindGrammarSymbolSet.FindFrontSymbol() == null)
                        {
                            if (behindGrammar.IsValidFollow())
                            {
                                followSymbolList.addAll(behindGrammar.GetFollowSymbolList());
                            }
                            else
                            {
                                followSymbolList.addAll(FindFollow(behindSymbol));
                                behindGrammar.SetValidFollow(true);

                            }
                        }
                        else
                        {
                            followSymbolList.addAll(FindFirst(behindSymbol));
                        }
                    }
                }
                else
                {
                    if (IsNullable(behindSymbol))
                    {
                        if (searchGrammar.IsValidFollow())
                        {
                            followSymbolList.addAll(searchGrammar.GetFollowSymbolList());
                        }
                        else
                        {
                            followSymbolList.addAll(FindFollow(searchGrammar.GetLeftSymbol()));
                            searchGrammar.SetValidFollow(true);
                        }
                    }
                    else
                    {
                        followSymbolList.addAll(FindFirst(behindSymbol));
                    }
                }
            }
        }

        return followSymbolList;
    }

    public boolean IsNullable(Symbol symbol)
    {
        return FindFirst(symbol).contains(new Epsilon());
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

    public ArrayList<Grammar> GetGrammarList()
    {
        return grammarList;
    }
}
