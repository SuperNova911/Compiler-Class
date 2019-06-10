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
        if (grammarList.isEmpty())
        {
            System.out.println("FOLLOW 를 생성할 문법이 없습니다");
            return;
        }

        grammarList.get(0).AddFollowSymbol(new EndMarker());

        boolean hasUpdated = true;
        while (hasUpdated)
        {
            hasUpdated = false;

            for (Grammar targetGrammar : grammarList)
            {
                for (Grammar searchGrammar : grammarList)
                {
                    if (targetGrammar.equals(searchGrammar))
                    {
                        continue;
                    }

                    for (SymbolSet containSymbolSet : searchGrammar.FindContainSymbolSet(targetGrammar.GetLeftSymbol()))
                    {
                        Symbol behindSymbol = containSymbolSet.FindBehindSymbol(targetGrammar.GetLeftSymbol());
                        if (behindSymbol.GetSymbolType().equals(Symbol.SymbolType.NonTerminal))
                        {
                            Grammar behindGrammar = FindMatchGrammar(behindSymbol);
                            if (behindGrammar == null)
                            {
                                throw new IllegalArgumentException(String.format("'%s'의 이름을 시작으로 하는 문법이 없습니다", behindSymbol.GetName()));
                            }

                            for (SymbolSet behindGrammarSymbolSet : behindGrammar.GetRightSymbolSetList())
                            {
                                if (behindGrammarSymbolSet.FindFrontSymbol() == null)
                                {
                                    for (Symbol symbol : searchGrammar.GetFollowSymbolList())
                                    {
                                        if (targetGrammar.GetFollowSymbolList().contains(symbol) == false)
                                        {
                                            hasUpdated = true;
                                            targetGrammar.AddFollowSymbol(symbol);
                                        }
                                    }
                                }
                                else if (behindGrammarSymbolSet.FindFrontSymbol().GetSymbolType().equals(Symbol.SymbolType.NonTerminal))
                                {
                                    for (Symbol symbol : behindGrammar.GetFollowSymbolList())
                                    {
                                        if (targetGrammar.GetFollowSymbolList().contains(symbol) == false)
                                        {
                                            hasUpdated = true;
                                            targetGrammar.AddFollowSymbol(symbol);
                                        }
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

                                        if (targetGrammar.GetFollowSymbolList().contains(symbol) == false)
                                        {
                                            hasUpdated = true;
                                            targetGrammar.AddFollowSymbol(symbol);
                                        }
                                    }
                                }
                            }

                            for (Symbol symbol : FindFirst(behindSymbol))
                            {
                                if (symbol.GetSymbolType().equals(Symbol.SymbolType.Epsilon))
                                {
                                    continue;
                                }

                                if (targetGrammar.GetFollowSymbolList().contains(symbol) == false)
                                {
                                    hasUpdated = true;
                                    targetGrammar.AddFollowSymbol(symbol);
                                }
                            }
                        }
                        else if (IsNullable(behindSymbol))
                        {
                            for (Symbol symbol : searchGrammar.GetFollowSymbolList())
                            {
                                if (targetGrammar.GetFollowSymbolList().contains(symbol) == false)
                                {
                                    hasUpdated = true;
                                    targetGrammar.AddFollowSymbol(symbol);
                                }
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

                                if (targetGrammar.GetFollowSymbolList().contains(symbol) == false)
                                {
                                    hasUpdated = true;
                                    targetGrammar.AddFollowSymbol(symbol);
                                }
                            }
                        }
                    }
                }
            }
        }
//
//        for (Grammar targetGrammar : grammarList)
//        {
//            if (targetGrammar.equals(grammarList.get(0)))
//            {
//                targetGrammar.AddFollowSymbol(new EndMarker());
//            }
//
//            for (Grammar searchGrammar : grammarList)
//            {
//                for (SymbolSet containSymbolSet : searchGrammar.FindContainSymbolSet(targetGrammar.GetLeftSymbol()))
//                {
//                    Symbol behindSymbol = containSymbolSet.FindBehindSymbol(targetGrammar.GetLeftSymbol());
//
//                    if (behindSymbol.GetSymbolType().equals(Symbol.SymbolType.NonTerminal))
//                    {
//                        for (Symbol symbol : FindFirst(behindSymbol))
//                        {
//                            if (symbol.GetSymbolType().equals(Symbol.SymbolType.Epsilon))
//                            {
//                                continue;
//                            }
//
//                            targetGrammar.AddFollowSymbol(symbol);
//                        }
//                    }
//
//                    if (IsNullable(behindSymbol))
//                    {
//                        for (Symbol symbol : searchGrammar.GetFollowSymbolList())
//                        {
//                            targetGrammar.AddFollowSymbol(symbol);
//                        }
//                    }
//                    else
//                    {
//                        for (Symbol symbol : FindFirst(behindSymbol))
//                        {
//                            if (symbol.GetSymbolType().equals(Symbol.SymbolType.Epsilon))
//                            {
//                                continue;
//                            }
//
//                            targetGrammar.AddFollowSymbol(symbol);
//                        }
//                    }
//                }
//            }
//        }
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

    public boolean IsNullable(Symbol symbol)
    {
        return FindFirst(symbol).contains(new Epsilon());
    }

    public void AugmentGrammar()
    {
        final Symbol START_SYMBOL = new Symbol("S", true);

        if (grammarList.isEmpty())
        {
            System.out.println("존재하는 문법이 없어 증가 문법을 만들 수 없습니다.");
            return;
        }

        if (grammarList.get(0).GetLeftSymbol().equals(START_SYMBOL))
        {
            System.out.println("이미 증가 문법으로 만들어졌습니다");
            return;
        }

        Grammar startGrammar = new Grammar(START_SYMBOL);
        startGrammar.AddRightSymbolSet(new SymbolSet(grammarList.get(0).GetLeftSymbol()));
        grammarList.add(0, startGrammar);
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
